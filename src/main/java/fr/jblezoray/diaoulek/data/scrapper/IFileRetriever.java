package fr.jblezoray.diaoulek.data.scrapper;

import fr.jblezoray.diaoulek.data.model.FileIndexEntry;

import java.util.Optional;

public interface IFileRetriever {

    byte[] getFileContent(FileIndexEntry uri, Optional<String> dir) throws FileRetrieverException;
    byte[] getFileContent(String filename, Optional<String> dir) throws FileRetrieverException;

}
