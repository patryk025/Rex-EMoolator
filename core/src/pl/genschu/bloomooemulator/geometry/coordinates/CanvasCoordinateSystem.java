package pl.genschu.bloomooemulator.geometry.coordinates;

/**
 * The single definition of the emulator's coordinate-system boundaries.
 *
 * <p>Game state and script APIs use the fixed DirectDraw canvas. OpenGL and physics
 * coordinates are boundary representations and must be obtained through this class.</p>
 */
public final class CanvasCoordinateSystem {
    public static final int WIDTH = 800;
    public static final int HEIGHT = 600;
    public static final double CENTER_X = WIDTH / 2.0;
    public static final double CENTER_Y = HEIGHT / 2.0;
    public static final CanvasRect BOUNDS = new CanvasRect(0, 0, WIDTH, HEIGHT);

    private CanvasCoordinateSystem() {}

    public static OpenGlPoint toOpenGl(CanvasPoint canvasPoint) {
        return new OpenGlPoint(canvasPoint.x(), HEIGHT - canvasPoint.y());
    }

    public static CanvasPoint fromOpenGl(OpenGlPoint openGlPoint) {
        return new CanvasPoint(openGlPoint.x(), HEIGHT - openGlPoint.y());
    }

    public static OpenGlRect toOpenGl(CanvasRect canvasRect) {
        return new OpenGlRect(
                canvasRect.left(),
                HEIGHT - canvasRect.bottom(),
                canvasRect.width(),
                canvasRect.height());
    }

    public static double toOpenGlY(double canvasY) {
        return HEIGHT - canvasY;
    }

    public static double toOpenGlBottom(double canvasTop, double height) {
        return HEIGHT - canvasTop - height;
    }

    /** Converts a clockwise-positive canvas rotation to OpenGL's counter-clockwise convention. */
    public static double toOpenGlRotationDegrees(double canvasDegrees) {
        return -canvasDegrees;
    }

    public static PhysicsPoint toPhysics(CanvasPoint canvasPoint, double z) {
        return toPhysics(canvasPoint, z, CanvasScroll.NONE);
    }

    public static PhysicsPoint toPhysics(
            CanvasPoint canvasPoint,
            double z,
            CanvasScroll cameraScroll
    ) {
        return new PhysicsPoint(
                canvasPoint.x() - CENTER_X + cameraScroll.x(),
                CENTER_Y - canvasPoint.y() - cameraScroll.y(),
                z);
    }

    public static CanvasPoint fromPhysics(PhysicsPoint physicsPoint) {
        return fromPhysics(physicsPoint, CanvasScroll.NONE);
    }

    public static CanvasPoint fromPhysics(
            PhysicsPoint physicsPoint,
            CanvasScroll cameraScroll
    ) {
        return new CanvasPoint(
                physicsPoint.x() + CENTER_X - cameraScroll.x(),
                CENTER_Y - physicsPoint.y() - cameraScroll.y());
    }

    public static OpenGlPoint physicsToOpenGl(
            PhysicsPoint physicsPoint,
            CanvasScroll cameraScroll
    ) {
        return toOpenGl(fromPhysics(physicsPoint, cameraScroll));
    }

    public static PhysicsBox toPhysics(CanvasRect canvasRect, double minZ, double maxZ) {
        return toPhysics(
                canvasRect.left(), canvasRect.top(), canvasRect.right(), canvasRect.bottom(),
                minZ, maxZ);
    }

    /** Converts continuous script-visible canvas bounds without truncating legacy doubles. */
    public static PhysicsBox toPhysics(
            double left,
            double top,
            double right,
            double bottom,
            double minZ,
            double maxZ
    ) {
        if (right < left || bottom < top) {
            throw new IllegalArgumentException("Canvas bounds must be normalized");
        }
        if (minZ > maxZ) {
            throw new IllegalArgumentException("Physics Z bounds must be normalized");
        }
        return new PhysicsBox(
                new PhysicsPoint(
                        left - CENTER_X,
                        CENTER_Y - bottom,
                        minZ),
                new PhysicsPoint(
                        right - CENTER_X,
                        CENTER_Y - top,
                        maxZ));
    }
}
