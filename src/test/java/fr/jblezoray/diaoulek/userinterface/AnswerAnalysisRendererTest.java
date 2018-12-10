package fr.jblezoray.diaoulek.userinterface;

import fr.jblezoray.diaoulek.core.AnswerAnalyser;
import fr.jblezoray.diaoulek.data.model.Part;
import fr.jblezoray.diaoulek.data.model.analysis.AnswerAnalysis;
import fr.jblezoray.diaoulek.data.model.lessonelement.QRCouple;
import fr.jblezoray.diaoulek.data.model.lessonelement.qrcouple.Response;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

public class AnswerAnalysisRendererTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(AnswerAnalysisRendererTest.class);

    @Test
    public void nominalCase() {
        // having
        Response r = new Response();
        r.setParts(parts("Mort aux vaches bleues"));
        QRCouple qr = new QRCouple();
        qr.setResponse(r);
        AnswerAnalysis aa = new AnswerAnalyser(qr)
                .analyze("mor pas aux Waches!");

        // when
        String rendered = new AnswerAnalysisRenderer(aa).render();

        // then
        System.out.println(rendered);
    }

    private static Part[] parts(String... parts) {
        return Arrays.stream(parts)
                .map(Part::new)
                .toArray(Part[]::new);
    }

}
