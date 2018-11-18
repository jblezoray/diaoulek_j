package fr.jblezoray.diaoulek.core;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * An implementation of the Levenshtein edit distance.
 *
 * See :
 * <ul>
 *     <li>https://en.wikipedia.org/wiki/Levenshtein_distance</li>
 * </ul>
 */
public class Levenshtein {

    public static class WordMatch {
        /** the expected word */
        public String expected;
        /** the answer word */
        public String answer;
        /** the raw levenshtein score */
        public int levenshtein;
        /** levenshtein score ponderated by string length, value within
         * [0.0,1.0] */
        public float ponderatedLevenshtein;
    }

    public static List<WordMatch> compute(String[] expectedWords, String[] answerWords) {
        // initial list.
        LinkedList res = new LinkedList();
        for (String expectedWord : expectedWords) {
            WordMatch wordMatch = new WordMatch();
            wordMatch.expected = expectedWord;
            res.add(wordMatch);
        }

        // TODO



        return res;
    }

    /**
     * Computes a levenshtein score for the edit distance bewteen two words.
     *
     * @param s word
     * @param t word
     * @return levenshtein score.
     */
    public static int compute(String s, String t) {
        s = pretreat(s);
        t = pretreat(t);
        return compute(s, s.length(), t, t.length());
    }

    private static String pretreat(String s) {
        return s.toLowerCase();
    }

    private static int compute(String s, int sCursor, String t, int tCursor) {
        // base case: empty strings
        if (sCursor == 0) return tCursor;
        if (tCursor == 0) return sCursor;

        // test if last characters of the strings match
        int cost = s.charAt(sCursor-1) == t.charAt(tCursor-1) ? 0 : 1;

        // return minimum of delete char from s, delete char from t, and delete
        // char from both
        return minOf(
                compute(s, sCursor - 1, t, tCursor    ) + 1,
                compute(s, sCursor    , t, tCursor - 1) + 1,
                compute(s, sCursor - 1, t, tCursor - 1) + cost);

    }

    private static int minOf(int... a) {
        return Arrays.stream(a)
                .min()
                .orElseThrow(() -> new RuntimeException("Unexpected array length"));
    }
}
