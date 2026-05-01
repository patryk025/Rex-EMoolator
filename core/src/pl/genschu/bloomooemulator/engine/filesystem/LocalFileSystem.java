package pl.genschu.bloomooemulator.engine.filesystem;

import java.io.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

/**
 * IFileSystem backed by a regular folder on disk.
 *
 * Path lookups are case-insensitive — game scripts written for the
 * original Windows engine often disagree with on-disk casing, especially
 * once the data is unpacked to a Linux/macOS host. Resolved paths are
 * cached per VFS-relative input string.
 */
public class LocalFileSystem implements IWritableFileSystem {
    private final File root;
    private final Map<String, File> resolveCache = new ConcurrentHashMap<>();

    public LocalFileSystem(File root) {
        this.root = root;
    }

    private File resolve(String path) {
        if (path == null || path.isEmpty()) return root;
        return resolveCache.computeIfAbsent(path, this::resolveUncached);
    }

    private File resolveUncached(String path) {
        String normalized = path.replace('\\', '/');
        File direct = new File(root, normalized.replace('/', File.separatorChar));
        if (direct.exists()) return direct;

        File current = root;
        for (String segment : normalized.split("/")) {
            if (segment.isEmpty()) continue;
            File match = null;
            File[] entries = current.listFiles();
            if (entries == null) return direct;
            for (File entry : entries) {
                if (entry.getName().equalsIgnoreCase(segment)) {
                    match = entry;
                    break;
                }
            }
            if (match == null) return direct; // first miss → return non-existent path
            current = match;
        }
        return current;
    }

    @Override
    public InputStream open(String path) throws IOException {
        return new FileInputStream(resolve(path));
    }

    @Override
    public boolean exists(String path) {
        return resolve(path).exists();
    }

    @Override
    public boolean isDirectory(String path) {
        return resolve(path).isDirectory();
    }

    @Override
    public String[] list(String path) {
        return resolve(path).list();
    }

    @Override
    public long length(String path) {
        return resolve(path).length();
    }

    @Override
    public OutputStream openWrite(String path) throws IOException {
        // Writes use the literal path (no case folding) and create parents.
        File file = new File(root, path.replace('\\', File.separatorChar).replace('/', File.separatorChar));
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }
        resolveCache.remove(path); // future reads should see the new file
        return new FileOutputStream(file);
    }

    @Override
    public boolean delete(String path) {
        File f = resolve(path);
        resolveCache.remove(path);
        return f.delete();
    }

    @Override
    public boolean mkdirs(String path) {
        return new File(root, path.replace('\\', File.separatorChar).replace('/', File.separatorChar)).mkdirs();
    }
}
