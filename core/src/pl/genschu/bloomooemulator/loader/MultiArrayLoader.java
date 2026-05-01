package pl.genschu.bloomooemulator.loader;

import com.badlogic.gdx.Gdx;
import pl.genschu.bloomooemulator.interpreter.values.*;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

public class MultiArrayLoader {
    private static int readInt(InputStream f) throws IOException {
        byte[] bytes = new byte[4];
        f.read(bytes);
        return ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).getInt();
    }

    private static double readDouble(InputStream f) throws IOException {
        byte[] bytes = new byte[4];
        f.read(bytes);
        return ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).getInt() / 10000f;
    }

    private static boolean readBoolean(InputStream f) throws IOException {
        byte[] bytes = new byte[1];
        f.read(bytes);
        return ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).get() != 0;
    }

    private static String readString(InputStream f) throws IOException {
        byte[] bytes = new byte[4];
        f.read(bytes);
        int length = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).getInt();
        bytes = new byte[length];
        f.read(bytes);
        return new String(bytes, StandardCharsets.UTF_8).split("\0")[0];
    }

    public static void loadMultiArray(pl.genschu.bloomooemulator.interpreter.variable.MultiArrayVariable variable, InputStream f) {
        try {
            readMultiArrayV2(variable, f);
        } catch (IOException e) {
            Gdx.app.error("MultiArrayLoader", "Error while loading multi-array: " + e.getMessage());
        }
    }

    private static void readMultiArrayV2(pl.genschu.bloomooemulator.interpreter.variable.MultiArrayVariable variable, InputStream f) throws IOException {
        int dimensionsCount = readInt(f);
        int[] dimensions = new int[dimensionsCount];

        int totalElements = 1;
        for (int i = 0; i < dimensionsCount; i++) {
            dimensions[i] = readInt(f);
            totalElements *= dimensions[i];
        }

        Gdx.app.log("MultiArrayLoader", String.format(Locale.getDefault(), "Loading %dD array with dimensions: %s (total: %d elements)",
                dimensionsCount, java.util.Arrays.toString(dimensions), totalElements));

        variable.setDimensions(dimensions);

        int loadedCount = 0;
        try {
            while (f.available() > 0) {
                int index = readInt(f);

                if (index == totalElements) {
                    Gdx.app.log("MultiArrayLoader", "Found terminator at index " + index);
                    break;
                }

                if (index < 0 || index >= totalElements) {
                    Gdx.app.error("MultiArrayLoader", "Invalid index: " + index + " (max: " + (totalElements - 1) + ")");
                    break;
                }

                Value val = readValue(f);
                variable.getData()[index] = val;
                loadedCount++;
            }
        } catch (IOException e) {
            Gdx.app.log("MultiArrayLoader", "Stopped loading after " + loadedCount + " elements: " + e.getMessage());
        }

        Gdx.app.log("MultiArrayLoader", String.format(Locale.getDefault(), "Successfully loaded %d/%d elements (%.1f%% filled)",
                loadedCount, totalElements, 100.0 * loadedCount / totalElements));
    }

    private static Value readValue(InputStream f) throws IOException {
        int dataType = readInt(f);

        if (dataType == 1) {
            return new IntValue(readInt(f));
        } else if (dataType == 4) {
            return new DoubleValue(readDouble(f));
        } else if (dataType == 2) {
            return new StringValue(readString(f));
        } else if (dataType == 3) {
            return new BoolValue(readBoolean(f));
        } else {
            throw new IllegalArgumentException("Unknown data type: " + dataType);
        }
    }

}
