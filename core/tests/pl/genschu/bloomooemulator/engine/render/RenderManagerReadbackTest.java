package pl.genschu.bloomooemulator.engine.render;

import com.badlogic.gdx.graphics.Pixmap;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import pl.genschu.bloomooemulator.TestEnvironment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RenderManagerReadbackTest {

    @BeforeAll
    static void initialiseLibGdx() {
        TestEnvironment.init();
    }

    @Test
    void convertsOpenGlBottomUpRowsToTopDownCanvasRows() {
        Pixmap readback = new Pixmap(2, 3, Pixmap.Format.RGBA8888);
        Pixmap canvas = new Pixmap(2, 3, Pixmap.Format.RGB565);
        try {
            // glReadPixels writes the framebuffer's bottom row first.
            readback.drawPixel(0, 0, 0xff0000ff);
            readback.drawPixel(1, 0, 0xffff00ff);
            readback.drawPixel(0, 1, 0x00ff00ff);
            readback.drawPixel(1, 1, 0xff00ffff);
            readback.drawPixel(0, 2, 0x0000ffff);
            readback.drawPixel(1, 2, 0xffffffff);

            RenderManager.copyOpenGlReadbackToCanvas(readback, canvas);

            assertEquals(0x0000ffff, canvas.getPixel(0, 0));
            assertEquals(0xffffffff, canvas.getPixel(1, 0));
            assertEquals(0x00ff00ff, canvas.getPixel(0, 1));
            assertEquals(0xff00ffff, canvas.getPixel(1, 1));
            assertEquals(0xff0000ff, canvas.getPixel(0, 2));
            assertEquals(0xffff00ff, canvas.getPixel(1, 2));
        } finally {
            canvas.dispose();
            readback.dispose();
        }
    }

    @Test
    void rejectsMismatchedReadbackDimensions() {
        Pixmap readback = new Pixmap(2, 3, Pixmap.Format.RGBA8888);
        Pixmap canvas = new Pixmap(2, 2, Pixmap.Format.RGB565);
        try {
            assertThrows(
                    IllegalArgumentException.class,
                    () -> RenderManager.copyOpenGlReadbackToCanvas(readback, canvas));
        } finally {
            canvas.dispose();
            readback.dispose();
        }
    }
}
