package fr.jblezoray.diaoulek.core.levenshtein;

public interface LengthOf<A> {

    /**
     * A method to compute the length of an element of type A.
     *
     * @param element
     * @return
     */
    int length(A element);

}
