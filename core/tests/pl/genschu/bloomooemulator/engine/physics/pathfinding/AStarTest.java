package pl.genschu.bloomooemulator.engine.physics.pathfinding;

import org.junit.jupiter.api.Test;
import pl.genschu.bloomooemulator.geometry.points.Point3D;
import pl.genschu.bloomooemulator.world.PointsData;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AStarTest {
    private static final int BOTH_WAYS = 3;

    /** A straight row of nodes 10 apart: (0,0), (10,0), (20,0), ... with the given tags. */
    private static AStar row(int... tags) {
        PointsData points = new PointsData();
        for (int i = 0; i < tags.length; i++) {
            points.addPoint(new Point3D(i * 10, 0, 0), tags[i]);
        }
        for (int i = 0; i + 1 < tags.length; i++) {
            points.addPath(i, i + 1, BOTH_WAYS);
        }
        return new AStar(Graph.fromPointsData(points, false));
    }

    private static List<String> route(AStar pathfinder, Point3D start, Point3D target, boolean appendTarget) {
        List<Point3D> path = new ArrayList<>();
        pathfinder.findPath(start, target, appendTarget, path);
        return describe(path);
    }

    private static List<String> describe(List<Point3D> path) {
        return path.stream().map(p -> (int) p.x + "," + (int) p.y).toList();
    }

    @Test
    void routeRunsFromTheNearestNodeOfTheStartToTheNearestNodeOfTheTarget() {
        AStar pathfinder = row(0, 0, 0, 0);

        assertEquals(List.of("0,0", "10,0", "20,0", "30,0"),
                route(pathfinder, new Point3D(1, 2, 0), new Point3D(31, 3, 0), false));
    }

    @Test
    void goalNextToTheStartNodeSkipsTheStartNode() {
        AStar pathfinder = row(0, 0, 0, 0);

        assertEquals(List.of("10,0"),
                route(pathfinder, new Point3D(1, 2, 0), new Point3D(11, 3, 0), false));
    }

    @Test
    void sharedNodeGivesAnEmptyRouteThatStillCountsAsFound() {
        AStar pathfinder = row(0, 0, 0, 0);
        List<Point3D> path = new ArrayList<>(List.of(new Point3D(99, 99, 0)));

        assertTrue(pathfinder.findPath(new Point3D(9, 1, 0), new Point3D(11, -1, 0), true, path));
        assertTrue(path.isEmpty(), "the exact target is not appended when no node has to be visited");
    }

    @Test
    void appendTargetFinishesAtTheExactTargetUnlessItIsTheNodeItself() {
        AStar pathfinder = row(0, 0, 0, 0);

        assertEquals(List.of("0,0", "10,0", "20,0", "22,4"),
                route(pathfinder, new Point3D(0, 1, 0), new Point3D(22, 4, 0), true));
        assertEquals(List.of("0,0", "10,0", "20,0"),
                route(pathfinder, new Point3D(0, 1, 0), new Point3D(20, 0, 0), true));
    }

    @Test
    void disabledTagIsNotARouteTarget_stawPondRegression() {
        // STAW: the nodes leading into the pond carry tag 10 and are switched off while it holds water.
        AStar pathfinder = row(0, 0, 10, 10);
        Point3D shore = new Point3D(0, 1, 0);
        Point3D pond = new Point3D(30, 1, 0);

        assertEquals(List.of("0,0", "10,0", "20,0", "30,0"), route(pathfinder, shore, pond, false));

        pathfinder.graph().setActiveByTag(10, false);
        assertEquals(List.of("10,0"), route(pathfinder, shore, pond, false),
                "the route must end at the last node that is still active");

        pathfinder.graph().setActiveByTag(10, true);
        assertEquals(List.of("0,0", "10,0", "20,0", "30,0"), route(pathfinder, shore, pond, false));
    }

    @Test
    void disabledNodeCannotBeCrossed() {
        AStar pathfinder = row(0, 0, 10, 0, 0);
        pathfinder.graph().setActiveByTag(10, false);
        List<Point3D> path = new ArrayList<>();

        assertFalse(pathfinder.findPath(new Point3D(0, 1, 0), new Point3D(40, 1, 0), false, path));
        assertTrue(path.isEmpty());

        // Without a route the exact target alone is still appended when requested.
        assertFalse(pathfinder.findPath(new Point3D(0, 1, 0), new Point3D(40, 1, 0), true, path));
        assertEquals(List.of("40,1"), describe(path));
    }

    @Test
    void inactiveFirstNodeStillLimitsTheSearchRadiusAndAMissKeepsTheOldRoute() {
        // Sekai seeds the running minimum with the distance to node 0 even when it is inactive.
        AStar pathfinder = row(10, 0, 0);
        pathfinder.graph().setActiveByTag(10, false);
        List<Point3D> path = new ArrayList<>(List.of(new Point3D(7, 7, 0)));

        assertFalse(pathfinder.findPath(new Point3D(0, 1, 0), new Point3D(20, 1, 0), true, path));
        assertEquals(List.of("7,7"), describe(path), "a failed node lookup must not clear the route");
    }

    @Test
    void equidistantNodesAreDecidedByTheFloatRoundingOfTheDistance() {
        // sqrt(50) rounds down as a float, so later ties are not "closer": the first node wins.
        PointsData square = new PointsData();
        square.addPoint(new Point3D(200, 200, 0)); // node 0, far away
        square.addPoint(new Point3D(-5, -5, 0));
        square.addPoint(new Point3D(5, -5, 0));
        square.addPoint(new Point3D(5, 5, 0));
        square.addPoint(new Point3D(40, 5, 0));
        square.addPoint(new Point3D(80, 5, 0));
        square.addPath(1, 4, BOTH_WAYS);
        square.addPath(2, 4, BOTH_WAYS);
        square.addPath(3, 4, BOTH_WAYS);
        square.addPath(4, 5, BOTH_WAYS);
        assertEquals(List.of("-5,-5", "40,5", "80,5"),
                route(new AStar(Graph.fromPointsData(square, false)),
                        new Point3D(0, 0, 0), new Point3D(90, 5, 0), false));

        // sqrt(5) rounds up as a float, so every later tie compares as closer: the last node wins.
        PointsData knight = new PointsData();
        knight.addPoint(new Point3D(200, 200, 0)); // node 0, far away
        knight.addPoint(new Point3D(1, 2, 0));
        knight.addPoint(new Point3D(2, 1, 0));
        knight.addPoint(new Point3D(40, 5, 0));
        knight.addPoint(new Point3D(80, 5, 0));
        knight.addPath(1, 3, BOTH_WAYS);
        knight.addPath(2, 3, BOTH_WAYS);
        knight.addPath(3, 4, BOTH_WAYS);
        assertEquals(List.of("2,1", "40,5", "80,5"),
                route(new AStar(Graph.fromPointsData(knight, false)),
                        new Point3D(0, 0, 0), new Point3D(90, 5, 0), false));
    }

    @Test
    void oneWayArcIsFollowedOnlyInItsDirection() {
        PointsData points = new PointsData();
        points.addPoint(new Point3D(0, 0, 0));
        points.addPoint(new Point3D(10, 0, 0));
        points.addPoint(new Point3D(20, 0, 0));
        points.addPath(0, 1, 2); // first -> second only
        points.addPath(1, 2, 2);
        AStar pathfinder = new AStar(Graph.fromPointsData(points, false));

        assertEquals(List.of("0,0", "10,0", "20,0"),
                route(pathfinder, new Point3D(0, 1, 0), new Point3D(20, 1, 0), false));

        List<Point3D> back = new ArrayList<>();
        assertFalse(pathfinder.findPath(new Point3D(20, 1, 0), new Point3D(0, 1, 0), false, back));
    }
}
