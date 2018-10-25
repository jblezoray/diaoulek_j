package fr.jblezoray.diaoulek.data;

import java.io.IOException;

public class DataException extends Exception {

    public DataException(String data) {
        super("Error on data: '"+data+"'");
    }

    public DataException(String s, Exception e) {
        super(s,e);
    }

    public DataException() {
        super();
    }

}
