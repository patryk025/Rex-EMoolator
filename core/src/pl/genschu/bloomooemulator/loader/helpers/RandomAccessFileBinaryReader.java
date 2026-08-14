package pl.genschu.bloomooemulator.loader.helpers;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.Charset;
import java.util.Objects;
public final class RandomAccessFileBinaryReader implements SeekableBinaryReader {

    private final RandomAccessFile input;

    public RandomAccessFileBinaryReader(RandomAccessFile input) {
        this.input = Objects.requireNonNull(input, "input");
    }

    @Override
    public byte readI8() throws IOException {
        return input.readByte();
    }

    @Override
    public int readU8() throws IOException {
        return input.readUnsignedByte();
    }

    @Override
    public short readI16LE() throws IOException {
        return Short.reverseBytes(input.readShort());
    }

    @Override
    public int readU16LE() throws IOException {
        return Short.toUnsignedInt(
                Short.reverseBytes(input.readShort())
        );
    }

    @Override
    public int readI32LE() throws IOException {
        return Integer.reverseBytes(input.readInt());
    }

    @Override
    public long readU32LE() throws IOException {
        return Integer.toUnsignedLong(
                Integer.reverseBytes(input.readInt())
        );
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
        input.readFully(bytes);
        return bytes;
    }

    @Override
    public void skipFully(long length) throws IOException {
        if (length < 0) {
            throw new IOException("Negative skip length: " + length);
        }

        input.seek(Math.addExact(input.getFilePointer(), length));
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

    @Override
    public long position() throws IOException {
        return input.getFilePointer();
    }

    @Override
    public void seek(long position) throws IOException {
        input.seek(position);
    }
}
