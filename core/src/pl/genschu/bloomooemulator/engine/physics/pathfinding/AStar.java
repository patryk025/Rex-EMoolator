package pl.genschu.bloomooemulator.engine.physics.pathfinding;

import pl.genschu.bloomooemulator.geometry.points.Point3D;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Route search over a SEK waypoint graph, ported from Sekai.dll (path finder at
 * {@code 0x10001400} in both Reksio i Czarodzieje and Reksio i Wehikuł Czasu).
 *
 * <p>The port is deliberately literal. Waypoints lie on a regular grid, so equal distances and
 * equal path costs are the norm, and which of the tied candidates wins is decided by details:
 * values kept as 32-bit floats, strict comparisons, and the order of the open list.
 */
public class AStar {
    // Sekai's heuristic is "10 * (1 - cos(angle to a preferred direction)) + distance", but the
    // preferred direction is always the zero vector, which leaves a constant.
    private static final float DIRECTION_TERM = 10.0f;

    private final Graph g;

    public AStar(Graph g) { this.g = g; }

    public Graph graph() { return g; }

    /**
     * Finds a route and writes it to {@code outPath}.
     *
     * <p>{@code outPath} is cleared only once both end nodes are known; when either position has
     * no usable node the previous route is left in place, as in the original.
     *
     * @param appendTarget append the exact target after the last node (also used on its own
     *                     when no route exists, sending the object straight to the target)
     * @return {@code false} when no route exists
     */
    public boolean findPath(Point3D startPos, Point3D targetPos, boolean appendTarget,
                            Collection<Point3D> outPath) {
        Point3D start = toFloat(startPos);
        Point3D target = toFloat(targetPos);

        int startNode = findNearestActiveNode(start);
        int goalNode = findNearestActiveNode(target);
        if (startNode < 0 || goalNode < 0) return false;

        outPath.clear();

        Point3D startNodePos = g.node(startNode).pos();
        Point3D goalNodePos = g.node(goalNode).pos();
        if (samePosition(startNodePos, goalNodePos)) {
            return true; // already there: an empty route, reported as ATGOAL
        }

        // A goal next to the start skips the search, and the start node itself.
        for (Arc arc : g.neighbors(startNode)) {
            if (arc.to() == goalNode && g.node(goalNode).active()) {
                outPath.add(goalNodePos);
                if (appendTarget && !samePosition(goalNodePos, target)) {
                    outPath.add(target);
                }
                return true;
            }
        }

        List<Integer> nodePath = search(startNode, goalNode);
        if (nodePath == null) {
            if (appendTarget) {
                outPath.add(target);
            }
            return false;
        }

        for (int id : nodePath) {
            outPath.add(g.node(id).pos());
        }
        if (appendTarget && !samePosition(goalNodePos, target)) {
            outPath.add(target);
        }
        return true;
    }

    /** @return node ids from start to goal (both included), or {@code null} when unreachable */
    private List<Integer> search(int startId, int goalId) {
        int n = g.size();
        float[] gScore = new float[n];
        float[] fScore = new float[n];
        int[] parent = new int[n];
        boolean[] open = new boolean[n];
        boolean[] closed = new boolean[n];
        Arrays.fill(parent, -1);

        Point3D goalPos = g.node(goalId).pos();

        // Ascending by f; a new entry goes behind entries with the same f.
        List<Integer> openList = new ArrayList<>();

        fScore[startId] = (float) (DIRECTION_TERM + distance(g.node(startId).pos(), goalPos));
        int current = startId;

        while (current != goalId) {
            Node currentNode = g.node(current);
            // An inactive node may still be the end of a route, but nothing leads out of it.
            if (currentNode.active()) {
                for (Arc arc : g.neighbors(current)) {
                    int nb = arc.to();
                    Point3D nbPos = g.node(nb).pos();

                    if (parent[current] >= 0 && samePosition(g.node(parent[current]).pos(), nbPos)) {
                        continue; // never step straight back
                    }

                    double tentative = (double) arc.cost() + (double) gScore[current];
                    if ((closed[nb] || open[nb]) && !(tentative < gScore[nb])) {
                        continue;
                    }

                    parent[nb] = current;
                    gScore[nb] = (float) tentative;
                    float h = (float) (DIRECTION_TERM + distance(nbPos, goalPos));
                    fScore[nb] = (float) ((double) h + (double) gScore[nb]);
                    closed[nb] = false;
                    // A node already waiting keeps its place in the list despite the better cost.
                    if (!open[nb]) {
                        open[nb] = true;
                        insertSorted(openList, nb, fScore);
                    }
                }
            }
            closed[current] = true;

            if (openList.isEmpty()) {
                return null;
            }
            current = openList.remove(0);
            open[current] = false;
        }

        List<Integer> path = new ArrayList<>();
        for (int id = current; id >= 0; id = parent[id]) {
            path.add(id);
        }
        Collections.reverse(path);
        return path;
    }

    private static void insertSorted(List<Integer> openList, int node, float[] fScore) {
        for (int i = 0; i < openList.size(); i++) {
            if (fScore[openList.get(i)] > fScore[node]) {
                openList.add(i, node);
                return;
            }
        }
        openList.add(node);
    }

    /**
     * Nearest active node, with the original's quirks: the running minimum starts at the
     * distance to node 0 even when that node is inactive (so nothing farther can win), and is
     * kept as a 32-bit float while each candidate is compared at full precision — on an exact
     * tie the float rounding decides whether the first or the last candidate is taken.
     */
    private int findNearestActiveNode(Point3D pos) {
        if (g.size() < 1) return -1;

        float best = (float) distance(pos, g.node(0).pos());
        int result = -1;

        for (int i = 0; i < g.size(); i++) {
            Node node = g.node(i);
            if (!node.active()) continue;
            if (i == 0) {
                result = 0;
                continue;
            }
            double dist = distance(pos, node.pos());
            if (dist < best) {
                best = (float) dist;
                result = i;
            }
        }
        return result;
    }

    /** Distance as Sekai computes it: 32-bit operands, double-precision arithmetic. */
    static double distance(Point3D a, Point3D b) {
        double dx = (double) (float) a.x - (double) (float) b.x;
        double dy = (double) (float) a.y - (double) (float) b.y;
        double dz = (double) (float) a.z - (double) (float) b.z;
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

    private static boolean samePosition(Point3D a, Point3D b) {
        return (float) a.x == (float) b.x && (float) a.y == (float) b.y && (float) a.z == (float) b.z;
    }

    private static Point3D toFloat(Point3D p) {
        return new Point3D((float) p.x, (float) p.y, (float) p.z);
    }
}
