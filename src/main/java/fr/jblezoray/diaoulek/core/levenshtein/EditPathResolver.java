package fr.jblezoray.diaoulek.core.levenshtein;

import fr.jblezoray.diaoulek.data.model.analysis.EditOperation;
import fr.jblezoray.diaoulek.data.model.analysis.EditOperation.Delete;
import fr.jblezoray.diaoulek.data.model.analysis.EditOperation.Equality;
import fr.jblezoray.diaoulek.data.model.analysis.EditOperation.Insert;
import fr.jblezoray.diaoulek.data.model.analysis.EditOperation.Replace;
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
                output.add(i.getIndex(), (PART)i.getToValue());

            } else if (editOpetation instanceof EditOperation.Replace) {
                Replace r = (Replace)editOpetation;
                output.set(r.getIndex(), (PART)r.getToValue());

            } else if (editOpetation instanceof EditOperation.Equality) {
                Equality e = (Equality)editOpetation;
                output.set(e.getIndex(), (PART)e.getToValue());

            } else {
                throw new RuntimeException("unknown EditEperation");
            }
        }
        return output;
    }


    public static <PART> List<PART> unresolve(List<PART> input, EditPath<PART> editPath) {

        List<PART> output = new ArrayList<>(input.size());
        output.addAll(input);

        int shift = 0;
        for (int i=editPath.getPath().size()-1; i>=0; i--) {
            EditOperation<PART> editOperation = editPath.getPath().get(i);

            if (editOperation instanceof Delete) {
                Delete de = (Delete)editOperation;
                output.add(de.getIndex()+shift, (PART) de.getFromValue());

            } else if (editOperation instanceof EditOperation.Insert) {
                Insert in = (Insert)editOperation;
                output.set(in.getIndex()+shift, (PART) in.getFromValue());
                shift++;

            } else if (editOperation instanceof EditOperation.Replace) {
                Replace re = (Replace)editOperation;
                output.set(re.getIndex()+shift, (PART) re.getFromValue());

            } else if (editOperation instanceof EditOperation.Equality) {
                Equality eq = (Equality)editOperation;
                output.set(eq.getIndex()+shift, (PART) eq.getFromValue());

            } else {
                throw new RuntimeException("unknown EditEperation");
            }
        }
        return output;
    }
}
