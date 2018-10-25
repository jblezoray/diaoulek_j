package fr.jblezoray.diaoulek.data.model;

import java.util.Optional;

public enum Language {
    BREZHONEG("br"),
    ENGLISH("en"),
    FRANCAIS("fr");

    private final String twoLetters;

    Language(String twoLetters) {
        this.twoLetters = twoLetters;
    }

    public static Optional<Language> getLanguageFor(String twoLetters) {
        Optional<Language> optL = Optional.empty();
        for (Language l : Language.values()) {
            if (twoLetters==l.twoLetters) {
                optL = Optional.of(l);
                break;
            }
        }
        return optL;
    }


}
