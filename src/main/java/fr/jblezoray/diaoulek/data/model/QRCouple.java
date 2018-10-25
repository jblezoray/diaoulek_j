package fr.jblezoray.diaoulek.data.model;


import java.util.ArrayList;
import java.util.List;

public class QRCouple extends LessonElement {

    private QRCoupleSeparationLine separationLine;
    private QRCoupleSeparationLine separationLineLegacy;
    private QRCoupleSeparationLine separationLineReverse;
    private QRCoupleSound sound;


    public QRCoupleSeparationLine getSeparationLine() {
        return separationLine;
    }

    public void setSeparationLine(QRCoupleSeparationLine separationLine) {
        this.separationLine = separationLine;
    }

    public QRCoupleSeparationLine getSeparationLineReverse() {
        return separationLineReverse;
    }

    public void setSeparationLineReverse(QRCoupleSeparationLine separationLineReverse) {
        this.separationLineReverse = separationLineReverse;
    }

    public void setSeparationLineLegacy(QRCoupleSeparationLine separationLineLegacy) {
        this.separationLineLegacy = separationLineLegacy;
    }

    public QRCoupleSeparationLine getSeparationLineLegacy() {
        return this.separationLineLegacy;
    }

    public QRCoupleSound getSound() {
        return sound;
    }

    public void setSound(QRCoupleSound sound) {
        this.sound = sound;
    }
}
