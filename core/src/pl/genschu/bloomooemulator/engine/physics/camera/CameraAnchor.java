package pl.genschu.bloomooemulator.engine.physics.camera;

/**
 * Tracks the scrolling background camera for a WORLD scene.
 *
 * <p>World space has its origin at the canvas centre with Y pointing up; screen space is
 * 800x600 with Y pointing down. A static object at world (wx, wy) maps to screen
 * (wx + 400, 300 - wy). When the background is larger than the screen, the camera scrolls
 * to follow the reference object, clamped so the background edges never show.
 *
 * <p>{@code scrollX/scrollY} is how far the world has been shifted under the screen. It is
 * 0 for a non-scrolling 800x600 scene. The screen position of any object is then
 * {@code worldToScreen(world) = world±half - scroll}.
 */
public class CameraAnchor {
    static final float HALF_WIDTH = 400.0f;
    static final float HALF_HEIGHT = 300.0f;
    static final float FULL_WIDTH = 800.0f;
    static final float FULL_HEIGHT = 600.0f;

    // Map (background) bounds in scroll space, from SETBKGSIZE: the camera may scroll within
    // [minX, maxX - FULL_WIDTH] / [minY, maxY - FULL_HEIGHT].
    private float minX, maxX, minY, maxY;
    private float scrollX, scrollY;
    private boolean trackX = true, trackY = true;

    /**
     * Recompute the scroll offset from the reference object's WORLD position so that the
     * object stays centred, clamped to the background bounds. For a non-scrolling scene the
     * clamp range is empty and scroll stays 0.
     */
    public void updateCameraAnchor(float refWorldX, float refWorldY, float refWorldZ) {
        // Scroll needed to centre the reference object equals its world coordinate
        // (screenCentre = world + half; scroll = screenPos - half = world).
        if (trackX) scrollX = clamp(refWorldX, minX, maxX - FULL_WIDTH);
        if (trackY) scrollY = clamp(-refWorldY, minY, maxY - FULL_HEIGHT);
    }

    private static float clamp(float v, float lo, float hi) {
        if (hi < lo) hi = lo; // degenerate (background not larger than screen) -> pin to lo
        return Math.max(lo, Math.min(hi, v));
    }

    /** World X -> screen X for the current scroll. */
    public float worldToScreenX(float worldX) {
        return worldX + HALF_WIDTH - scrollX;
    }

    /** World Y (up) -> screen Y (down) for the current scroll. */
    public float worldToScreenY(float worldY) {
        return HALF_HEIGHT - worldY - scrollY;
    }

    /** Screen X -> world X for the current scroll (inverse of {@link #worldToScreenX}). */
    public float screenToWorldX(float screenX) {
        return screenX - HALF_WIDTH + scrollX;
    }

    /** Screen Y (down) -> world Y (up) for the current scroll (inverse of {@link #worldToScreenY}). */
    public float screenToWorldY(float screenY) {
        return HALF_HEIGHT - screenY - scrollY;
    }

    /**
     * WORLD.SETBKGSIZE — sets the map (background) bounds used for camera clamping.
     * Script passes (minX, maxX, minY, maxY) = (-tx, tx+800, -ty, ty+600) where
     * tx=(bkgW-800)/2, ty=(bkgH-600)/2, so the camera may scroll within [-tx, tx] / [-ty, ty].
     */
    public void setLimits(float minX, float maxX, float minY, float maxY) {
        this.minX = minX;
        this.maxX = maxX;
        this.minY = minY;
        this.maxY = maxY;
    }

    /**
     * WORLD.SETMOVEFLAGS — the script passes the per-axis max scroll amounts (tx, ty),
     * not bit flags. An axis scrolls only when its background is larger than the screen
     * (amount &gt;= 0); otherwise it stays pinned (scroll 0).
     */
    public void setMoveFlags(float moveX, float moveY) {
        trackX = moveX >= 0;
        trackY = moveY >= 0;
        if (!trackX) scrollX = 0;
        if (!trackY) scrollY = 0;
    }

    /** WORLD.GETBKGPOSX — current horizontal background scroll (0 when not scrolling). */
    public float getBkgPosX() {
        return scrollX;
    }

    /** WORLD.GETBKGPOSY — current vertical background scroll (0 when not scrolling). */
    public float getBkgPosY() {
        return scrollY;
    }
}
