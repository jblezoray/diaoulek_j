package fr.jblezoray.diaoulek.core.levenshtein;

import java.util.List;

public interface Tokenizer<WHOLE, PART> {

    List<PART> tokenize(WHOLE whole);

}
