package pl.genschu.bloomooemulator.world;

public class GameObject {
    private int id;
    private float x;
    private float y;
    private float z;
    private float rotationZ;


    public GameObject(int id, float x, float y, float z, float rotationZ) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.z = z;
        this.rotationZ = rotationZ;
    }
}
