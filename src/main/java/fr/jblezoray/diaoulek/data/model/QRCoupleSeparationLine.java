package fr.jblezoray.diaoulek.data.model;

public class QRCoupleSeparationLine {
    private String[] wordReferences = new String[0];
    private String note;
    private String[] tags = new String[0];

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
