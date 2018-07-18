package fr.jblezoray.diaoulek.online.model;

import java.util.Date;

public class OnlineUpdateEntry {

    private final String filename;
    private final String filesize;
    private final String md5;
    private final Date filetimeDate;

    public OnlineUpdateEntry(
            String filename,
            String filesize,
            String md5,
            Date filetimeDate) {
        this.filename = filename;
        this.filesize = filesize;
        this.md5 = md5;
        this.filetimeDate = filetimeDate;
    }


    public String getFilename() {
        return filename;
    }

    public String getFilesize() {
        return filesize;
    }

    public String getMd5() {
        return md5;
    }

    public Date getFiletimeDate() {
        return filetimeDate;
    }
}
