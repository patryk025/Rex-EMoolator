package pl.genschu.bloomooemulator.geometry.spartial;

import pl.genschu.bloomooemulator.geometry.points.Point3D;
import pl.genschu.bloomooemulator.geometry.shapes.Box3D;
import pl.genschu.bloomooemulator.interpreter.variable.Variable;

import java.util.ArrayList;
import java.util.List;

public class OctTree {
    private static final int MAX_OBJECTS = 10;
    private static final int MAX_LEVELS = 5;

    private final int level;
    private final List<Variable> objects;
    private final Box3D bounds;
    private final OctTree[] nodes;

    public OctTree(int level, Box3D bounds) {
        this.level = level;
        this.objects = new ArrayList<>();
        this.bounds = bounds;
        this.nodes = new OctTree[8];
    }

    public void clear() {
        objects.clear();
        for (int i = 0; i < nodes.length; i++) {
            if (nodes[i] != null) {
                nodes[i].clear();
                nodes[i] = null;
            }
        }
    }

    private void split() {
        int dx = (int) (bounds.getWidth() / 2);
        int dy = (int) (bounds.getHeight() / 2);
        int dz = (int) (bounds.getDepth() / 2);

        int x = (int) bounds.getMin().x;
        int y = (int) bounds.getMin().y;
        int z = (int) bounds.getMin().z;

        nodes[0] = new OctTree(level + 1, new Box3D(new Point3D(x + dx, y + dy, z + dz), new Point3D(x + 2 * dx, y + 2 * dy, z + 2 * dz))); // Północ-wschód-góra
        nodes[1] = new OctTree(level + 1, new Box3D(new Point3D(x, y + dy, z + dz), new Point3D(x + dx, y + 2 * dy, z + 2 * dz))); // Północ-zachód-góra
        nodes[2] = new OctTree(level + 1, new Box3D(new Point3D(x, y, z + dz), new Point3D(x + dx, y + dy, z + 2 * dz))); // Południe-zachód-góra
        nodes[3] = new OctTree(level + 1, new Box3D(new Point3D(x + dx, y, z + dz), new Point3D(x + 2 * dx, y + dy, z + 2 * dz))); // Południe-wschód-góra
        nodes[4] = new OctTree(level + 1, new Box3D(new Point3D(x + dx, y + dy, z), new Point3D(x + 2 * dx, y + 2 * dy, z + dz))); // Północ-wschód-dół
        nodes[5] = new OctTree(level + 1, new Box3D(new Point3D(x, y + dy, z), new Point3D(x + dx, y + 2 * dy, z + dz))); // Północ-zachód-dół
        nodes[6] = new OctTree(level + 1, new Box3D(new Point3D(x, y, z), new Point3D(x + dx, y + dy, z + dz))); // Południe-zachód-dół
        nodes[7] = new OctTree(level + 1, new Box3D(new Point3D(x + dx, y, z), new Point3D(x + 2 * dx, y + dy, z + dz))); // Południe-wschód-dół
    }

    private int getIndex(Box3D box) {
        if (box == null) return -1;

        int midX = (int) (bounds.getMin().x + bounds.getWidth() / 2);
        int midY = (int) (bounds.getMin().y + bounds.getHeight() / 2);
        int midZ = (int) (bounds.getMin().z + bounds.getDepth() / 2);

        boolean left = box.getMax().x <= midX;
        boolean right = box.getMin().x >= midX;
        boolean bottom = box.getMax().y <= midY;
        boolean top = box.getMin().y >= midY;
        boolean back = box.getMax().z <= midZ;
        boolean front = box.getMin().z >= midZ;

        if (left) {
            if (bottom) {
                if (back) return 6;
                else if (front) return 2;
            } else if (top) {
                if (back) return 5;
                else if (front) return 1;
            }
        } else if (right) {
            if (bottom) {
                if (back) return 7;
                else if (front) return 3;
            } else if (top) {
                if (back) return 4;
                else if (front) return 0;
            }
        }

        return -1; // nie mieści się w jednym oktancie
    }

    public void insert(Variable obj) {
        if (nodes[0] != null) {
            int index = getIndex(getBox(obj));

            if (index != -1) {
                nodes[index].insert(obj);
                return;
            }
        }

        objects.add(obj);

        if (objects.size() > MAX_OBJECTS && level < MAX_LEVELS) {
            if (nodes[0] == null) {
                split();
            }

            int i = 0;
            while (i < objects.size()) {
                int index = getIndex(getBox(objects.get(i)));
                if (index != -1) {
                    nodes[index].insert(objects.remove(i));
                } else {
                    i++;
                }
            }
        }
    }

    public List<Variable> retrieve(List<Variable> returnObjects, Variable obj) {
        int index = getIndex(getBox(obj));
        if (index != -1 && nodes[0] != null) {
            nodes[index].retrieve(returnObjects, obj);
        }

        returnObjects.addAll(objects);

        return returnObjects;
    }

    public void remove(Variable obj) {
        if (nodes[0] != null) {
            int index = getIndex(getBox(obj));
            if (index != -1) {
                nodes[index].remove(obj);
                return;
            }
        }

        objects.remove(obj);
    }

    private Box3D getBox(Variable obj) {
        // TODO: current doesn't have working 3D
        return null;
    }
}
