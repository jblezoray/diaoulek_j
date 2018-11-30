package fr.jblezoray.diaoulek.data.model;

public class Part {

    private String rawString;

    public Part(String rawString) {
        this.rawString = rawString;
    }

    public String getRawString() {
        return rawString;
    }

    @Override
    public String toString() {
        return rawString;
    }
}
