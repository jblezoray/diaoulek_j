package fr.jblezoray.diaoulek.data.model;

public class WordReference extends LessonElement {
    private String word;
    private String lessonAlias;
    private String[] tags;

    public void setWord(String word) {
        this.word = word;
    }

    public String getWord() {
        return word;
    }

    public void setLessonAlias(String lessonAlias) {
        this.lessonAlias = lessonAlias;
    }

    public String getLessonAlias() {
        return lessonAlias;
    }

    public void setTags(String[] tags) {
        this.tags = tags;
    }

    public String[] getTags() {
        return tags;
    }
}
