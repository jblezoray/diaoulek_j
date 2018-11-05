package fr.jblezoray.diaoulek.core;

import fr.jblezoray.diaoulek.data.model.FileIndexEntry;
import fr.jblezoray.diaoulek.data.model.LessonCategory;
import fr.jblezoray.diaoulek.data.model.LessonEntry;
import fr.jblezoray.diaoulek.data.parser.DataException;
import fr.jblezoray.diaoulek.data.parser.FileIndexParser;
import fr.jblezoray.diaoulek.data.parser.LessonParser;
import fr.jblezoray.diaoulek.data.scrapper.FileRetrieverException;
import fr.jblezoray.diaoulek.data.scrapper.IFileRetriever;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Stream;

public class DiaoulekService {
    private static final Logger LOGGER = LoggerFactory.getLogger(DiaoulekService.class);

    private final IFileRetriever fileRetriever;
    private final String fileIndexName;
    private final LessonParser lessonParser;
    private final FileIndexParser fileIndexParser;

    public DiaoulekService(
            IFileRetriever fileRetriever,
            String fileIndexName,
            FileIndexParser fileIndexParser,
            LessonParser lessonParser) {
        this.fileRetriever = fileRetriever;
        this.fileIndexName = fileIndexName;
        this.lessonParser = lessonParser;
        this.fileIndexParser = fileIndexParser;
    }


    public Stream<FileIndexEntry> streamAllLessons()
            throws FileRetrieverException, DataException {
        byte[] indexContent = this.fileRetriever.getFileContent(this.fileIndexName, Optional.empty());
        return this.fileIndexParser.parse(indexContent, null)
                .stream()
                .filter(fileIndexEntry -> lessonParser.seemsParseable(fileIndexEntry) )
                .sorted(Comparator.comparing(FileIndexEntry::getFilename));
    }


    public Stream<FileIndexEntry> streamLessonsWithin(LessonCategory lc)
            throws FileRetrieverException, DataException {
        return streamAllLessons()
                .filter(fie -> fie.getFilename().matches(lc.getFilenameRegExp()))
                // sort by comparing numbers in filenames (1 < 2 < 10)
                .sorted((fie1, fie2) -> {
                    String s1 = fie1.getFilename().replaceAll("[\\D]+", "");
                    String s2 = fie2.getFilename().replaceAll("[\\D]+", "");
                    return Integer.valueOf(s1).compareTo(Integer.valueOf(s2));
                });
    }


    public LessonEntry getLesson(FileIndexEntry fie)
            throws FileRetrieverException, DataException {
        byte[] bytes = this.fileRetriever.getFileContent(fie, Optional.empty());
        return this.lessonParser.parse(bytes, fie);
    }
}
