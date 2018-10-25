package fr.jblezoray.diaoulek.data.model;

public class QRCoupleSeparationLine {
    private String[] wordReferences;
    private String note;
    private String[] tags;

    public String[] getWordReferences() {
        return wordReferences;
    }

    public void setWordReferences(String[] wordReferences) {
        this.wordReferences = wordReferences;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getNote() {
        return note;
    }

    public String[] getTags() {
        return tags;
    }

    public void setTags(String[] tags) {
        this.tags = tags;
    }

}
