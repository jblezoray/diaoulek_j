package fr.jblezoray.diaoulek.data.parser;

import fr.jblezoray.diaoulek.data.model.lessonelement.Text;
import fr.jblezoray.diaoulek.data.model.lessonelement.lesson.LessonTextLine;
import fr.jblezoray.diaoulek.data.model.lessonelement.qrcouple.SoundReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LessonTextParser {

    private static final Pattern SOUND_REF_PATTERN = Pattern.compile("^\\p{Upper}>(\\p{Digit}+)");

    private final List<String> inputLines;

    private final Map<String, SoundReference> sounds = new HashMap<>();
    private final StringBuilder commentText = new StringBuilder();
    private final List<LessonTextLine> lessonText = new ArrayList<>();

    private LessonTextLine previousLessonTextLine = null;

    public LessonTextParser(List<String> lines) {
        this.inputLines = lines;
    }


    public Text parseLessonText() throws DataException {

        boolean soundMode = false;
        boolean commentMode = false;

        for (String line : inputLines) {
            String lineTrimed = line.trim();
            if (lineTrimed.startsWith("<)start)")) {
                soundMode = true;

            } else if (lineTrimed.startsWith("<)end)")) {
                soundMode = false;

            } else if (lineTrimed.startsWith("Commentaire")) {
                commentMode = true;

            } else if (soundMode) {
                readSoundReference(lineTrimed);

            } else if (lineTrimed.length() == 0) {
                // empty line, noop

            } else if (commentMode) {
                readCommentLine(lineTrimed);

            } else {
                readLessonLine(lineTrimed);
            }
        }

        return new Text(lessonText, getCommentTextString());
    }


    private String getCommentTextString() {
        // removes last \n
        int idx = commentText.lastIndexOf(System.lineSeparator());
        return (idx==-1) ? commentText.toString() :
                commentText.deleteCharAt(idx).toString();
    }


    private void readLessonLine(String rawLine) {

        // search for a reference of this sound in the 'sounds' index
        String soundKey = searchForSoundReference(rawLine);
        if (soundKey != null) {
            String txt = rawLine.substring(soundKey.length()).trim();
            LessonTextLine line = new LessonTextLine(txt, soundKey, sounds.get(soundKey));
            this.lessonText.add(line);
            previousLessonTextLine = line;

        } else {

            // otherwise, it may be a translation of a previous line.
            Matcher m = SOUND_REF_PATTERN.matcher(rawLine);
            if (m.find()) {
                // it looks like a sound pattern ma'm !  Now set this line as
                // the translation of a previous line that looks similar.  e.g.,
                // "F>23" looks similar as "B>23".
                this.lessonText.stream()
                        .filter(l -> l.getSoundTag()!=null)
                        .filter(l -> l.getSoundTag().endsWith(">"+m.group(1)))
                        .findFirst()
                        .ifPresent(l -> {
                            String tr = rawLine.substring(m.group().length()).trim();
                            l.setTranslation(tr);
                        });

            } else {
                // nope.  it is simply a new lesson line.
                if (previousLessonTextLine==null) {
                    LessonTextLine line = new LessonTextLine(rawLine.trim());
                    this.lessonText.add(line);
                    previousLessonTextLine = line;
                }
                // ... or a new line continuing a translation
                else if (previousLessonTextLine.getTranslation()!=null) {
                    String newTr = previousLessonTextLine.getTranslation().trim()
                            + " " + rawLine.trim();
                    previousLessonTextLine.setTranslation(newTr);
                }
                // ... or a new line continuing a text.
                else if (previousLessonTextLine.getLine()!=null) {
                    String newLine = previousLessonTextLine.getLine().trim()
                            + " " +  rawLine.trim();
                    previousLessonTextLine.setLine(newLine);
                }
            }
        }
    }


    private String searchForSoundReference(String line) {
        String sk = null;
        for (String soundKey: sounds.keySet()) {
            if (line.startsWith(soundKey)) {
                sk = soundKey;
                break;
            }
        }
        return sk;
    }


    private void readSoundReference(String line) throws DataException {
        int spaceIndex = line.indexOf(" ");
        if (spaceIndex==-1)
            return ;
        String key = line.substring(0, spaceIndex);
        SoundReference sound = new LessonSoundReferenceParser(line).parseSound();
        sounds.put(key, sound);
    }


    private void readCommentLine(String line) {
        commentText.append(line).append(System.lineSeparator());
    }
}
