package fr.jblezoray.diaoulek;

import java.io.File;
import java.nio.charset.Charset;

interface Config {
    String URL_UPDATE = "http://furchhadiaoulek.free.fr/TANK-BF/";
    Charset CHARSET =
            // Charset.forName("UTF-8");
            Charset.forName("ISO-8859-1"); // ISO-8859-1
    File CACHE_DIR = new File("./downloaded_files_cache");
}
