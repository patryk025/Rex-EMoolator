package pl.genschu.bloomooemulator.geometry.coordinates;

/** Camera/background displacement expressed along the DirectDraw canvas axes. */
public record CanvasScroll(double x, double y) {
    public static final CanvasScroll NONE = new CanvasScroll(0.0, 0.0);
}
