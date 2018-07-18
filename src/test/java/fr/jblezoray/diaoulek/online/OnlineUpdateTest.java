package fr.jblezoray.diaoulek.online;

import fr.jblezoray.diaoulek.Config;
import fr.jblezoray.diaoulek.online.model.OnlineUpdateEntry;
import jdk.nashorn.internal.ir.annotations.Ignore;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

public class OnlineUpdateTest {


    @Test
    @Ignore
    public void testIntegration() throws IOException, URISyntaxException {
        URI indexURI = new URI(Config.URL_UPDATE + "/tot-file-tank.txt");
        new OnlineUpdate().readIndex(indexURI);
    }

    @Test
    public void test() throws IOException, URISyntaxException {
        // having
        String fileContent = readResource("/tot-file-tank.txt");

        // when
        List<OnlineUpdateEntry> entries = new OnlineUpdate().parseIndex(fileContent);

        // then
        Assertions.assertEquals(698, entries.size()); // number of '#' in the file.
        for (OnlineUpdateEntry entry: entries) {
            Assertions.assertNotNull(entry);
            Assertions.assertNotNull(entry.getFilename());
            Assertions.assertNotNull(entry.getFilesize());
            Assertions.assertNotNull(entry.getFiletimeDate());
            Assertions.assertNotNull(entry.getMd5());
        }
    }


    private String readResource(String resourceName) throws IOException {
        InputStream is = this.getClass().getResourceAsStream(resourceName);

        final int bufferSize = 1024;
        final char[] buffer = new char[bufferSize];
        final StringBuilder out = new StringBuilder();
        Reader in = new InputStreamReader(is, "UTF-8");
        for (; ; ) {
            int rsz = in.read(buffer, 0, buffer.length);
            if (rsz < 0)
                break;
            out.append(buffer, 0, rsz);
        }
        return out.toString();
    }

}
