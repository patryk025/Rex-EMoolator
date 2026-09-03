package pl.genschu.bloomooemulator.engine.filesystem;

import pl.genschu.bloomooemulator.loader.helpers.ByteArrayBinaryReader;
import pl.genschu.bloomooemulator.loader.helpers.SeekableBinaryReader;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

/**
 * A {@link DataSource} held in memory.
 *
 * The fallback for nesting on top of storage that cannot be sliced — compressed
 * ZIP entries, fragmented UDF extents — so it materialises the whole entry.
 * Prefer {@link SlicedDataSource} wherever the bytes are contiguous.
 */
public final class MemoryDataSource implements DataSource {
    private final String name;
    private final byte[] data;

    public MemoryDataSource(String name, byte[] data) {
        this.name = Objects.requireNonNull(name, "name");
        this.data = Objects.requireNonNull(data, "data");
    }

    /** Drains {@code input} into memory, closing it. */
    public static MemoryDataSource drain(String name, InputStream input) throws IOException {
        try (InputStream stream = input;
             java.io.ByteArrayOutputStream buffer = new java.io.ByteArrayOutputStream()) {
            byte[] chunk = new byte[8192];
            int read;
            while ((read = stream.read(chunk)) != -1) {
                buffer.write(chunk, 0, read);
            }
            return new MemoryDataSource(name, buffer.toByteArray());
        }
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public long length() {
        return data.length;
    }

    @Override
    public SeekableBinaryReader openReader() {
        return new ByteArrayBinaryReader(data);
    }
}
