package pl.genschu.bloomooemulator.loader;

import com.badlogic.gdx.Gdx;
import pl.genschu.bloomooemulator.interpreter.variable.Variable;
import pl.genschu.bloomooemulator.interpreter.variable.types.*;
import pl.genschu.bloomooemulator.utils.FileUtils;

import java.io.FileInputStream;
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

    private static Variable readVariable(InputStream f, MultiArrayVariable parent) throws IOException {
        int dataType = readInt(f);

        if (dataType == 1) {
            return new IntegerVariable("", readInt(f), parent.getContext());
        } else if (dataType == 4) {
            return new DoubleVariable("", readDouble(f), parent.getContext());
        } else if (dataType == 2) {
            String string = readString(f);
            return new StringVariable("", string, parent.getContext());
        } else if (dataType == 3) {
            return new BoolVariable("", readBoolean(f), parent.getContext());
        } else {
            throw new IllegalArgumentException("Unknown data type: " + dataType);
        }
    }

    public static void loadMultiArray(MultiArrayVariable variable, String path) {
        String filePath = FileUtils.resolveRelativePath(variable, path);
        try (FileInputStream f = new FileInputStream(filePath)) {
            readMultiArray(variable, f);
        } catch (IOException e) {
            Gdx.app.error("MultiArrayLoader", "Error while loading multi-array: " + e.getMessage());
        }
    }

    private static void readMultiArray(MultiArrayVariable variable, FileInputStream f) throws IOException {
        // Read dimensions count
        int dimensionsCount = readInt(f);
        int[] dimensions = new int[dimensionsCount];

        // Read all dimensions size
        int totalElements = 1;
        for (int i = 0; i < dimensionsCount; i++) {
            dimensions[i] = readInt(f);
            totalElements *= dimensions[i];
        }

        Gdx.app.log("MultiArrayLoader", String.format(Locale.getDefault(), "Loading %dD array with dimensions: %s (total: %d elements)",
                dimensionsCount, java.util.Arrays.toString(dimensions), totalElements));

        // Allocate array
        variable.setDimensions(dimensions);

        // Read elements with their indices (sparse format)
        // Format: [index, type, value]
        // Terminator: index == totalElements
        int loadedCount = 0;
        try {
            while (f.available() > 0) {
                int index = readInt(f);

                // Check if terminator
                if (index == totalElements) {
                    Gdx.app.log("MultiArrayLoader", "Found terminator at index " + index);
                    break;
                }

                // Check if index is valid
                if (index < 0 || index >= totalElements) {
                    Gdx.app.error("MultiArrayLoader", "Invalid index: " + index + " (max: " + (totalElements - 1) + ")");
                    break;
                }

                // Read variable
                Variable var = readVariable(f, variable);
                variable.getData()[index] = var;
                loadedCount++;
            }
        } catch (IOException e) {
            // End of file
            Gdx.app.log("MultiArrayLoader", "Stopped loading after " + loadedCount + " elements: " + e.getMessage());
        }

        Gdx.app.log("MultiArrayLoader", String.format(Locale.getDefault(), "Successfully loaded %d/%d elements (%.1f%% filled)",
                loadedCount, totalElements, 100.0 * loadedCount / totalElements));
    }
}