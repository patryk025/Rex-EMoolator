package pl.genschu.bloomooemulator.engine.filesystem;

import pl.genschu.bloomooemulator.loader.helpers.SeekableBinaryReader;
import pl.genschu.bloomooemulator.loader.helpers.SlicedBinaryReader;

import java.io.IOException;
import java.util.Objects;

/**
 * A byte range of another {@link DataSource}, exposed as a source of its own.
 *
 * This is the zero-copy path for nesting: an uncompressed, contiguous entry on
 * a mounted image becomes a mountable source without reading it into memory.
 */
public final class SlicedDataSource implements DataSource {
    private final DataSource parent;
    private final String name;
    private final long offset;
    private final long length;

    public SlicedDataSource(DataSource parent, String name, long offset, long length) {
        this.parent = Objects.requireNonNull(parent, "parent");
        this.name = Objects.requireNonNull(name, "name");
        if (offset < 0 || length < 0) {
            throw new IllegalArgumentException("Negative slice bounds: offset=" + offset + ", length=" + length);
        }
        this.offset = offset;
        this.length = length;
    }

    @Override
    public String name() {
        return parent.name() + "!" + name;
    }

    @Override
    public long length() {
        return length;
    }

    @Override
    public SeekableBinaryReader openReader() throws IOException {
        return new SlicedBinaryReader(parent.openReader(), offset, length);
    }
}
