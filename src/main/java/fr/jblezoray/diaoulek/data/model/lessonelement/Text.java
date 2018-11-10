package fr.jblezoray.diaoulek.data.model.lessonelement;

import com.sun.tools.javac.util.Pair;
import fr.jblezoray.diaoulek.data.model.lessonelement.qrcouple.SoundReference;

import java.util.List;

public class Text extends LessonElement {

    private final List<Pair<String, SoundReference>> text;
    private final String comment;

    public Text(List<Pair<String, SoundReference>> text, String comment) {
        this.text = text;
        this.comment = comment;
    }

    public List<Pair<String, SoundReference>> getText() {
        return text;
    }

    public String getComment() {
        return comment;
    }
}
