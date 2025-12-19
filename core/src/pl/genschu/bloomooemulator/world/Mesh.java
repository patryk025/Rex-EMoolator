package pl.genschu.bloomooemulator.world;

import java.util.ArrayList;
import java.util.List;

public class Mesh {
    private final List<MeshTriangle> triangles;

    public Mesh(List<MeshTriangle> triangles) {
        this.triangles = triangles;
    }

    public List<MeshTriangle> getTriangles() {
        return triangles;
    }
}
