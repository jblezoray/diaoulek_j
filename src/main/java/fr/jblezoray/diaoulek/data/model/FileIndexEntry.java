package fr.jblezoray.diaoulek.data.model;

import java.util.Date;

public class FileIndexEntry {

    private String filename;
    private Integer filesize;
    private String md5;
    private Date filetimeDate;

    public FileIndexEntry() {}

    public FileIndexEntry(FileIndexEntry oue) {
        this.filename = oue.filename;
        this.filesize = oue.filesize;
        this.md5 = oue.md5;
        this.filetimeDate = oue.filetimeDate;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public Integer getFilesize() {
        return filesize;
    }

    public void setFilesize(Integer filesize) {
        this.filesize = filesize;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public Date getFiletimeDate() {
        return filetimeDate;
    }

    public void setFiletimeDate(Date filetimeDate) {
        this.filetimeDate = filetimeDate;
    }
}
