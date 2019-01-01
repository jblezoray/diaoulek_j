package fr.jblezoray.diaoulek.core;

import fr.jblezoray.diaoulek.entrypoint.Config;
import org.mozilla.universalchardet.UniversalDetector;

import java.nio.charset.Charset;

public class CharsetDetector {

    public static Charset guessEncoding(byte[] bytes) {
        UniversalDetector detector = new UniversalDetector(null);
        detector.handleData(bytes, 0, bytes.length);
        detector.dataEnd();
        String encoding = detector.getDetectedCharset();
        detector.reset();

        Charset charset = null;
        if (encoding != null) {
            try {
                charset = Charset.forName(encoding);
            } catch (IllegalArgumentException e) {
                // noop
            }
        }

        return charset==null ? Config.DEFAULT_CHARSET : charset;
    }
}
