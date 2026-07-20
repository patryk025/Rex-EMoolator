package pl.genschu.bloomooemulator.loader.helpers;

import java.io.IOException;
import java.nio.charset.Charset;

/**
 * Strict reader for the primitive encodings used by the game's binary formats.
 * Every fixed-size operation either consumes the requested number of bytes or
 * throws an {@link java.io.EOFException}.
 */
public interface BinaryReader {
    byte readI8() throws IOException;

    int readU8() throws IOException;

    short readI16LE() throws IOException;

    int readU16LE() throws IOException;

    int readI32LE() throws IOException;

    long readU32LE() throws IOException;

    float readF32LE() throws IOException;

    byte[] readBytes(int length) throws IOException;

    void skipFully(long length) throws IOException;

    String readNullTerminatedString(Charset charset) throws IOException;

    String readFixedString(int length, Charset charset, boolean trimAtNull) throws IOException;

    default String readLengthPrefixedString32LE(Charset charset, boolean trimAtNull) throws IOException {
        int length = readI32LE();
        if (length < 0) {
            throw new IOException("Negative string length: " + length);
        }
        return decode(readBytes(length), charset, trimAtNull);
    }

    private static String decode(byte[] bytes, Charset charset, boolean trimAtNull) {
        int length = bytes.length;
        if (trimAtNull) {
            for (int i = 0; i < bytes.length; i++) {
                if (bytes[i] == 0) {
                    length = i;
                    break;
                }
            }
        }
        return new String(bytes, 0, length, charset);
    }
}
