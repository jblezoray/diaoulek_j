package fr.jblezoray.diaoulek.data.parser;

import fr.jblezoray.diaoulek.data.model.FileIndexEntry;
import fr.jblezoray.diaoulek.data.model.LessonEntry;
import fr.jblezoray.diaoulek.data.model.Part;
import fr.jblezoray.diaoulek.data.model.lessonelement.QRCouple;
import fr.jblezoray.diaoulek.data.model.lessonelement.Text;
import fr.jblezoray.diaoulek.data.model.lessonelement.WordReference;
import fr.jblezoray.diaoulek.data.model.lessonelement.qrcouple.QRCoupleSeparationLine;
import fr.jblezoray.diaoulek.data.model.lessonelement.qrcouple.Question;
import fr.jblezoray.diaoulek.data.model.lessonelement.qrcouple.Response;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Matcher;

/**
 * Parses a Lesson file.
 */
public class LessonParser implements IParser<LessonEntry> {

    private final Charset charset;

    private static final Predicate<String> EMPTY_LINE = l -> l.trim().length() == 0;
    private static final Predicate<String> COMMENT_LINE = l -> l.startsWith("!");
    private static final Predicate<String> INDENTED_LINE = l -> l.startsWith("  ");

    public LessonParser(Charset charset) {
         this.charset = charset;
    }

    @Override
    public boolean seemsParseable(FileIndexEntry fileIndexEntry) {
        String filename = fileIndexEntry.getFilename();
        return filename.endsWith(".txt") &&
                !filename.startsWith("IDX1");
    }

    @Override
    public LessonEntry parse(byte[] fileContent, FileIndexEntry fileIndexEntry) throws DataException {
        LessonEntry le = new LessonEntry(fileIndexEntry);

        try (DiaoulekFileReader reader = new DiaoulekFileReader(fileContent, this.charset)) {
            le.setAlias(reader.readFileAlias());

            reader.setIgnore(COMMENT_LINE, EMPTY_LINE);
            reader.setMergeLineWithPreviousIf(INDENTED_LINE);

            String line;
            while ((line = reader.readNextLine()) != null) {

                // something between two lines of '###...' is a Lesson text.
                if (line.startsWith("###")) {
                    // read all the lines until another line of '###...'.
                    reader.setIgnore();
                    reader.setMergeLineWithPreviousIf();
                    List<String> lines = reader.readLinesUntilNextline(l ->
                            l.replaceAll(" ", "")
                                .matches("^#{3,}$"));
                    reader.readNextLine(); // pass the '###' line.
                    reader.setIgnore(COMMENT_LINE, EMPTY_LINE);
                    reader.setMergeLineWithPreviousIf(INDENTED_LINE);

                    Text text = new LessonTextParser(lines).parseLessonText();
                    le.getLessonElements().add(text);
                }
                // "##something..." is a word reference from another lesson.
                else if (line.startsWith("##")) {
                    WordReference wr = parseWordReference(line);
                    le.getLessonElements().add(wr);
                }
                // DOC: On introduira ensuite les couples de questions et
                // réponses par les signes «#», «Q>» et «R>»
                else if (line.startsWith("#")) {
                    List<String> lines = reader.readLinesUntilNextline(l ->
                            l.startsWith("#"));
                    lines.add(0, line);
                    QRCouple qrCouple = parseQRCouple(lines);
                    le.getLessonElements().add(qrCouple);
                }
            }
        } catch  (IOException e) {
            throw new DataException("Cannot read file.", e);
        }

        return le;
    }



    // Pour réutiliser l'entrée « anken » dans une de vos leçon, il suffit
    // de faire un copier-coller de la ligne :
    // "##anken *KK12-3 3995 * Q3 sentiment"
    // En fait, seul le début de la ligne est nécessaire, il rappelle
    // l'entrée recherchée, « anken » et donne le nom de la leçon où on
    // peut le trouver, ici « KK12-3 ». Le reste de la ligne n'est pas
    // indispensable, on y trouve cependant la liste des étiquettes attachées
    // à cette entrée, ici « sentiment » et c'est parfois bien utile pour
    // choisir le mot ou améliorer la base de données.
    private static WordReference parseWordReference(String line) throws DataException {
        String[] split = line.substring(2).split("\\s+");
        if (split.length<2) throw new DataException(line);

        WordReference wr = new WordReference();
        wr.setWord(split[0]);
        wr.setLessonAlias(split[1].substring(1));
        // IDK what field 3 (the integer) is for.
        if (split.length>4) {
            String[] tags = Arrays.copyOfRange(split, 4, split.length);
            wr.setTags(tags);
        }
        return wr;
    }


