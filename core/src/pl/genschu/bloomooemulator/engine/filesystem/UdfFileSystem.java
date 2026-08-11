package pl.genschu.bloomooemulator.engine.filesystem;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class UdfFileSystem implements IFileSystem {
    private static final int SECTOR_SIZE = 2048;

    private final File isoFile;

    public UdfFileSystem(File isoFile) {
        if (isoFile == null) {
            throw new IllegalArgumentException("isoFile cannot be null");
        }
        this.isoFile = isoFile;
    }

    @Override
    public InputStream open(String path) throws IOException {
        return null;
    }

    @Override
    public boolean exists(String path) {
        return false;
    }

    @Override
    public boolean isDirectory(String path) {
        return false;
    }

    @Override
    public String[] list(String path) {
        return new String[0];
    }

    @Override
    public long length(String path) {
        return 0;
    }

    private record Entry(boolean directory, long offset, long length) {}
}
