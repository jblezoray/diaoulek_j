package fr.jblezoray.diaoulek.data.model.lessonelement;

import fr.jblezoray.diaoulek.data.model.lessonelement.lesson.LessonTextLine;

import java.util.List;

public class Text extends LessonElement {

    private final List<LessonTextLine> text;
    private final String comment;

    public Text(List<LessonTextLine> text, String comment) {
        this.text = text;
        this.comment = comment;
    }

    public List<LessonTextLine> getText() {
        return text;
    }

    public String getComment() {
        return comment;
    }
}
