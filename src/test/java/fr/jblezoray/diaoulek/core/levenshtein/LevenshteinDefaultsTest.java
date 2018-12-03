package fr.jblezoray.diaoulek.core.levenshtein;

import fr.jblezoray.diaoulek.data.model.analysis.EditOperation;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static fr.jblezoray.diaoulek.core.levenshtein.LevenshteinDefaults.*;

public class LevenshteinDefaultsTest {

    @Test
    public void withPhrase() {
        // having
        //             S  E     I    E   E      D  D
        String from = "J  aime       les frites au ketchup";
        String to =   "Tu aimes bien les  pates";

        // when
        int editDistance = PHRASE_LEVENSHTEIN.compute(from, to);

        // then
        Assertions.assertEquals(4, editDistance);
    }

    @Test
    public void withPhraseEditPath() {
        // having
        String from = " J aime       les frites au ketchup";
        String to =   "Tu aimes bien les  pates";

        // when
        List<EditOperation<String>> editList = PHRASE_LEVENSHTEIN.computePath(from, to);

        // then
        Assertions.assertEquals(7, editList.size());
        Assertions.assertEquals(4, PHRASE_LEVENSHTEIN.getScore(editList));
        Assertions.assertEquals(new EditOperation.Delete<>(5, "ketchup", null), editList.get(0));
        Assertions.assertEquals(new EditOperation.Delete<>(4, "au", null), editList.get(1));
        Assertions.assertEquals(new EditOperation.Equality<>(3, "frites", "pates"), editList.get(2));
        Assertions.assertEquals(new EditOperation.Equality<>(2, "les", "les"), editList.get(3));
        Assertions.assertEquals(new EditOperation.Insert<>(2, null, "bien"), editList.get(4));
        Assertions.assertEquals(new EditOperation.Equality<>(1, "aime", "aimes"), editList.get(5));
        Assertions.assertEquals(new EditOperation.Replace<>(0, "j", "tu"), editList.get(6));
    }


}
