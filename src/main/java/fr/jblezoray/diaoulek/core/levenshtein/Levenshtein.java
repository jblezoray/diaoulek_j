package fr.jblezoray.diaoulek.core.levenshtein;

import fr.jblezoray.diaoulek.data.model.analysis.EditOperation;

import java.util.*;

/**
 * An implementation of the Levenshtein edit distance.
 *
 * It works for any WHOLE composed of PARTs, e.g:
 * <ul>
 *     <li>a word (String), the base element being a letter (Character)</li>
 *     <li>a phrase (List of String), the base element being a word (String)</li>
 *     <li>...</li>
 * </ul>
 *
 * May also compute edit paths.
 *
 * See :
 * <ul>
 *     <li>https://en.wikipedia.org/wiki/Levenshtein_distance</li>
 * </ul>
 */
public class Levenshtein<WHOLE,PART> {

    private final Tokenizer<WHOLE, PART> tokenizer;
    private final ComparePart<PART> comparePart;


    /**
     * Constructor.
     *
     * @param tokenizer a method to tokanize a WHOLE as a list of PARTs.
     * @param comparePart a method to compare two PARTs.
     */
    public Levenshtein(
            Tokenizer<WHOLE, PART> tokenizer,
            ComparePart<PART> comparePart) {
        this.tokenizer = tokenizer;
        this.comparePart = comparePart;
    }

    public Tokenizer<WHOLE, PART> getTokenizer() {
        return tokenizer;
    }

    public ComparePart<PART> getComparePart() {
        return comparePart;
    }

    /**
     * Computes a levenshtein edit distance between two elements.
     *
     * @param s a first element.
     * @param t a second element
     * @return a levenshtein edit score.
     */
    public int compute(WHOLE s, WHOLE t) {
        List<PART> sTokenized = this.tokenizer.tokenize(s);
        List<PART> tTokenized = this.tokenizer.tokenize(t);
        return computeScore(sTokenized,  sTokenized.size(), tTokenized,  tTokenized.size());
    }

    private int computeScore(
            List<PART> s, int sCursor,
            List<PART> t, int tCursor) {

        // if a list is empty, the edit distance left is how many insertions
        // have to be done to reach the beginning of the element.
        if (sCursor == 0) return tCursor;
        if (tCursor == 0) return sCursor;

        // tests if the last element of each list match
        boolean eq = this.comparePart.isEqualTo(s.get(sCursor-1), t.get(tCursor-1));

        // compute three edit distances
        int deleteFromS = computeScore(s, sCursor-1, t, tCursor) + 1;
        int deleteFromT = computeScore(s, sCursor, t, tCursor-1) + 1;
        int deleteFromBoth = computeScore(s, sCursor-1, t, tCursor-1) + (eq? 0:1);

        // returns the minimum of the edit distances
        return minOf(deleteFromS, deleteFromT, deleteFromBoth);
    }


    /**
     * Computes an edit path for going from 's' to 't'.
     *
     * implements a Wagner–Fischer algorithm, modified for computing the edit
     * path instead of the edit distance.
     *
     * See:
     * <ul>
     *     <li>https://en.wikipedia.org/wiki/Wagner%E2%80%93Fischer_algorithm</li>
     * </ul>
     *
     * @param s
     * @param t
     * @return
     */
    public List<EditOperation<PART>> computePath(WHOLE s, WHOLE t) {
        List<PART> sTokenized = this.tokenizer.tokenize(s);
        List<PART> tTokenized = this.tokenizer.tokenize(t);
        return computePath(sTokenized, tTokenized);
    }


    private List<EditOperation<PART>> computePath(List<PART> s, List<PART> t) {

        int m = s.size();
        int n = t.size();

        List<EditOperation<PART>>[][] ope = new List[m+1][n+1];

        // the edit path of an empty stuff to an empty stuff is the empty path.
        ope[0][0] = Collections.EMPTY_LIST;

        // the edit path of any first stuff to an empty second stuff is a
        // succession of delete operations.
        for (int i=1; i<=m; i++)
            ope[i][0] = copyAndAppend(ope[i-1][0], new EditOperation.Delete(i-1, s.get(i-1)));

        // the edit path of the empty stuff to any stuff is a succession of
        // insert operations.
        for (int j=1; j<=n; j++)
            ope[0][j] = copyAndAppend(ope[0][j-1], new EditOperation.Insert<>(0, t.get(j-1)));

        for (int j=1; j<=n; j++) {
            for (int i=1; i<=m; i++) {
                if (this.comparePart.isEqualTo(s.get(i-1), t.get(j-1))) {
                    // no operation required.
                    ope[i][j] = ope[i-1][j-1];

                } else {

                    // compute three edit distances
                    int sizeIfDeletion = ope[i-1][j].size() + 1;
                    int sizeIfInsertion = ope[i][j-1].size() + 1;
                    int sizeIfSubstitution = ope[i-1][j-1].size() + 1;
                    int minScore = minOf(
                            sizeIfDeletion, sizeIfInsertion, sizeIfSubstitution);

                    // keep the smallest score, with a new operation :
                    if (minScore == sizeIfSubstitution) {
                        ope[i][j] = copyAndAppend(
                                ope[i-1][j-1],
                                new EditOperation.Replace<>(i-1, t.get(j-1)));

                    } else if (minScore == sizeIfDeletion) {
                        ope[i][j] = copyAndAppend(
                                ope[i-1][j],
                                new EditOperation.Delete<>(i-1, s.get(i-1)));

                    } else {
                        ope[i][j] = copyAndAppend(
                                ope[i][j-1],
                                new EditOperation.Insert<>(i, t.get(j-1)));
                    }
                }
            }
        }

        return ope[m][n];
    }


    private List<EditOperation<PART>> copyAndAppend(
            List<EditOperation<PART>> toCopy,
            EditOperation<PART> toAppend) {
        ArrayList<EditOperation<PART>> result = new ArrayList<>(toCopy.size()+1);
        Iterator<EditOperation<PART>> toCopyIterator = toCopy.iterator();
        result.add(toAppend);
        while (toCopyIterator.hasNext())
            result.add(toCopyIterator.next());
        return Collections.unmodifiableList(result);
    }


    private static int minOf(int... a) {
        return Arrays.stream(a)
                .min()
                .orElseThrow(() -> new RuntimeException("Unexpected array length"));
    }
}
