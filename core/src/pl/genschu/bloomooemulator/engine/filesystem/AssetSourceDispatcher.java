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

        String name = path.getName().toLowerCase(Locale.ROOT);
        // TODO: implement more robust checking
        if (name.endsWith(".iso")) {
            return new IsoFileSystem(path);
        }

        throw new IOException("Unsupported game asset source: " + path);
    }
}
