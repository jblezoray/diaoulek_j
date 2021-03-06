package fr.jblezoray.diaoulek.core;

import fr.jblezoray.diaoulek.core.levenshtein.EditPathResolver;
import fr.jblezoray.diaoulek.data.model.Part;
import fr.jblezoray.diaoulek.data.model.analysis.AnswerAnalysis;
import fr.jblezoray.diaoulek.data.model.analysis.EditPath;
import fr.jblezoray.diaoulek.data.model.lessonelement.QRCouple;

import java.util.ArrayList;
import java.util.List;

import static fr.jblezoray.diaoulek.core.levenshtein.LevenshteinDefaults.PHRASE_LEVENSHTEIN;
import static fr.jblezoray.diaoulek.core.levenshtein.LevenshteinDefaults.WORD_LEVENSHTEIN;

public class AnswerAnalyser {

    private final QRCouple qr;

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
        EditPath<String> editPath =
                PHRASE_LEVENSHTEIN.computePath(bestPhrase.getRawString(), inputPhrase);

        // compute a score per word.
        List<String> bestPhraseTokenized =
                PHRASE_LEVENSHTEIN.getTokenizer().tokenize(bestPhrase.getRawString());
        List<String> inputUnresolved =
                EditPathResolver.unresolve(inputWords, editPath);
        List<Float> scoresPerWord = new ArrayList<>();
        List<EditPath<Character>> editPathsPerWord = new ArrayList<>();
        for (int i=0; i<Utils.maxSize(inputUnresolved, inputWords); i++) {
            String expectedWord = Utils.getOrNull(inputUnresolved, i);
            String inputWord = Utils.getOrNull(inputWords, i);
            if (expectedWord != null && inputWord != null) {
                EditPath<Character> editPathWord =
                        WORD_LEVENSHTEIN.computePath(expectedWord, inputWord);
                editPathsPerWord.add(i, editPathWord);

                float score = 1 - (float)editPathWord.getLevenshteinScore()
                        / (float)expectedWord.length();
                scoresPerWord.add(i, score);
            } else {
                editPathsPerWord.add(i, null);
                scoresPerWord.add(i, null);
            }

        }

        // prepare result.
        AnswerAnalysis aa = new AnswerAnalysis();
        aa.setExpectedResponseTokenized(bestPhraseTokenized);
        aa.setAnswerAccuracy(1 - (float) editPath.getLevenshteinScore()
                / (float) bestPhraseTokenized.size());
        aa.setPhraseEditPath(editPath);
        aa.setInputWordsTokenized(inputWords);
        aa.setInputWordsAccuracy(scoresPerWord);
        aa.setInputWordsEditPath(editPathsPerWord);
        return aa;
    }

}
