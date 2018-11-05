package fr.jblezoray.diaoulek.data.model;

import com.google.common.base.Objects;

public class LessonCategory implements Comparable<LessonCategory> {

    private String filenameRegExp;
    private String key;
    private String fullName;
    private String description;
    private Long count;

    public LessonCategory(String filenameRegExp, String key, String fullName,
                          String description) {
        this.filenameRegExp = filenameRegExp;
        this.key = key;
        this.fullName = fullName;
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LessonCategory that = (LessonCategory) o;
        return Objects.equal(filenameRegExp, that.filenameRegExp) &&
                Objects.equal(key, that.key) &&
                Objects.equal(fullName, that.fullName) &&
                Objects.equal(description, that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(filenameRegExp, key, fullName, description);
    }

    @Override
    public int compareTo(LessonCategory o) {
        return this.key.compareTo(o.key);
    }

    public String getFilenameRegExp() {
        return filenameRegExp;
    }

    public String getKey() {
        return key;
    }

    public String getFullName() {
        return fullName;
    }

    public String getDescription() {
        return description;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public Long getCount() {
        return count;
    }
}
