package pl.genschu.bloomooemulator.world;

import java.util.ArrayList;
import java.util.List;

public class GameObject {
    private int id;
    private float x;
    private float y;
    private float z;
    private float rotationZ;
    private final List<EntityProp> props = new ArrayList<>();
    private final List<Mesh> meshes = new ArrayList<>(); // one object can have multiple meshes, AABB is faster for boxes than complicated meshes

    public GameObject() {
    }

    public GameObject(int id, float x, float y, float z, float rotationZ) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.z = z;
        this.rotationZ = rotationZ;
    }

    public void addProp(EntityProp prop) {
        this.props.add(prop);
    }

    public void addMesh(Mesh mesh) {
        this.meshes.add(mesh);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getZ() {
        return z;
    }

    public void setZ(float z) {
        this.z = z;
    }

    public float getRotationZ() {
        return rotationZ;
    }

    public void setRotationZ(float rotationZ) {
        this.rotationZ = rotationZ;
    }

    public List<EntityProp> getProps() {
        return props;
    }
}
