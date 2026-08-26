package pl.genschu.bloomooemulator.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.TextureData;
import pl.genschu.bloomooemulator.engine.context.CanvasBoundsProvider;
import pl.genschu.bloomooemulator.engine.context.CurrentImageProvider;
import pl.genschu.bloomooemulator.engine.context.EngineVariable;
import pl.genschu.bloomooemulator.geometry.coordinates.CanvasRect;
import pl.genschu.bloomooemulator.interpreter.variable.AnimoVariable;
import pl.genschu.bloomooemulator.interpreter.variable.ImageVariable;
import pl.genschu.bloomooemulator.objects.Image;

import java.util.Optional;

public class CollisionChecker {

    public static boolean checkCollision(EngineVariable obj1, EngineVariable obj2) {
        if (!(obj1 instanceof CanvasBoundsProvider bounds1)
                || !(obj2 instanceof CanvasBoundsProvider bounds2)) {
            return false;
        }
        CanvasRect rect1 = bounds1.getCanvasBounds();
        CanvasRect rect2 = bounds2.getCanvasBounds();

        if (rect1 == null || rect2 == null) {
            return false;
        }
        Optional<CanvasRect> intersection = rect1.intersection(rect2);
        if (intersection.isEmpty()) return false;

        boolean checkAlpha1 = isCheckingAlpha(obj1);
        boolean checkAlpha2 = isCheckingAlpha(obj2);

        if (!checkAlpha1 && !checkAlpha2) {
            return true;
        }

        return checkPixelPerfectCollision(
                obj1, obj2, rect1, rect2, intersection.orElseThrow(), checkAlpha1, checkAlpha2);
    }

    private static boolean checkPixelPerfectCollision(EngineVariable obj1, EngineVariable obj2,
                                                      CanvasRect rect1, CanvasRect rect2,
                                                      CanvasRect intersection,
                                                      boolean checkAlpha1, boolean checkAlpha2) {
        Image image1 = obj1 instanceof CurrentImageProvider imageProvider
                ? imageProvider.getCurrentImage()
                : null;
        Image image2 = obj2 instanceof CurrentImageProvider imageProvider
                ? imageProvider.getCurrentImage()
                : null;

        if (image1 == null || image2 == null ||
                image1.getImageTexture() == null || image2.getImageTexture() == null) {
            return true;
        }

        for (int y = intersection.top(); y < intersection.bottom(); y++) {
            for (int x = intersection.left(); x < intersection.right(); x++) {
                int alpha1 = getAlphaAtPoint(
                        image1, x - rect1.left(), y - rect1.top(), checkAlpha1);
                int alpha2 = getAlphaAtPoint(
                        image2, x - rect2.left(), y - rect2.top(), checkAlpha2);

                if (alpha1 > 0 && alpha2 > 0) {
                    return true;
                }
            }
        }

        return false;
    }

    private static int getAlphaAtPoint(Image image, int x, int y, boolean checkAlpha) {
        if (!checkAlpha) {
            return 255;
        }

        try {
            TextureData textureData = image.getImageTexture().getTextureData();
            if (!textureData.isPrepared()) {
                textureData.prepare();
            }
            Pixmap pixmap = textureData.consumePixmap();

            if (x < 0 || y < 0 || x >= pixmap.getWidth() || y >= pixmap.getHeight()) {
                return 0;
            }

            Color color = new Color(pixmap.getPixel(x, y));
            return (int)(color.a * 255);
        } catch (Exception e) {
            Gdx.app.error("CollisionChecker", "Error reading alpha: " + e.getMessage());
            return 255;
        }
    }

    private static boolean isCheckingAlpha(EngineVariable variable) {
        if (variable instanceof ImageVariable img) {
            return img.state().monitorCollisionAlpha;
        }
        if (variable instanceof AnimoVariable animo) {
            return animo.isMonitorCollisionAlpha();
        }
        return false;
    }
}
