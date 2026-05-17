package pl.genschu.bloomooemulator.engine.render;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Disposable;

/**
 * Snapshot of a graphics object pasted onto the background by CANVAS_OBSERVER.PASTE.
 * In the original engine PASTE blits the object onto the background pixmap; we approximate
 * it by re-rendering an immutable snapshot between the background and scene objects.
 */
public final class PastedGraphic implements Disposable {
    private final Texture texture;
    private final int x;
    private final int y;
    private final int width;
    private final int height;
    private final float opacity;

    public PastedGraphic(Texture texture, int x, int y, int width, int height, float opacity) {
        this.texture = texture;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.opacity = opacity;
    }

    public Texture texture() { return texture; }
    public int x() { return x; }
    public int y() { return y; }
    public int width() { return width; }
    public int height() { return height; }
    public float opacity() { return opacity; }

    @Override
    public void dispose() {
        if (texture != null) texture.dispose();
    }
}
