package fr.jblezoray.diaoulek.data.rawfiles;

import fr.jblezoray.diaoulek.data.model.FileIndexEntry;
import org.apache.http.client.fluent.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.Charset;

public class FileDownloader implements IFileRetriever {
    private static final Logger LOGGER = LoggerFactory.getLogger(FileDownloader.class);

    private final String baseURL;
    private final Charset charset;

    public FileDownloader(String baseURL,
                          Charset charset) {
        this.baseURL = baseURL;
        this.charset = charset;
    }


    @Override
    public String getFileContent(FileIndexEntry uri) throws FileRetrieverException {
        String fileContent = downloadFile(uri.getFilename());
        // TODO check md5 value.
        return fileContent;
    }

    @Override
    public String getFileContent(String filename) throws FileRetrieverException {
        return downloadFile(filename);
    }

    private String downloadFile(String filename) throws FileRetrieverException {

        LOGGER.info("downloading file {}", filename);

        String uri = this.baseURL + "/" + filename;

        String fileContent;
        try {
            fileContent = Request
                    .Get(uri)
                    .execute()
                    .returnContent()
                    .asString(this.charset);

        } catch (IOException e) {
            throw new FileRetrieverException(e);
        }

        return fileContent;
    }
}
