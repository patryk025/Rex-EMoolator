package pl.genschu.bloomooemulator.saver;

import com.badlogic.gdx.Gdx;
import pl.genschu.bloomooemulator.engine.compatibility.CompatibilityProfile;
import pl.genschu.bloomooemulator.interpreter.serialization.ArrayValueCodec;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Locale;

public class MultiArraySaver {
    public static void saveMultiArray(pl.genschu.bloomooemulator.interpreter.variable.MultiArrayVariable variable, OutputStream f) {
        saveMultiArray(variable, f, CompatibilityProfile.unknown());
    }

    public static void saveMultiArray(
            pl.genschu.bloomooemulator.interpreter.variable.MultiArrayVariable variable,
            OutputStream f,
            CompatibilityProfile profile) {
        try {
            writeMultiArrayV2(variable, f, profile);
        } catch (IOException e) {
            Gdx.app.error("MultiArraySaver", "Error while saving multi-array: " + e.getMessage());
        }
    }

    private static void writeMultiArrayV2(
            pl.genschu.bloomooemulator.interpreter.variable.MultiArrayVariable variable,
            OutputStream f,
            CompatibilityProfile profile) throws IOException {
        int[] dimensions = variable.getDimensions();
        pl.genschu.bloomooemulator.interpreter.values.Value[] data = variable.getData();
        int totalElements = variable.getTotalElements();

        ArrayValueCodec.writeInt(f, dimensions.length);
        for (int dimension : dimensions) {
            ArrayValueCodec.writeInt(f, dimension);
        }

        int savedCount = 0;
        for (int i = 0; i < totalElements; i++) {
            if (data[i] != null) {
                ArrayValueCodec.writeInt(f, i);
                ArrayValueCodec.write(f, data[i], profile);
                savedCount++;
            }
        }

        ArrayValueCodec.writeInt(f, totalElements);

        Gdx.app.log("MultiArraySaver", String.format(Locale.getDefault(), "Saved %d/%d elements (%.1f%% filled) to multi-array",
                savedCount, totalElements, 100.0 * savedCount / totalElements));
    }

}
