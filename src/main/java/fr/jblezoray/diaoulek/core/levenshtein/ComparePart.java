package fr.jblezoray.diaoulek.core.levenshtein;

public interface ComparePart<PART> {

    /**
     * A method to compare two entities, witha levenshtein logic.
     *
     * @param left
     * @param right
     * @return
     */
    boolean isEqualTo(PART left, PART right);

}
