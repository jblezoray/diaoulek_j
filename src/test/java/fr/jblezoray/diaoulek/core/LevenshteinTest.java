package fr.jblezoray.diaoulek.core;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class LevenshteinTest {

    @Test
    public void nominalCase() {
        int editDistance = Levenshtein.compute("kitten", "sitting");
        Assertions.assertEquals(3, editDistance);
    }

    @Test
    public void withoutCaseSensitivity() {
        int editDistance = Levenshtein.compute("sAtUrDaY", "SuNdAy");
        Assertions.assertEquals(3, editDistance);
    }

    @Test
    public void withDiacritics() {
        int editDistance = Levenshtein.compute("sàtürdaÿ", "sûnday");
        Assertions.assertEquals(5, editDistance);
    }

}
