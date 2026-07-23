package pl.genschu.bloomooemulator.loader;

import com.badlogic.gdx.Gdx;
import pl.genschu.bloomooemulator.engine.compatibility.CompatibilityProfile;
import pl.genschu.bloomooemulator.interpreter.serialization.ArrayValueCodec;
import pl.genschu.bloomooemulator.interpreter.values.Value;
import pl.genschu.bloomooemulator.loader.helpers.BinaryReader;
import pl.genschu.bloomooemulator.loader.helpers.InputStreamBinaryReader;
import pl.genschu.bloomooemulator.interpreter.variable.MultiArrayVariable;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

public class MultiArrayLoader {
    public static void loadMultiArray(MultiArrayVariable variable, InputStream f) {
        loadMultiArray(variable, f, CompatibilityProfile.unknown());
    }

    public static void loadMultiArray(MultiArrayVariable variable, InputStream f,
                                      CompatibilityProfile profile) {
        try {
            readMultiArrayV2(variable, new InputStreamBinaryReader(f), profile);
        } catch (IOException e) {
            Gdx.app.error("MultiArrayLoader", "Error while loading multi-array: " + e.getMessage());
        }
    }

    private static void readMultiArrayV2(MultiArrayVariable variable, BinaryReader reader,
                                         CompatibilityProfile profile) throws IOException {
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

                Value val = ArrayValueCodec.read(reader, profile);
                variable.getData()[index] = val;
                loadedCount++;
            }
        } catch (IOException e) {
            Gdx.app.log("MultiArrayLoader", "Stopped loading after " + loadedCount + " elements: " + e.getMessage());
        }

        Gdx.app.log("MultiArrayLoader", String.format(Locale.getDefault(), "Successfully loaded %d/%d elements (%.1f%% filled)",
                loadedCount, totalElements, 100.0 * loadedCount / totalElements));
    }

}
