package fr.jblezoray.diaoulek.core.levenshtein;

import java.util.ArrayList;
import java.util.List;

public class LevenshteinFactory {

    private LevenshteinFactory() {}

    /**
     * Computes a levenshtein edit score, by considering how many letter
     * deletion, insertion, and substitution should be made to go from word
     * 's' to word 't'.
     *
     * This is therefore a classic string levenshtein computation.
     *
     * @return a Levenshtein object.
     */
    public static Levenshtein<String, Character> buildWordLevensthein() {
        return new Levenshtein<>(
                (str) -> {
                    char[] chars = str.toLowerCase().toCharArray();
                    List<Character> l = new ArrayList<>(chars.length);
                    for(int i=0; i<chars.length; i++)
                        l.add(new Character(chars[i]));
                    return l;
                },
                (left, right) -> left.compareTo(right)==0);
    }


    /**
     * Computes a levenshtein edit score, by considering how many letter
     * deletion, insertion, and substitution should be made to go from word
     * 's' to word 't'.
     *
     * This is therefore a classic string levenshtein computation.
     *
     * @return a Levenshtein object.
     */
    public static Levenshtein<List<String>, String> buildPhraseLevensthein() {
        return new Levenshtein<>(
                (list) -> list,
                // TODO a better comparison than equality.
                (left, right) -> left.equalsIgnoreCase(right));
    }

}
