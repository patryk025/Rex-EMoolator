package pl.genschu.bloomooemulator.saver;

import com.badlogic.gdx.Gdx;
import pl.genschu.bloomooemulator.interpreter.values.*;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

public class MultiArraySaver {
    private static void writeInt(OutputStream f, int value) throws IOException {
        byte[] bytes = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(value).array();
        f.write(bytes);
    }

    private static void writeDouble(OutputStream f, double value) throws IOException {
        int intValue = (int) (value * 10000);
        writeInt(f, intValue);
    }

    private static void writeBoolean(OutputStream f, boolean value) throws IOException {
        byte boolByte = (byte) (value ? 1 : 0);
        f.write(boolByte);
    }

    private static void writeString(OutputStream f, String value) throws IOException {
        byte[] stringBytes = value.getBytes(StandardCharsets.UTF_8);
        writeInt(f, stringBytes.length + 1); // including null terminator
        f.write(stringBytes);
        f.write(0); // null terminator
    }

    public static void saveMultiArray(pl.genschu.bloomooemulator.interpreter.variable.MultiArrayVariable variable, OutputStream f) {
        try {
            writeMultiArrayV2(variable, f);
        } catch (IOException e) {
            Gdx.app.error("MultiArraySaver", "Error while saving multi-array: " + e.getMessage());
        }
    }

    private static void writeMultiArrayV2(pl.genschu.bloomooemulator.interpreter.variable.MultiArrayVariable variable, OutputStream f) throws IOException {
        int[] dimensions = variable.getDimensions();
        pl.genschu.bloomooemulator.interpreter.values.Value[] data = variable.getData();
        int totalElements = variable.getTotalElements();

        writeInt(f, dimensions.length);
        for (int dimension : dimensions) {
            writeInt(f, dimension);
        }

        int savedCount = 0;
        for (int i = 0; i < totalElements; i++) {
            if (data[i] != null) {
                writeInt(f, i);
                writeValue(f, data[i]);
                savedCount++;
            }
        }

        writeInt(f, totalElements);

        Gdx.app.log("MultiArraySaver", String.format(Locale.getDefault(), "Saved %d/%d elements (%.1f%% filled) to multi-array",
                savedCount, totalElements, 100.0 * savedCount / totalElements));
    }

    private static void writeValue(OutputStream f, pl.genschu.bloomooemulator.interpreter.values.Value value) throws IOException {
        switch (value) {
            case IntValue iv -> { writeInt(f, 1); writeInt(f, iv.value()); }
            case DoubleValue dv -> { writeInt(f, 4); writeDouble(f, dv.value()); }
            case StringValue sv -> { writeInt(f, 2); writeString(f, sv.value()); }
            case BoolValue bv -> { writeInt(f, 3); writeBoolean(f, bv.value()); }
            default -> throw new IllegalArgumentException("Unsupported data type: " + value.getType());
        }
    }
}
