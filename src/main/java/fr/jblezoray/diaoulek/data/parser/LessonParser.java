package fr.jblezoray.diaoulek.data.parser;

import com.google.common.collect.ListMultimap;
import com.google.common.collect.MultimapBuilder;
import com.sun.tools.javac.util.Pair;
import fr.jblezoray.diaoulek.data.model.*;
import fr.jblezoray.diaoulek.data.model.lessonelement.QRCouple;
import fr.jblezoray.diaoulek.data.model.lessonelement.Text;
import fr.jblezoray.diaoulek.data.model.lessonelement.WordReference;
import fr.jblezoray.diaoulek.data.model.lessonelement.qrcouple.QRCoupleSeparationLine;
import fr.jblezoray.diaoulek.data.model.lessonelement.qrcouple.SoundReference;
import fr.jblezoray.diaoulek.data.model.lessonelement.qrcouple.Question;
import fr.jblezoray.diaoulek.data.model.lessonelement.qrcouple.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.*;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

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


    private static Text parseLessonText(List<String> lines) throws DataException {
        List<Pair<String, SoundReference>> lessonText = new ArrayList<>();
        Map<String, SoundReference> sounds = new HashMap<>();
        StringBuilder commentText = new StringBuilder();



        boolean soundMode = false;
        boolean commentMode = false;
        for (String line : lines) {
            if (line.startsWith("<)start)")) {
                soundMode = true;

            } else if (line.startsWith("<)end)")) {
                soundMode = false;

            } else if (line.startsWith("Commentaire")) {
                commentMode = true;

            } else if (soundMode) {
                int spaceIndex = line.trim().indexOf(" ");
                if (spaceIndex!=-1) {
                    String key = line.trim().substring(0, spaceIndex);
                    SoundReference sound = parseSound(line);
                    sounds.put(key, sound);
                }

            } else {
                String l = line.trim();
                if (l.length() > 0) {
                    if (commentMode) {
                        commentText.append(l).append(System.lineSeparator());
                    } else {
                        SoundReference sr = null;
                        for (String soundKey: sounds.keySet()) {
                            if (l.startsWith(soundKey)) {
                                sr = sounds.get(soundKey);
                                l = l.substring(soundKey.length()).trim();
                            }
                        }
                        lessonText.add(new Pair<>(l, sr));
                    }
                }
            }
        }

        // remove last \n
        int idx = commentText.lastIndexOf(System.lineSeparator());
        String commentTextStr = (idx==-1) ? commentText.toString() :
                commentText.deleteCharAt(idx).toString();

        return new Text(lessonText, commentTextStr);
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
            line = lines.stream().reduce(line, (l1, l2) -> l1 + "\n" + l2);

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
                qrCouple.setQuestion(parseQuestion(line));

            } else if (line.startsWith("R>")) {
                qrCouple.setResponse(parseResponse(line));

            } else {
                throw new DataException("Cannot parse line " + line);
            }
        }
//        if (qrCouple.getQuestion()==null ||qrCouple.getResponse()==null ||qrCouple.getSeparationLine()==null) {
//            throw new DataException("QR misses mandatory elements : '" + firstLine + "'");
//        }
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


    private static SoundReference parseSound(String line) throws DataException {
        SoundReference sound = new SoundReference();
        String[] split = line.trim().split("\\s+");
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

    private static Question parseQuestion(String line) {
        String raw = removeDuplicatesWhitespaces(removePrefix(line, "Q>"));
        Part[] parts = Arrays.stream(raw.split(";"))
                .map(String::trim)
                .map(part -> new Part(part, parsePhrase(part)))
                .collect(Collectors.toList())
                .toArray(new Part[0]);

        Question q = new Question();
        q.setRawString(raw);
        q.setParts(parts);
        return q;
    }

    private static Response parseResponse(String line) {
        String raw = removeDuplicatesWhitespaces(removePrefix(line, "R>"));
        Part[] parts = Arrays.stream(raw.split(";"))
                .map(String::trim)
                .map(part -> new Part(part, parsePhrase(part)))
                .collect(Collectors.toList())
                .toArray(new Part[0]);

        Response r = new Response();
        r.setRawString(raw);
        r.setParts(parts);
        return r;
    }

    private static String[] parsePhrase(String part) {
        return Arrays.stream(part.split(","))
                .map(String::trim)
                .collect(Collectors.toList())
                .toArray(new String[0]);
    }

    private static String removeDuplicatesWhitespaces(String line) {
        return line
                .trim()
                .replaceAll("\\h*\\r?\\v+\\h*", Matcher.quoteReplacement("\n"))
                .replaceAll("\\h+", Matcher.quoteReplacement(" "));
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
