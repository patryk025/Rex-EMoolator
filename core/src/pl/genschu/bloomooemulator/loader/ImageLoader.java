package pl.genschu.bloomooemulator.loader;

import com.badlogic.gdx.Gdx;
import pl.genschu.bloomooemulator.objects.Image;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;

public class ImageLoader {
    /**
     * Loads an image from a stream into a v2 ImageVariable.
     */
    public static void loadImage(pl.genschu.bloomooemulator.interpreter.variable.ImageVariable variable, InputStream inputStream) {
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
     * Loads an image from an absolute path into a v2 ImageVariable.
     */
    @Deprecated
    public static void loadImage(pl.genschu.bloomooemulator.interpreter.variable.ImageVariable variable, String absolutePath) {
        try (FileInputStream f = new FileInputStream(absolutePath)) {
            loadImage(variable, f);
        } catch (IOException e) {
            Gdx.app.error("ImageLoader", "Error while opening IMG file: " + e.getMessage());
        }
    }

    /**
     * Reads image data from stream and returns an Image object.
     */
    private static Image readImageData(InputStream f) throws IOException {
        byte[] magicIdBytes = new byte[4];
        f.read(magicIdBytes);
        String magicId = new String(magicIdBytes, StandardCharsets.UTF_8);
        if (!magicId.equals("PIK\0")) {
            throw new IllegalArgumentException("To nie jest poprawny plik obrazu. Oczekiwano: PIK\\0, otrzymano: " + magicId);
        }

        byte[] headerBytes = new byte[36];
        f.read(headerBytes);
        ByteBuffer buffer = ByteBuffer.wrap(headerBytes).order(ByteOrder.LITTLE_ENDIAN);

        int width = buffer.getInt();
        int height = buffer.getInt();
        int colorDepth = buffer.getInt();
        int imageSize = buffer.getInt();
        buffer.position(buffer.position() + 4);
        int compressionType = buffer.getInt();
        int alphaSize = buffer.getInt();
        int offsetX = buffer.getInt();
        int offsetY = buffer.getInt();

        if (compressionType == 4) {
            compressionType = 0;
        }

        byte[] imageData = new byte[imageSize];
        f.read(imageData);
        byte[] alphaData;
        if (alphaSize > 0) {
            alphaData = new byte[alphaSize];
            f.read(alphaData);
        } else {
            alphaData = null;
        }

        return new Image(width, height, offsetX, offsetY, colorDepth, imageData, alphaData, compressionType);
    }
}
