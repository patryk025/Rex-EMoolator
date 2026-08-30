package pl.genschu.bloomooemulator.loader.helpers;

import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.nio.charset.Charset;

/**
 * Base for {@link SeekableBinaryReader} implementations: decodes every primitive
 * on top of a single bulk read plus cursor management, so a new backing medium
 * only has to supply {@link #readFully}, {@link #position()}, {@link #seek(long)},
 * {@link #length()} and {@link #close()}.
 */
public abstract class AbstractSeekableBinaryReader implements SeekableBinaryReader {
    private final byte[] scratch = new byte[8];

    /** Reads exactly {@code length} bytes and advances the cursor, or throws {@link EOFException}. */
    @Override
    public abstract void readFully(byte[] buffer, int offset, int length) throws IOException;

    @Override
    public byte readI8() throws IOException {
        return (byte) readU8();
    }

    @Override
    public int readU8() throws IOException {
        readFully(scratch, 0, 1);
        return scratch[0] & 0xFF;
    }

    @Override
    public short readI16LE() throws IOException {
        return (short) readU16LE();
    }

    @Override
    public int readU16LE() throws IOException {
        readFully(scratch, 0, 2);
        return (scratch[0] & 0xFF) | ((scratch[1] & 0xFF) << 8);
    }

    @Override
    public int readI32LE() throws IOException {
        readFully(scratch, 0, 4);
        return (scratch[0] & 0xFF)
                | ((scratch[1] & 0xFF) << 8)
                | ((scratch[2] & 0xFF) << 16)
                | ((scratch[3] & 0xFF) << 24);
    }

    @Override
    public long readU32LE() throws IOException {
        return readI32LE() & 0xFFFF_FFFFL;
    }

    @Override
    public float readF32LE() throws IOException {
        return Float.intBitsToFloat(readI32LE());
    }

    @Override
    public long readI64LE() throws IOException {
        long low = readU32LE();
        long high = readU32LE();
        return low | (high << 32);
    }

    @Override
    public long readU64LE() throws IOException {
        // so... Java doesn't have unsigned longs, so we just read it as a signed long and treat it as unsigned
        return readI64LE();
    }

    @Override
    public byte[] readBytes(int length) throws IOException {
        if (length < 0) {
            throw new IOException("Negative byte count: " + length);
        }
        byte[] bytes = new byte[length];
        readFully(bytes, 0, length);
        return bytes;
    }

    @Override
    public void skipFully(long count) throws IOException {
        if (count < 0) {
            throw new IOException("Negative skip length: " + count);
        }
        long target = Math.addExact(position(), count);
        if (target > length()) {
            throw new EOFException("Skip past end: position=" + target + ", length=" + length());
        }
        seek(target);
    }

    @Override
    public String readNullTerminatedString(Charset charset) throws IOException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        int value;
        while ((value = readU8()) != 0) {
            bytes.write(value);
        }
        return new String(bytes.toByteArray(), charset);
    }

    @Override
    public String readFixedString(int length, Charset charset, boolean trimAtNull) throws IOException {
        byte[] bytes = readBytes(length);
        int decodedLength = bytes.length;
        if (trimAtNull) {
            for (int i = 0; i < bytes.length; i++) {
                if (bytes[i] == 0) {
                    decodedLength = i;
                    break;
                }
            }
        }
        return new String(bytes, 0, decodedLength, charset);
    }
}
