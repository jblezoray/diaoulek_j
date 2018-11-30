package fr.jblezoray.diaoulek.data.model.analysis;

import fr.jblezoray.diaoulek.data.model.Part;

import java.util.List;

public class AnswerAnalysis {

    private Part expectedResponse;
    private double answerAccuracy;
    private List<String> expectedResponseTokenized;
    private List<String> inputWordsTokenized;
    private List<Float> inputWordsAccuracy;
    private List<EditOperation<String>> phraseEditPath;
    private List<List<EditOperation<Character>>> inputWordsEditPath;

    public Part getExpectedResponse() {
        return expectedResponse;
    }

    public void setExpectedResponse(Part expectedResponse) {
        this.expectedResponse = expectedResponse;
    }

    public double getAnswerAccuracy() {
        return answerAccuracy;
    }

    public void setAnswerAccuracy(double answerAccuracy) {
        this.answerAccuracy = answerAccuracy;
    }

    public void setExpectedResponseTokenized(List<String> expectedResponseTokenized) {
        this.expectedResponseTokenized = expectedResponseTokenized;
    }

    public List<String> getExpectedResponseTokenized() {
        return expectedResponseTokenized;
    }

    public void setInputWordsTokenized(List<String> inputWordsTokenized) {
        this.inputWordsTokenized = inputWordsTokenized;
    }

    public List<String> getInputWordsTokenized() {
        return inputWordsTokenized;
    }

    public List<Float> getInputWordsAccuracy() {
        return inputWordsAccuracy;
    }

    public void setInputWordsAccuracy(List<Float> inputWordsAccuracy) {
        this.inputWordsAccuracy = inputWordsAccuracy;
    }

    public List<EditOperation<String>> getPhraseEditPath() {
        return phraseEditPath;
    }

    public void setPhraseEditPath(List<EditOperation<String>> phraseEditPath) {
        this.phraseEditPath = phraseEditPath;
    }

    public List<List<EditOperation<Character>>> getInputWordsEditPath() {
        return inputWordsEditPath;
    }

    public void setInputWordsEditPath(List<List<EditOperation<Character>>> inputWordsEditPath) {
        this.inputWordsEditPath = inputWordsEditPath;
    }
}
