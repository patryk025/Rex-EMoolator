package pl.genschu.bloomooemulator.loader;

import com.badlogic.gdx.Gdx;
import pl.genschu.bloomooemulator.interpreter.values.*;
import pl.genschu.bloomooemulator.loader.helpers.BinaryReader;
import pl.genschu.bloomooemulator.loader.helpers.InputStreamBinaryReader;
import pl.genschu.bloomooemulator.interpreter.variable.MultiArrayVariable;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

public class MultiArrayLoader {
    public static void loadMultiArray(MultiArrayVariable variable, InputStream f) {
        try {
            readMultiArrayV2(variable, new InputStreamBinaryReader(f));
        } catch (IOException e) {
            Gdx.app.error("MultiArrayLoader", "Error while loading multi-array: " + e.getMessage());
        }
    }

    private static void readMultiArrayV2(MultiArrayVariable variable, BinaryReader reader) throws IOException {
        int dimensionsCount = reader.readI32LE();
        int[] dimensions = new int[dimensionsCount];

        int totalElements = 1;
        for (int i = 0; i < dimensionsCount; i++) {
            dimensions[i] = reader.readI32LE();
            totalElements *= dimensions[i];
        }

        Gdx.app.log("MultiArrayLoader", String.format(Locale.getDefault(), "Loading %dD array with dimensions: %s (total: %d elements)",
                dimensionsCount, java.util.Arrays.toString(dimensions), totalElements));

        variable.setDimensions(dimensions);

        int loadedCount = 0;
        try {
            while (true) {
                int index = reader.readI32LE();

                if (index == totalElements) {
                    Gdx.app.log("MultiArrayLoader", "Found terminator at index " + index);
                    break;
                }

                if (index < 0 || index >= totalElements) {
                    Gdx.app.error("MultiArrayLoader", "Invalid index: " + index + " (max: " + (totalElements - 1) + ")");
                    break;
                }

                Value val = readValue(reader);
                variable.getData()[index] = val;
                loadedCount++;
            }
        } catch (IOException e) {
            Gdx.app.log("MultiArrayLoader", "Stopped loading after " + loadedCount + " elements: " + e.getMessage());
        }

        Gdx.app.log("MultiArrayLoader", String.format(Locale.getDefault(), "Successfully loaded %d/%d elements (%.1f%% filled)",
                loadedCount, totalElements, 100.0 * loadedCount / totalElements));
    }

    private static Value readValue(BinaryReader reader) throws IOException {
        int dataType = reader.readI32LE();

        if (dataType == 1) {
            return new IntValue(reader.readI32LE());
        } else if (dataType == 4) {
            return new DoubleValue(reader.readI32LE() / 10000.0);
        } else if (dataType == 2) {
            return new StringValue(reader.readLengthPrefixedString32LE(StandardCharsets.UTF_8, true));
        } else if (dataType == 3) {
            return new BoolValue(reader.readU8() != 0);
        } else {
            throw new IllegalArgumentException("Unknown data type: " + dataType);
        }
    }

}
