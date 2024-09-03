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
import java.util.List;

public class ArraySaver {
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

    public static void saveArray(ArrayVariable variable, String path) {
        String filePath = FileUtils.resolveRelativePath(variable, path);
        try (FileOutputStream f = new FileOutputStream(filePath)) {
            writeArray(variable, f);
        } catch (IOException e) {
            Gdx.app.error("ArraySaver", "Error while saving array: " + e.getMessage());
        }
    }

    private static void writeArray(ArrayVariable variable, FileOutputStream f) throws IOException {
        List<Variable> array = variable.getElements();
        writeInt(f, array.size());

        for (Variable element : array) {
            if (element instanceof IntegerVariable) {
                writeInt(f, 1);
                writeInt(f, ((IntegerVariable) element).GET());
            } else if (element instanceof DoubleVariable) {
                writeInt(f, 4);
                writeDouble(f, ((DoubleVariable) element).GET());
            } else if (element instanceof StringVariable) {
                writeInt(f, 2);
                writeString(f, ((StringVariable) element).GET());
            } else if (element instanceof BoolVariable) {
                writeInt(f, 3);
                writeBoolean(f, ((BoolVariable) element).GET());
            } else {
                throw new IllegalArgumentException("Unsupported data type");
            }
        }
    }
}

