package fr.jblezoray.diaoulek.core.levenshtein;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class LevenshteinDefaults {

    private LevenshteinDefaults() {}

    private final static double WORD_COMPARATOR_THRESHOLD = 0.5;

    public final static Levenshtein<String, Character> WORD_LEVENSHTEIN = new Levenshtein<>(
            (str) -> {
                char[] chars = str.toLowerCase().toCharArray();
                List<Character> l = new ArrayList<>(chars.length);
                for (int i = 0; i < chars.length; i++)
                    l.add(new Character(chars[i]));
                return l;
            },
            (char1, char2) -> char1.compareTo(char2)==0
    );

    public final static Levenshtein<String, String> PHRASE_LEVENSHTEIN = new Levenshtein<>(
            phrase -> Arrays.stream(phrase.split("\\s+"))
                    .map(String::toLowerCase)
                    .map(str -> str.replaceAll("\\P{Alnum}", ""))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.toList()),
            (word1, word2) -> {
                double levenshteinScore = WORD_LEVENSHTEIN.compute(word1, word2);
                double score = 1 - (levenshteinScore / word1.length());
                return score >= WORD_COMPARATOR_THRESHOLD;
            }
    );
}
