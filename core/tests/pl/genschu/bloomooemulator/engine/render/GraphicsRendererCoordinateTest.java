package pl.genschu.bloomooemulator.engine.render;

import org.junit.jupiter.api.Test;
import pl.genschu.bloomooemulator.geometry.coordinates.CanvasRect;
import pl.genschu.bloomooemulator.geometry.coordinates.OpenGlRect;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GraphicsRendererCoordinateTest {

    @Test
    void imageDestinationUsesCanonicalCanvasBounds() {
        CanvasRect bounds = CanvasRect.fromPositionAndSize(12, 34, 20, 30);

        OpenGlRect destination = GraphicsRenderer.imageDestination(bounds);

        assertEquals(new OpenGlRect(12, 536, 20, 30), destination);
    }

    @Test
    void scissorIsClippedInCanvasSpaceBeforeConversion() {
        OpenGlRect scissor = LogicalScissor.scissorRect(
                new CanvasRect(-20, -10, 100, 50)).orElseThrow();

        assertEquals(new OpenGlRect(0, 550, 100, 50), scissor);
    }

    @Test
    void scissorAtCanvasBottomUsesOpenGlBottomOrigin() {
        OpenGlRect scissor = LogicalScissor.scissorRect(
                new CanvasRect(790, 590, 810, 610)).orElseThrow();

        assertEquals(new OpenGlRect(790, 0, 10, 10), scissor);
    }

    @Test
    void scissorRejectsRectangleOutsideCanvas() {
        assertTrue(LogicalScissor.scissorRect(
                new CanvasRect(800, 0, 810, 10)).isEmpty());
    }
}
