package pl.genschu.bloomooemulator.loader.helpers;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;

public class BinaryHelper {
    public static int readIntLE(FileInputStream in) throws IOException {
        byte[] buffer = readFully(in, 4);
        return ByteBuffer.wrap(buffer).order(ByteOrder.LITTLE_ENDIAN).getInt();
    }

    public static float readFloatLE(FileInputStream in) throws IOException {
        byte[] buffer = readFully(in, 4);
        return ByteBuffer.wrap(buffer).order(ByteOrder.LITTLE_ENDIAN).getFloat();
    }

    public static short readShortLE(FileInputStream in) throws IOException {
        byte[] buffer = readFully(in, 2);
        return ByteBuffer.wrap(buffer).order(ByteOrder.LITTLE_ENDIAN).getShort();
    }

    public static byte readByte(FileInputStream in) throws IOException {
        byte[] buffer = readFully(in, 1);
        return buffer[0];
    }

    // read NUL-terminated string
    public static String readString(FileInputStream in) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int b;
        while ((b = in.read()) != -1 && b != 0) {
            buffer.write(b);
        }
        // Yeah, I know, "Inefficient conversion from ByteArrayOutputStream"
        return new String(buffer.toByteArray(), StandardCharsets.UTF_8);
    }

    // read string with known length
    public static String readString(FileInputStream in, int length) throws IOException {
        byte[] buffer = readFully(in, length);
        return new String(buffer, StandardCharsets.UTF_8);
    }

    public static byte[] readFully(FileInputStream in, int length) throws IOException {
        byte[] buffer = new byte[length];
        int offset = 0;
        while (offset < length) {
            int bytesRead = in.read(buffer, offset, length - offset);
            if (bytesRead == -1) {
                throw new IOException("Unexpected end of stream");
            }
            offset += bytesRead;
        }
        return buffer;
    }
}

