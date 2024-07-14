package pl.genschu.bloomooemulator.utils;
import pl.genschu.bloomooemulator.interpreter.util.Point;
import com.badlogic.gdx.Gdx;

public class CoordinatesHelper {
    private static final int VIRTUAL_WIDTH = 800;
    private static final int VIRTUAL_HEIGHT = 600;
    
    public static int reverseY(Point point) {
        return CoordinatesHelper.VIRTUAL_HEIGHT - 1 - point.getY();
    }
}