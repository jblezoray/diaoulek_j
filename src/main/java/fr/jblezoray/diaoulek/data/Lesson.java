package fr.jblezoray.diaoulek.data;

import fr.jblezoray.diaoulek.data.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

public class Lesson {

    private static final Logger LOGGER = LoggerFactory.getLogger(Lesson.class);

    private final LessonEntry l;

    public Lesson(FileIndexEntry fileIndexEntry) {
         l = new LessonEntry(fileIndexEntry);
    }

    public LessonEntry getResult() {
        return l;
    }

    public boolean smellsLikeLesson() {
        String filename = this.l.getFileIndexEntry().getFilename();
        return filename.endsWith(".txt") &&
                !filename.startsWith("IDX1");
    }

    public void parse(String fileContent) throws DataException {

        try (BufferedReader br =  new BufferedReader(new StringReader(fileContent))) {

            // DOC: La première ligne de la leçon doit commencer par les
            // signes « !# » suivis d'un espace et de l'alias de la leçon.
            String line = br.readLine();
            if (!line.startsWith("!#")) throw new DataException(line);
            l.setAlias(line.substring(2).trim());

            while ((line =br.readLine()) != null) {

                // DOC: Les lignes commençant par le signe « ! » sont des
                // commentaires et sont ignorées.
                if (line.startsWith("!")) continue;

                // Empty line to ignore.
                if (line.trim().length()==0) continue;

                // something between two lines of '###...' is a Lesson text.
                if (line.startsWith("###")) {
                    // read all the lines until another line of '#'.
                    List<String> lines = readLinesUntil(br,
                            l -> !l.replaceAll(" ", "").matches("^#{3,}$"));
                    parseLessonText(lines);
                }
                // "##something..." is a word reference from another lesson.
                else if (line.startsWith("##")) {
                    parseWordReference(line);
                }
                // DOC: On introduira ensuite les couples de questions et
                // réponses par les signes «#», «Q>» et «R>»
                else if (line.startsWith("#")) {
                    // read all the subsequent lines, until a blank one.
                    List<String> lines = readLinesUntil(br,
                            l -> l.trim().length() > 0);
                    parseQRCouple(line, lines);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    private List<String> readLinesUntil(BufferedReader br, Predicate<String> continuationCondition) throws IOException {
        List<String> lines = new ArrayList<>();
        String line;
        while ((line = br.readLine())!=null && continuationCondition.test(line))
            lines.add(line);
        return lines;
    }

    private void parseLessonText(List<String> lines) {
        String lessonText = String.join("\n", lines);
        this.l.getLessonElements().add(new Text(lessonText));
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
    private void parseWordReference(String line) throws DataException {
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
        l.getLessonElements().add(wr);
    }


    private void parseQRCouple(String firstLine, List<String> lines) throws DataException{
        QRCouple qrCouple = new QRCouple();
        qrCouple.setSeparationLine(parseSeparationLine(firstLine, "#"));

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
                qrCouple.setSound(parseSound(line));

            } else if (line.startsWith("Q>")) {
                // TODO

            } else if (line.startsWith("R>")) {
                // TODO

            } else if (line.startsWith("  ")) {
                // skip it.
                // it is part of a Q> or a R>, and was already parsed.
            } else {
                throw new DataException("unrecognized line : " + line);
            }
        }

        l.getLessonElements().add(qrCouple);
    }

    // #faim, loup ; faim de loup *  expression tag3
    //  ---REFS---   ----NOTE----    ------TAGS-----
    private QRCoupleSeparationLine parseSeparationLine(String line, String prefix) {
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


    private QRCoupleSound parseSound(String line) throws DataException {
        QRCoupleSound sound = new QRCoupleSound();
        String[] split = line.split("\\s+");
        if (split.length!=2 && split.length!=4)
            throw new DataException(line);
        sound.setSoundFileName(split[1]);
        if (split.length==4) {
            try {
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
        if (t==null) return null;
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
