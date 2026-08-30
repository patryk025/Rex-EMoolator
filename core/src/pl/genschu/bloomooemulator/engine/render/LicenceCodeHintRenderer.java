package pl.genschu.bloomooemulator.engine.render;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Disposable;
import pl.genschu.bloomooemulator.engine.context.GameContext;
import pl.genschu.bloomooemulator.geometry.coordinates.CanvasCoordinateSystem;
import pl.genschu.bloomooemulator.objects.Image;

import java.util.Optional;

/** Draws a compact strip made from the scene's original picture-code symbols. */
final class LicenceCodeHintRenderer implements Disposable {
    private static final float PANEL_TOP = 18f;
    private static final float PANEL_RIGHT = 18f;
    private static final float PANEL_HEIGHT = 68f;
    private static final float PANEL_PADDING = 10f;
    private static final float SYMBOL_BOX = 48f;
    private static final float SEPARATOR_GAP = 14f;
    private static final float BORDER = 2f;

    private static final Color PANEL_COLOR = new Color(0.035f, 0.035f, 0.035f, 0.78f);
    private static final Color BORDER_COLOR = new Color(0.93f, 0.82f, 0.52f, 0.95f);
    private static final Color SEPARATOR_COLOR = new Color(0.93f, 0.82f, 0.52f, 0.55f);

    private final SpriteBatch batch;
    private final Texture pixel;
    private GameContext cachedContext;
    private Optional<LicenceCodeHintResolver.Hint> cachedHint = Optional.empty();

    LicenceCodeHintRenderer(SpriteBatch batch) {
        this.batch = batch;
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        try {
            pixmap.setColor(Color.WHITE);
            pixmap.fill();
            pixel = new Texture(pixmap);
        } finally {
            pixmap.dispose();
        }
    }

    void render(GameContext context) {
        if (context != cachedContext) {
            cachedContext = context;
            cachedHint = LicenceCodeHintResolver.resolve(context);
        }
        cachedHint.ifPresent(this::renderHint);
    }

    void clear() {
        cachedContext = null;
        cachedHint = Optional.empty();
    }

    private void renderHint(LicenceCodeHintResolver.Hint hint) {
        float panelWidth = PANEL_PADDING * 2f
                + SYMBOL_BOX * hint.symbols().size()
                + SEPARATOR_GAP * (hint.symbols().size() - 1);
        float panelX = CanvasCoordinateSystem.WIDTH - PANEL_RIGHT - panelWidth;
        float panelY = CanvasCoordinateSystem.HEIGHT - PANEL_TOP - PANEL_HEIGHT;

        drawPanel(panelX, panelY, panelWidth);

        float slotX = panelX + PANEL_PADDING;
        for (int i = 0; i < hint.symbols().size(); i++) {
            drawSymbol(hint.symbols().get(i), slotX, panelY);
            slotX += SYMBOL_BOX;
            if (i + 1 < hint.symbols().size()) {
                float separatorX = slotX + SEPARATOR_GAP / 2f - 1f;
                batch.setColor(SEPARATOR_COLOR);
                batch.draw(pixel, separatorX, panelY + 16f, 2f, PANEL_HEIGHT - 32f);
                slotX += SEPARATOR_GAP;
            }
        }
        batch.setColor(Color.WHITE);
    }

    private void drawPanel(float x, float y, float width) {
        batch.setColor(PANEL_COLOR);
        batch.draw(pixel, x, y, width, PANEL_HEIGHT);

        batch.setColor(BORDER_COLOR);
        batch.draw(pixel, x, y, width, BORDER);
        batch.draw(pixel, x, y + PANEL_HEIGHT - BORDER, width, BORDER);
        batch.draw(pixel, x, y, BORDER, PANEL_HEIGHT);
        batch.draw(pixel, x + width - BORDER, y, BORDER, PANEL_HEIGHT);
    }

    private void drawSymbol(Image image, float slotX, float panelY) {
        if (image == null || image.getImageTexture() == null || image.width <= 0 || image.height <= 0) {
            return;
        }
        float scale = Math.min(SYMBOL_BOX / image.width, SYMBOL_BOX / image.height);
        float width = image.width * scale;
        float height = image.height * scale;
        float x = slotX + (SYMBOL_BOX - width) / 2f;
        float y = panelY + (PANEL_HEIGHT - height) / 2f;

        batch.setColor(Color.WHITE);
        batch.draw(image.getImageTexture(), x, y, width, height);
    }

    @Override
    public void dispose() {
        pixel.dispose();
    }
}
