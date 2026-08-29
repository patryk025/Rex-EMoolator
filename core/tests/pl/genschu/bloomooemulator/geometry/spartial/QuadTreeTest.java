package pl.genschu.bloomooemulator.geometry.spartial;

import org.junit.jupiter.api.Test;
import pl.genschu.bloomooemulator.engine.context.EngineVariable;
import pl.genschu.bloomooemulator.geometry.coordinates.CanvasRect;
import pl.genschu.bloomooemulator.interpreter.variable.ImageVariable;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

class QuadTreeTest {
    @Test
    void retrievalContainsEveryActuallyIntersectingObjectAfterSubdivision() {
        QuadTree tree = new QuadTree(0, new CanvasRect(0, 0, 100, 100));
        List<ImageVariable> objects = new ArrayList<>();
        for (int y = 2; y < 100; y += 20) {
            for (int x = 2; x < 100; x += 20) {
                ImageVariable image = image("I_" + x + "_" + y,
                        new CanvasRect(x, y, Math.min(x + 12, 100), Math.min(y + 12, 100)));
                objects.add(image);
                tree.insert(image);
            }
        }

        List<CanvasRect> queries = List.of(
                new CanvasRect(45, 45, 80, 80),
                new CanvasRect(50, 0, 60, 100),
                new CanvasRect(0, 50, 100, 60),
                new CanvasRect(0, 0, 100, 100));

        for (CanvasRect queryRect : queries) {
            ImageVariable query = image("QUERY", queryRect);
            List<EngineVariable> candidates = tree.retrieve(new ArrayList<>(), query);
            List<ImageVariable> expected = objects.stream()
                    .filter(candidate -> candidate.getRect().intersects(queryRect))
                    .toList();

            assertTrue(candidates.containsAll(expected),
                    () -> "QuadTree missed an intersection for query " + queryRect);
        }
    }

    @Test
    void removeFindsObjectInItsInsertionBranchAfterItMoves() {
        QuadTree tree = new QuadTree(0, new CanvasRect(0, 0, 100, 100));
        ImageVariable moving = image("MOVING", new CanvasRect(5, 5, 10, 10));
        tree.insert(moving);
        for (int i = 0; i < 10; i++) {
            tree.insert(image("FILLER_" + i, new CanvasRect(60 + i, 60, 61 + i, 61)));
        }

        moving.state().rect = new CanvasRect(80, 80, 90, 90);
        tree.remove(moving);

        List<EngineVariable> oldBranch = tree.retrieve(
                new ArrayList<>(), image("QUERY", new CanvasRect(0, 0, 20, 20)));
        assertFalse(oldBranch.contains(moving));
    }

    private static ImageVariable image(String name, CanvasRect rect) {
        ImageVariable image = new ImageVariable(name);
        image.state().rect = rect;
        return image;
    }
}
