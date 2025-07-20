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
    private Mesh mesh;
    private boolean rigidBody = false;

    public GameObject() {
        this.id = -1;
        this.x = 0f;
        this.y = 0f;
        this.z = 0f;
        this.rotationZ = 0f;
        this.mesh = null;
        this.rigidBody = false;
    }

    public GameObject(int id, float x, float y, float z, float rotationZ, boolean rigidBody) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.z = z;
        this.rotationZ = rotationZ;
        this.mesh = null;
        this.rigidBody = rigidBody;
    }

    public void addProp(EntityProp prop) {
        this.props.add(prop);
    }

    public void setMesh(Mesh mesh) {
        this.mesh = mesh;
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

    public Mesh getMesh() {
        return mesh;
    }

    public boolean isRigidBody() {
        return rigidBody;
    }

    public void setRigidBody(boolean rigidBody) {
        this.rigidBody = rigidBody;
    }
}
