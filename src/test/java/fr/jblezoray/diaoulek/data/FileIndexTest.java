package fr.jblezoray.diaoulek.data;

import fr.jblezoray.diaoulek.data.model.FileIndexEntry;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

public class FileIndexTest {


    @Test
    public void testParseIndex() throws IOException, DataException {
        // having
        String fileContent = ResourceReader.readResource("/tot-file-tank.txt");

        // when
        List<FileIndexEntry> entries = new FileIndex().parseIndex(fileContent);

        // then
        Assertions.assertEquals(698, entries.size()); // number of '#' in the file.
        for (FileIndexEntry entry: entries) {
            Assertions.assertNotNull(entry);
            Assertions.assertNotNull(entry.getFilename());
            Assertions.assertNotNull(entry.getFilesize());
            Assertions.assertNotNull(entry.getFiletimeDate());
            Assertions.assertNotNull(entry.getMd5());
        }
    }


}
