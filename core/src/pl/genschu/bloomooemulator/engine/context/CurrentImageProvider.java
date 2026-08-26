package pl.genschu.bloomooemulator.engine.context;

import pl.genschu.bloomooemulator.objects.Image;

/**
 * Engine-facing capability for graphics that expose their currently displayed raster image.
 */
public interface CurrentImageProvider {
    Image getCurrentImage();
}
