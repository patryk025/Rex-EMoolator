package pl.genschu.bloomooemulator.loader;

import com.badlogic.gdx.Gdx;
import pl.genschu.bloomooemulator.interpreter.variable.ImageVariable;
import pl.genschu.bloomooemulator.objects.Image;
import pl.genschu.bloomooemulator.loader.helpers.BinaryReader;
import pl.genschu.bloomooemulator.loader.helpers.InputStreamBinaryReader;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class ImageLoader {
    public static void loadImage(ImageVariable variable, InputStream inputStream) {
        try {
            Image image = readImageData(inputStream);
            variable.state().image = image;
            variable.state().posX = image.offsetX;
            variable.state().posY = image.offsetY;
        } catch (IOException e) {
            Gdx.app.error("ImageLoader", "Error while loading IMG: " + e.getMessage());
        }
    }

    /**
     * Reads image data from stream and returns an Image object.
     */
    private static Image readImageData(InputStream f) throws IOException {
        BinaryReader reader = new InputStreamBinaryReader(f);
        String magicId = reader.readFixedString(4, StandardCharsets.UTF_8, false);
        if (!magicId.equals("PIK\0")) {
            throw new IllegalArgumentException("To nie jest poprawny plik obrazu. Oczekiwano: PIK\\0, otrzymano: " + magicId);
        }

        int width = reader.readI32LE();
        int height = reader.readI32LE();
        int colorDepth = reader.readI32LE();
        int imageSize = reader.readI32LE();
        reader.skipFully(4);
        int compressionType = reader.readI32LE();
        int alphaSize = reader.readI32LE();
        int offsetX = reader.readI32LE();
        int offsetY = reader.readI32LE();

        if (compressionType == 4) {
            compressionType = 0;
        }

        byte[] imageData = reader.readBytes(imageSize);
        byte[] alphaData;
        if (alphaSize > 0) {
            alphaData = reader.readBytes(alphaSize);
        } else {
            alphaData = null;
        }

        return new Image(width, height, offsetX, offsetY, colorDepth, imageData, alphaData, compressionType);
    }
}
