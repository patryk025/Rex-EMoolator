package pl.genschu.bloomooemulator.loader;

import com.badlogic.gdx.Gdx;
import pl.genschu.bloomooemulator.interpreter.variable.types.AnimoVariable;
import pl.genschu.bloomooemulator.interpreter.variable.types.ImageVariable;
import pl.genschu.bloomooemulator.objects.Image;
import pl.genschu.bloomooemulator.utils.FileUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;

public class ImageLoader {
    public static void loadImage(ImageVariable variable) {
        String filePath = FileUtils.resolveRelativePath(variable);
        try (FileInputStream f = new FileInputStream(filePath)) {
            readImage(variable, f);
        } catch (IOException e) {
            Gdx.app.error("ImageLoader", "Error while loading IMG: " + e.getMessage());
        }
    }

    private static void readImage(ImageVariable variable, InputStream f) throws IOException {
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

        byte[] imageData = new byte[imageSize];
        f.read(imageData);
        byte[] alphaData;
        if(alphaSize > 0) {
            alphaData = new byte[alphaSize];
            f.read(alphaData);
        }
        else {
            alphaData = null;
        }

        Image image = new Image(width, height, offsetX, offsetY, colorDepth, imageData, alphaData, compressionType);
        variable.setImage(image);
    }
}
