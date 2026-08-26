package pl.genschu.bloomooemulator.engine.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import pl.genschu.bloomooemulator.TestEnvironment;
import pl.genschu.bloomooemulator.engine.Game;
import pl.genschu.bloomooemulator.engine.config.EngineConfig;
import pl.genschu.bloomooemulator.geometry.coordinates.CanvasCoordinateSystem;
import pl.genschu.bloomooemulator.geometry.coordinates.CanvasPoint;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class InputManagerCoordinateTest {
    private Graphics originalGraphics;

    @BeforeAll
    static void initialiseLibGdx() {
        TestEnvironment.init();
    }

    @AfterEach
    void restoreGraphics() {
        if (originalGraphics != null) {
            Gdx.graphics = originalGraphics;
        }
    }

    @Test
    void fitViewportMapsContentAndRejectsLetterboxBars() {
        InputManager input = inputManager(
                new FitViewport(
                        CanvasCoordinateSystem.WIDTH,
                        CanvasCoordinateSystem.HEIGHT,
                        new OrthographicCamera()),
                1600,
                900);

        assertPoint(input.getCorrectedMouseCoords(200, 0), 0, 0);
        assertPoint(input.getCorrectedMouseCoords(800, 450), 400, 300);
        assertTrue(input.getCorrectedMouseCoords(199, 450).isEmpty());
        assertTrue(input.getCorrectedMouseCoords(1400, 450).isEmpty());
    }

    @Test
    void stretchViewportUsesIndependentHorizontalAndVerticalScale() {
        InputManager input = inputManager(
                new StretchViewport(
                        CanvasCoordinateSystem.WIDTH,
                        CanvasCoordinateSystem.HEIGHT,
                        new OrthographicCamera()),
                1600,
                900);

        assertPoint(input.getCorrectedMouseCoords(0, 0), 0, 0);
        assertPoint(input.getCorrectedMouseCoords(800, 450), 400, 300);
        assertPoint(input.getCorrectedMouseCoords(1599, 899), 799.5, 599.333333);
    }

    @Test
    void fitViewportRejectsTopAndBottomBarsWithTopLeftScreenCoordinates() {
        InputManager input = inputManager(
                new FitViewport(
                        CanvasCoordinateSystem.WIDTH,
                        CanvasCoordinateSystem.HEIGHT,
                        new OrthographicCamera()),
                900,
                1600);

        assertTrue(input.getCorrectedMouseCoords(450, 462).isEmpty());
        assertPoint(input.getCorrectedMouseCoords(450, 463), 400, 0);
        assertPoint(input.getCorrectedMouseCoords(450, 1137), 400, 599.111111);
        assertTrue(input.getCorrectedMouseCoords(450, 1138).isEmpty());
    }

    private InputManager inputManager(Viewport viewport, int width, int height) {
        originalGraphics = Gdx.graphics;
        Graphics graphics = mock(Graphics.class);
        when(graphics.getWidth()).thenReturn(width);
        when(graphics.getHeight()).thenReturn(height);
        when(graphics.getBackBufferWidth()).thenReturn(width);
        when(graphics.getBackBufferHeight()).thenReturn(height);
        Gdx.graphics = graphics;

        viewport.update(width, height, true);
        return new InputManager(viewport, mock(Game.class), new EngineConfig());
    }

    private static void assertPoint(
            Optional<CanvasPoint> actual,
            double expectedX,
            double expectedY
    ) {
        CanvasPoint point = actual.orElseThrow();
        assertEquals(expectedX, point.x(), 0.0001);
        assertEquals(expectedY, point.y(), 0.0001);
    }
}
