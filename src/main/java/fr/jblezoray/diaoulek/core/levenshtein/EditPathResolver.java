package fr.jblezoray.diaoulek.core.levenshtein;

import fr.jblezoray.diaoulek.data.model.analysis.EditOperation;
import fr.jblezoray.diaoulek.data.model.analysis.EditOperation.Delete;
import fr.jblezoray.diaoulek.data.model.analysis.EditOperation.Insert;
import fr.jblezoray.diaoulek.data.model.analysis.EditOperation.Replace;

import java.util.ArrayList;
import java.util.List;

public class EditPathResolver {

    public static <PART> List<PART> resolve(List<PART> input, List<EditOperation<PART>> editPath) {

        List<PART> output = new ArrayList<>(input.size());
        output.addAll(input);

        for (EditOperation<PART> editOpetation : editPath) {
            if (editOpetation instanceof Delete) {
                Delete d = (Delete)editOpetation;
                output.remove(d.getIndex());

            } else if (editOpetation instanceof EditOperation.Insert) {
                Insert i = (Insert)editOpetation;
                output.add(i.getIndex(), null);

            } else if (editOpetation instanceof EditOperation.Replace) {
                Replace r = (Replace)editOpetation;
                output.set(r.getIndex(), null);

            } else {
                throw new RuntimeException("unknown EditEperation");
            }
        }
        return output;
    }
}
