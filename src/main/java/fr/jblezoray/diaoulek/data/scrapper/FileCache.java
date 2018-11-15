package fr.jblezoray.diaoulek.data.scrapper;

import fr.jblezoray.diaoulek.data.model.FileIndexEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.concurrent.Callable;

public class FileCache implements IFileRetriever {
    private static final Logger LOGGER = LoggerFactory.getLogger(FileCache.class);

    private final File cacheDir;
    private final FileDownloader downloader;

    public FileCache(File cacheDir,
                     FileDownloader downloader)
            throws FileRetrieverException {
        this.cacheDir = cacheDir;
        this.downloader = downloader;
        init();
    }

    private void init() throws FileRetrieverException {
        if (!cacheDir.exists() && !cacheDir.mkdir()) {
            throw new FileRetrieverException("Cannot create cache dir " + cacheDir.getAbsolutePath());
        }
    }

    @Override
    public byte[] getFileContent(String filename, Optional<String> dir) throws FileRetrieverException {
        return getFile(filename, () -> downloader.getFileContent(filename, dir));
    }

    @Override
    public byte[] getFileContent(FileIndexEntry file, Optional<String> dir) throws FileRetrieverException {
        return getFile(file.getFilename(), () -> downloader.getFileContent(file, dir));
    }

    /**
     * gets the file content either from the cache, or if a cached version
     * isn't available, from the original source.
     *
     * @param filename
     * @param originalSource
     * @return
     * @throws FileRetrieverException
     */
    private byte[] getFile(String filename, Callable<byte[]> originalSource)
    throws FileRetrieverException {
        File cachedFile = new File(cacheDir, filename + ".cache");
        byte[] fileContent;
        try {
            // return file content if it exists.
            if (cachedFile.exists()) {
                LOGGER.debug("File from cache : {}", filename);
                fileContent = Files.readAllBytes(Paths.get(cachedFile.toURI()));

            } else {
                // download file.
                LOGGER.debug("File from source : {}", filename);
                fileContent = originalSource.call();

                // write to new file.
                Files.write(Paths.get(cachedFile.toURI()), fileContent);
            }
        } catch (FileRetrieverException fre) {
            throw fre;
        } catch (Exception e) {
            throw new FileRetrieverException(e);
        }

        return fileContent;
    }
}
