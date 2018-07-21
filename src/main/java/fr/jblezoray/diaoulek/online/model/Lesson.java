package fr.jblezoray.diaoulek.online.model;

public class Lesson extends OnlineUpdateEntry {

    private String alias;
    private Language languageForQuestions;
    private Language languageForResponses;

    public Lesson(OnlineUpdateEntry onlineUpdateEntry) {
        super(onlineUpdateEntry);
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getAlias() {
        return alias;
    }

    public void setLanguageForQuestions(Language languageForQuestions) {
        this.languageForQuestions = languageForQuestions;
    }

    public Language getLanguageForQuestions() {
        return languageForQuestions;
    }

    public void setLanguageForResponses(Language languageForResponses) {
        this.languageForResponses = languageForResponses;
    }

    public Language getLanguageForResponses() {
        return languageForResponses;
    }
}
