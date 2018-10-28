package fr.jblezoray.diaoulek.data.parser;


import fr.jblezoray.diaoulek.data.model.FileIndexEntry;

public interface IParser<B> {

    boolean seemsParseable(FileIndexEntry fileIndexEntry);

    B parse(byte[] fileContent, FileIndexEntry fileIndexEntry) throws DataException;

}
