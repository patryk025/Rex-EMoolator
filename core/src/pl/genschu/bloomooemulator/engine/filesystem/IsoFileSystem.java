package pl.genschu.bloomooemulator.engine.filesystem;

import pl.genschu.bloomooemulator.loader.helpers.SeekableBinaryReader;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Read-only filesystem backed by an ISO9660 / Joliet image.
 */
public class IsoFileSystem implements IFileSystem {
    private static final int SECTOR_SIZE = 2048;
    private static final int PVD_START = 0x8000;
    private static final int VD_TYPE_PRIMARY = 1;
    private static final int VD_TYPE_SUPPLEMENTARY = 2;
    private static final int VD_TYPE_TERMINATOR = 0xFF;
    private static final int ROOT_RECORD_OFFSET = 156;
    private static final int DIR_FLAG_DIRECTORY = 0x02;
    private static final int DIR_FLAG_ASSOCIATED = 0x04;

    private final DataSource source;
    private final Map<String, Entry> entries = new ConcurrentHashMap<>();
    private volatile boolean indexed;

    private record VolumeDescriptor(int rootExtent, long rootSize, int blockSize, Charset nameCharset, boolean joliet) {}

    public IsoFileSystem(File isoFile) {
        this(new FileDataSource(requireNonNullFile(isoFile)));
    }

    public IsoFileSystem(DataSource source) {
        if (source == null) {
            throw new IllegalArgumentException("source cannot be null");
        }
        this.source = source;
        registerDirectory("");
    }

    private static File requireNonNullFile(File isoFile) {
        if (isoFile == null) {
            throw new IllegalArgumentException("isoFile cannot be null");
        }
        return isoFile;
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

        SeekableBinaryReader reader = source.openReader();
        try {
            reader.seek(entry.offset());
        } catch (IOException e) {
            reader.close();
            throw e;
        }
        return new BoundedReaderInputStream(reader, entry.length());
    }

    /** ISO9660 extents are contiguous, so a nested container is a plain slice. */
    @Override
    public DataSource openSource(String path) throws IOException {
        ensureIndexed();

        Entry entry = entries.get(normalize(path));
        if (entry == null || entry.directory()) {
            throw new FileNotFoundException(path);
        }
        return new SlicedDataSource(source, normalize(path), entry.offset(), entry.length());
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

    protected final DataSource getSource() {
        return source;
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
            throw new IllegalStateException("Failed to index ISO image: " + source.name(), e);
        }
    }

    protected void buildIndex() throws IOException {
        try (SeekableBinaryReader reader = source.openReader()) {
            List<VolumeDescriptor> descriptors = readVolumeDescriptors(reader);
            VolumeDescriptor chosen = pickPreferredDescriptor(descriptors);
            if (chosen == null) {
                throw new IOException("No usable ISO9660 volume descriptor in " + source.name());
            }
            walkDirectory(reader, chosen, chosen.rootExtent(), chosen.rootSize(), "");
        }
    }

    private List<VolumeDescriptor> readVolumeDescriptors(SeekableBinaryReader reader) throws IOException {
        List<VolumeDescriptor> descriptors = new ArrayList<>();
        long position = PVD_START;
        long fileLength = reader.length();
        byte[] sector = new byte[SECTOR_SIZE];
        while (position + SECTOR_SIZE <= fileLength) {
            reader.seek(position);
            reader.readFully(sector, 0, sector.length);
            if (sector[1] != 'C' || sector[2] != 'D' || sector[3] != '0' || sector[4] != '0' || sector[5] != '1') {
                break;
            }
            int type = sector[0] & 0xFF;
            if (type == VD_TYPE_TERMINATOR) {
                break;
            }
            VolumeDescriptor descriptor = parseVolumeDescriptor(sector, type);
            if (descriptor != null) {
                descriptors.add(descriptor);
            }
            position += SECTOR_SIZE;
        }
        return descriptors;
    }

