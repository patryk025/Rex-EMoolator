package pl.genschu.bloomooemulator.engine.filesystem;

import java.io.IOException;
import java.io.OutputStream;

/**
 * A filesystem that also supports writes. Reads should still work via
 * the inherited {@link IFileSystem} contract — overrides written through
 * this filesystem must be visible to subsequent {@code open()} calls.
 */
public interface IWritableFileSystem extends IFileSystem {
    OutputStream openWrite(String path) throws IOException;
    boolean delete(String path);
    boolean mkdirs(String path);
}
