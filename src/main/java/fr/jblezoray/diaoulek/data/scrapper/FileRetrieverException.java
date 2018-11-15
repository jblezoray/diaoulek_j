package fr.jblezoray.diaoulek.data.scrapper;

public class FileRetrieverException extends Exception {
    public FileRetrieverException(String error, Exception exception) {
        super(error, exception);
    }
    public FileRetrieverException(String error) {
        super(error);
    }
    public FileRetrieverException(Exception exception) {
        super(exception);
    }
}
