package pl.genschu.bloomooemulator.loader.helpers;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/** @deprecated Prefer passing a {@link BinaryReader} through the loader. */
@Deprecated
public final class BinaryHelper {
    private BinaryHelper() {}

    public static int readIntLE(InputStream in) throws IOException {
        return reader(in).readI32LE();
    }

    public static float readFloatLE(InputStream in) throws IOException {
        return reader(in).readF32LE();
    }

    public static short readShortLE(InputStream in) throws IOException {
        return reader(in).readI16LE();
    }

    public static byte readByte(InputStream in) throws IOException {
        return reader(in).readI8();
    }

    // read NUL-terminated string
    public static String readString(InputStream in) throws IOException {
        return reader(in).readNullTerminatedString(StandardCharsets.UTF_8);
    }

    // read string with known length
    public static String readString(InputStream in, int length) throws IOException {
        return reader(in).readFixedString(length, StandardCharsets.UTF_8, false);
    }

    public static byte[] readFully(InputStream in, int length) throws IOException {
        return reader(in).readBytes(length);
    }

    public static void skipFully(InputStream in, long length) throws IOException {
        reader(in).skipFully(length);
    }

    private static BinaryReader reader(InputStream in) {
        return new InputStreamBinaryReader(in);
    }
}
