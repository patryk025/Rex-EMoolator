package pl.genschu.bloomooemulator.geometry.points;

public class Point3D implements Cloneable {
    public double x, y, z;

    public Point3D(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public Point3D clone() {
        try {
            return (Point3D) super.clone();
        } catch (CloneNotSupportedException e) {
            // This should never happen since we implement Cloneable
            throw new AssertionError();
        }
    }

    public float distance(Point3D point) {
        return (float) Math.sqrt((x - point.x) * (x - point.x) + (y - point.y) * (y - point.y) + (z - point.z) * (z - point.z));
    }
}
