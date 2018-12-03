package fr.jblezoray.diaoulek.core.levenshtein;

import fr.jblezoray.diaoulek.data.model.analysis.EditOperation;
import fr.jblezoray.diaoulek.data.model.analysis.EditOperation.Delete;
import fr.jblezoray.diaoulek.data.model.analysis.EditOperation.Insert;
import fr.jblezoray.diaoulek.data.model.analysis.EditOperation.Replace;
import fr.jblezoray.diaoulek.data.model.analysis.EditOperation.Equality;
import fr.jblezoray.diaoulek.data.model.analysis.EditPath;

import java.util.ArrayList;
import java.util.List;

public class EditPathResolver {

    public static <PART> List<PART> resolve(List<PART> input, EditPath<PART> editPath) {

        List<PART> output = new ArrayList<>(input.size());
        output.addAll(input);

        for (EditOperation<PART> editOpetation : editPath.getPath()) {
            if (editOpetation instanceof Delete) {
                Delete d = (Delete)editOpetation;
                output.remove(d.getIndex());

            } else if (editOpetation instanceof EditOperation.Insert) {
                Insert i = (Insert)editOpetation;
                output.add(i.getIndex(), (PART)i.getFromValue());

            } else if (editOpetation instanceof EditOperation.Replace) {
                Replace r = (Replace)editOpetation;
                output.set(r.getIndex(), (PART)r.getFromValue());

            } else if (editOpetation instanceof EditOperation.Equality) {
                Equality e = (Equality)editOpetation;
                output.set(e.getIndex(), (PART)e.getFromValue());

            } else {
                throw new RuntimeException("unknown EditEperation");
            }
        }
        return output;
    }
}
