package fr.jblezoray.diaoulek.data.parser;

import fr.jblezoray.diaoulek.data.model.Dictionary;
import fr.jblezoray.diaoulek.data.model.FileIndexEntry;

public class DicoParser implements IParser<Dictionary> {

    @Override
    public boolean seemsParseable(FileIndexEntry fileIndexEntry) {
        String name = fileIndexEntry.getFilename();
        return name.endsWith("dico") || name.endsWith("dicqr");
    }

    @Override
    public Dictionary parse(byte[] fileContent, FileIndexEntry fileIndexEntry)
            throws DataException {
        return null;
    }
}