    private static QRCouple parseQRCouple(List<String> lines) throws DataException{
        QRCouple qrCouple = new QRCouple();
        qrCouple.setSeparationLine(parseSeparationLine(lines.get(0), "#"));
        lines.remove(0);

        for (String line : lines) {

            if (line.startsWith("%#")) {
                // ancienne ligne de séparation
                // il peut y en avoir plusieurs, mais seule la dernière compte.
                qrCouple.setSeparationLineLegacy(
                        parseSeparationLine(line, "%#"));

            } else if (line.startsWith("R#")) {
                // ligne spéciale pour le dictionnaire inverse.
                qrCouple.setSeparationLineReverse(
                        parseSeparationLine(line, "R#"));

            } else if (line.startsWith("<))")) {
                // référence audio
                qrCouple.setSound(new LessonSoundReferenceParser(line).parseSound());

            } else if (line.startsWith("Q>")) {
                qrCouple.setQuestion(parseQuestion(line));

            } else if (line.startsWith("R>")) {
                qrCouple.setResponse(parseResponse(line));

            } else {
                throw new DataException("Cannot parse line " + line);
            }
        }
        if (qrCouple.getQuestion()==null ||qrCouple.getResponse()==null ||qrCouple.getSeparationLine()==null) {
            throw new DataException("QR misses mandatory elements : '" + lines + "'");
        }
        return qrCouple;
    }

    // #faim, loup ; faim de loup *  expression tag3
    //  ---REFS---   ----NOTE----    ------TAGS-----
    private static QRCoupleSeparationLine parseSeparationLine(String line, String prefix) {
        String[] split = split(removePrefix(line, prefix), "\\*");
        QRCoupleSeparationLine sepLine = new QRCoupleSeparationLine();
        if (split.length >=1) {
            String[] split2 = split(split[0], ";");
            if (split2.length >=1)
                sepLine.setWordReferences(split(split2[0], ",\\s*"));
            if (split2.length >=2)
                sepLine.setNote(trimToNull(split2[1]));
        }
        if (split.length >=2)
            sepLine.setTags(split(split[1], "\\s+"));
        return sepLine;
    }


    private static Question parseQuestion(String line) {
        String raw = removeDuplicatesWhitespaces(removePrefix(line, "Q>"));
        Part[] parts = Arrays.stream(raw.split(";"))
                .map(String::trim)
                .map(part -> new Part(part))
                .toArray(Part[]::new);
        Question q = new Question();
        q.setRawString(raw);
        q.setParts(parts);
        return q;
    }

    private static Response parseResponse(String line) {
        String raw = removeDuplicatesWhitespaces(removePrefix(line, "R>"));
        Part[] parts = Arrays.stream(raw.split(";"))
                .map(String::trim)
                .map(part -> new Part(part))
                .toArray(Part[]::new);
        Response r = new Response();
        r.setRawString(raw);
        r.setParts(parts);
        return r;
    }

    private static String removeDuplicatesWhitespaces(String line) {
        return line
                .trim()
//                .replaceAll("\\h*\\r?\\v+\\h*", Matcher.quoteReplacement(" "))
                .replaceAll("\\s+", Matcher.quoteReplacement(" "));
    }

    private static String removePrefix(String original, String prefix) {
        return original==null ? null :
                prefix==null ? original :
                original.startsWith(prefix) ? original.substring(prefix.length()) :
                original;
    }

    private static String[] split(String original, String regexSeparator) {
        String t = trimToNull(original);
        if (t==null) return new String[0];
        String[] s = t.split(regexSeparator);
        ArrayList<String> l = new ArrayList<>(s.length);
        for (int i=0; i<s.length; i++) {
            s[i] = trimToNull(s[i]);
            if (s[i]!=null) l.add(s[i]);
        }
        return s;
    }

    private static String trimToNull(String input) {
        if (input==null) return null;

        String output = input.trim();
        if (output.length()==0) return null;

        return output;
    }

}
