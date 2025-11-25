package pl.genschu.bloomooemulator.engine.physics.pathfinding;

import com.badlogic.gdx.Gdx;
import pl.genschu.bloomooemulator.geometry.points.Point3D;

import java.util.*;

public class AStar {
    private final Graph g;

    public AStar(Graph g) { this.g = g; }

    public List<Point3D> findPath(Point3D startPos, Point3D goalPos) {
        int startNode = findNearestWalkableNode(startPos);
        int goalNode = findNearestWalkableNode(goalPos);

        if (startNode < 0 || goalNode < 0) return List.of();

        List<Integer> path = findPathInternal(startNode, goalNode);
        List<Point3D> result = new ArrayList<>();
        for (Integer id : path) {
            result.add(g.node(id).pos());
        }
        return result;
    }

    private List<Integer> findPathInternal(int startId, int goalId) {
        int n = g.size();
        float[] gScore = new float[n];
        float[] fScore = new float[n];
        int[] parent = new int[n];
        Arrays.fill(gScore, Float.POSITIVE_INFINITY);
        Arrays.fill(fScore, Float.POSITIVE_INFINITY);
        Arrays.fill(parent, -1);

        PriorityQueue<Integer> open = new PriorityQueue<>(
                Comparator.comparingDouble(i -> fScore[i])
        );
        boolean[] closed = new boolean[n];

        gScore[startId] = 0f;
        fScore[startId] = heuristic(startId, goalId, -1);
        open.add(startId);

        while (!open.isEmpty()) {
            int current = open.poll();
            if (current == goalId) return reconstruct(parent, current);

            closed[current] = true;

            for (Arc arc : g.neighbors(current)) {
                int nb = arc.to();
                if (closed[nb]) continue;

                float tentative = gScore[current] + arc.cost();
                if (tentative < gScore[nb]) {
                    parent[nb] = current;
                    gScore[nb] = tentative;
                    fScore[nb] = tentative + heuristic(nb, goalId, current);
                    open.add(nb);
                }
            }
        }
        Gdx.app.log("AStar", "No path found.");
        return List.of();
    }

    private int findNearestWalkableNode(Point3D pos) {
        int best = -1;
        float bestDist = Float.POSITIVE_INFINITY;

        for (int i = 0; i < g.size(); i++) {
            Node n = g.node(i);
            if (!n.walkable()) continue;

            float dist = pos.distance(n.pos());
            if (dist < bestDist) {
                bestDist = dist;
                best = i;
            }
        }
        return best;
    }

    private float heuristic(int nodeId, int goalId, int parentId) {
        Point3D curr = g.node(nodeId).pos();
        Point3D goal = g.node(goalId).pos();

        float dist = curr.distance(goal);
        float h = dist * 10.0f;

        if (parentId >= 0) {
            Point3D parent = g.node(parentId).pos();

            Point3D moveDir = curr.subtract(parent).normalize();
            Point3D goalDir = goal.subtract(curr).normalize();

            float cosine = moveDir.dot(goalDir);
            h -= cosine;
        }

        return h;
    }

    private List<Integer> reconstruct(int[] parent, int cur) {
        Gdx.app.log("AStar", "Found path. Reconstructing...");
        ArrayList<Integer> out = new ArrayList<>();
        while (cur >= 0) { out.add(cur); cur = parent[cur]; }
        Collections.reverse(out);
        Gdx.app.log("AStar", "Path reconstructed: " + out);
        return out;
    }
}

