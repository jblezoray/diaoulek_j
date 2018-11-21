package fr.jblezoray.diaoulek.core.levenshtein;

import fr.jblezoray.diaoulek.data.model.analysis.EditOperation;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LevenshteinTest {

    /**
     * Computes a levenshtein edit score, by considering how many letter
     * deletion, insertion, and substitution should be made to go from a word to
     * another.
     *
     * This is therefore a classic string levenshtein computation.
     */
    private Levenshtein<String, Character> levenshteinWord = new Levenshtein<>(
            (str) -> {
                char[] chars = str.toLowerCase().toCharArray();
                List<Character> l = new ArrayList<>(chars.length);
                for (int i = 0; i < chars.length; i++)
                    l.add(new Character(chars[i]));
                return l;
            },
            (left, right) -> left.compareTo(right) == 0);

    /**
     * Computes a levenshtein edit score, by considering how many word
     * deletion, insertion, and substitution should be made to go from a phrase
     * to another.
     */
    private Levenshtein<List<String>, String> levenshteinPhrase = new Levenshtein<>(
            (list) -> list,
            (left, right) -> left.equalsIgnoreCase(right));

    @Test
    public void nominalCase() {
        int editDistance = levenshteinWord.compute("kitten", "sitting");
        Assertions.assertEquals(3, editDistance);
    }

    @Test
    public void withoutCaseSensitivity() {
        int editDistance = levenshteinWord.compute("sAtUrDaY", "SuNdAy");
        Assertions.assertEquals(3, editDistance);
    }

    @Test
    public void withDiacritics() {
        int editDistance = levenshteinWord.compute("sàtürdaÿ", "sûnday");
        Assertions.assertEquals(5, editDistance);
    }


    @Test
    public void withPhrase() {
        int editDistance = levenshteinPhrase.compute(
                Arrays.asList("J",  "aime",          "les", "frites", "au", "ketchup"),
                Arrays.asList("Tu", "aimes", "bien", "les", "pâtes"));
        Assertions.assertEquals(6, editDistance);
    }

    @Test
    public void withPhraseEditPath() {
        // having
        List<String> from = Arrays.asList("J",  "aime",          "les", "frites", "au", "ketchup");
        List<String> to = Arrays.asList(  "Tu", "aimes", "bien", "les", "pâtes");

        // when
        List<EditOperation<String>> editList = levenshteinPhrase.computePath(from, to);

        // then
        Assertions.assertEquals(6, editList.size());
        Assertions.assertEquals(new EditOperation.Delete(5), editList.get(0));
        Assertions.assertEquals(new EditOperation.Delete(4), editList.get(1));
        Assertions.assertEquals(new EditOperation.Replace(3, "pâtes"), editList.get(2));
        Assertions.assertEquals(new EditOperation.Insert(2, "bien"), editList.get(3));
        Assertions.assertEquals(new EditOperation.Replace(1, "aimes"), editList.get(4));
        Assertions.assertEquals(new EditOperation.Replace(0, "Tu"), editList.get(5));
    }

    @Test
    public void withEditPath() {
        // having
        String from = "kitten";
        String to   = "sitting";

        // when
        List<EditOperation<Character>> editList = levenshteinWord.computePath(from, to);

        // then
        Assertions.assertEquals(3, editList.size());
        Assertions.assertEquals(new EditOperation.Insert(6, 'g'), editList.get(0));
        Assertions.assertEquals(new EditOperation.Replace(4, 'i'), editList.get(1));
        Assertions.assertEquals(new EditOperation.Replace(0, 's'), editList.get(2));
    }


}
