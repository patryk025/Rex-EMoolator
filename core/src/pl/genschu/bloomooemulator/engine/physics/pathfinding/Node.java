package pl.genschu.bloomooemulator.engine.physics.pathfinding;

import pl.genschu.bloomooemulator.geometry.points.Point3D;

import java.util.Objects;

public final class Node {
    private final int id;
    private final Point3D pos;
    private final int tag;
    private boolean active;

    public Node(int id, Point3D pos) {
        this(id, pos, 0);
    }

    public Node(int id, Point3D pos, int tag) {
        this.id = id;
        this.pos = pos;
        this.tag = tag;
        this.active = true;
    }

    public int id() {
        return id;
    }

    public Point3D pos() {
        return pos;
    }

    public int tag() {
        return tag;
    }

    public boolean active() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node node = (Node) o;
        return id == node.id && Objects.equals(pos, node.pos);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, pos);
    }

    @Override
    public String toString() {
        return "Node{" +
                "id=" + id +
                ", pos=" + pos +
                ", tag=" + tag +
                ", active=" + active +
                '}';
    }
}
