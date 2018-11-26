package fr.jblezoray.diaoulek.data.model.analysis;

import java.util.List;

public class AnswerAnalysis {

    private String expectedResponse;
    private double answerAccuracy;
    private List<String> expectedResponseTokenized;
    private List<String> inputWordsTokenized;
    private List<Double> inputWordsAccuracy;

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

    public List<Double> getInputWordsAccuracy() {
        return inputWordsAccuracy;
    }

    public void setInputWordsAccuracy(List<Double> inputWordsAccuracy) {
        this.inputWordsAccuracy = inputWordsAccuracy;
    }
}
