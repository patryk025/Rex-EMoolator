package pl.genschu.bloomooemulator.loader.helpers;

import java.io.Closeable;
import java.io.IOException;

/**
 * A {@link BinaryReader} with a movable cursor.
 *
 * Readers own whatever handle they were opened on (a file, a slice of a parent
 * reader, ...), so callers must close them — typically via try-with-resources.
 */
public interface SeekableBinaryReader extends BinaryReader, Closeable {

    long position() throws IOException;

    void seek(long position) throws IOException;

    @Override
    void close() throws IOException;
}
