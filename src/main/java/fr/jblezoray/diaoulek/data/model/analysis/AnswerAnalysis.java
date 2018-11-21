package fr.jblezoray.diaoulek.data.model.analysis;

import java.util.List;

public class AnswerAnalysis {

    private String expectedResponse;
    private double answerAccuracy;
    private List<String> inputWords;
    private List<String> expectedResponseTokenized;
    private List<String> inputWordsTokenized;

    public String getExpectedResponse() {
        return expectedResponse;
    }

    public void setExpectedResponse(String expectedResponse) {
        this.expectedResponse = expectedResponse;
    }

    public double getAnswerAccuracy() {
        return answerAccuracy;
    }

    public void setAnswerAccuracy(double answerAccuracy) {
        this.answerAccuracy = answerAccuracy;
    }

    public List<String> getInputWords() {
        return inputWords;
    }

    public void setInputWords(List<String> inputWords) {
        this.inputWords = inputWords;
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
}
