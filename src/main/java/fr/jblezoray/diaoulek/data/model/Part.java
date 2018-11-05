package fr.jblezoray.diaoulek.data.model;

public class Part {

    private String rawString;
    private String[] phrases;

    public Part(String rawString, String[] phrases) {
        this.rawString = rawString;
        this.phrases = phrases;
    }

    public String[] getPhrases() {
        return phrases;
    }

    @Override
    public String toString() {
        return rawString;
    }
}