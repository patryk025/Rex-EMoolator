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
 *
 * All operations reject absolute paths and paths resolving outside the root
 * with SecurityException. Canonical containment is rechecked on cache hits.
 * This assumes the host filesystem is not concurrently modified by an attacker
 * between validation and IO; it is not a race-free OS sandbox.
 */
public class LocalFileSystem implements IWritableFileSystem {
    private final File root;
    private final Map<String, File> resolveCache = new ConcurrentHashMap<>();

    public LocalFileSystem(File root) {
        try {
            this.root = root.getCanonicalFile();
        } catch (IOException e) {
            throw new IllegalArgumentException("Cannot resolve filesystem root", e);
        }
    }

    private File resolve(String path) {
        if (path == null || path.isEmpty()) return checked(root);
        return checked(resolveCache.computeIfAbsent(path, this::resolveUncached));
    }

    private File resolveUncached(String path) {
        String normalized = relativePath(path);
        File direct = checked(new File(root, normalized));
        if (direct.exists()) return direct;

        File current = root;
        for (String segment : normalized.split("/")) {
            if (segment.isEmpty()) continue;
            File match = null;
            File[] entries = checked(current).listFiles();
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
        return checked(current);
    }

    private String relativePath(String path) {
        String normalized = path.replace('\\', '/');
        if (normalized.startsWith("/") || normalized.indexOf(':') >= 0
                || normalized.indexOf('\0') >= 0) {
            throw new SecurityException("Expected a relative filesystem path");
        }
        return normalized;
    }

    /** Recheck even cached paths: a directory may have become a symlink. */
    private File checked(File file) {
        try {
            String base = root.getPath();
            String target = file.getCanonicalPath();
            String prefix = base.endsWith(File.separator) ? base : base + File.separator;
            if (!target.equals(base) && !target.startsWith(prefix)) {
                throw new SecurityException("Path escapes filesystem root");
            }
            return file;
        } catch (IOException e) {
            throw new SecurityException("Cannot validate filesystem path", e);
        }
    }

    @Override
    public InputStream open(String path) throws IOException {
        return new FileInputStream(resolve(path));
    }

    @Override
    public DataSource openSource(String path) {
        return new FileDataSource(resolve(path));
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
        File file = checked(new File(root, relativePath(path)));
        File parent = file.getParentFile();
        if (parent != null && !parent.exists() && !parent.mkdirs()) {
            throw new IOException("Failed to create directory: " + parent);
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
        resolveCache.remove(path);
        return checked(new File(root, relativePath(path))).mkdirs();
    }
}
