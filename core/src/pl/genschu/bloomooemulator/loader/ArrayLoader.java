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
import java.util.ArrayList;
import java.util.List;

public class ArrayLoader {
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

    public static void loadArray(ArrayVariable variable, String path) {
        String filePath = FileUtils.resolveRelativePath(variable, path);
        try (FileInputStream f = new FileInputStream(filePath)) {
            readArray(variable, f);
        } catch (IOException e) {
            Gdx.app.error("ArrayLoader", "Error while loading array: " + e.getMessage());
        }
    }

    private static void readArray(ArrayVariable variable, FileInputStream f) throws IOException {
        List<Variable> array = new ArrayList<>();
        int arrayLength = readInt(f);

        for (int i = 0; i < arrayLength; i++) {
            int dataType = readInt(f);

            if (dataType == 1) {
                array.add(new IntegerVariable("", readInt(f), variable.getContext()));
            } else if (dataType == 4) {
                array.add(new DoubleVariable("", readDouble(f), variable.getContext()));
            } else if (dataType == 2) {
                String string = readString(f);
                array.add(new StringVariable("", string, variable.getContext()));
            } else if (dataType == 3) {
                array.add(new BoolVariable("", readBoolean(f), variable.getContext()));
            } else {
                throw new IllegalArgumentException("Unknown data type");
            }
        }

        variable.setElements(array);
    }
}
