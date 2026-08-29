package pl.genschu.bloomooemulator.geometry.coordinates;

/**
 * A point in the script-visible DirectDraw canvas coordinate system.
 * X grows to the right and Y grows down from the top-left corner.
 */
public record CanvasPoint(double x, double y) {
    public CanvasPoint translated(double dx, double dy) {
        return new CanvasPoint(x + dx, y + dy);
    }
}
