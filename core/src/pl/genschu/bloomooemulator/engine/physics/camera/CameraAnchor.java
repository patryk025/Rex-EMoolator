package pl.genschu.bloomooemulator.engine.physics.camera;

import pl.genschu.bloomooemulator.geometry.coordinates.CanvasCoordinateSystem;
import pl.genschu.bloomooemulator.geometry.coordinates.CanvasPoint;
import pl.genschu.bloomooemulator.geometry.coordinates.CanvasScroll;
import pl.genschu.bloomooemulator.geometry.coordinates.PhysicsPoint;

/**
 * Tracks the scrolling background camera for a WORLD scene.
 *
 * <p>World space has its origin at the canvas centre with Y pointing up; canvas space has
 * its origin at the top-left with Y pointing down. Conversions between the two are owned by
 * {@link CanvasCoordinateSystem}; this class only supplies the current camera scroll.
 *
 * <p>{@code scrollX/scrollY} is how far the world has been shifted under the canvas. It is
 * zero for a non-scrolling canvas.
 */
public class CameraAnchor {
    // Map (background) bounds in scroll space, from SETBKGSIZE: the camera may scroll within
    // [minX, maxX - canvas width] / [minY, maxY - canvas height].
    private float minX, maxX, minY, maxY;
    private float scrollX, scrollY;
    private boolean trackX = true, trackY = true;

    /**
     * Recompute the scroll offset from the reference object's WORLD position so that the
     * object stays centred, clamped to the background bounds. For a non-scrolling scene the
     * clamp range is empty and scroll stays 0.
     */
    public void updateCameraAnchor(PhysicsPoint referencePosition) {
        // Scroll needed to centre the reference object equals its world coordinate
        // (canvas centre = world + half; scroll = canvas position - half = world).
        if (trackX) {
            scrollX = clamp(
                    (float) referencePosition.x(),
                    minX,
                    maxX - CanvasCoordinateSystem.WIDTH);
        }
        if (trackY) {
            scrollY = clamp(
                    (float) -referencePosition.y(),
                    minY,
                    maxY - CanvasCoordinateSystem.HEIGHT);
        }
    }

    private static float clamp(float v, float lo, float hi) {
        if (hi < lo) hi = lo; // degenerate (background not larger than screen) -> pin to lo
        return Math.max(lo, Math.min(hi, v));
    }

    /** Converts a native Sekai/ODE point to the DirectDraw canvas for the current scroll. */
    public CanvasPoint physicsToCanvas(PhysicsPoint physicsPoint) {
        return CanvasCoordinateSystem.fromPhysics(physicsPoint, canvasScroll());
    }

    /** Converts a DirectDraw canvas point to native Sekai/ODE space for the current scroll. */
    public PhysicsPoint canvasToPhysics(CanvasPoint canvasPoint, double z) {
        return CanvasCoordinateSystem.toPhysics(canvasPoint, z, canvasScroll());
    }

    /** Returns the camera displacement expressed along the DirectDraw canvas axes. */
    public CanvasScroll canvasScroll() {
        return new CanvasScroll(scrollX, scrollY);
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
     * WORLD.SETMOVEFLAGS — two boolean values selecting the tracked axes.
     */
    public void setMoveFlags(float moveX, float moveY) {
        trackX = moveX != 0;
        trackY = moveY != 0;
        if (!trackX) scrollX = 0;
        if (!trackY) scrollY = 0;
    }

}
