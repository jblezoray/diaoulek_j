package fr.jblezoray.diaoulek.core;

import fr.jblezoray.diaoulek.core.levenshtein.EditPathResolver;
import fr.jblezoray.diaoulek.core.levenshtein.Levenshtein;
import fr.jblezoray.diaoulek.data.model.Part;
import fr.jblezoray.diaoulek.data.model.analysis.AnswerAnalysis;
import fr.jblezoray.diaoulek.data.model.analysis.EditOperation;
import fr.jblezoray.diaoulek.data.model.lessonelement.QRCouple;

import java.util.ArrayList;
import java.util.List;

public class AnswerAnalyser {

    private final QRCouple qr;

    private final static double WORD_COMPARATOR_THRESHOLD = 0.8;

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
            phrase -> {
                ArrayList<String> tokens = new ArrayList<>();
                for (String word : phrase.split("\\s+")) {
                    word = word.toLowerCase();
                    tokens.add(word);
                }
                return tokens;
            },
            (word1, word2) -> {
                double medianLength = (word1.length() + word2.length()) / 2;
                double levenshteinScore = WORD_LEVENSHTEIN.compute(word1, word2);
                double score = 1 - (levenshteinScore / medianLength);
                return score > WORD_COMPARATOR_THRESHOLD;
            }
    );


    public AnswerAnalyser(QRCouple qr) {
        this.qr = qr;
    }

    public AnswerAnalysis analyze(String answer) {

        List<EditOperation<String>> bestEditPath = null;
        String bestPhrase = null;
        for (Part part : this.qr.getResponse().getParts()) {
            for (String phrase : part.getPhrases()) {
                List<EditOperation<String>> editPath = PHRASE_LEVENSHTEIN.computePath(answer, phrase);
                if (bestEditPath==null || bestEditPath.size()>editPath.size()) {
                    bestEditPath = editPath;
                    bestPhrase = phrase;
                }
            }
        }

        AnswerAnalysis aa = new AnswerAnalysis();
        aa.setExpectedResponse(bestPhrase);
        List<String> bestPhraseTokenized = PHRASE_LEVENSHTEIN.getTokenizer().tokenize(bestPhrase);
        aa.setExpectedResponseTokenized(bestPhraseTokenized);

        List<String> inputWords = PHRASE_LEVENSHTEIN.getTokenizer().tokenize(answer);
        aa.setInputWordsTokenized(inputWords);

        aa.setAnswerAccuracy(1 - (bestEditPath.size() / aa.getExpectedResponseTokenized().size()));

        List<String> bestPhraseResolved = EditPathResolver.resolve(bestPhraseTokenized, bestEditPath);
        List<Double> scores = new ArrayList<>();
        for (int i=0; i<bestPhraseResolved.size(); i++) {
            String expectedWord = bestPhraseResolved.get(i);
            String inputWord = inputWords.get(i);
            double score =
                    1 - (WORD_LEVENSHTEIN.compute(expectedWord, inputWord)) / expectedWord.length();
            scores.add(i, score);
        }
        aa.setInputWordsAccuracy(scores);

        return aa;
    }

}
