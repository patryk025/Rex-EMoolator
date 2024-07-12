package pl.genschu.bloomooemulator.utils;
import pl.genschu.bloomooemulator.interpreter.util.Point;
import com.badlogic.gdx.Gdx;

public class CoordinatesHelper {
    private static final float VIRTUAL_WIDTH = 800;
    private static final float VIRTUAL_HEIGHT = 600;
    private static int width = Gdx.graphics.getWidth();
    private static int height = Gdx.graphics.getHeight();
    
    private static double scale = Math.min(width/VIRTUAL_WIDTH, height/VIRTUAL_HEIGHT);
    
    public static void transformCoords(Point point) {
        point.setX(point.getX() * CoordinatesHelper.scale);
        point.setY(CoordinatesHelper.height - 1 - point.getY() * CoordinatesHelper.scale);
    }
    
    public static void updateScale(int width, int height) {
        scale = Math.min(width/VIRTUAL_WIDTH, height/VIRTUAL_HEIGHT);
    }
}