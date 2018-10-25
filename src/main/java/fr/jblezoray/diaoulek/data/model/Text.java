package fr.jblezoray.diaoulek.data.model;

public class Text extends LessonElement {
    private final String text;

    public Text(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }
}
