package fr.jblezoray.diaoulek.data.model.analysis;

import fr.jblezoray.diaoulek.data.model.Part;

import java.util.List;

public class AnswerAnalysis {

    private Part expectedResponse;
    private double answerAccuracy;
    private List<String> expectedResponseTokenized;
    private List<String> inputWordsTokenized;
    private List<Float> inputWordsAccuracy;
    private EditPath<String> phraseEditPath;
    private List<EditPath<Character>> inputWordsEditPath;

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

    public EditPath<String> getPhraseEditPath() {
        return phraseEditPath;
    }

    public void setPhraseEditPath(EditPath<String> phraseEditPath) {
        this.phraseEditPath = phraseEditPath;
    }

    public List<EditPath<Character>> getInputWordsEditPath() {
        return inputWordsEditPath;
    }

    public void setInputWordsEditPath(List<EditPath<Character>> inputWordsEditPath) {
        this.inputWordsEditPath = inputWordsEditPath;
    }
}
