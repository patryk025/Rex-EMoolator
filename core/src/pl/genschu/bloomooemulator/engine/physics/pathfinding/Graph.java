package pl.genschu.bloomooemulator.engine.physics.pathfinding;

import pl.genschu.bloomooemulator.geometry.points.Point3D;
import pl.genschu.bloomooemulator.world.Edge;
import pl.genschu.bloomooemulator.world.PointsData;

import java.util.ArrayList;
import java.util.List;

public class Graph {
    private final List<Node> nodes;
    private final List<List<Arc>> adj;

    public Graph(List<Node> nodes, List<List<Arc>> adj) {
        this.nodes = nodes; this.adj = adj;
    }

    public List<Arc> neighbors(int id) { return adj.get(id); }
    public Node node(int id) { return nodes.get(id); }
    public int size() { return nodes.size(); }

    public static Graph fromPointsData(PointsData pd, boolean undirected) {
        final int n = pd.getPoints().size();
        List<Node> nodes = new ArrayList<>(n);
        for (int i = 0; i < n; i++) nodes.add(new Node(i, pd.getPoints().get(i)));

        List<List<Arc>> adj = new ArrayList<>(n);
        for (int i = 0; i < n; i++) adj.add(new ArrayList<>());

        for (Edge e : pd.getEdges()) {
            float cost = pd.getPoints().get(e.from()).distance(pd.getPoints().get(e.to()));

            boolean fromFirstToSecond = (e.flags() & 2) != 0 || undirected;
            boolean fromSecondToFirst = (e.flags() & 1) != 0 || undirected;

            if(fromFirstToSecond) {
                adj.get(e.from()).add(new Arc(e.to(), cost, e.flags()));
            }
            if (fromSecondToFirst) {
                adj.get(e.to()).add(new Arc(e.from(), cost, e.flags()));
            }
        }
        return new Graph(nodes, adj);
    }
}
