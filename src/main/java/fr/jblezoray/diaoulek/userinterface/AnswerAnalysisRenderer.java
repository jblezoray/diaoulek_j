package fr.jblezoray.diaoulek.userinterface;

import fr.jblezoray.diaoulek.core.Utils;
import fr.jblezoray.diaoulek.core.levenshtein.EditPathResolver;
import fr.jblezoray.diaoulek.data.model.analysis.AnswerAnalysis;
import fr.jblezoray.diaoulek.data.model.analysis.EditPath;

import java.util.List;

public class AnswerAnalysisRenderer {

    private final AnswerAnalysis aa;

    public AnswerAnalysisRenderer(AnswerAnalysis aa) {
        this.aa = aa;
    }

    public String render() {

        EditPath<String> phraseEditPath = aa.getPhraseEditPath();
        List<String> inputwordsTokenized = aa.getInputWordsTokenized();
        List<String> inputWordsUnresolved = EditPathResolver.unresolve(inputwordsTokenized, phraseEditPath);

        StringBuilder sbInput = new StringBuilder();
        StringBuilder sbExpected = new StringBuilder();

        for (int i = 0; i<Utils.maxSize(inputwordsTokenized, inputWordsUnresolved); i++) {
            String inputWord = Utils.getOrNull(inputwordsTokenized, i);
            String resolvedExpectedWord = Utils.getOrNull(inputWordsUnresolved, i);

            int pad = Utils.maxLength(inputWord, resolvedExpectedWord) +1;
            writePad(sbInput, inputWord, pad);
            writePad(sbExpected, resolvedExpectedWord, pad);
        }

        return "   " + sbExpected.toString() + "\n   " + sbInput.toString();
    }

    private static void writePad(StringBuilder sb, String s, int padToLength) {
        int sLen;
        if (s!=null) {
            sb.append(s);
            sLen = s.length();
        } else {
            sLen = 0;
        }
        for(int i=sLen; i<padToLength; i++)
            sb.append(' ');
    }

}
