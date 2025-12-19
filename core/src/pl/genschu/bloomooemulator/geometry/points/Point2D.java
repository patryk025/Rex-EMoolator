package pl.genschu.bloomooemulator.geometry.points;

public class Point2D implements Cloneable {
    public double x, y;

    public Point2D(double x, double y) {
        this.x = x;
        this.y = y;
    }
    
    @Override
    public Point2D clone() {
        try {
            return (Point2D) super.clone();
        } catch (CloneNotSupportedException e) {
            // This should never happen since we implement Cloneable
            throw new AssertionError();
        }
    }
}
