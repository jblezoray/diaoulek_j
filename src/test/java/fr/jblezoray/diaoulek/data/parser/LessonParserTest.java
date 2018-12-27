package fr.jblezoray.diaoulek.data.parser;

import fr.jblezoray.diaoulek.data.model.FileIndexEntry;
import fr.jblezoray.diaoulek.data.model.LessonEntry;
import fr.jblezoray.diaoulek.data.model.lessonelement.QRCouple;
import fr.jblezoray.diaoulek.data.model.lessonelement.Text;
import fr.jblezoray.diaoulek.data.model.lessonelement.WordReference;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
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
        LessonParser lesson = new LessonParser(Charset.forName("UTF-8"));
        LessonEntry e = lesson.parse(fileContent.getBytes(), fieMock);

        // then
        Assertions.assertEquals("KK12-2", e.getAlias());
        Assertions.assertEquals(19, e.getLessonElements().size());

        Text elt0 = (Text) e.getLessonElements().get(0);
        Assertions.assertTrue(elt0.getComment().startsWith("Les mots bretons en \"er\""));
        Assertions.assertTrue(elt0.getComment().endsWith("Exemple : < habaskted > (patience)."));

        QRCouple elt1 = (QRCouple) e.getLessonElements().get(1);
        Assertions.assertArrayEquals(new String[]{"lez"}, elt1.getSeparationLine().getWordReferences());
        Assertions.assertArrayEquals(new String[]{"Q2", "paysage", "terre"}, elt1.getSeparationLine().getTags());
        Assertions.assertEquals("aln-kk12-2.ogg", elt1.getSound().getSoundFileName());
        Assertions.assertEquals(5632, (int) elt1.getSound().getSoundBeginIndex());
        Assertions.assertEquals(358912, (int) elt1.getSound().getSoundEndIndex());
        Assertions.assertEquals("al lez (2) ; lezoù ; daou lez ; (KA-91) lez ar c'hoadoù ; Lezardrev ;", elt1.getQuestion().toString());
        Assertions.assertEquals("al lez (2)", elt1.getQuestion().getParts()[0].getRawString());
        Assertions.assertEquals("le bord, la lisière ; ... la lisière des bois ; Lézardrieux ;", elt1.getResponse().toString());
        Assertions.assertEquals("le bord, la lisière", elt1.getResponse().getParts()[0].getRawString());

        QRCouple elt2 = (QRCouple) e.getLessonElements().get(2);
        Assertions.assertArrayEquals(new String[]{"efficacité", "redoutable"}, elt2.getSeparationLineReverse().getWordReferences());
        Assertions.assertEquals("une efficacité redoutable", elt2.getSeparationLineReverse().getNote());
        Assertions.assertEquals(0, elt2.getSeparationLineReverse().getTags().length);
        Assertions.assertEquals("un efedusted vras (fém.) ; un efedusted spouronus ;", elt2.getQuestion().toString());
        Assertions.assertEquals("un efedusted vras (fém.)", elt2.getQuestion().getParts()[0].getRawString());
        Assertions.assertEquals("une grande efficacité (m/f) ; une efficacité redoutable ;", elt2.getResponse().toString());

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

    @Test
    public void testParseLessonText() throws IOException, DataException {
        // having
        String fileContent = ResourceReader.readResource("/ee-4.txt");
        FileIndexEntry fieMock = new FileIndexEntry();

        // when
        LessonParser lesson = new LessonParser(Charset.forName("ISO-8859-1"));
        LessonEntry e = lesson.parse(fileContent.getBytes(), fieMock);

        // then
        Assertions.assertEquals(1, e.getLessonElements()
                .stream().filter(le -> le instanceof Text).count());
        Text t = (Text) e.getLessonElements()
                .stream().filter(le -> le instanceof Text).findFirst().get();
        // lessonText
        Assertions.assertEquals("ee-4.ogg", t.getText().get(0).getSound().getSoundFileName());
        Assertions.assertEquals(5120, (int)t.getText().get(0).getSound().getSoundBeginIndex());
        Assertions.assertEquals(1278464, (int)t.getText().get(0).getSound().getSoundEndIndex());
        Assertions.assertEquals("", t.getText().get(0).getLine());
        Assertions.assertEquals("Ur c'helenner pe ur skolaer eo ?", t.getText().get(1).getLine());
        Assertions.assertEquals("Et en ce qui concerne mon professeur de breton, il n'a aucun salaire.", t.getText().get(8).getTranslation());
        // comment
        Assertions.assertTrue(t.getComment().startsWith("1)  < Ur c'hele"));
        Assertions.assertTrue(t.getComment().endsWith(" deux par < mieux >"));
    }



    @Disabled("TODO ! ")
    @Test
    public void testGuessEncoding() throws IOException, DataException {
        // having
        String fileContent = ResourceReader.readResource("/ex2.txt"); // UTF-8 !
        FileIndexEntry fieMock = new FileIndexEntry();

        // when
        LessonParser lesson = new LessonParser(Charset.forName("ISO-8859-1")); // ikes ...
        LessonEntry e = lesson.parse(fileContent.getBytes(), fieMock);

        // then
        QRCouple qr = (QRCouple) e.getLessonElements().get(2);
        Assertions.assertEquals("mère", qr.getResponse().getParts()[0]);
    }


    @Test
    public void testIgnoreEmptyLines() throws IOException, DataException {
        // having
        String fileContent = ResourceReader.readResource("/ks53.txt");
        FileIndexEntry fieMock = new FileIndexEntry();

        // when
        LessonParser lesson = new LessonParser(Charset.forName("ISO-8859-1"));
        LessonEntry e = lesson.parse(fileContent.getBytes(), fieMock);

        // then
        Assertions.assertEquals(2, e.getLessonElements()
                .stream().filter(le -> le instanceof QRCouple).count());
        Assertions.assertEquals("bander une blessure",
                ((QRCouple) e.getLessonElements().get(0)).getResponse().getParts()[0].getRawString());
        Assertions.assertEquals("ur gouli",
                ((QRCouple) e.getLessonElements().get(1)).getQuestion().getParts()[0].getRawString());
    }

}
