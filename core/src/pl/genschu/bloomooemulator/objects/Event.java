package pl.genschu.bloomooemulator.objects;

import java.util.List;

// It's part of Animo class
public class Event {
    private String name;
    private int framesCount;
    private int loopBy;
    private int opacity;
    private List<Integer> framesNumbers;
    private List<FrameData> frameData;
    private List<Image> frames;

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

    public int getLoopBy() {
        return loopBy;
    }

    public void setLoopBy(int loopBy) {
        this.loopBy = loopBy;
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
}
