package pl.genschu.bloomooemulator.world;

import pl.genschu.bloomooemulator.geometry.points.Point3D;

import java.util.ArrayList;
import java.util.List;

public class PointsData {
    private final List<Point3D> points;
    private final List<Integer> tags;
    private final List<Edge> edges;

    public PointsData() {
        points = new ArrayList<>();
        tags = new ArrayList<>();
        edges = new ArrayList<>();
    }

    public void addPoint(Point3D point) {
        addPoint(point, 0);
    }

    public void addPoint(Point3D point, int tag) {
        points.add(point);
        tags.add(tag);
    }

    public void addPath(int firstIdx, int secondIdx, int unknown) {
        if (firstIdx < 0 || firstIdx >= points.size() || secondIdx < 0 || secondIdx >= points.size()) {
            throw new IndexOutOfBoundsException("Invalid point index for path.");
        }
        edges.add(new Edge(firstIdx, secondIdx, unknown));
    }

    public List<Point3D> getPoints() {
        return points;
    }

    public List<Integer> getTags() {
        return tags;
    }

    public List<Edge> getEdges() {
        return edges;
    }
}
