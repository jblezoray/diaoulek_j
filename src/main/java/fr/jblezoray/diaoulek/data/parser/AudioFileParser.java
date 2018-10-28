package fr.jblezoray.diaoulek.data.parser;

import fr.jblezoray.diaoulek.data.model.AudioFile;
import fr.jblezoray.diaoulek.data.model.FileIndexEntry;

public class AudioFileParser implements IParser<AudioFile> {

    @Override
    public boolean seemsParseable(FileIndexEntry fileIndexEntry) {
        String filename = fileIndexEntry.getFilename();
        return filename.endsWith(".ogg");
    }

    @Override
    public AudioFile parse(byte[] fileContent, FileIndexEntry fileIndexEntry) throws DataException {
        AudioFile af = new AudioFile();
        // TODO
        return af;
    }

}
