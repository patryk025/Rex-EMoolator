package pl.genschu.bloomooemulator.objects;

import java.util.List;

// It's part of Animo class
public class Event {
    private String name;
    private int framesCount;
    private int loopStart; // start index in loop (usually 0)
    private int loopEnd; // end index in loop
    private int repeatCount; // max number of loops?
    private int repeatCounter; // current loop
    private int opacity;
    private List<Integer> framesNumbers;
    private List<FrameData> frameData;
    private List<Image> frames;
    private int flags; // Geez, 32 bits for flags? Really? What a waste of space


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getFramesCount() {
        return framesCount;
    }

    public void setFramesCount(int framesCount) {
        this.framesCount = framesCount;
    }

    public int getLoopStart() {
        return loopStart;
    }

    public void setLoopStart(int loopStart) {
        this.loopStart = loopStart;
    }

    public int getLoopEnd() {
        return loopEnd;
    }

    public void setLoopEnd(int loopEnd) {
        this.loopEnd = loopEnd;
    }

    public int getRepeatCount() {
        return repeatCount;
    }

    public void setRepeatCount(int repeatCount) {
        this.repeatCount = repeatCount;
    }

    public int getRepeatCounter() {
        return repeatCounter;
    }

    public void setRepeatCounter(int repeatCounter) {
        this.repeatCounter = repeatCounter;
    }

    public int getOpacity() {
        return opacity;
    }

    public void setOpacity(int opacity) {
        this.opacity = opacity;
    }

    public List<Integer> getFramesNumbers() {
        return framesNumbers;
    }

    public void setFramesNumbers(List<Integer> framesNumbers) {
        this.framesNumbers = framesNumbers;
    }

    public List<FrameData> getFrameData() {
        return frameData;
    }

    public void setFrameData(List<FrameData> frameData) {
        this.frameData = frameData;
    }

    public List<Image> getFrames() {
        return frames;
    }

    public void setFrames(List<Image> frames) {
        this.frames = frames;
    }

    public int getFlags() {
        return flags;
    }

    public void setFlags(int flags) {
        this.flags = flags;
    }
}
