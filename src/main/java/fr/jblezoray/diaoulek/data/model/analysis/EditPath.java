package fr.jblezoray.diaoulek.data.model.analysis;

import java.util.List;

public class EditPath<PART> {

    private final List<EditOperation<PART>> path;
    private final int levenshteinScore;

    public EditPath(List<EditOperation<PART>> path, int levenshteinScore) {
        this.path = path;
        this.levenshteinScore = levenshteinScore;
    }


    public List<EditOperation<PART>> getPath() {
        return path;
    }

    public int getLevenshteinScore() {
        return levenshteinScore;
    }
}
