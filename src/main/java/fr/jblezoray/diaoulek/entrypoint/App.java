package fr.jblezoray.diaoulek.entrypoint;

import fr.jblezoray.diaoulek.core.DiaoulekService;
import fr.jblezoray.diaoulek.data.parser.FileIndexParser;
import fr.jblezoray.diaoulek.data.parser.LessonParser;
import fr.jblezoray.diaoulek.data.scrapper.FileCache;
import fr.jblezoray.diaoulek.data.scrapper.FileDownloader;
import fr.jblezoray.diaoulek.data.scrapper.FileRetrieverException;
import fr.jblezoray.diaoulek.userinterface.CommandLineUserInterface;

public class App {

    public static void main(String[] args) throws FileRetrieverException {

        FileDownloader fd = new FileDownloader(Config.URL_UPDATE);
        FileCache fc = new FileCache(Config.CACHE_DIR, fd);

        LessonParser lessonParser = new LessonParser(Config.CHARSET);
        FileIndexParser fileIndexParser = new FileIndexParser(Config.CHARSET);
        DiaoulekService s = new DiaoulekService(fc, Config.FILE_INDEX_NAME, fileIndexParser, lessonParser);

        CommandLineUserInterface cli = new CommandLineUserInterface(s, System.out, System.in);
        cli.start();
    }
}
