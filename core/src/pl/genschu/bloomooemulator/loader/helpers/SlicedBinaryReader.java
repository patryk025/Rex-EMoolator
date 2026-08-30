package pl.genschu.bloomooemulator.loader.helpers;

import java.io.EOFException;
import java.io.IOException;
import java.util.Objects;

/**
 * Seekable view over a contiguous byte range of another reader, with positions
 * rebased to the start of the slice. This is what lets a container stored inside
 * another container (an archive on an ISO, say) be parsed without copying it out.
 *
 * The slice takes ownership of the parent reader and closes it.
 */
public final class SlicedBinaryReader extends AbstractSeekableBinaryReader {
    private final SeekableBinaryReader parent;
    private final long base;
    private final long length;
    private long position;

    public SlicedBinaryReader(SeekableBinaryReader parent, long base, long length) {
        this.parent = Objects.requireNonNull(parent, "parent");
        if (base < 0 || length < 0) {
            throw new IllegalArgumentException("Negative slice bounds: base=" + base + ", length=" + length);
        }
        this.base = base;
        this.length = length;
    }

    @Override
    public void readFully(byte[] buffer, int offset, int count) throws IOException {
        if (count < 0) {
            throw new IOException("Negative byte count: " + count);
        }
        if (position + count > length) {
            throw new EOFException("Read past end of slice: position=" + position
                    + ", requested=" + count + ", length=" + length);
        }
        parent.seek(Math.addExact(base, position));
        parent.readFully(buffer, offset, count);
        position += count;
    }

    @Override
    public long position() {
        return position;
    }

    @Override
    public void seek(long position) throws IOException {
        if (position < 0 || position > length) {
            throw new IOException("Seek outside slice: " + position + " (length " + length + ")");
        }
        this.position = position;
    }

    @Override
    public long length() {
        return length;
    }

    @Override
    public void close() throws IOException {
        parent.close();
    }
}
