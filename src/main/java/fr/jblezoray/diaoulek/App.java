package fr.jblezoray.diaoulek;

import fr.jblezoray.diaoulek.data.model.AudioFile;
import fr.jblezoray.diaoulek.data.model.LessonEntry;
import fr.jblezoray.diaoulek.data.parser.*;
import fr.jblezoray.diaoulek.data.model.FileIndexEntry;
import fr.jblezoray.diaoulek.data.scrapper.FileCache;
import fr.jblezoray.diaoulek.data.scrapper.FileDownloader;
import fr.jblezoray.diaoulek.data.scrapper.FileRetrieverException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

/**
 * Hello world!
 *
 */
public class App {

    private static final Logger LOGGER = LoggerFactory.getLogger(App.class);

    public static void main( String[] args ) throws FileRetrieverException, DataException {
        LOGGER.info("Hello World!");

        FileDownloader fd = new FileDownloader(Config.URL_UPDATE);
        FileCache fc = new FileCache(Config.CACHE_DIR, fd);

        byte[] indexContent = fc.getFileContent("tot-file-tank.txt", Optional.empty());
        List<FileIndexEntry> indexEntries = new FileIndexParser(Config.CHARSET).parse(indexContent, null);
        int sizeIndex = indexEntries.size();
        LOGGER.debug("There are {} entries in the index.", sizeIndex);

        // reading lessons.
        int nb = 0;
        LessonParser lessonParser = new LessonParser(Config.CHARSET);
        AudioFileParser audioFileParser = new AudioFileParser();
        DicoParser dicoParser = new DicoParser();
        for (FileIndexEntry indexEntry : indexEntries) {
            try {
                LOGGER.debug("{} : parsing", indexEntry.getFilename());
                if (lessonParser.seemsParseable(indexEntry)) {
                    byte[] fileContent = fc.getFileContent(indexEntry, Optional.empty());
                    LessonEntry le = lessonParser.parse(fileContent, indexEntry);
                    nb++;

                } else if (audioFileParser.seemsParseable(indexEntry)) {
                    byte[] fileContent = fc.getFileContent(indexEntry, Optional.of("SOUND"));
                    AudioFile af = audioFileParser.parse(fileContent, indexEntry);
                    nb++;

                } else if (dicoParser.seemsParseable(indexEntry)) {
                    byte[] fileContent = fc.getFileContent(indexEntry, Optional.empty());
                    dicoParser.parse(fileContent, indexEntry);
                    nb++;

                } else {
                    LOGGER.warn("unrecognized type : '"+indexEntry.getFilename()+"'");
                }
            } catch (DataException e) {
                LOGGER.warn("Cannot parse file '"+indexEntry.getFilename()+"'", e);
            }
        }

        LOGGER.info("Done : {} lessons and audio files / {} entries", nb, sizeIndex);
    }
}
