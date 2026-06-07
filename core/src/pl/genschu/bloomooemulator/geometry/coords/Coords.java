package pl.genschu.bloomooemulator.geometry.coords;

/**
 * Single source of truth for the emulated engine's coordinate spaces and the
 * conversions between them.
 *
 * <p>The original engine rendered through DirectDraw, which is <b>y-down with the
 * origin in the top-left corner</b>. libGDX / OpenGL is <b>y-up with the origin in
 * the bottom-left corner</b>. On top of that the physics/camera layer uses its own
 * convention. Historically the flip {@code VIRTUAL_HEIGHT - yTop - height} was copy-pasted
 * across the renderer, debug overlay, input and collision code, and the {@code 800x600}
 * constants were redefined in five different classes. This class collapses all of that
 * into one place.</p>
 *
 * <h2>Coordinate spaces</h2>
 * <table>
 *   <tr><th>Space</th><th>Convention</th><th>Used by</th></tr>
 *   <tr><td>SCRIPT / DirectDraw</td><td>y-down, origin top-left</td>
 *       <td>.cnv scripts, {@code Box2D} (getYTop/getYBottom) &mdash; the semantic source of truth</td></tr>
 *   <tr><td>GL / render</td><td>y-up, origin bottom-left</td>
 *       <td>{@code batch.draw(...)}, scissor rects</td></tr>
 *   <tr><td>PHYSICS / camera</td><td>y-up, centred on the screen middle</td>
 *       <td>{@code CameraAnchor} &mdash; converted explicitly at its boundary, not via these helpers</td></tr>
 *   <tr><td>SCREEN / window</td><td>y-down, raw window pixels</td>
 *       <td>{@code Gdx.input.getX/getY} before viewport correction</td></tr>
 *   <tr><td>TEXTURE-LOCAL</td><td>y-down within a single image</td>
 *       <td>{@code CollisionChecker}, GETALPHA/GETPIXEL pixel lookups</td></tr>
 * </table>
 *
 * <p><b>Rule of thumb:</b> never write a bare {@code VIRTUAL_HEIGHT - y} again. Pick the
 * named conversion that says which space you start in and which you end up in, so the
 * intent survives the next refactor.</p>
 */
public final class Coords {
    /** Width of the fixed virtual canvas the original engine renders into. */
    public static final float VIRTUAL_WIDTH = 800f;
    /** Height of the fixed virtual canvas the original engine renders into. */
    public static final float VIRTUAL_HEIGHT = 600f;

    private Coords() {}

    /**
     * Flips a single Y coordinate between SCRIPT (y-down, top-left origin) and GL
     * (y-up, bottom-left origin). The conversion is its own inverse, so this also maps
     * GL back to SCRIPT.
     */
    public static float flipY(float y) {
        return VIRTUAL_HEIGHT - y;
    }

    /**
     * Computes the GL bottom-left corner Y for an image whose SCRIPT-space top edge is
     * {@code scriptTopY} and whose pixel height is {@code height}. This is the
     * {@code VIRTUAL_HEIGHT - yTop - height} idiom that {@code batch.draw} expects.
     */
    public static float glDrawY(float scriptTopY, float height) {
        return VIRTUAL_HEIGHT - scriptTopY - height;
    }
}
