package pl.genschu.bloomooemulator.utils;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import pl.genschu.bloomooemulator.TestEnvironment;
import pl.genschu.bloomooemulator.geometry.coordinates.CanvasRect;
import pl.genschu.bloomooemulator.interpreter.variable.ImageVariable;
import pl.genschu.bloomooemulator.objects.Image;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CollisionCheckerTest {
    private final List<Image> imagesToDispose = new ArrayList<>();

    @BeforeAll
    static void boot() {
        TestEnvironment.init();
    }

    @AfterEach
    void disposeImages() {
        for (Image image : imagesToDispose) {
            if (image.getImageTexture() != null) image.getImageTexture().dispose();
        }
    }

    @Test
    void detectsOverlapBetweenSpritesWithUnequalHeights() {
        ImageVariable tall = graphic("TALL", new CanvasRect(0, 0, 100, 100));
        ImageVariable shortGraphic = graphic("SHORT", new CanvasRect(0, 90, 100, 100));

        assertTrue(CollisionChecker.checkCollision(tall, shortGraphic));
    }

    @Test
    void touchingExclusiveEdgesDoNotCollide() {
        ImageVariable left = graphic("LEFT", new CanvasRect(0, 0, 10, 10));
        ImageVariable right = graphic("RIGHT", new CanvasRect(10, 0, 20, 10));

        assertFalse(CollisionChecker.checkCollision(left, right));
    }

    @Test
    void pixelPerfectCollisionConvertsCanvasIntersectionToEachLocalRaster() {
        ImageVariable first = graphicWithPixels(
                "FIRST", 0, 0, 2, 2, new byte[]{0, 0, 0, (byte) 255});
        ImageVariable second = graphicWithPixels(
                "SECOND", 1, 1, 1, 1, new byte[]{(byte) 255});
        first.state().monitorCollisionAlpha = true;
        second.state().monitorCollisionAlpha = true;

        assertTrue(CollisionChecker.checkCollision(first, second));
    }

    private static ImageVariable graphic(String name, CanvasRect rect) {
        ImageVariable image = new ImageVariable(name);
        image.state().rect = rect;
        return image;
    }

    private ImageVariable graphicWithPixels(
            String name,
            int x,
            int y,
            int width,
            int height,
            byte[] alpha
    ) {
        Image pixels = new Image(
                width, height, 0, 0, 16, new byte[width * height * 2], alpha, 0);
        imagesToDispose.add(pixels);
        ImageVariable image = new ImageVariable(name);
        image.state().image = pixels;
        image.state().posX = x;
        image.state().posY = y;
        image.state().updateRect();
        return image;
    }
}
