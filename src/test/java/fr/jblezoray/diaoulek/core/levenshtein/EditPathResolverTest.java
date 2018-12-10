package fr.jblezoray.diaoulek.core.levenshtein;

import fr.jblezoray.diaoulek.data.model.analysis.EditOperation;
import fr.jblezoray.diaoulek.data.model.analysis.EditOperation.Delete;
import fr.jblezoray.diaoulek.data.model.analysis.EditOperation.Equality;
import fr.jblezoray.diaoulek.data.model.analysis.EditOperation.Insert;
import fr.jblezoray.diaoulek.data.model.analysis.EditOperation.Replace;
import fr.jblezoray.diaoulek.data.model.analysis.EditPath;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EditPathResolverTest {

    private List<Character> reference = Arrays.asList('a', 'b', 'c', 'd',      'f', 'g');

    private List<Character> input     = Arrays.asList('g', 'b',      'd', 'x', 'f', 'g');

    private EditPath<Character> editPath = new EditPath<>(
            Arrays.asList(
                    new EditOperation.Equality(5, 'g', 'g'),
                    new EditOperation.Equality(4, 'f', 'f'),
                    new EditOperation.Insert(4, null, 'x'),
                    new EditOperation.Equality(3, 'd', 'd'),
                    new EditOperation.Delete(2, 'c', null),
                    new EditOperation.Equality(1, 'b', 'b'),
                    new EditOperation.Replace(0, 'a', 'g')
            ),
            0);


    @Test
    public void unresolve() {
        // having
        List<Character> referenceWithNull = Arrays.asList('a', 'b', 'c', 'd', null, 'f', 'g');

        // when
        List<Character> unresolved = EditPathResolver.unresolve(input, editPath);

        // then
        Assertions.assertEquals(referenceWithNull, unresolved);
    }

    @Test
    public void resolve() {
        // having

        // when
        List<Character> resolved = EditPathResolver.resolve(reference, editPath);

        // then
        Assertions.assertEquals(input, resolved);
    }

}
