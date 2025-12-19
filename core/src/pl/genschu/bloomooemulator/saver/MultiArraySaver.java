package pl.genschu.bloomooemulator.saver;

import com.badlogic.gdx.Gdx;
import pl.genschu.bloomooemulator.interpreter.variable.Variable;
import pl.genschu.bloomooemulator.interpreter.variable.types.*;
import pl.genschu.bloomooemulator.utils.FileUtils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

public class MultiArraySaver {
    private static void writeInt(FileOutputStream f, int value) throws IOException {
        byte[] bytes = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(value).array();
        f.write(bytes);
    }

    private static void writeDouble(FileOutputStream f, double value) throws IOException {
        int intValue = (int) (value * 10000);
        writeInt(f, intValue);
    }

    private static void writeBoolean(FileOutputStream f, boolean value) throws IOException {
        byte boolByte = (byte) (value ? 1 : 0);
        f.write(boolByte);
    }

    private static void writeString(FileOutputStream f, String value) throws IOException {
        byte[] stringBytes = value.getBytes(StandardCharsets.UTF_8);
        writeInt(f, stringBytes.length + 1); // including null terminator
        f.write(stringBytes);
        f.write(0); // null terminator
    }

    private static void writeVariable(FileOutputStream f, Variable variable) throws IOException {
        if (variable instanceof IntegerVariable) {
            writeInt(f, 1);
            writeInt(f, ((IntegerVariable) variable).GET());
        } else if (variable instanceof DoubleVariable) {
            writeInt(f, 4);
            writeDouble(f, ((DoubleVariable) variable).GET());
        } else if (variable instanceof StringVariable) {
            writeInt(f, 2);
            writeString(f, ((StringVariable) variable).GET());
        } else if (variable instanceof BoolVariable) {
            writeInt(f, 3);
            writeBoolean(f, ((BoolVariable) variable).GET());
        } else {
            throw new IllegalArgumentException("Unsupported data type: " + variable.getType());
        }
    }

    public static void saveMultiArray(MultiArrayVariable variable, String path) {
        String filePath = FileUtils.resolveRelativePath(variable, path);
        try (FileOutputStream f = new FileOutputStream(filePath)) {
            writeMultiArray(variable, f);
        } catch (IOException e) {
            Gdx.app.error("MultiArraySaver", "Error while saving multi-array: " + e.getMessage());
        }
    }

    private static void writeMultiArray(MultiArrayVariable variable, FileOutputStream f) throws IOException {
        int[] dimensions = variable.getDimensions();
        Variable[] data = variable.getData();
        int totalElements = variable.getTotalElements();

        // Save dimensions
        writeInt(f, dimensions.length);

        // Save dimensions size
        for (int dimension : dimensions) {
            writeInt(f, dimension);
        }

        // Save only filled indexes (sparse format)
        int savedCount = 0;
        for (int i = 0; i < totalElements; i++) {
            if (data[i] != null) {
                // Save flat index
                writeInt(f, i);

                // Save variable
                writeVariable(f, data[i]);
                savedCount++;
            }
        }

        // Save terminator
        writeInt(f, totalElements);

        Gdx.app.log("MultiArraySaver", String.format(Locale.getDefault(), "Saved %d/%d elements (%.1f%% filled) to multi-array",
                savedCount, totalElements, 100.0 * savedCount / totalElements));
    }
}