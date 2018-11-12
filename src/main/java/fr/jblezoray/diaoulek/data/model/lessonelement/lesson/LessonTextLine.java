package fr.jblezoray.diaoulek.data.model.lessonelement.lesson;

import fr.jblezoray.diaoulek.data.model.lessonelement.qrcouple.SoundReference;

public class LessonTextLine {

    private String line;
    private String soundTag;
    private SoundReference sound;
    private String translation;

    public LessonTextLine(String line) {
        this.line = line;
    }

    public LessonTextLine(String soundTag, SoundReference sound) {
        this.sound = sound;
    }

    public LessonTextLine(String line, String soundTag, SoundReference sound) {
        this.line = line;
        this.soundTag = soundTag;
        this.sound = sound;
    }

    public void setTranslation(String translation) {
        this.translation = translation;
    }

    public String getLine() {
        return line;
    }

    public SoundReference getSound() {
        return sound;
    }

    public String getSoundTag() {
        return soundTag;
    }

    public String getTranslation() {
        return translation;
    }

    public void setLine(String line) {
        this.line = line;
    }
}
