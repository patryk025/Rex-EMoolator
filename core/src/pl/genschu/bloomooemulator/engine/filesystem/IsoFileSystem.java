package pl.genschu.bloomooemulator.engine.filesystem;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Read-only filesystem backed by an ISO image.
 *
 * This is intentionally only a scaffold. The missing piece is building the
 * directory index from ISO9660/Joliet records and registering entries with
 * {@link #registerFile(String, long, long)} and {@link #registerDirectory(String)}.
 */
public class IsoFileSystem implements IFileSystem {
    private final File isoFile;
    private final Map<String, Entry> entries = new ConcurrentHashMap<>();
    private volatile boolean indexed;

    private final int SECTOR_SIZE = 2048; // typically, although the specification allows for alternative sector sizes
    private final int PVD_START = 0x8000;


    public IsoFileSystem(File isoFile) {
        if (isoFile == null) {
            throw new IllegalArgumentException("isoFile cannot be null");
        }
        this.isoFile = isoFile;
        registerDirectory("");
    }

    @Override
    public InputStream open(String path) throws IOException {
        ensureIndexed();

        Entry entry = entries.get(normalize(path));
        if (entry == null || entry.directory()) {
            throw new FileNotFoundException(path);
        }

        if (entry.length() == 0) {
            return new ByteArrayInputStream(new byte[0]);
        }

        RandomAccessFile raf = new RandomAccessFile(isoFile, "r");
        raf.seek(entry.offset());
        return new BoundedRandomAccessInputStream(raf, entry.length());
    }

    @Override
    public boolean exists(String path) {
        ensureIndexedUnchecked();
        return entries.containsKey(normalize(path));
    }

    @Override
    public boolean isDirectory(String path) {
        ensureIndexedUnchecked();
        Entry entry = entries.get(normalize(path));
        return entry != null && entry.directory();
    }

    @Override
    public String[] list(String path) {
        ensureIndexedUnchecked();

        String directory = normalize(path);
        Entry entry = entries.get(directory);
        if (entry == null || !entry.directory()) {
            return null;
        }

        Set<String> names = new LinkedHashSet<>();
        String prefix = directory.isEmpty() ? "" : directory + "/";
        for (String candidate : entries.keySet()) {
            if (candidate.equals(directory) || !candidate.startsWith(prefix)) {
                continue;
            }

            String rest = candidate.substring(prefix.length());
            int slash = rest.indexOf('/');
            names.add(slash >= 0 ? rest.substring(0, slash) : rest);
        }

        ArrayList<String> sorted = new ArrayList<>(names);
        Collections.sort(sorted);
        return sorted.toArray(new String[0]);
    }

    @Override
    public long length(String path) {
        ensureIndexedUnchecked();
        Entry entry = entries.get(normalize(path));
        return entry == null || entry.directory() ? 0 : entry.length();
    }

    protected final File getIsoFile() {
        return isoFile;
    }

    protected final void registerFile(String path, long offset, long length) {
        entries.put(normalize(path), new Entry(false, offset, length));
        registerParentDirectories(path);
    }

    protected final void registerDirectory(String path) {
        entries.put(normalize(path), new Entry(true, 0, 0));
        registerParentDirectories(path);
    }

    private void registerParentDirectories(String path) {
        String normalized = normalize(path);
        int slash = normalized.lastIndexOf('/');
        while (slash >= 0) {
            String parent = normalized.substring(0, slash);
            entries.putIfAbsent(parent, new Entry(true, 0, 0));
            slash = parent.lastIndexOf('/');
        }
        entries.putIfAbsent("", new Entry(true, 0, 0));
    }

    private void ensureIndexed() throws IOException {
        if (indexed) {
            return;
        }

        synchronized (this) {
            if (indexed) {
                return;
            }
            buildIndex();
            indexed = true;
        }
    }

    private void ensureIndexedUnchecked() {
        try {
            ensureIndexed();
        } catch (IOException e) {
            throw new IllegalStateException("Failed to index ISO image: " + isoFile, e);
        }
    }

    /**
     * Parse the ISO directory records here and populate {@link #entries}.
     */
    protected void buildIndex() throws IOException {
        // TODO: parse ISO9660/Joliet directory records and register entries.
        try (RandomAccessFile raf = new RandomAccessFile(isoFile, "r")) {
            raf.seek(PVD_START);
            byte[] pvd = new byte[SECTOR_SIZE];
            raf.readFully(pvd);

        }
    }

    private static String normalize(String path) {
        if (path == null || path.isEmpty()) {
            return "";
        }
        return path
                .replace('\\', '/')
                .replaceAll("/+", "/")
                .replaceFirst("^/+", "")
                .replaceFirst("/+$", "")
                .toLowerCase(Locale.ROOT);
    }

    private record Entry(boolean directory, long offset, long length) {
    }

    private static final class BoundedRandomAccessInputStream extends InputStream {
        private final RandomAccessFile file;
        private long remaining;

        private BoundedRandomAccessInputStream(RandomAccessFile file, long length) {
            this.file = file;
            this.remaining = length;
        }

        @Override
        public int read() throws IOException {
            if (remaining <= 0) {
                return -1;
            }
            int value = file.read();
            if (value >= 0) {
                remaining--;
            }
            return value;
        }

        @Override
        public int read(byte[] buffer, int offset, int length) throws IOException {
            if (remaining <= 0) {
                return -1;
            }
            int toRead = (int) Math.min(length, remaining);
            int read = file.read(buffer, offset, toRead);
            if (read > 0) {
                remaining -= read;
            }
            return read;
        }

        @Override
        public void close() throws IOException {
            file.close();
        }
    }
}
