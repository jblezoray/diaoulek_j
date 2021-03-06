package fr.jblezoray.diaoulek.data.model.lessonelement.qrcouple;

import fr.jblezoray.diaoulek.data.model.Part;

public class Response {

    private String rawString;
    private Part[] parts;

    public void setRawString(String rawString) {
        this.rawString = rawString;
    }

    public void setParts(Part[] parts) {
        this.parts = parts;
    }

    public Part[] getParts() {
        return this.parts;
    }

    @Override
    public String toString() {
        return rawString;
    }
}
