package pl.genschu.bloomooemulator.world;

import pl.genschu.bloomooemulator.geometry.points.Point3D;

public class TriangleVertex {
    private final Point3D point;
    private final Point3D normal;
    private final float u, v; // ?

    public TriangleVertex(Point3D point, Point3D normal, float u, float v) {
        this.point = point;
        this.normal = normal;
        this.u = u;
        this.v = v;
    }

    public Point3D getPoint() {
        return point;
    }

    public Point3D getNormal() {
        return normal;
    }

    public float getU() {
        return u;
    }

    public float getV() {
        return v;
    }
}
