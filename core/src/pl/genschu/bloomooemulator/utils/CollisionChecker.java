package pl.genschu.bloomooemulator.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.TextureData;
import pl.genschu.bloomooemulator.engine.context.EngineVariable;
import pl.genschu.bloomooemulator.geometry.shapes.Box2D;
import pl.genschu.bloomooemulator.interpreter.variable.AnimoVariable;
import pl.genschu.bloomooemulator.interpreter.variable.ImageVariable;
import pl.genschu.bloomooemulator.objects.Image;

public class CollisionChecker {

    public static boolean checkCollision(EngineVariable obj1, EngineVariable obj2) {
        Box2D rect1 = getRect(obj1);
        Box2D rect2 = getRect(obj2);

        if (rect1 == null || rect2 == null) {
            return false;
        }

        if (!rect1.intersects(rect2)) {
            return false;
        }

        boolean checkAlpha1 = isCheckingAlpha(obj1);
        boolean checkAlpha2 = isCheckingAlpha(obj2);

        if (!checkAlpha1 && !checkAlpha2) {
            return true;
        }

        return checkPixelPerfectCollision(obj1, obj2, rect1, rect2, checkAlpha1, checkAlpha2);
    }

    private static boolean checkPixelPerfectCollision(EngineVariable obj1, EngineVariable obj2,
                                                      Box2D rect1, Box2D rect2,
                                                      boolean checkAlpha1, boolean checkAlpha2) {
        int left = Math.max(rect1.getXLeft(), rect2.getXLeft());
        int right = Math.min(rect1.getXRight(), rect2.getXRight());
        int top = Math.min(rect1.getYTop(), rect2.getYTop());
        int bottom = Math.max(rect1.getYBottom(), rect2.getYBottom());

        Image image1 = getImage(obj1);
        Image image2 = getImage(obj2);

        if (image1 == null || image2 == null ||
                image1.getImageTexture() == null || image2.getImageTexture() == null) {
            return true;
        }

        for (int y = bottom; y <= top; y++) {
            for (int x = left; x <= right; x++) {
                int alpha1 = getAlphaAtPoint(image1, x - rect1.getXLeft(), y - rect1.getYBottom(), checkAlpha1);
                int alpha2 = getAlphaAtPoint(image2, x - rect2.getXLeft(), y - rect2.getYBottom(), checkAlpha2);

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

    public static Box2D getRect(EngineVariable variable) {
        if (variable instanceof ImageVariable img) {
            return img.getRect();
        }
        if (variable instanceof AnimoVariable animo) {
            return animo.getRect();
        }
        return null;
    }

    public static Image getImage(EngineVariable variable) {
        if (variable instanceof ImageVariable img) {
            return img.getImage();
        }
        if (variable instanceof AnimoVariable animo) {
            return animo.getCurrentImage();
        }
        return null;
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
