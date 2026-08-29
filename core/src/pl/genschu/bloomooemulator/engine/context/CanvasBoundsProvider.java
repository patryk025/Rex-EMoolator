package pl.genschu.bloomooemulator.engine.context;

import pl.genschu.bloomooemulator.geometry.coordinates.CanvasRect;

/**
 * Engine-facing capability for objects occupying script-visible canvas space.
 */
public interface CanvasBoundsProvider {
    CanvasRect getCanvasBounds();
}
