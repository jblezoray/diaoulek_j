package fr.jblezoray.diaoulek.data.parser;

import fr.jblezoray.diaoulek.data.model.Dictionary;
import fr.jblezoray.diaoulek.data.model.FileIndexEntry;
import fr.jblezoray.diaoulek.data.parser.DataException;
import fr.jblezoray.diaoulek.data.parser.DicoParser;
import fr.jblezoray.diaoulek.data.parser.IParser;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class DicoParserTest {

    @Test
    public void testParseDico() throws IOException, DataException {
        // having
        String fileContent = ResourceReader.readResource("/kozh_skridou.dicqr");
        FileIndexEntry fieMock = new FileIndexEntry();

        // when
        Dictionary dico = new DicoParser().parse(fileContent.getBytes(), fieMock);

        // then
        // TODO

    }

}
