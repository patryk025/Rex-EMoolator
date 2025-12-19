package pl.genschu.bloomooemulator.geometry.shapes;

import pl.genschu.bloomooemulator.geometry.points.Point3D;

public class Box3D {
    private Point3D min; // left bottom front
    private Point3D max; // right top back

    public Box3D(Point3D min, Point3D max) {
        this.min = min;
        this.max = max;
    }

    public double getWidth() {
        return max.x - min.x;
    }

    public double getHeight() {
        return max.y - min.y;
    }

    public double getDepth() {
        return max.z - min.z;
    }

    public boolean contains(Point3D p) {
        return p.x >= min.x && p.x <= max.x &&
                p.y >= min.y && p.y <= max.y &&
                p.z >= min.z && p.z <= max.z;
    }

    public boolean intersects(Box3D other) {
        return this.max.x > other.min.x && this.min.x < other.max.x &&
                this.max.y > other.min.y && this.min.y < other.max.y &&
                this.max.z > other.min.z && this.min.z < other.max.z;
    }

    public Point3D getMin() {
        return min;
    }

    public Point3D getMax() {
        return max;
    }

    public double volume() {
        return getWidth() * getHeight() * getDepth();
    }

    @Override
    public String toString() {
        return "Box3D[min=(" + min.x + "," + min.y + "," + min.z +
                "), max=(" + max.x + "," + max.y + "," + max.z + ")]";
    }
}

