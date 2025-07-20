package pl.genschu.bloomooemulator.world;

import pl.genschu.bloomooemulator.geometry.points.Point3D;

import java.util.ArrayList;
import java.util.List;

public class PointsData {
    private final List<Point3D> points;
    private final List<Path> paths;

    public PointsData() {
        points = new ArrayList<>();
        paths = new ArrayList<>();
    }

    public void addPoint(Point3D point) {
        points.add(point);
    }

    public void addPath(int firstIdx, int secondIdx, int unknown) {
        if (firstIdx < 0 || firstIdx >= points.size() || secondIdx < 0 || secondIdx >= points.size()) {
            throw new IndexOutOfBoundsException("Invalid point index for path.");
        }
        Point3D firstPoint = points.get(firstIdx - 1); // Assuming 1-based index
        Point3D secondPoint = points.get(secondIdx - 1);
        paths.add(new Path(firstPoint, secondPoint, unknown));
    }
}
