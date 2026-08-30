package pl.genschu.bloomooemulator.engine.filesystem;

import pl.genschu.bloomooemulator.loader.helpers.RandomAccessFileBinaryReader;
import pl.genschu.bloomooemulator.loader.helpers.SeekableBinaryReader;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Objects;

/** {@link DataSource} backed by a file on disk. */
public final class FileDataSource implements DataSource {
    private final File file;

    public FileDataSource(File file) {
        this.file = Objects.requireNonNull(file, "file");
    }

    @Override
    public String name() {
        return file.getPath();
    }

    @Override
    public long length() {
        return file.length();
    }

    @Override
    public SeekableBinaryReader openReader() throws IOException {
        return new RandomAccessFileBinaryReader(new RandomAccessFile(file, "r"));
    }

    @Override
    public File asFile() {
        return file;
    }
}
