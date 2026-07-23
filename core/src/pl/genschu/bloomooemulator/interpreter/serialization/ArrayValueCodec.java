package pl.genschu.bloomooemulator.interpreter.serialization;

import pl.genschu.bloomooemulator.engine.compatibility.CompatibilityProfile;
import pl.genschu.bloomooemulator.interpreter.values.BoolValue;
import pl.genschu.bloomooemulator.interpreter.values.DoubleValue;
import pl.genschu.bloomooemulator.interpreter.values.IntValue;
import pl.genschu.bloomooemulator.interpreter.values.StringValue;
import pl.genschu.bloomooemulator.interpreter.values.Value;
import pl.genschu.bloomooemulator.loader.helpers.BinaryReader;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;

/**
 * Shared value encoding used by ARRAY and MULTIARRAY files.
 *
 * <p>Both containers delegate values to the same variable store/restore
 * implementation in the original engines. Only the fixed-point DOUBLE scale
 * differs between engine variants.</p>
 */
public final class ArrayValueCodec {
    private ArrayValueCodec() {}

    public static Value read(BinaryReader reader, CompatibilityProfile profile) throws IOException {
        int dataType = reader.readI32LE();
        return switch (dataType) {
            case 1 -> new IntValue(reader.readI32LE());
            case 2 -> new StringValue(
                    reader.readLengthPrefixedString32LE(StandardCharsets.UTF_8, false));
            case 3 -> new BoolValue(reader.readI32LE() != 0);
            case 4 -> new DoubleValue(
                    reader.readI32LE() / (double) profile.arrayDoubleScale());
            default -> throw new IOException("Unknown array data type: " + dataType);
        };
    }

    public static void write(OutputStream output, Value value,
                             CompatibilityProfile profile) throws IOException {
        switch (value) {
            case IntValue iv -> {
                writeInt(output, 1);
                writeInt(output, iv.value());
            }
            case StringValue sv -> {
                writeInt(output, 2);
                byte[] bytes = sv.value().getBytes(StandardCharsets.UTF_8);
                writeInt(output, bytes.length);
                output.write(bytes);
            }
            case BoolValue bv -> {
                writeInt(output, 3);
                writeInt(output, bv.value() ? 1 : 0);
            }
            case DoubleValue dv -> {
                writeInt(output, 4);
                writeInt(output, (int) (dv.value() * profile.arrayDoubleScale()));
            }
            default -> throw new IOException(
                    "Unsupported array element type: " + value.getType());
        }
    }

    public static void writeInt(OutputStream output, int value) throws IOException {
        output.write(ByteBuffer.allocate(Integer.BYTES)
                .order(ByteOrder.LITTLE_ENDIAN)
                .putInt(value)
                .array());
    }
}
