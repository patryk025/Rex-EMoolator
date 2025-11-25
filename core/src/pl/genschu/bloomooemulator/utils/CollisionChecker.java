package pl.genschu.bloomooemulator.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.TextureData;
import pl.genschu.bloomooemulator.interpreter.variable.Attribute;
import pl.genschu.bloomooemulator.interpreter.variable.Variable;
import pl.genschu.bloomooemulator.interpreter.variable.types.AnimoVariable;
import pl.genschu.bloomooemulator.interpreter.variable.types.ImageVariable;
import pl.genschu.bloomooemulator.objects.Image;
import pl.genschu.bloomooemulator.geometry.shapes.Box2D;

/**
 * Klasa pomocnicza do sprawdzania kolizji między obiektami gry.
 */
public class CollisionChecker {

    /**
     * Sprawdza kolizję między dwoma obiektami gry.
     *
     * @param obj1 Pierwszy obiekt
     * @param obj2 Drugi obiekt
     * @return true, jeśli obiekty kolidują, false w przeciwnym wypadku
     */
    public static boolean checkCollision(Variable obj1, Variable obj2) {
        Box2D rect1 = getRect(obj1);
        Box2D rect2 = getRect(obj2);

        if (rect1 == null || rect2 == null) {
            return false;
        }

        // Najpierw sprawdź kolizję prostokątów (szybki test)
        if (!rect1.intersects(rect2)) {
            return false;
        }

        // Sprawdź, czy uwzględniamy kanał alfa
        boolean checkAlpha1 = isCheckingAlpha(obj1);
        boolean checkAlpha2 = isCheckingAlpha(obj2);

        // Jeśli oba obiekty nie sprawdzają alfy, wystarczy kolizja prostokątów
        if (!checkAlpha1 && !checkAlpha2) {
            return true;
        }

        // Dokładniejsze sprawdzenie kolizji z uwzględnieniem kanału alfa
        return checkPixelPerfectCollision(obj1, obj2, rect1, rect2, checkAlpha1, checkAlpha2);
    }

    /**
     * Sprawdza kolizję piksel po pikselu między dwoma obiektami.
     */
    private static boolean checkPixelPerfectCollision(Variable obj1, Variable obj2,
                                                      Box2D rect1, Box2D rect2,
                                                      boolean checkAlpha1, boolean checkAlpha2) {
        // Oblicz prostokąt przecięcia
        int left = Math.max(rect1.getXLeft(), rect2.getXLeft());
        int right = Math.min(rect1.getXRight(), rect2.getXRight());
        int top = Math.min(rect1.getYTop(), rect2.getYTop());
        int bottom = Math.max(rect1.getYBottom(), rect2.getYBottom());

        // Pobierz obrazy
        Image image1 = getImage(obj1);
        Image image2 = getImage(obj2);

        if (image1 == null || image2 == null ||
                image1.getImageTexture() == null || image2.getImageTexture() == null) {
            return true; // Jeśli brak obrazów, zakładamy kolizję
        }

        // Sprawdź piksel po pikselu obszar przecięcia
        for (int y = bottom; y <= top; y++) {
            for (int x = left; x <= right; x++) {
                int alpha1 = getAlphaAtPoint(image1, x - rect1.getXLeft(), y - rect1.getYBottom(), checkAlpha1);
                int alpha2 = getAlphaAtPoint(image2, x - rect2.getXLeft(), y - rect2.getYBottom(), checkAlpha2);

                // Jeśli oba piksele są nieprzezroczyste, mamy kolizję
                if (alpha1 > 0 && alpha2 > 0) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Pobiera wartość kanału alfa dla punktu w obrazie.
     */
    private static int getAlphaAtPoint(Image image, int x, int y, boolean checkAlpha) {
        if (!checkAlpha) {
            return 255; // Jeśli nie sprawdzamy alfy, zakładamy pełną nieprzezroczystość
        }

        try {
            TextureData textureData = image.getImageTexture().getTextureData();
            if (!textureData.isPrepared()) {
                textureData.prepare();
            }
            Pixmap pixmap = textureData.consumePixmap();

            // Sprawdź, czy punkt jest w zakresie obrazu
            if (x < 0 || y < 0 || x >= pixmap.getWidth() || y >= pixmap.getHeight()) {
                return 0; // Punkt poza obrazem
            }

            // Pobierz wartość alfy dla piksela
            Color color = new Color(pixmap.getPixel(x, y));
            return (int)(color.a * 255);
        } catch (Exception e) {
            Gdx.app.error("CollisionChecker", "Błąd podczas pobierania alfy: " + e.getMessage());
            return 255; // W przypadku błędu zakładamy pełną nieprzezroczystość
        }
    }

    /**
     * Pobiera prostokąt ograniczający obiekt.
     */
    public static Box2D getRect(Variable variable) {
        if (variable instanceof ImageVariable) {
            return ((ImageVariable) variable).getRect();
        } else if (variable instanceof AnimoVariable) {
            return ((AnimoVariable) variable).getRect();
        }
        return null;
    }

    /**
     * Pobiera obraz dla obiektu.
     */
    public static Image getImage(Variable variable) {
        if (variable instanceof ImageVariable) {
            return ((ImageVariable) variable).getImage();
        } else if (variable instanceof AnimoVariable) {
            return ((AnimoVariable) variable).getCurrentImage();
        }
        return null;
    }

    /**
     * Sprawdza, czy obiekt ma włączone sprawdzanie kanału alfa przy kolizjach.
     */
    private static boolean isCheckingAlpha(Variable variable) {
        Attribute attribute = variable.getAttribute("MONITORCOLLISIONALPHA");
        if (attribute != null) {
            return attribute.getValue().toString().equals("TRUE");
        }
        return false;
    }

    /**
     * Sprawdza, czy dwa prostokąty kolidują.
     */
    public static boolean rectanglesCollide(Box2D rect1, Box2D rect2) {
        return rect1.getXLeft() < rect2.getXRight() &&
                rect1.getXRight() > rect2.getXLeft() &&
                rect1.getYTop() > rect2.getYBottom() &&
                rect1.getYBottom() < rect2.getYTop();
    }

    /**
     * Oblicza procentowy wskaźnik przecięcia dwóch prostokątów (IoU).
     *
     * @param rect1 Pierwszy prostokąt
     * @param rect2 Drugi prostokąt
     * @return Wartość IoU (Intersection over Union) w zakresie 0-100
     */
    public static double calculateIoU(Box2D rect1, Box2D rect2) {
        int intersectionX = Math.max(rect1.getXLeft(), rect2.getXLeft());
        int intersectionY = Math.max(rect1.getYBottom(), rect2.getYBottom());
        int intersectionWidth = Math.min(rect1.getXRight(), rect2.getXRight()) - intersectionX;
        int intersectionHeight = Math.min(rect1.getYTop(), rect2.getYTop()) - intersectionY;

        if (intersectionWidth <= 0 || intersectionHeight <= 0) {
            return 0; // Brak przecięcia
        }

        int intersectionArea = intersectionWidth * intersectionHeight;
        int rect1Area = rect1.area();
        int rect2Area = rect2.area();
        int unionArea = rect1Area + rect2Area - intersectionArea;

        return (double) intersectionArea / unionArea * 100;
    }
}