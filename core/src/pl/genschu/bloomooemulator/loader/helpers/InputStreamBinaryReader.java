package pl.genschu.bloomooemulator.loader.helpers;

import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Objects;

public final class InputStreamBinaryReader implements BinaryReader {
    private final InputStream input;

    public InputStreamBinaryReader(InputStream input) {
        this.input = Objects.requireNonNull(input, "input");
    }

    @Override
    public byte readI8() throws IOException {
        return (byte) readU8();
    }

    @Override
    public int readU8() throws IOException {
        int value = input.read();
        if (value < 0) {
            throw new EOFException("Unexpected end of stream");
        }
        return value;
    }

    @Override
    public short readI16LE() throws IOException {
        return (short) readU16LE();
    }

    @Override
    public int readU16LE() throws IOException {
        return readU8() | (readU8() << 8);
    }

    @Override
    public int readI32LE() throws IOException {
        return readU8()
                | (readU8() << 8)
                | (readU8() << 16)
                | (readU8() << 24);
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
    public byte[] readBytes(int length) throws IOException {
        if (length < 0) {
            throw new IOException("Negative byte count: " + length);
        }
        byte[] bytes = new byte[length];
        int offset = 0;
        while (offset < length) {
            int read = input.read(bytes, offset, length - offset);
            if (read < 0) {
                throw new EOFException("Unexpected end of stream: read " + offset + " of " + length + " bytes");
            }
            if (read == 0) {
                bytes[offset++] = (byte) readU8();
            } else {
                offset += read;
            }
        }
        return bytes;
    }

    @Override
    public void skipFully(long length) throws IOException {
        if (length < 0) {
            throw new IOException("Negative skip length: " + length);
        }
        long remaining = length;
        while (remaining > 0) {
            long skipped = input.skip(remaining);
            if (skipped > 0) {
                remaining -= skipped;
            } else {
                readU8();
                remaining--;
            }
        }
    }

    @Override
    public String readNullTerminatedString(Charset charset) throws IOException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        int value;
        while ((value = readU8()) != 0) {
            bytes.write(value);
        }
        return bytes.toString(charset);
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
