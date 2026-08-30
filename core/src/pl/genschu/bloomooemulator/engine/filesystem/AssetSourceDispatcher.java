package pl.genschu.bloomooemulator.engine.filesystem;

import java.io.File;
import java.io.IOException;

public final class AssetSourceDispatcher {
    private AssetSourceDispatcher() {}

    public static IFileSystem openAssets(File path) throws IOException {
        if (path.isDirectory()) {
            return new LocalFileSystem(path);
        }
        return openAssets(new FileDataSource(path));
    }

    /**
     * Mounts any seekable source, so a container found inside an already mounted
     * filesystem — {@code fs.openSource("game.pik")} — goes through the very same
     * path as a file picked off disk.
     */
    public static IFileSystem openAssets(DataSource source) throws IOException {
        FileSystemType type = FileSystemDetector.detectFileSystemType(source);

        switch (type) {
            case ZIP -> {
                return new ZipFileSystem(source);
            }
            case ISO9660 -> {
                return new IsoFileSystem(source);
            }
            case UDF -> {
                return new UdfFileSystem(source);
            }
            default -> throw new IOException("Unsupported game asset source: " + source.name());
        }
    }
}
