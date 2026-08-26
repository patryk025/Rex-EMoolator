package pl.genschu.bloomooemulator.engine.render;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Disposable;
import pl.genschu.bloomooemulator.geometry.coordinates.CanvasRect;

/**
 * Snapshot of a graphics object pasted onto the background by CANVAS_OBSERVER.PASTE.
 * In the original engine PASTE blits the object onto the background pixmap; we approximate
 * it by re-rendering an immutable snapshot between the background and scene objects.
 */
public final class PastedGraphic implements Disposable {
    private final Texture texture;
    private final CanvasRect bounds;
    private final float opacity;

    public PastedGraphic(Texture texture, CanvasRect bounds, float opacity) {
        this.texture = texture;
        this.bounds = bounds;
        this.opacity = opacity;
    }

    public Texture texture() { return texture; }
    public CanvasRect bounds() { return bounds; }
    public float opacity() { return opacity; }

    @Override
    public void dispose() {
        if (texture != null) texture.dispose();
    }
}
