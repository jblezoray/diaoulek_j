package fr.jblezoray.diaoulek.data.scrapper;

import fr.jblezoray.diaoulek.data.model.FileIndexEntry;
import org.apache.http.client.fluent.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Optional;

public class FileDownloader implements IFileRetriever {
    private static final Logger LOGGER = LoggerFactory.getLogger(FileDownloader.class);

    private final String baseURL;

    public FileDownloader(String baseURL) {
        this.baseURL = baseURL;
    }


    @Override
    public byte[] getFileContent(FileIndexEntry uri, Optional<String> dir) throws FileRetrieverException {
        byte[] fileContent = downloadFile(uri.getFilename(), dir);
        // TODO check md5 value from uri.getMd5()
        return fileContent;
    }

    @Override
    public byte[] getFileContent(String filename, Optional<String> dir) throws FileRetrieverException {
        return downloadFile(filename, dir);
    }

    private byte[] downloadFile(String filename, Optional<String> dir) throws FileRetrieverException {

        LOGGER.info("downloading file {}", filename);

        String uri = this.baseURL + (dir.isPresent() ? dir.get() + "/" : "") + filename;

        byte[] fileContent;
        try {
            fileContent = Request
                    .Get(uri)
                    .execute()
                    .returnContent()
                    .asBytes();

        } catch (IOException e) {
            throw new FileRetrieverException("error: "+uri, e);
        }

        return fileContent;
    }
}
