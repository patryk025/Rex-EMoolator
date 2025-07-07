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

        byte[] imgData = generateData(data, width, height, 16, 0, null, 0, 0);

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

        ByteBuffer header = ByteBuffer.allocate(36).order(ByteOrder.LITTLE_ENDIAN);
        header.putInt(width);
        header.putInt(height);
        header.putInt(colorDepth);
        header.putInt(imageSize);
        header.putInt(0); // Zarezerwowane miejsce
        header.putInt(compressionType);
        header.putInt(alphaSize);
        header.putInt(offsetX);
        header.putInt(offsetY);

        ByteBuffer finalBuffer = ByteBuffer.allocate(magicId.length + header.capacity() + imageSize + alphaSize);
        finalBuffer.put(magicId);
        finalBuffer.put(header.array());
        finalBuffer.put(data);
        if (alphaData != null) {
            finalBuffer.put(alphaData);
        }

        return finalBuffer.array();
    }
}
