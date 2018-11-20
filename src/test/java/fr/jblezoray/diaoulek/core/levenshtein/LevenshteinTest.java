package fr.jblezoray.diaoulek.core.levenshtein;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

public class LevenshteinTest {

    private Levenshtein levenshteinWord = LevenshteinFactory.buildWordLevensthein();
    private Levenshtein levenshteinPhrase = LevenshteinFactory.buildPhraseLevensthein();

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
        List<EditOperation> editList = levenshteinPhrase.computePath(from, to);

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
        List<EditOperation> editList = levenshteinWord.computePath(from, to);

        // then
        Assertions.assertEquals(3, editList.size());
        Assertions.assertEquals(new EditOperation.Insert(6, 'g'), editList.get(0));
        Assertions.assertEquals(new EditOperation.Replace(4, 'i'), editList.get(1));
        Assertions.assertEquals(new EditOperation.Replace(0, 's'), editList.get(2));
    }


}
