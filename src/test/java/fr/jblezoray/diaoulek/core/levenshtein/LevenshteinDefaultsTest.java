package fr.jblezoray.diaoulek.core.levenshtein;

import fr.jblezoray.diaoulek.data.model.analysis.EditOperation;
import fr.jblezoray.diaoulek.data.model.analysis.EditPath;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

import static fr.jblezoray.diaoulek.core.levenshtein.LevenshteinDefaults.PHRASE_LEVENSHTEIN;
import static fr.jblezoray.diaoulek.core.levenshtein.LevenshteinDefaults.WORD_LEVENSHTEIN;

public class LevenshteinDefaultsTest {

    @Test
    public void word() {
        // having
        String from = "waches";
        String to =   "vache";

        // when
        int editDistance = WORD_LEVENSHTEIN.compute(from, to);
        EditPath<Character> editPath = WORD_LEVENSHTEIN.computePath(from, to);

        // then
        Assertions.assertEquals(2, editDistance);
        Assertions.assertEquals(2, editPath.getLevenshteinScore());
        List<EditOperation<Character>> path = editPath.getPath();

        Assertions.assertEquals(new EditOperation.Delete<>(5, 's', null), path.get(0));
        Assertions.assertEquals(new EditOperation.Equality<>(4, 'e', 'e'), path.get(1));
        Assertions.assertEquals(new EditOperation.Equality<>(3, 'h', 'h'), path.get(2));
        Assertions.assertEquals(new EditOperation.Equality<>(2, 'c', 'c'), path.get(3));
        Assertions.assertEquals(new EditOperation.Equality<>(1, 'a', 'a'), path.get(4));
        Assertions.assertEquals(new EditOperation.Replace<>(0, 'w', 'v'), path.get(5));
    }

    @Test
    public void withPhrase() {
        // having
        //             S  E     I    E   E      D  D
        String from = "J  aime       les frites au ketchup";
        String to =   "Tu aimes bien les fritte";

        // when
        int editDistance = PHRASE_LEVENSHTEIN.compute(from, to);

        // then
        Assertions.assertEquals(4, editDistance);
    }

    @Test
    public void withPhraseEditPath() {
        // having
        String from = " J aime       les frites  au ketchup";
        String to =   "Tu aimes bien les fritte";

        // when
        EditPath<String> editList = PHRASE_LEVENSHTEIN.computePath(from, to);

        // then
        Assertions.assertEquals(7, editList.getPath().size());
        Assertions.assertEquals(4, editList.getLevenshteinScore());
        List<EditOperation<String>> path = editList.getPath();
        Assertions.assertEquals(new EditOperation.Delete<>(5, "ketchup", null), path.get(0));
        Assertions.assertEquals(new EditOperation.Delete<>(4, "au", null), path.get(1));
        Assertions.assertEquals(new EditOperation.Equality<>(3, "frites", "fritte"), path.get(2));
        Assertions.assertEquals(new EditOperation.Equality<>(2, "les", "les"), path.get(3));
        Assertions.assertEquals(new EditOperation.Insert<>(2, null, "bien"), path.get(4));
        Assertions.assertEquals(new EditOperation.Equality<>(1, "aime", "aimes"), path.get(5));
        Assertions.assertEquals(new EditOperation.Replace<>(0, "j", "tu"), path.get(6));
    }


}
