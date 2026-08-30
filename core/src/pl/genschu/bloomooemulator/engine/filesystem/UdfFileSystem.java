package pl.genschu.bloomooemulator.engine.filesystem;

import pl.genschu.bloomooemulator.loader.helpers.SeekableBinaryReader;

import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static pl.genschu.bloomooemulator.engine.filesystem.UdfReader.DataSegment;
import static pl.genschu.bloomooemulator.engine.filesystem.UdfReader.Entry;

/** Read-only filesystem backed by a Universal Disk Format image. */
public class UdfFileSystem implements IFileSystem {
    private final DataSource source;
    private final Map<String, Entry> entries;

    public UdfFileSystem(File image) {
        this(toSource(image));
    }

    public UdfFileSystem(DataSource source) {
        if (source == null) {
            throw new IllegalArgumentException("source cannot be null");
        }
        this.source = source;
        try {
            entries = new UdfReader(source).readIndex();
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to index UDF image: " + source.name(), e);
        }
    }

    private static DataSource toSource(File image) {
        if (image == null) {
            throw new IllegalArgumentException("image cannot be null");
        }
        return new FileDataSource(image);
    }

    @Override
    public InputStream open(String path) throws IOException {
        Entry entry = entries.get(normalize(path));
        if (entry == null || entry.directory()) {
            throw new FileNotFoundException(path);
        }
        if (entry.inlineData() != null) {
            return new ByteArrayInputStream(entry.inlineData());
        }
        if (entry.length() == 0) {
            return new ByteArrayInputStream(new byte[0]);
        }
        return new SegmentedReaderInputStream(
                source.openReader(),
                entry.segments(),
                entry.length()
        );
    }

    /**
     * Slices the entry when its data sits in one recorded extent — the usual
     * layout — and otherwise falls back to buffering it via {@link IFileSystem}.
     */
    @Override
    public DataSource openSource(String path) throws IOException {
        Entry entry = entries.get(normalize(path));
        if (entry == null || entry.directory()) {
            throw new FileNotFoundException(path);
        }
        if (entry.inlineData() == null
                && entry.segments().size() == 1
                && entry.segments().get(0).recorded()
                && entry.segments().get(0).length() >= entry.length()) {
            return new SlicedDataSource(
                    source,
                    normalize(path),
                    entry.segments().get(0).offset(),
                    entry.length()
            );
        }
        return IFileSystem.super.openSource(path);
    }

    @Override
    public boolean exists(String path) {
        return entries.containsKey(normalize(path));
    }

    @Override
    public boolean isDirectory(String path) {
        Entry entry = entries.get(normalize(path));
        return entry != null && entry.directory();
    }

    @Override
    public String[] list(String path) {
        String directory = normalize(path);
        Entry directoryEntry = entries.get(directory);
        if (directoryEntry == null || !directoryEntry.directory()) {
            return null;
        }

        String prefix = directory.isEmpty() ? "" : directory + "/";
        Set<String> childKeys = new LinkedHashSet<>();
        for (String candidate : entries.keySet()) {
            if (candidate.equals(directory) || !candidate.startsWith(prefix)) {
                continue;
            }
            String remainder = candidate.substring(prefix.length());
            int slash = remainder.indexOf('/');
            childKeys.add(slash < 0 ? candidate : prefix + remainder.substring(0, slash));
        }

        List<String> names = new ArrayList<>(childKeys.size());
        for (String childKey : childKeys) {
            Entry child = entries.get(childKey);
            if (child != null) {
                names.add(child.name());
            }
        }
        names.sort(String.CASE_INSENSITIVE_ORDER);
        return names.toArray(new String[0]);
    }

    @Override
    public long length(String path) {
        Entry entry = entries.get(normalize(path));
        return entry == null || entry.directory() ? 0 : entry.length();
    }

    public static String decodeOstaCompressedUnicode(byte[] data) throws IOException {
        return UdfStructures.decodeOstaCompressedUnicode(data);
    }

    static String normalize(String path) {
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

    private static final class SegmentedReaderInputStream extends InputStream {
        private final SeekableBinaryReader reader;
        private final List<DataSegment> segments;
        private long remaining;
        private int segmentIndex;
        private long positionInSegment;
        private boolean positioned;

        private SegmentedReaderInputStream(
                SeekableBinaryReader reader,
                List<DataSegment> segments,
                long length
        ) {
            this.reader = reader;
            this.segments = Collections.unmodifiableList(new ArrayList<>(segments));
            this.remaining = length;
        }

        @Override
        public int read() throws IOException {
            byte[] one = new byte[1];
            return read(one, 0, 1) < 0 ? -1 : one[0] & 0xFF;
        }

        @Override
        public int read(byte[] buffer, int offset, int length) throws IOException {
            Objects.checkFromIndexSize(offset, length, buffer.length);
            if (length == 0) {
                return 0;
            }
            if (remaining == 0) {
                return -1;
            }

            int total = 0;
            while (length > 0 && remaining > 0) {
                DataSegment segment = currentSegment();
                int count = (int) Math.min(
                        Math.min(length, remaining),
                        segment.length() - positionInSegment
                );
                if (segment.recorded()) {
                    if (!positioned) {
                        reader.seek(Math.addExact(segment.offset(), positionInSegment));
                        positioned = true;
                    }
                    reader.readFully(buffer, offset, count);
                } else {
                    java.util.Arrays.fill(buffer, offset, offset + count, (byte) 0);
                }

                offset += count;
                length -= count;
                total += count;
                remaining -= count;
                positionInSegment += count;
                if (positionInSegment == segment.length()) {
                    segmentIndex++;
                    positionInSegment = 0;
                    positioned = false;
                }
            }
            return total;
        }

        private DataSegment currentSegment() throws EOFException {
            while (segmentIndex < segments.size()
                    && positionInSegment == segments.get(segmentIndex).length()) {
                segmentIndex++;
                positionInSegment = 0;
                positioned = false;
            }
            if (segmentIndex >= segments.size()) {
                throw new EOFException("UDF allocation descriptors do not cover file length");
            }
            return segments.get(segmentIndex);
        }

        @Override
        public void close() throws IOException {
            reader.close();
        }
    }
}
