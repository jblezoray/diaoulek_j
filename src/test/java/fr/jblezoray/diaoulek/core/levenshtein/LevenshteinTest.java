package fr.jblezoray.diaoulek.core.levenshtein;

import fr.jblezoray.diaoulek.data.model.analysis.EditOperation;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class LevenshteinTest {

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
                    l.add(chars[i]);
                return l;
            },
            (left, right) -> left.compareTo(right) == 0);


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
    public void withEditPath() {
        // having
        String from = "kitten";
        String to   = "sitting";

        // when
        List<EditOperation<Character>> editList = levenshteinWord.computePath(from, to);

        // then
        Assertions.assertEquals(3, levenshteinWord.getScore(editList));
        Assertions.assertEquals(7, editList.size());
        Assertions.assertEquals(new EditOperation.Insert<>(6, null, 'g'), editList.get(0));
        Assertions.assertEquals(new EditOperation.Equality<>(5, 'n', 'n'), editList.get(1));
        Assertions.assertEquals(new EditOperation.Replace<>(4, 'e', 'i'), editList.get(2));
        Assertions.assertEquals(new EditOperation.Equality<>(3, 't', 't'), editList.get(3));
        Assertions.assertEquals(new EditOperation.Equality<>(2, 't', 't'), editList.get(4));
        Assertions.assertEquals(new EditOperation.Equality<>(1, 'i', 'i'), editList.get(5));
        Assertions.assertEquals(new EditOperation.Replace<>(0, 'k', 's'), editList.get(6));
    }


}
