package pl.genschu.bloomooemulator.engine.physics.camera;

public class CameraAnchor {
    static final float HALF_WIDTH = 400.0f;
    static final float HALF_HEIGHT = 300.0f;
    static final float FULL_WIDTH = 800.0f;
    static final float FULL_HEIGHT = 600.0f;

    float minX, maxX, minY, maxY;  // map limit
    float cameraPosX, cameraPosY;  // camera position
    float lastObjectX, lastObjectY; // last object position
    int flags = 3; // bit 0=track X, bit 1=track Y

    public void updateCameraAnchor(float objectX, float objectY, float objectZ) {
        // Convert object position to screen space
        float screenX = objectX + HALF_WIDTH;
        float screenY = HALF_HEIGHT - objectY;

        boolean trackX = (flags & 1) != 0;
        boolean trackY = (flags & 2) != 0;

        // Calculate delta (how far object is from camera center)
        float multiplierX = trackX ? 1.0f : 0.0f;
        float multiplierY = trackY ? 1.0f : 0.0f;

        float deltaX = (screenX - cameraPosX) * multiplierX;
        float deltaY = (screenY - cameraPosY) * multiplierY;

        // Clamp to map limits
        // Camera can not show area outside [minX, maxX] and [minY, maxY]
        // but must take into account screen size (800x600)

        float correctionX = 0;
        if (deltaX < minX) {
            // too close to left edge
            correctionX = minX - deltaX;
        } else if (deltaX > maxX - FULL_WIDTH) {
            // too close to right edge (subtract screen width)
            correctionX = (maxX - FULL_WIDTH) - deltaX;
        }

        float correctionY = 0;
        if (deltaY < minY) {
            // too close to top edge
            correctionY = minY - deltaY;
        } else if (deltaY > maxY - FULL_HEIGHT) {
            // too close to bottom edge (subtract screen height)
            correctionY = (maxY - FULL_HEIGHT) - deltaY;
        }

        // Apply corrections
        deltaX += correctionX;
        deltaY += correctionY;

        // Move camera
        cameraPosX += deltaX;
        cameraPosY += deltaY;

        // If tracking is enabled, reverse the correction
        if (trackX) {
            cameraPosX -= correctionX;
        }
        if (trackY) {
            cameraPosY -= correctionY;
        }

        // Zapisz końcowe wartości dla renderowania
        lastObjectX = screenX;
        lastObjectY = objectY + screenY;
    }

    public float getCameraPosX() {
        return cameraPosX;
    }

    public float getCameraPosY() {
        return cameraPosY;
    }
}
