package pl.genschu.bloomooemulator.saver;

import com.badlogic.gdx.Gdx;
import pl.genschu.bloomooemulator.interpreter.variable.Variable;
import pl.genschu.bloomooemulator.utils.FileUtils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;

public class ImageSaver {
    public static void saveScreenshot(Variable variable, String path, byte[] data, int width, int height) {
        String filePath = FileUtils.resolveRelativePath(variable, path);

        byte[] imgData = generateData(data, width, height, 16, 0, null, -2, 0); // -2, temporary fix

        try (FileOutputStream f = new FileOutputStream(filePath)) {
            f.write(imgData);
        } catch (IOException e) {
            Gdx.app.error("ImageSaver", "Error while saving IMG: " + e.getMessage());
        }
    }

    private static byte[] generateData(byte[] data, int width, int height, int colorDepth, int compressionType, byte[] alphaData, int offsetX, int offsetY) {
        // Magic ID
        byte[] magicId = "PIK\0".getBytes(StandardCharsets.UTF_8);

        // Rozmiary danych
        int imageSize = data.length;
        int alphaSize = alphaData != null ? alphaData.length : 0;

        ByteBuffer buffer = ByteBuffer.allocate(40).order(ByteOrder.LITTLE_ENDIAN);
        buffer.putInt(width);
        buffer.putInt(height);
        buffer.putInt(colorDepth);
        buffer.putInt(imageSize);
        buffer.putInt(0); // Zarezerwowane miejsce
        buffer.putInt(compressionType);
        buffer.putInt(alphaSize);
        buffer.putInt(offsetX);
        buffer.putInt(offsetY);

        ByteBuffer finalBuffer = ByteBuffer.allocate(magicId.length + buffer.capacity() + imageSize + alphaSize);
        finalBuffer.put(magicId);
        finalBuffer.put(buffer.array());
        finalBuffer.put(data);
        if (alphaData != null) {
            finalBuffer.put(alphaData);
        }

        return finalBuffer.array();
    }
}
