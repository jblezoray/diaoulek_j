package fr.jblezoray.diaoulek.entrypoint;

import java.io.File;
import java.nio.charset.Charset;

public interface Config {
    String URL_UPDATE = "http://furchhadiaoulek.free.fr/TANK-BF/";
    Charset DEFAULT_CHARSET = Charset.forName("ISO-8859-1");
    File CACHE_DIR = new File("./downloaded_files_cache");
    String FILE_INDEX_NAME = "tot-file-tank.txt";
}
