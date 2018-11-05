package fr.jblezoray.diaoulek.data.parser;

import fr.jblezoray.diaoulek.data.model.*;
import fr.jblezoray.diaoulek.data.model.lessonelement.QRCouple;
import fr.jblezoray.diaoulek.data.model.lessonelement.Text;
import fr.jblezoray.diaoulek.data.model.lessonelement.WordReference;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.Charset;

public class LessonParserTest {


    @Test
    public void testParseLesson() throws IOException, URISyntaxException, DataException {
        // having
        String fileContent = ResourceReader.readResource("/kk12-2.txt");
        FileIndexEntry fieMock = new FileIndexEntry();

        // when
        LessonParser lesson = new LessonParser(Charset.forName("ISO-8859-1"));
        LessonEntry e = lesson.parse(fileContent.getBytes(), fieMock);

        // then
        Assertions.assertEquals("KK12-2", e.getAlias());
        Assertions.assertEquals(19, e.getLessonElements().size());

        Text elt0 = (Text) e.getLessonElements().get(0);
        Assertions.assertTrue(elt0.getText().startsWith("\nCommentaire : \n"));
        Assertions.assertTrue(elt0.getText().endsWith("(patience).\n\n\n"));

        QRCouple elt1 = (QRCouple) e.getLessonElements().get(1);
        Assertions.assertArrayEquals(new String[]{"lez"}, elt1.getSeparationLine().getWordReferences());
        Assertions.assertArrayEquals(new String[]{"Q2", "paysage", "terre"}, elt1.getSeparationLine().getTags());
        Assertions.assertEquals("aln-kk12-2.ogg", elt1.getSound().getSoundFileName());
        Assertions.assertEquals(5632, (int) elt1.getSound().getSoundBeginIndex());
        Assertions.assertEquals(358912, (int) elt1.getSound().getSoundEndIndex());
        Assertions.assertEquals("al lez (2) ; lezoù ; daou lez ; (KA-91)\nlez ar c'hoadoù ;\nLezardrev ;", elt1.getQuestion().toString());
        Assertions.assertEquals("al lez (2)", elt1.getQuestion().getParts()[0].toString());
        Assertions.assertEquals("le bord, la lisière ; ...\nla lisière des bois ;\nLézardrieux ;", elt1.getResponse().toString());
        Assertions.assertEquals("le bord, la lisière", elt1.getResponse().getParts()[0].toString());
        Assertions.assertArrayEquals(new String[]{"le bord", "la lisière"}, elt1.getResponse().getParts()[0].getPhrases());

        QRCouple elt2 = (QRCouple) e.getLessonElements().get(2);
        Assertions.assertArrayEquals(new String[]{"efficacité", "redoutable"}, elt2.getSeparationLineReverse().getWordReferences());
        Assertions.assertEquals("une efficacité redoutable", elt2.getSeparationLineReverse().getNote());
        Assertions.assertEquals(0, elt2.getSeparationLineReverse().getTags().length);
        Assertions.assertEquals("un efedusted vras (fém.) ;\nun efedusted spouronus ;", elt2.getQuestion().toString());
        Assertions.assertEquals("un efedusted vras (fém.)", elt2.getQuestion().getParts()[0].toString());
        Assertions.assertEquals("une grande efficacité (m/f) ;\nune efficacité redoutable ;", elt2.getResponse().toString());
        Assertions.assertArrayEquals(new String[]{"une grande efficacité (m/f)"}, elt2.getResponse().getParts()[0].getPhrases());

        WordReference elt18 = (WordReference) e.getLessonElements().get(18);
        Assertions.assertEquals("sil", elt18.getWord());
        Assertions.assertEquals("KE-17", elt18.getLessonAlias());
        Assertions.assertArrayEquals(new String[]{"ustensile"}, elt18.getTags());
    }


    @Test
    public void testCasLimites() throws IOException, URISyntaxException, DataException {
        // having
        String fileContent = ResourceReader.readResource("/casLimites.txt");
        FileIndexEntry fieMock = new FileIndexEntry();

        // when
        LessonParser lesson = new LessonParser(Charset.forName("ISO-8859-1"));
        LessonEntry e = lesson.parse(fileContent.getBytes(), fieMock);

        // then no exception.
    }
}