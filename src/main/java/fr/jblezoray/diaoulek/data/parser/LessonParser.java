package fr.jblezoray.diaoulek.data.parser;

import fr.jblezoray.diaoulek.data.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class LessonParser implements IParser<LessonEntry> {

    private static final Logger LOGGER = LoggerFactory.getLogger(LessonParser.class);

    private final Charset charset;

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

            String line;
            while ((line = reader.readNextLine(
                    DiaoulekFileReader.EMPTY_LINE.negate(),
                    DiaoulekFileReader.COMMENT_LINE.negate() )) != null) {

                // something between two lines of '###...' is a Lesson text.
                if (line.startsWith("###")) {
                    // read all the lines until another line of '###...'.
                    List<String> lines = reader.readLinesUntil(l ->
                            !l.replaceAll(" ", "")
                                .matches("^#{3,}$"));
                    Text text = parseLessonText(lines);
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
                    QRCouple qrCouple = parseQRCouple(line, reader);
                    le.getLessonElements().add(qrCouple);
                }
            }
        } catch  (IOException e) {
            throw new DataException("Cannot read file.", e);
        }

        return le;
    }


    private static Text parseLessonText(List<String> lines) {
        String lessonText = String.join("\n", lines);
        return new Text(lessonText);
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


    private static QRCouple parseQRCouple(String firstLine, DiaoulekFileReader reader) throws DataException{
        QRCouple qrCouple = new QRCouple();
        qrCouple.setSeparationLine(parseSeparationLine(firstLine, "#"));

        String line;
        while ((line = reader.readNextLine())!=null && !DiaoulekFileReader.EMPTY_LINE.test(line)) {
            List<String> lines = reader.readLinesUntilNextline(l -> !l.startsWith("  "));
            line = lines.stream().reduce(line, (l1, l2) -> l1 + l2);

            lines.add(0, line);

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
                qrCouple.setSound(parseSound(line));

            } else if (line.startsWith("Q>")) {
                // TODO

            } else if (line.startsWith("R>")) {
                // TODO

            } else {
                // Skip it.  It is either :
                // - part of a Q> or a R>, and was already parsed.
                // - a blank line
            }
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


    private static QRCoupleSound parseSound(String line) throws DataException {
        QRCoupleSound sound = new QRCoupleSound();
        String[] split = line.split("\\s+");
        if (split.length!=2 && split.length!=4)
            throw new DataException(line);
        sound.setSoundFileName(split[1]);
        if (split.length==4) {
            try {
                // I've seen case twice a case with "something" at the end :
                // <))  aln-ke-41.ogg   4365824   5159936:
                // <))  aln-ke-50.ogg   332288   713728:set
                // hence, this little hack :
                if (split[3].contains(":")) split[3] = split[3].split(":")[0];

                sound.setSoundBeginIndex(Integer.valueOf(split[2]));
                sound.setSoundEndIndex(Integer.valueOf(split[3]));
            } catch (NumberFormatException nfe) {
                throw new DataException("Cannot parse value", nfe);
            }
        } else {
            sound.setSoundBeginIndex(0);
            sound.setSoundEndIndex(0);
        }
        return sound;
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
