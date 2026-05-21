package pl.genschu.bloomooemulator.engine.filesystem;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Read-only filesystem backed by a ZIP archive.
 */
public class ZipFileSystem implements IFileSystem {
    private final File zipFile;
    private final Map<String, Entry> entries = new ConcurrentHashMap<>();
    private volatile boolean indexed;

    public ZipFileSystem(File zipFile) {
        if (zipFile == null) {
            throw new IllegalArgumentException("zipFile cannot be null");
        }
        this.zipFile = zipFile;
        registerDirectory("");
    }

    @Override
    public InputStream open(String path) throws IOException {
        ensureIndexed();

        Entry entry = entries.get(normalize(path));
        if (entry == null || entry.directory()) {
            throw new FileNotFoundException(path);
        }

        ZipFile archive = new ZipFile(zipFile);
        ZipEntry zipEntry = archive.getEntry(entry.zipPath());
        if (zipEntry == null || zipEntry.isDirectory()) {
            archive.close();
            throw new FileNotFoundException(path);
        }
        return new ZipEntryInputStream(archive, archive.getInputStream(zipEntry));
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
            throw new IllegalStateException("Failed to index ZIP archive: " + zipFile, e);
        }
    }

    private void buildIndex() throws IOException {
        try (ZipFile archive = new ZipFile(zipFile)) {
            var zipEntries = archive.entries();
            while (zipEntries.hasMoreElements()) {
                ZipEntry zipEntry = zipEntries.nextElement();
                String normalized = normalize(zipEntry.getName());
                if (normalized.isEmpty()) {
                    continue;
                }

                if (zipEntry.isDirectory()) {
                    registerDirectory(normalized);
                } else {
                    entries.put(normalized, new Entry(false, zipEntry.getName(), zipEntry.getSize()));
                    registerParentDirectories(normalized);
                }
            }
        }
    }

    private void registerDirectory(String path) {
        entries.put(normalize(path), new Entry(true, null, 0));
        registerParentDirectories(path);
    }

    private void registerParentDirectories(String path) {
        String normalized = normalize(path);
        int slash = normalized.lastIndexOf('/');
        while (slash >= 0) {
            String parent = normalized.substring(0, slash);
            entries.putIfAbsent(parent, new Entry(true, null, 0));
            slash = parent.lastIndexOf('/');
        }
        entries.putIfAbsent("", new Entry(true, null, 0));
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

    private record Entry(boolean directory, String zipPath, long length) {
    }

    private static final class ZipEntryInputStream extends InputStream {
        private final ZipFile archive;
        private final InputStream delegate;

        private ZipEntryInputStream(ZipFile archive, InputStream delegate) {
            this.archive = archive;
            this.delegate = delegate;
        }

        @Override
        public int read() throws IOException {
            return delegate.read();
        }

        @Override
        public int read(byte[] buffer, int offset, int length) throws IOException {
            if (length == 0) {
                return 0;
            }

            int totalRead = 0;
            while (totalRead < length) {
                int read = delegate.read(buffer, offset + totalRead, length - totalRead);
                if (read < 0) {
                    return totalRead == 0 ? -1 : totalRead;
                }
                totalRead += read;
            }
            return totalRead;
        }

        @Override
        public long skip(long bytes) throws IOException {
            long skippedTotal = 0;
            while (skippedTotal < bytes) {
                long skipped = delegate.skip(bytes - skippedTotal);
                if (skipped > 0) {
                    skippedTotal += skipped;
                    continue;
                }
                if (delegate.read() < 0) {
                    break;
                }
                skippedTotal++;
            }
            return skippedTotal;
        }

        @Override
        public int available() throws IOException {
            return delegate.available();
        }

        @Override
        public void close() throws IOException {
            try {
                delegate.close();
            } finally {
                archive.close();
            }
        }
    }
}
