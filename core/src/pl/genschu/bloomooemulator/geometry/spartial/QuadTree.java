package pl.genschu.bloomooemulator.geometry.spartial;

import pl.genschu.bloomooemulator.engine.context.EngineVariable;
import pl.genschu.bloomooemulator.geometry.shapes.Box2D;

import java.util.ArrayList;
import java.util.List;

public class QuadTree {
    private final int MAX_OBJECTS = 10;
    private final int MAX_LEVELS = 5;

    private final int level;
    private final List<EngineVariable> objects;
    private final Box2D bounds;
    private final QuadTree[] nodes;

    public QuadTree(int level, Box2D bounds) {
        this.level = level;
        this.objects = new ArrayList<>();
        this.bounds = bounds;
        this.nodes = new QuadTree[4];
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
        int subWidth = bounds.getWidth() / 2;
        int subHeight = bounds.getHeight() / 2;
        int x = bounds.getXLeft();
        int y = bounds.getYBottom();

        nodes[0] = new QuadTree(level + 1, new Box2D(x + subWidth, y, subWidth, subHeight));
        nodes[1] = new QuadTree(level + 1, new Box2D(x, y, subWidth, subHeight));
        nodes[2] = new QuadTree(level + 1, new Box2D(x, y + subHeight, subWidth, subHeight));
        nodes[3] = new QuadTree(level + 1, new Box2D(x + subWidth, y + subHeight, subWidth, subHeight));
    }

    private int getIndex(Box2D rect) {
        int index = -1;

        if(rect == null) return index;

        double verticalMidpoint = bounds.getXLeft() + (bounds.getWidth() / 2.0);
        double horizontalMidpoint = bounds.getYBottom() + (bounds.getHeight() / 2.0);

        boolean topQuadrant = (rect.getYTop() < horizontalMidpoint);
        boolean bottomQuadrant = (rect.getYBottom() > horizontalMidpoint);

        if (rect.getXRight() < verticalMidpoint) {
            if (topQuadrant) {
                index = 1;
            } else if (bottomQuadrant) {
                index = 2;
            }
        } else if (rect.getXLeft() > verticalMidpoint) {
            if (topQuadrant) {
                index = 0;
            } else if (bottomQuadrant) {
                index = 3;
            }
        }

        return index;
    }

    public void insert(EngineVariable obj) {
        if (nodes[0] != null) {
            int index = getIndex(getRect(obj));

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
                int index = getIndex(getRect(objects.get(i)));
                if (index != -1) {
                    nodes[index].insert(objects.remove(i));
                } else {
                    i++;
                }
            }
        }
    }

    public List<EngineVariable> retrieve(List<EngineVariable> returnObjects, EngineVariable obj) {
        int index = getIndex(getRect(obj));
        if (index != -1 && nodes[0] != null) {
            nodes[index].retrieve(returnObjects, obj);
        }

        returnObjects.addAll(objects);

        return returnObjects;
    }

    public void remove(EngineVariable obj) {
        if (nodes[0] != null) {
            int index = getIndex(getRect(obj));
            if (index != -1) {
                nodes[index].remove(obj);
                return;
            }
        }

        objects.remove(obj);
    }

    private Box2D getRect(EngineVariable obj) {
        // v2 types
        if (obj instanceof pl.genschu.bloomooemulator.interpreter.variable.ImageVariable img) {
            return img.getRect();
        } else if (obj instanceof pl.genschu.bloomooemulator.interpreter.variable.AnimoVariable animo) {
            return animo.getRect();
        }
        // v1 types
        if (obj instanceof pl.genschu.bloomooemulator.interpreter.v1.variable.types.ImageVariable v1Img) {
            return v1Img.getRect();
        } else if (obj instanceof pl.genschu.bloomooemulator.interpreter.v1.variable.types.AnimoVariable v1Animo) {
            return v1Animo.getRect();
        }
        return null;
    }
}
