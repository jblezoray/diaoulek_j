package fr.jblezoray.diaoulek.data.model.lessonelement.qrcouple;

public class SoundReference {
    private String soundFileName;
    private Integer soundBeginIndex;
    private Integer soundEndIndex;

    public String getSoundFileName() {
        return soundFileName;
    }

    public void setSoundFileName(String soundFileName) {
        this.soundFileName = soundFileName;
    }

    public Integer getSoundBeginIndex() {
        return soundBeginIndex;
    }

    public void setSoundBeginIndex(Integer soundBeginIndex) {
        this.soundBeginIndex = soundBeginIndex;
    }

    public Integer getSoundEndIndex() {
        return soundEndIndex;
    }

    public void setSoundEndIndex(Integer soundEndIndex) {
        this.soundEndIndex = soundEndIndex;
    }
}
