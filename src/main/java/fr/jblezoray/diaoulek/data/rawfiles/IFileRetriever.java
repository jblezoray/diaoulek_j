package fr.jblezoray.diaoulek.data.rawfiles;

import fr.jblezoray.diaoulek.data.model.FileIndexEntry;

public interface IFileRetriever {

    String getFileContent(FileIndexEntry uri) throws FileRetrieverException;
    String getFileContent(String filename) throws FileRetrieverException;

}
