package pl.genschu.bloomooemulator.engine.filesystem;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

public final class AssetSourceDispatcher {
    private AssetSourceDispatcher() {}

    public static IFileSystem openAssets(File path) throws IOException {
        if (path.isDirectory()) {
            return new LocalFileSystem(path);
        }

        FileSystemType type = FileSystemDetector.detectFileSystemType(path);

        switch (type) {
            case DIRECTORY -> {
                return new LocalFileSystem(path);
            }
            case ZIP -> {
                return new ZipFileSystem(path);
            }
            case ISO9660 -> {
                return new IsoFileSystem(path);
            }
            case UDF -> {
                return new UdfFileSystem(path);
            }
            default -> throw new IOException("Unsupported game asset source: " + path);
        }
    }
}
