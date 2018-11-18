package fr.jblezoray.diaoulek.data.model.lessonelement.qrcouple;

import fr.jblezoray.diaoulek.data.model.Part;

public class Question {

    private String rawString;
    private Part[] parts;

    public void setRawString(String rawString) {
        this.rawString = rawString;
    }

    public void setParts(Part[] parts) {
        this.parts = parts;
    }

    /**
     * parts of a question are separated by a ';' in lesson files.
     * @return
     */
    public Part[] getParts() {
        return this.parts;
    }

    @Override
    public String toString() {
        return rawString;
    }

}
