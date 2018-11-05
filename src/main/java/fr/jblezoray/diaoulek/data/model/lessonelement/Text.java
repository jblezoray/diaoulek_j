package fr.jblezoray.diaoulek.data.model.lessonelement;

import fr.jblezoray.diaoulek.data.model.lessonelement.LessonElement;
import fr.jblezoray.diaoulek.data.model.lessonelement.qrcouple.SoundReference;

import java.util.Map;

public class Text extends LessonElement {

    private final Map<String, SoundReference> sounds;
    private final String text;

    public Text(String text, Map<String, SoundReference> sounds) {
        this.text = text;
        this.sounds = sounds;
    }

    public String getText() {
        return text;
    }

    public Map<String, SoundReference> getSounds() {
        return sounds;
    }
}
