package pl.cba.genszu.amcodetranslator;

public class CoordsTransform {
    private int xOriginal = 800;
    private int yOriginal = 600;

    /*TODO: get resolution of screen*/
    private int xDest = 1280;
    private int yDest = 720;

    private float scaleRate = yDest/yOriginal;

    public int[] transformCoords(int x, int y) {
        return new int[]{(int) (x*scaleRate), (int) (y*scaleRate)};
    }
}
