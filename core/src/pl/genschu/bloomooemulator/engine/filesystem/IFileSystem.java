package pl.genschu.bloomooemulator.engine.filesystem;

import java.io.IOException;
import java.io.InputStream;

/**
 * Read-only access to a filesystem (folder, ISO image, archive...).
 * Implementations should perform case-insensitive path resolution where
 * the underlying medium is case-sensitive (e.g. ISO9660 with Joliet).
 */
public interface IFileSystem {
    InputStream open(String path) throws IOException;
    boolean exists(String path);
    boolean isDirectory(String path);
    String[] list(String path);
    long length(String path);

    /**
     * Opens {@code path} as a randomly-accessible source, so an entry that is
     * itself a container can be handed to {@link AssetSourceDispatcher} and
     * mounted as another layer of the VFS.
     *
     * The default materialises the entry in memory, which is the only option for
     * compressed or fragmented storage. Implementations whose entries are stored
     * contiguously should override with a {@link SlicedDataSource}.
     */
    default DataSource openSource(String path) throws IOException {
        return MemoryDataSource.drain(path, open(path));
    }
}
