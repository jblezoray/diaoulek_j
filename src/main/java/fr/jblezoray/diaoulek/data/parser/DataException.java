package fr.jblezoray.diaoulek.data.parser;

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
