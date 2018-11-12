package fr.jblezoray.diaoulek.data.parser;

import fr.jblezoray.diaoulek.data.model.lessonelement.qrcouple.SoundReference;

public class LessonSoundReferenceParser {

    private String line;

    public LessonSoundReferenceParser(String line) {
        this.line = line;
    }

    public SoundReference parseSound() throws DataException {
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

}
