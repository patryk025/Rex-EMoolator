package pl.genschu.bloomooemulator.world;

import pl.genschu.bloomooemulator.geometry.points.Point3D;

import java.util.ArrayList;
import java.util.List;

public class PointsData {
    private List<Point3D> points;
    private List<Waypoint> waypoints;

    public PointsData() {
        points = new ArrayList<>();
        waypoints = new ArrayList<>();
    }

    public void addPoint(Point3D point) {
        points.add(point);
    }

    public void addWaypoint(int firstIdx, int secondIdx, int direction) {
        if (firstIdx < 0 || firstIdx >= points.size() || secondIdx < 0 || secondIdx >= points.size()) {
            throw new IndexOutOfBoundsException("Invalid point index for waypoint.");
        }
        Point3D firstPoint = points.get(firstIdx - 1); // Assuming 1-based index
        Point3D secondPoint = points.get(secondIdx - 1);
        waypoints.add(new Waypoint(firstPoint, secondPoint, direction));
    }
}
