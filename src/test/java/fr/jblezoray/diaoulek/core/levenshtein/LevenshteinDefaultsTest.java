package fr.jblezoray.diaoulek.core.levenshtein;

import fr.jblezoray.diaoulek.data.model.analysis.EditOperation;
import fr.jblezoray.diaoulek.data.model.analysis.EditPath;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

import static fr.jblezoray.diaoulek.core.levenshtein.LevenshteinDefaults.PHRASE_LEVENSHTEIN;

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
        EditPath<String> editList = PHRASE_LEVENSHTEIN.computePath(from, to);

        // then
        Assertions.assertEquals(7, editList.getPath().size());
        Assertions.assertEquals(4, editList.getLevenshteinScore());
        List<EditOperation<String>> path = editList.getPath();
        Assertions.assertEquals(new EditOperation.Delete<>(5, "ketchup", null), path.get(0));
        Assertions.assertEquals(new EditOperation.Delete<>(4, "au", null), path.get(1));
        Assertions.assertEquals(new EditOperation.Equality<>(3, "frites", "pates"), path.get(2));
        Assertions.assertEquals(new EditOperation.Equality<>(2, "les", "les"), path.get(3));
        Assertions.assertEquals(new EditOperation.Insert<>(2, null, "bien"), path.get(4));
        Assertions.assertEquals(new EditOperation.Equality<>(1, "aime", "aimes"), path.get(5));
        Assertions.assertEquals(new EditOperation.Replace<>(0, "j", "tu"), path.get(6));
    }


}
