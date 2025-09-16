package pl.genschu.bloomooemulator.world;

import pl.genschu.bloomooemulator.geometry.points.Point3D;

public class Edge {
    private final int from;
    private final int to;
    private final int flags; // ?, bitwise?

    public Edge(int from, int to, int flags) {
        this.from = from;
        this.to = to;
        this.flags = flags;
    }

    public int from() {
        return from;
    }

    public int to() {
        return to;
    }

    public int flags() {
        return flags;
    }
}
