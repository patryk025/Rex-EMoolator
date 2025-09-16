package pl.genschu.bloomooemulator.engine.physics.pathfinding;

import java.util.Objects;

public final class Arc {
    private final int to;
    private final float cost;
    private final int flags;

    public Arc(int to, float cost, int flags) {
        this.to = to;
        this.cost = cost;
        this.flags = flags;
    }

    public int to() {
        return to;
    }

    public float cost() {
        return cost;
    }

    public int flags() {
        return flags;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Arc arc = (Arc) o;
        return to == arc.to && 
               Float.compare(arc.cost, cost) == 0 && 
               flags == arc.flags;
    }

    @Override
    public int hashCode() {
        return Objects.hash(to, cost, flags);
    }

    @Override
    public String toString() {
        return "Arc{" +
                "to=" + to +
                ", cost=" + cost +
                ", flags=" + flags +
                '}';
    }
}
