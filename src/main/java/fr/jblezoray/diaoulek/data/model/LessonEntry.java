package fr.jblezoray.diaoulek.data.model;

import fr.jblezoray.diaoulek.data.model.lessonelement.LessonElement;

import java.util.ArrayList;
import java.util.List;

public class LessonEntry extends FileIndexEntry {

    private final FileIndexEntry fileIndexEntry;

    private String alias;
    private List<LessonElement> lessonElements = new ArrayList<>();

    public LessonEntry(FileIndexEntry fileIndexEntry) {
        this.fileIndexEntry = fileIndexEntry;
    }

    public FileIndexEntry getFileIndexEntry() {
        return fileIndexEntry;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getAlias() {
        return alias;
    }

    public List<LessonElement> getLessonElements() {
        return lessonElements;
    }
}
