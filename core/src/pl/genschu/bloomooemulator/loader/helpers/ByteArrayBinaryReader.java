package pl.genschu.bloomooemulator.loader.helpers;

import java.io.EOFException;
import java.io.IOException;
import java.util.Objects;

/** Seekable reader over an in-memory buffer. */
public final class ByteArrayBinaryReader extends AbstractSeekableBinaryReader {
    private final byte[] data;
    private int position;

    public ByteArrayBinaryReader(byte[] data) {
        this.data = Objects.requireNonNull(data, "data");
    }

    @Override
    public void readFully(byte[] buffer, int offset, int length) throws IOException {
        if (length < 0) {
            throw new IOException("Negative byte count: " + length);
        }
        if ((long) position + length > data.length) {
            throw new EOFException("Read past end of buffer: position=" + position
                    + ", requested=" + length + ", length=" + data.length);
        }
        System.arraycopy(data, position, buffer, offset, length);
        position += length;
    }

    @Override
    public long position() {
        return position;
    }

    @Override
    public void seek(long position) throws IOException {
        if (position < 0 || position > data.length) {
            throw new IOException("Seek outside buffer: " + position + " (length " + data.length + ")");
        }
        this.position = (int) position;
    }

    @Override
    public long length() {
        return data.length;
    }

    @Override
    public void close() {
        // nothing to release
    }
}
