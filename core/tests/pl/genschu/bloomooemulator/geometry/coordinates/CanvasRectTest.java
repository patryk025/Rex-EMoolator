package pl.genschu.bloomooemulator.geometry.coordinates;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CanvasRectTest {
    @Test
    void usesDirectDrawTopLeftEdgesWithoutYAxisCompensation() {
        CanvasRect rect = new CanvasRect(10, 20, 30, 40);

        assertEquals(20, rect.width());
        assertEquals(20, rect.height());
        assertTrue(rect.contains(10, 20));
        assertTrue(rect.contains(29, 39));
        assertFalse(rect.contains(10, 19));
        assertFalse(rect.contains(30, 20));
        assertFalse(rect.contains(10, 40));
    }

    @Test
    void recognizesRectanglesFullyContainedByItsEdges() {
        CanvasRect outer = new CanvasRect(0, 0, 100, 100);

        assertTrue(outer.contains(new CanvasRect(0, 0, 100, 100)));
        assertTrue(outer.contains(new CanvasRect(10, 20, 30, 40)));
        assertFalse(outer.contains(new CanvasRect(-1, 20, 30, 40)));
        assertFalse(outer.contains(new CanvasRect(10, 20, 101, 40)));
    }

    @Test
    void rejectsInvertedEdgesInsteadOfGuessingTheirCoordinateSystem() {
        assertThrows(IllegalArgumentException.class, () -> new CanvasRect(0, 20, 10, 10));
        assertThrows(IllegalArgumentException.class, () -> new CanvasRect(20, 0, 10, 10));
    }

    @Test
    void centralizesIntersectionAndIouCalculations() {
        CanvasRect first = new CanvasRect(0, 0, 20, 20);
        CanvasRect second = new CanvasRect(10, 10, 30, 30);

        assertEquals(new CanvasRect(10, 10, 20, 20), first.intersection(second).orElseThrow());
        assertEquals(100L, first.intersectionArea(second));
        assertEquals(100.0 / 7.0, first.intersectionOverUnionPercent(second), 0.0001);
        assertFalse(first.intersects(new CanvasRect(20, 0, 30, 20)),
                "touching exclusive edges must not count as an overlap");
    }
}
