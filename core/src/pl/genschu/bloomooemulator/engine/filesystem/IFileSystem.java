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
}
