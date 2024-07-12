package pl.genschu.bloomooemulator.utils;
import pl.genschu.bloomooemulator.interpreter.util.Point;
import com.badlogic.gdx.Gdx;

public class CoordinatesHelper {
    private static final float VIRTUAL_WIDTH = 800;
    private static final float VIRTUAL_HEIGHT = 600;
    
    public static void reverseY(Point point) {
        point.setY(CoordinatesHelper.VIRTUAL_HEIGHT - 1 - point.getY());
    }
}