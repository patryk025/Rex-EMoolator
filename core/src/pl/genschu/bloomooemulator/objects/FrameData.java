package pl.genschu.bloomooemulator.objects;

import java.util.List;

public class FrameData {
    private byte[] startingBytes; // i don't know what this is for
    private int offsetX;
    private int offsetY;
    private int sfxRandomSeed; // i guess?
    private int opacity;
    private String name;
    private List<String> sfxAudio;

    public byte[] getStartingBytes() {
        return startingBytes;
    }

    public void setStartingBytes(byte[] startingBytes) {
        this.startingBytes = startingBytes;
    }

    public int getOffsetX() {
        return offsetX;
    }

    public void setOffsetX(int offsetX) {
        this.offsetX = offsetX;
    }

    public int getOffsetY() {
        return offsetY;
    }

    public void setOffsetY(int offsetY) {
        this.offsetY = offsetY;
    }

    public int getSfxRandomSeed() {
        return sfxRandomSeed;
    }

    public void setSfxRandomSeed(int sfxRandomSeed) {
        this.sfxRandomSeed = sfxRandomSeed;
    }

    public int getOpacity() {
        return opacity;
    }

    public void setOpacity(int opacity) {
        this.opacity = opacity;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getSfxAudio() {
        return sfxAudio;
    }

    public void setSfxAudio(List<String> sfxAudio) {
        this.sfxAudio = sfxAudio;
    }
}
