package fr.jblezoray.diaoulek;

import fr.jblezoray.diaoulek.data.DataException;
import fr.jblezoray.diaoulek.data.FileIndex;
import fr.jblezoray.diaoulek.data.Lesson;
import fr.jblezoray.diaoulek.data.model.FileIndexEntry;
import fr.jblezoray.diaoulek.data.rawfiles.FileCache;
import fr.jblezoray.diaoulek.data.rawfiles.FileDownloader;
import fr.jblezoray.diaoulek.data.rawfiles.FileRetrieverException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Hello world!
 *
 */
public class App {

    private static final Logger LOGGER = LoggerFactory.getLogger(App.class);

    public static void main( String[] args ) throws FileRetrieverException, DataException {
        LOGGER.info("Hello World!");

        FileDownloader fd = new FileDownloader(Config.URL_UPDATE, Config.CHARSET);
        FileCache fc = new FileCache(Config.CACHE_DIR, fd, Config.CHARSET);

        String indexContent = fc.getFileContent("tot-file-tank.txt");
        List<FileIndexEntry> indexEntries = new FileIndex().parseIndex(indexContent);
        int sizeIndex = indexEntries.size();
        LOGGER.debug("There are {} entries in the index.", sizeIndex);

        // reading lessons.
        int nbLessons = 0;
        for (FileIndexEntry indexEntry : indexEntries) {

            Lesson lesson = new Lesson(indexEntry);
            if (!lesson.smellsLikeLesson()) continue;

            String fileContent = fc.getFileContent(indexEntry);
            LOGGER.debug("{} : parsing", indexEntry.getFilename());
            lesson.parse(fileContent);
            nbLessons++;
        }

        LOGGER.info("Done : {} lessons / {} entries", nbLessons, sizeIndex);
    }
}
