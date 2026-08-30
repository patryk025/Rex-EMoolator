package pl.genschu.bloomooemulator.engine.filesystem;

import pl.genschu.bloomooemulator.loader.helpers.SeekableBinaryReader;

import java.io.IOException;
import java.io.InputStream;

/**
 * Streams a fixed-length span starting at the reader's current position, and
 * closes the reader with the stream.
 */
final class BoundedReaderInputStream extends InputStream {
    private final SeekableBinaryReader reader;
    private long remaining;

    BoundedReaderInputStream(SeekableBinaryReader reader, long length) {
        this.reader = reader;
        this.remaining = length;
    }

    @Override
    public int read() throws IOException {
        if (remaining <= 0) {
            return -1;
        }
        int value = reader.readU8();
        remaining--;
        return value;
    }

    @Override
    public int read(byte[] buffer, int offset, int length) throws IOException {
        if (length == 0) {
            return 0;
        }
        if (remaining <= 0) {
            return -1;
        }
        int count = (int) Math.min(length, remaining);
        reader.readFully(buffer, offset, count);
        remaining -= count;
        return count;
    }

    @Override
    public long skip(long count) throws IOException {
        long skipped = Math.min(count, remaining);
        if (skipped <= 0) {
            return 0;
        }
        reader.skipFully(skipped);
        remaining -= skipped;
        return skipped;
    }

    @Override
    public int available() {
        return (int) Math.min(remaining, Integer.MAX_VALUE);
    }

    @Override
    public void close() throws IOException {
        reader.close();
    }
}
