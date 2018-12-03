package fr.jblezoray.diaoulek.core.levenshtein;

import fr.jblezoray.diaoulek.core.AnswerAnalyser;
import fr.jblezoray.diaoulek.data.model.Part;
import fr.jblezoray.diaoulek.data.model.analysis.AnswerAnalysis;
import fr.jblezoray.diaoulek.data.model.analysis.EditOperation;
import fr.jblezoray.diaoulek.data.model.lessonelement.QRCouple;
import fr.jblezoray.diaoulek.data.model.lessonelement.qrcouple.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

public class AnswerAnalyserTest {

    @Test
    public void nominalCase() {
        // having
        Response r = new Response();
        r.setParts(parts("Mort aux Waches !"));
        QRCouple qr = new QRCouple();
        qr.setResponse(r);

        // when
        AnswerAnalyser answerAnalyser = new AnswerAnalyser(qr);
        AnswerAnalysis aa = answerAnalyser.analyze("Mords aux la vache");

        // then
        Assertions.assertIterableEquals(
                Arrays.asList("mort", "aux", "waches"),
                aa.getExpectedResponseTokenized());
        Assertions.assertIterableEquals(
                Arrays.asList("mords", "aux", "la", "vache"),
                aa.getInputWordsTokenized());
        // we're pretty sure each word corresponds to his counterpart :
        Assertions.assertEquals(4, aa.getPhraseEditPath().size());
        Assertions.assertEquals(0.50f, aa.getInputWordsAccuracy().get(0), 0.01);
        Assertions.assertEquals(1.00f, aa.getInputWordsAccuracy().get(1), 0.01);
        Assertions.assertNull(aa.getInputWordsAccuracy().get(2));
        Assertions.assertEquals(0.66f, aa.getInputWordsAccuracy().get(3), 0.01);
        // this is the distance.
        Assertions.assertEquals(2, aa.getInputWordsEditPath().get(0).size());
        Assertions.assertEquals(0, aa.getInputWordsEditPath().get(1).size());
        Assertions.assertNull(aa.getInputWordsEditPath().get(2));
        Assertions.assertEquals(2, aa.getInputWordsEditPath().get(3).size());
    }


    private static Part[] parts(String... parts) {
        return Arrays.stream(parts)
                .map(Part::new)
                .toArray(Part[]::new);
    }

}