    private VolumeDescriptor parseVolumeDescriptor(byte[] sector, int type) {
        if (type != VD_TYPE_PRIMARY && type != VD_TYPE_SUPPLEMENTARY) {
            return null;
        }
        int blockSize = readU16LE(sector, 128);
        if (blockSize <= 0) {
            blockSize = SECTOR_SIZE;
        }
        int rootExtent = readU32LE(sector, ROOT_RECORD_OFFSET + 2);
        long rootSize = readU32LE(sector, ROOT_RECORD_OFFSET + 10) & 0xFFFFFFFFL;
        boolean joliet = type == VD_TYPE_SUPPLEMENTARY && isJolietEscape(sector);
        Charset nameCharset = joliet ? StandardCharsets.UTF_16BE : StandardCharsets.US_ASCII;
        return new VolumeDescriptor(rootExtent, rootSize, blockSize, nameCharset, joliet);
    }

    private static boolean isJolietEscape(byte[] sector) {
        for (int i = 88; i <= 88 + 32 - 3; i++) {
            if (sector[i] == 0x25 && sector[i + 1] == 0x2F) {
                byte level = sector[i + 2];
                if (level == 0x40 || level == 0x43 || level == 0x45) {
                    return true;
                }
            }
        }
        return false;
    }

    private static VolumeDescriptor pickPreferredDescriptor(List<VolumeDescriptor> descriptors) {
        VolumeDescriptor fallback = null;
        for (VolumeDescriptor descriptor : descriptors) {
            if (descriptor.joliet()) {
                return descriptor;
            }
            if (fallback == null) {
                fallback = descriptor;
            }
        }
        return fallback;
    }

    private void walkDirectory(SeekableBinaryReader reader, VolumeDescriptor vd, int extent, long size, String parentPath) throws IOException {
        if (size <= 0) {
            return;
        }
        if (size > Integer.MAX_VALUE) {
            throw new IOException("Directory record block too large: " + size);
        }
        byte[] data = new byte[(int) size];
        reader.seek((long) extent * vd.blockSize());
        reader.readFully(data, 0, data.length);

        int pos = 0;
        while (pos < data.length) {
            int recordLen = data[pos] & 0xFF;
            if (recordLen == 0) {
                int nextBlock = ((pos / vd.blockSize()) + 1) * vd.blockSize();
                if (nextBlock <= pos) {
                    break;
                }
                pos = nextBlock;
                continue;
            }
            if (pos + recordLen > data.length) {
                break;
            }
            int flags = data[pos + 25] & 0xFF;
            int nameLen = data[pos + 32] & 0xFF;
            if (nameLen == 1 && (data[pos + 33] == 0x00 || data[pos + 33] == 0x01)) {
                pos += recordLen;
                continue;
            }
            if ((flags & DIR_FLAG_ASSOCIATED) != 0) {
                pos += recordLen;
                continue;
            }
            int childExtent = readU32LE(data, pos + 2);
            long childSize = readU32LE(data, pos + 10) & 0xFFFFFFFFL;
            byte[] nameBytes = new byte[nameLen];
            System.arraycopy(data, pos + 33, nameBytes, 0, nameLen);
            boolean isDirectory = (flags & DIR_FLAG_DIRECTORY) != 0;
            String name = decodeFileName(nameBytes, vd.nameCharset(), isDirectory);
            String childPath = parentPath.isEmpty() ? name : parentPath + "/" + name;
            if (isDirectory) {
                registerDirectory(childPath);
                walkDirectory(reader, vd, childExtent, childSize, childPath);
            } else {
                registerFile(childPath, (long) childExtent * vd.blockSize(), childSize);
            }
            pos += recordLen;
        }
    }

    private static String decodeFileName(byte[] bytes, Charset charset, boolean isDirectory) {
        String decoded = new String(bytes, charset);
        if (isDirectory) {
            return decoded;
        }
        int versionSeparator = decoded.lastIndexOf(';');
        if (versionSeparator >= 0) {
            decoded = decoded.substring(0, versionSeparator);
        }
        if (decoded.endsWith(".")) {
            decoded = decoded.substring(0, decoded.length() - 1);
        }
        return decoded;
    }

    private static int readU16LE(byte[] data, int offset) {
        return (data[offset] & 0xFF) | ((data[offset + 1] & 0xFF) << 8);
    }

    private static int readU32LE(byte[] data, int offset) {
        return (data[offset] & 0xFF)
                | ((data[offset + 1] & 0xFF) << 8)
                | ((data[offset + 2] & 0xFF) << 16)
                | ((data[offset + 3] & 0xFF) << 24);
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
}
