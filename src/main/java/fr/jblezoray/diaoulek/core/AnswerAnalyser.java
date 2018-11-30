package fr.jblezoray.diaoulek.core;

import fr.jblezoray.diaoulek.core.levenshtein.EditPathResolver;
import fr.jblezoray.diaoulek.core.levenshtein.Levenshtein;
import fr.jblezoray.diaoulek.data.model.Part;
import fr.jblezoray.diaoulek.data.model.analysis.AnswerAnalysis;
import fr.jblezoray.diaoulek.data.model.analysis.EditOperation;
import fr.jblezoray.diaoulek.data.model.lessonelement.QRCouple;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class AnswerAnalyser {

    private final QRCouple qr;

    private final static double WORD_COMPARATOR_THRESHOLD = 0.5;

    private final static Levenshtein<String, Character> WORD_LEVENSHTEIN = new Levenshtein<String, Character>(
            (str) -> {
                char[] chars = str.toLowerCase().toCharArray();
                List<Character> l = new ArrayList<>(chars.length);
                for (int i = 0; i < chars.length; i++)
                    l.add(new Character(chars[i]));
                return l;
            },
            (char1, char2) -> char1.compareTo(char2)==0
    );


    private final static Levenshtein<String, String> PHRASE_LEVENSHTEIN = new Levenshtein<>(
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


    public AnswerAnalyser(QRCouple qr) {
        this.qr = qr;
    }


    public AnswerAnalysis analyze(String inputPhrase) {
        List<String> inputWords = PHRASE_LEVENSHTEIN.getTokenizer().tokenize(inputPhrase);

        // find the best matching phrase within all possibles matching phrases.
        Part bestPhrase = null;
        Integer bestPhraseScore = null; // the lower the best.
        for (Part phrase : this.qr.getResponse().getParts()) {
            int partScore = PHRASE_LEVENSHTEIN.compute(phrase.getRawString(), inputPhrase);
            if (bestPhraseScore==null || partScore>bestPhraseScore) {
                bestPhrase = phrase;
                bestPhraseScore = partScore;
            }
        }
        if (bestPhrase==null) throw new RuntimeException("No best part found !?");

        // compute the edit path for the best match.
        List<EditOperation<String>> editPath =
                PHRASE_LEVENSHTEIN.computePath(bestPhrase.getRawString(), inputPhrase);

        // compute a score per word.
        List<String> bestPhraseTokenized =
                PHRASE_LEVENSHTEIN.getTokenizer().tokenize(bestPhrase.getRawString());
        List<String> bestPhraseResolved =
                EditPathResolver.resolve(bestPhraseTokenized, editPath);
        List<Float> scoresPerWord = new ArrayList<>();
        List<List<EditOperation<Character>>> editPathsPerWord = new ArrayList<>();
        for (int i=0; i<bestPhraseResolved.size(); i++) {
            String expectedWord = bestPhraseResolved.get(i);
            String inputWord = inputWords.get(i);
            if (expectedWord != null) {
                List<EditOperation<Character>> editPathWord =
                        WORD_LEVENSHTEIN.computePath(expectedWord, inputWord);
                editPathsPerWord.add(i, editPathWord);

                int levenshteinDistance = editPathWord.size();
                float score = 1 - (float)levenshteinDistance / (float)expectedWord.length();
                scoresPerWord.add(i, score);
            } else {
                editPathsPerWord.add(i, null);
                scoresPerWord.add(i, null);
            }

        }

        // prepare result.
        AnswerAnalysis aa = new AnswerAnalysis();
        aa.setExpectedResponseTokenized(bestPhraseTokenized);
        aa.setAnswerAccuracy(1 - (float)editPath.size() / (float)bestPhraseTokenized.size());
        aa.setPhraseEditPath(editPath);
        aa.setInputWordsTokenized(inputWords);
        aa.setInputWordsAccuracy(scoresPerWord);
        aa.setInputWordsEditPath(editPathsPerWord);
        return aa;
    }

}
