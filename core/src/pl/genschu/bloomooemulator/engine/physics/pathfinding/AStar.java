package pl.genschu.bloomooemulator.engine.physics.pathfinding;

import pl.genschu.bloomooemulator.geometry.points.Point3D;

import java.util.*;

public class AStar {
    private final Graph g;

    public AStar(Graph g) { this.g = g; }

    public List<Integer> findPath(int startId, int goalId) {
        int n = g.size();
        float[] gScore = new float[n];
        float[] fScore = new float[n];
        int[] parent = new int[n];
        Arrays.fill(gScore, Float.POSITIVE_INFINITY);
        Arrays.fill(fScore, Float.POSITIVE_INFINITY);
        Arrays.fill(parent, -1);

        Comparator<Integer> byF = Comparator.comparingDouble(i -> fScore[i]);
        PriorityQueue<Integer> open = new PriorityQueue<>(byF);
        boolean[] inOpen = new boolean[n];
        boolean[] closed = new boolean[n];

        gScore[startId] = 0f;
        fScore[startId] = heuristic(startId, goalId);
        open.add(startId);
        inOpen[startId] = true;

        while (!open.isEmpty()) {
            int current = open.poll();
            inOpen[current] = false;
            if (current == goalId) return reconstruct(parent, current);

            closed[current] = true;
            for (Arc arc : g.neighbors(current)) {
                int nb = arc.to();
                if (closed[nb]) continue;

                float tentative = gScore[current] + arc.cost();
                if (tentative < gScore[nb]) {
                    parent[nb] = current;
                    gScore[nb] = tentative;
                    fScore[nb] = tentative + heuristic(nb, goalId);
                    if (!inOpen[nb]) {
                        open.add(nb);
                        inOpen[nb] = true;
                    }
                }
            }
        }
        return List.of();
    }

    private float heuristic(int a, int b) {
        Point3D pa = g.node(a).pos();
        Point3D pb = g.node(b).pos();
        double dx = pa.x - pb.x;
        double dz = pa.z - pb.z;
        return (float)Math.hypot(dx, dz);
    }

    private List<Integer> reconstruct(int[] parent, int cur) {
        ArrayList<Integer> out = new ArrayList<>();
        while (cur >= 0) { out.add(cur); cur = parent[cur]; }
        Collections.reverse(out);
        return out;
    }
}

