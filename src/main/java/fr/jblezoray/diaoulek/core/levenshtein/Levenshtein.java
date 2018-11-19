package fr.jblezoray.diaoulek.core.levenshtein;

import java.util.Arrays;
import java.util.List;

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


    public List<EditOperation<PART>> computeEditPath(WHOLE from, WHOLE to) {
        // TODO
        return null;
    }


    /**
     * Computes a levenshtein edit distance between two elements.
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
     * Wagner–Fischer algorithm
     *
     * @param s
     * @param t
     * @return
     */
    private List<EditOperation<PART>> computePath(List<PART> s, List<PART> t) {

        int m = s.size();
        int n = t.size();

        // For all i and j, d[i,j] will hold the Levenshtein distance between
        // the first i characters of s and the first j characters of t.
        // Note that d has (m+1) x (n+1) values.
        int[][] d = new int[m+1][n+1];
        // the distance of any first string to an empty second string
        for (int i=0; i<=m; i++) d[i][0] = i;
        // (transforming the string of the first i characters of s into
        // the empty string requires i deletions)
        // the distance of any second string to an empty first string
        for (int j=0; j<=n; j++) d[0][j] = j;


        for (int j=1; j<=n; j++) {
            for (int i=1; i<=m; i++) {
                if (this.comparePart.isEqualTo(s.get(i-1), t.get(j-1))) {
                    d[i][j] = d[i-1][j-1]; // no operation required
                } else {
                    d[i][j] = minOf(
                            d[i-1][j] + 1,  // a deletion
                            d[i][j-1] + 1,  // an insertion
                            d[i-1][j-1] + 1 // a substitution
                    );
                }
            }
        }

        return d[m][n]; // TODO
    }



    private static int minOf(int... a) {
        return Arrays.stream(a)
                .min()
                .orElseThrow(() -> new RuntimeException("Unexpected array length"));
    }
}
