package pl.genschu.bloomooemulator.engine.filesystem;

import pl.genschu.bloomooemulator.loader.helpers.SeekableBinaryReader;

import java.io.File;
import java.io.IOException;

/**
 * A named, randomly-accessible blob of bytes that an {@link IFileSystem} can be
 * built on top of.
 *
 * Decoupling the container parsers from {@link File} is what makes mounting
 * nestable: a source can be a file on disk, a byte range of an image already
 * mounted in the VFS, or a buffer — so an archive stored inside an ISO is
 * mounted exactly like one sitting in a folder.
 *
 * Every {@link #openReader()} call hands out an independent cursor, because
 * filesystems keep several entry streams open at once. The caller closes it.
 */
public interface DataSource {
    /** Human-readable identifier, used in error messages and format detection. */
    String name();

    long length() throws IOException;

    /** Opens a fresh, independently positioned reader. The caller must close it. */
    SeekableBinaryReader openReader() throws IOException;

    /**
     * The backing file, when this source is a plain file on disk — {@code null}
     * otherwise. Exists for parsers that cannot work off a cursor alone
     * (java.util.zip needs a real file for random access into an archive).
     */
    default File asFile() {
        return null;
    }
}
