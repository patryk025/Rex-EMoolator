package pl.genschu.bloomooemulator.engine.filesystem;

import com.badlogic.gdx.files.FileHandle;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Virtual filesystem managing prioritized read-only asset sources plus a
 * single writable storage layer.
 *
 * Lookup order for {@link #openRead(String)}:
 *   1. storage (overrides game data)
 *   2. asset sources, last-mounted first
 *
 * If a language code is set, every layer is probed with {@code <lang>/<path>}
 * before falling back to the bare path — this mirrors the localization
 * convention used by the original engine.
 */
public class VFS {
    private final List<IFileSystem> assetsSources = new ArrayList<>();
    private IWritableFileSystem storage;
    private String language;

    /** Adds an asset source. Sources mounted later have higher priority. */
    public void mountAssets(IFileSystem source) {
        assetsSources.add(0, source);
    }

    public void setStorage(IWritableFileSystem storage) {
        this.storage = storage;
    }

    public IWritableFileSystem getStorage() {
        return storage;
    }

    public void setLanguage(String language) {
        this.language = (language == null || language.isEmpty()) ? null : language;
    }

    public InputStream openRead(String path) throws IOException {
        IFileSystem source = findSource(path);
        if (source == null) throw new IOException("Resource not found: " + path);
        return source.open(resolveOn(source, path));
    }

    public OutputStream openWrite(String path) throws IOException {
        if (storage == null) throw new IOException("No writable storage configured");
        return storage.openWrite(path);
    }

    public boolean exists(String path) {
        return findSource(path) != null;
    }

    public boolean isDirectory(String path) {
        IFileSystem source = findSource(path);
        return source != null && source.isDirectory(resolveOn(source, path));
    }

    /**
     * Lists entries at {@code path} across all layers (storage + assets),
     * deduplicated by name. The storage layer is queried first so writes
     * under it become visible, but its names don't shadow asset entries
     * that happen to share a parent directory.
     */
    public String[] list(String path) {
        java.util.LinkedHashSet<String> names = new java.util.LinkedHashSet<>();
        if (storage != null) {
            String resolved = resolveOn(storage, path);
            if (resolved != null) addAll(names, storage.list(resolved));
        }
        for (IFileSystem source : assetsSources) {
            String resolved = resolveOn(source, path);
            if (resolved != null) addAll(names, source.list(resolved));
        }
        return names.toArray(new String[0]);
    }

    private static void addAll(java.util.Set<String> dst, String[] src) {
        if (src == null) return;
        java.util.Collections.addAll(dst, src);
    }

    public long length(String path) {
        IFileSystem source = findSource(path);
        return source == null ? 0 : source.length(resolveOn(source, path));
    }

    public FileHandle getFileHandle(String path) {
        return new VFSFileHandle(this, path);
    }

    private IFileSystem findSource(String path) {
        if (storage != null) {
            String hit = resolveOn(storage, path);
            if (hit != null) return storage;
        }
        for (IFileSystem source : assetsSources) {
            String hit = resolveOn(source, path);
            if (hit != null) return source;
        }
        return null;
    }

    /**
     * Returns the actual path that exists on the given source, trying the
     * language-prefixed variant first. Returns {@code null} if nothing matches.
     */
    private String resolveOn(IFileSystem source, String path) {
        if (language != null) {
            String localized = language + "/" + path;
            if (source.exists(localized)) return localized;
        }
        if (source.exists(path)) return path;
        return null;
    }
}
