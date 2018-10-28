package fr.jblezoray.diaoulek.data;

import fr.jblezoray.diaoulek.data.model.FileIndexEntry;
import fr.jblezoray.diaoulek.data.parser.DataException;
import fr.jblezoray.diaoulek.data.parser.FileIndexParser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

public class FileIndexTest {


    @Test
    public void testParseIndex() throws IOException, DataException {
        // having
        String fileContent = ResourceReader.readResource("/tot-file-tank.txt");

        // when
        List<FileIndexEntry> entries = new FileIndexParser(Charset.forName("ISO-8859-1"))
                .parse(fileContent.getBytes(), null);

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
