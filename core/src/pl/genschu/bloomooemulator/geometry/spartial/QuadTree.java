package pl.genschu.bloomooemulator.geometry.spartial;

import pl.genschu.bloomooemulator.engine.context.CanvasBoundsProvider;
import pl.genschu.bloomooemulator.engine.context.EngineVariable;
import pl.genschu.bloomooemulator.geometry.coordinates.CanvasRect;

import java.util.ArrayList;
import java.util.List;

public class QuadTree {
    private final int MAX_OBJECTS = 10;
    private final int MAX_LEVELS = 5;

    private final int level;
    private final List<EngineVariable> objects;
    private final CanvasRect bounds;
    private final QuadTree[] nodes;

    public QuadTree(int level, CanvasRect bounds) {
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
        int midpointX = bounds.left() + bounds.width() / 2;
        int midpointY = bounds.top() + bounds.height() / 2;

        nodes[0] = new QuadTree(level + 1,
                new CanvasRect(midpointX, bounds.top(), bounds.right(), midpointY));
        nodes[1] = new QuadTree(level + 1,
                new CanvasRect(bounds.left(), bounds.top(), midpointX, midpointY));
        nodes[2] = new QuadTree(level + 1,
                new CanvasRect(bounds.left(), midpointY, midpointX, bounds.bottom()));
        nodes[3] = new QuadTree(level + 1,
                new CanvasRect(midpointX, midpointY, bounds.right(), bounds.bottom()));
    }

    private int getIndex(CanvasRect rect) {
        int index = -1;

        if(rect == null) return index;

        int verticalMidpoint = bounds.left() + bounds.width() / 2;
        int horizontalMidpoint = bounds.top() + bounds.height() / 2;

        boolean topQuadrant = rect.bottom() <= horizontalMidpoint;
        boolean bottomQuadrant = rect.top() >= horizontalMidpoint;

        if (rect.right() <= verticalMidpoint) {
            if (topQuadrant) {
                index = 1;
            } else if (bottomQuadrant) {
                index = 2;
            }
        } else if (rect.left() >= verticalMidpoint) {
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
            int index = getIndex(getBounds(obj));

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
                int index = getIndex(getBounds(objects.get(i)));
                if (index != -1) {
                    nodes[index].insert(objects.remove(i));
                } else {
                    i++;
                }
            }
        }
    }

    public List<EngineVariable> retrieve(List<EngineVariable> returnObjects, EngineVariable obj) {
        CanvasRect rect = getBounds(obj);
        if (nodes[0] != null) {
            int index = getIndex(rect);
            if (index != -1) {
                nodes[index].retrieve(returnObjects, obj);
            } else if (rect != null) {
                // Rect straddles quadrants: descend into every child whose bounds it overlaps.
                for (QuadTree node : nodes) {
                    if (node != null && rect.intersects(node.bounds)) {
                        node.retrieve(returnObjects, obj);
                    }
                }
            }
        }

        returnObjects.addAll(objects);

        return returnObjects;
    }

    public void remove(EngineVariable obj) {
        if (objects.remove(obj)) return;

        // Bounds are mutable at the object level (the CanvasRect value itself is not),
        // so the object's current quadrant need not be the quadrant where it was inserted.
        for (QuadTree node : nodes) {
            if (node != null) node.remove(obj);
        }
    }

    private CanvasRect getBounds(EngineVariable obj) {
        return obj instanceof CanvasBoundsProvider boundsProvider
                ? boundsProvider.getCanvasBounds()
                : null;
    }
}
