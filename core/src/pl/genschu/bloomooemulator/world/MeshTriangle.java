package pl.genschu.bloomooemulator.world;

public class MeshTriangle {
    private final String material; // ?
    private final TriangleVertex[] vertices;
    private final float unknown, unknown2, unknown3;

    public MeshTriangle(String material, TriangleVertex[] vertices, float unknown, float unknown2, float unknown3) {
        this.material = material;
        this.vertices = vertices;
        this.unknown = unknown;
        this.unknown2 = unknown2;
        this.unknown3 = unknown3;
    }

    public String getMaterial() {
        return material;
    }

    public TriangleVertex[] getVertices() {
        return vertices;
    }
}
