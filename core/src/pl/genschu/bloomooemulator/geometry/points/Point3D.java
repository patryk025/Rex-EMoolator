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

    public Point3D add(Point3D point) {
        return new Point3D(x + point.x, y + point.y, z + point.z);
    }
    public Point3D subtract(Point3D point) {
        return new Point3D(x - point.x, y - point.y, z - point.z);
    }
    public Point3D multiply(Point3D point) {
        return new Point3D(x * point.x, y * point.y, z * point.z);
    }
    public Point3D divide(Point3D point) {
        return new Point3D(x / point.x, y / point.y, z / point.z);
    }
    public Point3D normalize() {
        double length = Math.sqrt(x * x + y * y + z * z);
        return new Point3D(x / length, y / length, z / length);
    }
    public float dot(Point3D point) {
        return (float) (x * point.x + y * point.y + z * point.z);
    }

    public float distance(Point3D point) {
        return (float) Math.sqrt((x - point.x) * (x - point.x) + (y - point.y) * (y - point.y) + (z - point.z) * (z - point.z));
    }
}
