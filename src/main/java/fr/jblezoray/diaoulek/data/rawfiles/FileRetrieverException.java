package fr.jblezoray.diaoulek.data.rawfiles;

import java.io.IOException;

public class FileRetrieverException extends Exception {
    public FileRetrieverException(String error) {
        super(error);
    }
    public FileRetrieverException(IOException exception) {
        super(exception);
    }
}
