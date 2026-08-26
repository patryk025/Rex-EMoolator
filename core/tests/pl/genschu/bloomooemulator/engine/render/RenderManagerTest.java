package pl.genschu.bloomooemulator.engine.render;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RenderManagerTest {

    @Test
    void backgroundUsesImgOffsetAsTopLeftPosition_zeglarzeRegression() {
        // Zeglarze/BKG.IMG is 800x637 at (0, -37), so its bottom edge is
        // exactly at screen Y=600 and it must cover the whole logical canvas.
        assertEquals(0f, RenderManager.backgroundDrawX(0, 0));
        assertEquals(0f, RenderManager.backgroundDrawY(-37, 637, 0));
    }

    @Test
    void regularFullscreenBackgroundStartsAtLogicalCanvasOrigin() {
        assertEquals(0f, RenderManager.backgroundDrawY(0, 600, 0));
    }

    @Test
    void positiveBackgroundYPositionScrollsBitmapUp_skokiRegression() {
        float initialDrawY = RenderManager.backgroundDrawY(0, 1200, 0);

        assertEquals(initialDrawY + 2f, RenderManager.backgroundDrawY(0, 1200, 2));
    }

    @Test
    void positiveBackgroundXPositionScrollsBitmapLeft() {
        assertEquals(-2f, RenderManager.backgroundDrawX(0, 2));
    }
}
