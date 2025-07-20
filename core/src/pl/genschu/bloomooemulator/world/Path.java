package pl.genschu.bloomooemulator.world;

import pl.genschu.bloomooemulator.geometry.points.Point3D;

public class Path {
    private final Point3D firstPoint;
    private final Point3D secondPoint;
    private final int direction; // ?, bitwise?

    public Path(Point3D firstPoint, Point3D secondPoint, int direction) {
        this.firstPoint = firstPoint;
        this.secondPoint = secondPoint;
        this.direction = direction;
    }

    public Point3D getFirstPoint() {
        return firstPoint;
    }

    public Point3D getSecondPoint() {
        return secondPoint;
    }

    public int getDirection() {
        return direction;
    }
}
