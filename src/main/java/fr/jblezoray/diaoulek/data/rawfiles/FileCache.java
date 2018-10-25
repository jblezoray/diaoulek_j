package fr.jblezoray.diaoulek.data.rawfiles;

import fr.jblezoray.diaoulek.data.model.FileIndexEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileCache implements IFileRetriever {
    private static final Logger LOGGER = LoggerFactory.getLogger(FileCache.class);

    private final File cacheDir;
    private final FileDownloader downloader;
    private final Charset charset;

    public FileCache(File cacheDir,
                     FileDownloader downloader,
                     Charset charset)
            throws FileRetrieverException {
        this.cacheDir = cacheDir;
        this.downloader = downloader;
        this.charset = charset;
        init();
    }

    private void init() throws FileRetrieverException {
        if (!cacheDir.exists() && !cacheDir.mkdir()) {
            throw new FileRetrieverException("Cannot create cache dir " + cacheDir.getAbsolutePath());
        }
    }

    @Override
    public String getFileContent(String filename) throws FileRetrieverException {

        String hashedName = filename + ".cache";
        File cachedFile = new File(cacheDir, hashedName);

        String fileContent;
        try {
            // return file content if it exists.
            if (cachedFile.exists()) {
                LOGGER.debug("File from cache : {}", filename);
                byte[] encoded = Files.readAllBytes(Paths.get(cachedFile.toURI()));
                fileContent = new String(encoded, charset);

            } else {
                // download file.
                fileContent = downloader.getFileContent(filename);

                // write to new file.
                Files.write(Paths.get(cachedFile.toURI()), fileContent.getBytes());
            }

        } catch (IOException e) {
            throw new FileRetrieverException(e);
        }

        return fileContent;
    }

    @Override
    public String getFileContent(FileIndexEntry lesson) throws FileRetrieverException {

        String hashedName = lesson.getMd5() + "." + lesson.getFilename() + ".cache";
        File cachedFile = new File(cacheDir, hashedName);

        String fileContent;
        try {
            // return file content if it exists.
            if (cachedFile.exists()) {
                LOGGER.debug("File from cache : {}", lesson.getFilename());
                byte[] encoded = Files.readAllBytes(Paths.get(cachedFile.toURI()));
                fileContent = new String(encoded, charset);

            } else {
                // download file.
                fileContent = downloader.getFileContent(lesson);

                // write to new file.
                Files.write(Paths.get(cachedFile.toURI()), fileContent.getBytes());
            }

        } catch (IOException e) {
            throw new FileRetrieverException(e);
        }

        return fileContent;
    }
}
