package fr.jblezoray.diaoulek.entrypoint;

import fr.jblezoray.diaoulek.core.DiaoulekService;
import fr.jblezoray.diaoulek.data.parser.FileIndexParser;
import fr.jblezoray.diaoulek.data.parser.LessonParser;
import fr.jblezoray.diaoulek.data.scrapper.FileCache;
import fr.jblezoray.diaoulek.data.scrapper.FileDownloader;
import fr.jblezoray.diaoulek.data.scrapper.FileRetrieverException;
import fr.jblezoray.diaoulek.userinterface.CommandLineUserInterface;
import fr.jblezoray.diaoulek.userinterface.SoundPlayer;

import java.io.IOException;

public class App {

    public static void main(String[] args)
            throws FileRetrieverException, IOException {

        FileDownloader fd = new FileDownloader(Config.URL_UPDATE);
        FileCache cache = new FileCache(Config.CACHE_DIR, fd);

        LessonParser lessonParser = new LessonParser();
        FileIndexParser fileIndexParser = new FileIndexParser(Config.DEFAULT_CHARSET);
        DiaoulekService service = new DiaoulekService(
                cache, Config.FILE_INDEX_NAME, fileIndexParser, lessonParser);

        SoundPlayer soundPlayer = new SoundPlayer(cache);
        CommandLineUserInterface cli = new CommandLineUserInterface(
                service, System.out, System.in, soundPlayer);
        cli.start();
    }
}
