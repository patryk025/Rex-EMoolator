package pl.genschu.bloomooemulator.engine.physics.pathfinding;

import pl.genschu.bloomooemulator.geometry.points.Point3D;

import java.util.Objects;

public final class Node {
    private final int id;
    private final Point3D pos;
    private boolean walkable;

    public Node(int id, Point3D pos) {
        this.id = id;
        this.pos = pos;
        this.walkable = true;
    }

    public int id() {
        return id;
    }

    public Point3D pos() {
        return pos;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node node = (Node) o;
        return id == node.id && Objects.equals(pos, node.pos);
    }

    public boolean walkable() { return walkable; }

    @Override
    public int hashCode() {
        return Objects.hash(id, pos);
    }

    @Override
    public String toString() {
        return "Node{" +
                "id=" + id +
                ", pos=" + pos +
                '}';
    }
}
