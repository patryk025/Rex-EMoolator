package pl.genschu.bloomooemulator.engine.filesystem;

import pl.genschu.bloomooemulator.encoding.OggVorbisDecoder;
import pl.genschu.bloomooemulator.loader.helpers.BinaryReader;
import pl.genschu.bloomooemulator.loader.helpers.InputStreamBinaryReader;
import pl.genschu.bloomooemulator.saver.WaveSaver;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Read-only view of an installer sound archive ({@code wavs/wav.snd} in
 * Reksio i Kapitan Nemo) as the WAV files the installer would have unpacked
 * next to it.
 *
 * The game engine never reads the archive itself — setup.exe expands it at
 * install time (FUN_0040357f), so a game run straight from the disc has no
 * {@code wavs/*.wav}. The archive is a plain sequence of records:
 *
 * <pre>
 * u32 type      0 = Ogg Vorbis, 1 = Speex
 * u32 size      payload bytes
 * u32 nameLen   including the terminating NUL
 * char name[nameLen]
 * u8  payload[size]
 * </pre>
 *
 * Each record becomes {@code <archive directory>/<name>}, decoded to 16-bit PCM
 * and wrapped in the installer's {@code CWaveFile} layout. Speex records are
 * not exposed (no decoder; none occur in the known archive).
 *
 * The archive is only ever read as a stream through {@link IFileSystem#open},
 * never through {@link IFileSystem#openSource}: on sources that cannot slice
 * (ZIP, fragmented UDF) the latter would copy the whole archive into memory.
 * The index is built on the first lookup inside the archive directory.
 *
 * Mount this below the source it was found in, so files that really exist
 * (an installed copy) keep priority over the decoded ones.
 */
public final class SndArchiveFileSystem implements IFileSystem {
    private static final Logger LOGGER = Logger.getLogger(SndArchiveFileSystem.class.getName());
    private static final Charset ANSI_CHARSET = Charset.forName("windows-1250");

    static final int TYPE_OGG_VORBIS = 0;
    static final int TYPE_SPEEX = 1;

    /** install.ini next to setup.exe; FUN_00408ffa appends "\install.ini" to the module directory. */
    static final String INSTALL_INI = "install.ini";
    /** File list section; FUN_00408ffa preselects "std" and the mode dialog defaults to it. */
    static final String FILE_LIST_SECTION = "std";
    /** FUN_0040291b expands every list entry containing this text (after MakeLower). */
    private static final String ARCHIVE_MARKER = ".snd";

    /** MAX_PATH: setup.exe copies the name into a 0x104-byte stack buffer. */
    private static final int MAX_NAME_LENGTH = 0x104;
    /** Decoded WAVs kept in memory; a sound is typically opened twice in a row (data + header). */
    private static final long CACHE_BUDGET_BYTES = 32L * 1024 * 1024;

    private final IFileSystem base;
    private final String archivePath;
    private final String directory;

    private final Object indexLock = new Object();
    private volatile Map<String, Entry> entries;

    private final LinkedHashMap<Entry, Wave> cache = new LinkedHashMap<>(16, 0.75f, true);
    private final Map<Entry, Long> decodedLengths = new HashMap<>();
    private long cachedBytes;

    private record Entry(String name, long offset, long size) {}

    /** A decoded WAV: {@code data[0..length)} is the file. */
    private record Wave(byte[] data, int length) {}

    /**
     * @param base        filesystem holding the archive
     * @param archivePath path of the archive on {@code base}; entries are exposed
     *                    in its directory
     */
    public SndArchiveFileSystem(IFileSystem base, String archivePath) {
        this.base = base;
        this.archivePath = normalize(archivePath);
        int slash = this.archivePath.lastIndexOf('/');
        this.directory = slash < 0 ? "" : this.archivePath.substring(0, slash);
    }

    /**
     * Finds the sound archives the original installer would unpack: entries of
     * {@code install.ini [std]} whose lower-cased path contains {@code ".snd"}
     * (setup.exe FUN_0040306c builds the list, FUN_0040291b picks archives).
     * Without {@code install.ini} nothing is mounted. Failures are logged and
     * skipped, so a broken or inaccessible entry never stops the game from loading.
     */
    public static List<SndArchiveFileSystem> discover(IFileSystem base) {
        List<SndArchiveFileSystem> found = new ArrayList<>();
        List<String> listed;
        try {
            if (!base.exists(INSTALL_INI) || base.isDirectory(INSTALL_INI)) {
                return found;
            }
            listed = installerFileList(base);
        } catch (IOException | RuntimeException e) {
            LOGGER.log(Level.WARNING, "Cannot read " + INSTALL_INI, e);
            return found;
        }
        for (String path : listed) {
            try {
                if (base.exists(path) && !base.isDirectory(path)) {
                    found.add(new SndArchiveFileSystem(base, path));
                    LOGGER.info("Mounted sound archive " + path);
                } else {
                    LOGGER.info("Sound archive listed in " + INSTALL_INI + " is missing: " + path);
                }
            } catch (RuntimeException e) {
                LOGGER.log(Level.WARNING, "Skipping inaccessible sound archive " + path, e);
            }
        }
        return found;
    }

    /** Archive paths from the installer's file list, with '/' separators. */
    static List<String> installerFileList(IFileSystem base) throws IOException {
        Map<String, String> section;
        try (InputStream in = base.open(INSTALL_INI)) {
            section = readIniSection(in, FILE_LIST_SECTION);
        }
        int count = atoi(section.getOrDefault("files", ""));
        List<String> archives = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            String entry = section.get(Integer.toString(i));
            if (entry == null) continue;
            entry = entry.toLowerCase(Locale.ROOT);
            // Entries ending in '\' are directories (FUN_0040306c).
            if (entry.endsWith("\\")) continue;
            // ";<language>" suffixes restrict an entry to one install language
            // (FUN_0040291b); the filter itself is not modelled, the path is kept.
            int semicolon = entry.indexOf(';');
            if (semicolon >= 0) entry = entry.substring(0, semicolon);
            if (entry.contains(ARCHIVE_MARKER)) {
                archives.add(entry.replace('\\', '/'));
            }
        }
        return archives;
    }

    /**
     * GetPrivateProfileString lookup rules for one section: names are
     * case-insensitive, the first occurrence wins, whitespace around keys and
     * values is trimmed and lines starting with ';' are comments.
     */
    private static Map<String, String> readIniSection(InputStream in, String wanted) throws IOException {
        Map<String, String> values = new LinkedHashMap<>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(in, ANSI_CHARSET));
        boolean inSection = false;
        boolean seen = false;
        String line;
        while ((line = reader.readLine()) != null) {
            String trimmed = line.trim();
            if (trimmed.startsWith("[")) {
                int end = trimmed.indexOf(']');
                String name = (end < 0 ? trimmed.substring(1) : trimmed.substring(1, end)).trim();
                if (seen && inSection) break;
                inSection = name.equalsIgnoreCase(wanted);
                seen |= inSection;
                continue;
            }
            if (!inSection || trimmed.isEmpty() || trimmed.startsWith(";")) continue;
            int eq = trimmed.indexOf('=');
            if (eq < 0) continue;
            String key = trimmed.substring(0, eq).trim().toLowerCase(Locale.ROOT);
            values.putIfAbsent(key, trimmed.substring(eq + 1).trim());
        }
        return values;
    }

    /** C atoi: optional sign and leading digits, 0 when there are none. */
    private static int atoi(String text) {
        String s = text.trim();
        int i = 0;
        boolean negative = false;
        if (i < s.length() && (s.charAt(i) == '+' || s.charAt(i) == '-')) {
            negative = s.charAt(i) == '-';
            i++;
        }
        long value = 0;
        while (i < s.length() && Character.isDigit(s.charAt(i)) && value <= Integer.MAX_VALUE) {
            value = value * 10 + (s.charAt(i) - '0');
            i++;
        }
        return (int) Math.min(Integer.MAX_VALUE, negative ? -value : value);
    }

    /** Entries keyed by normalized full path; an unreadable archive reads as empty. */
    private Map<String, Entry> entries() {
        Map<String, Entry> current = entries;
        if (current != null) {
            return current;
        }
        synchronized (indexLock) {
            if (entries == null) {
                try {
                    entries = index();
                } catch (IOException | RuntimeException e) {
                    LOGGER.log(Level.WARNING, "Ignoring " + archivePath + ": not a sound archive", e);
                    entries = Collections.emptyMap();
                }
            }
            return entries;
        }
    }

    /** One sequential pass that reads record headers and skips payloads. */
    private Map<String, Entry> index() throws IOException {
        Map<String, Entry> result = new LinkedHashMap<>();
        int skipped = 0;
        long length = base.length(archivePath);
        try (InputStream in = base.open(archivePath)) {
            BinaryReader reader = new InputStreamBinaryReader(in);
            long position = 0;
            while (position < length) {
                if (length - position < 12) {
                    throw new IOException("Truncated record header at " + position);
                }
                long type = reader.readU32LE();
                long size = reader.readU32LE();
                long nameLength = reader.readU32LE();
                if (type != TYPE_OGG_VORBIS && type != TYPE_SPEEX) {
                    throw new IOException("Unknown record type " + type + " at " + position);
                }
                if (nameLength < 2 || nameLength > MAX_NAME_LENGTH) {
                    throw new IOException("Bad name length " + nameLength + " at " + position);
                }
                long dataOffset = position + 12 + nameLength;
                if (dataOffset + size > length) {
                    throw new IOException("Record at " + position + " runs past the end of the archive");
                }
                String name = reader.readFixedString((int) nameLength, ANSI_CHARSET, true);
                validateName(name);
                if (type == TYPE_OGG_VORBIS) {
                    // The installer writes records in order: a repeated name overwrites the file.
                    result.put(normalize(entryPath(name)), new Entry(name, dataOffset, size));
                } else {
                    skipped++;
                }
                reader.skipFully(size);
                position = dataOffset + size;
            }
        }
        if (skipped > 0) {
            LOGGER.warning(archivePath + ": " + skipped + " Speex record(s) not supported");
        }
        LOGGER.info("Indexed " + archivePath + " (" + result.size() + " sounds)");
        return result;
    }

    private static void validateName(String name) throws IOException {
        if (name.isEmpty() || name.indexOf('/') >= 0 || name.indexOf('\\') >= 0
                || name.indexOf(':') >= 0 || name.equals(".") || name.equals("..")) {
            throw new IOException("Unsafe entry name: " + name);
        }
    }

    private String entryPath(String name) {
        return directory.isEmpty() ? name : directory + "/" + name;
    }

    /** Only paths directly inside the archive directory can be entries; no index needed otherwise. */
    private Entry find(String path) {
        String normalized = normalize(path);
        int slash = normalized.lastIndexOf('/');
        String parent = slash < 0 ? "" : normalized.substring(0, slash);
        if (!parent.equals(directory)) {
            return null;
        }
        return entries().get(normalized);
    }

    @Override
    public InputStream open(String path) throws IOException {
        Entry entry = find(path);
        if (entry == null) {
            throw new FileNotFoundException(path);
        }
        Wave wave = decoded(entry);
        return new ByteArrayInputStream(wave.data(), 0, wave.length());
    }

    @Override
    public boolean exists(String path) {
        return isDirectory(path) || find(path) != null;
    }

    @Override
    public boolean isDirectory(String path) {
        String normalized = normalize(path);
        if (normalized.isEmpty() || normalized.equals(directory)) {
            return true;
        }
        return directory.startsWith(normalized + "/");
    }

    @Override
    public String[] list(String path) {
        String normalized = normalize(path);
        if (normalized.equals(directory)) {
            List<String> names = new ArrayList<>();
            for (Entry entry : entries().values()) {
                names.add(entry.name());
            }
            Collections.sort(names);
            return names.toArray(new String[0]);
        }
        if (isDirectory(normalized)) {
            String rest = normalized.isEmpty() ? directory : directory.substring(normalized.length() + 1);
            int slash = rest.indexOf('/');
            return new String[] {slash >= 0 ? rest.substring(0, slash) : rest};
        }
        return null;
    }

    /**
     * Decodes the entry on first use: the archive stores only the compressed size.
     * An undecodable entry reports 0 here; {@link #open} then throws.
     */
    @Override
    public long length(String path) {
        Entry entry = find(path);
        if (entry == null) {
            return 0;
        }
        synchronized (cache) {
            Long known = decodedLengths.get(entry);
            if (known != null) {
                return known;
            }
        }
        try {
            return decoded(entry).length();
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Cannot decode " + path + " from " + archivePath, e);
            return 0;
        }
    }

    /** Entries are unique instances from the index, so locking one dedupes concurrent decodes. */
    private Wave decoded(Entry entry) throws IOException {
        synchronized (entry) {
            synchronized (cache) {
                Wave cached = cache.get(entry);
                if (cached != null) {
                    return cached;
                }
            }
            Wave wave = decode(entry);
            synchronized (cache) {
                decodedLengths.put(entry, (long) wave.length());
                if (wave.length() <= CACHE_BUDGET_BYTES && cache.put(entry, wave) == null) {
                    cachedBytes += wave.data().length;
                    Iterator<Wave> eldest = cache.values().iterator();
                    while (cachedBytes > CACHE_BUDGET_BYTES && eldest.hasNext()) {
                        Wave evicted = eldest.next();
                        if (evicted == wave) break;
                        cachedBytes -= evicted.data().length;
                        eldest.remove();
                    }
                }
            }
            return wave;
        }
    }

    /** Decodes straight behind a reserved header, so the PCM is never copied into a second array. */
    private Wave decode(Entry entry) throws IOException {
        WaveBuffer buffer = new WaveBuffer(entry.size() * 8);
        OggVorbisDecoder.Format format;
        try (InputStream in = base.open(archivePath)) {
            skipFully(in, entry.offset());
            format = OggVorbisDecoder.decode(new BoundedInputStream(in, entry.size()), buffer);
        }
        long sampleBytes = buffer.size() - WaveSaver.INSTALLER_HEADER_SIZE;
        if ((sampleBytes & 1) != 0) {
            buffer.write(0);
        }
        byte[] data = buffer.array();
        WaveSaver.writeInstallerHeader(data, format.channels(), format.sampleRate(), format.bitsPerSample(),
                sampleBytes);
        return new Wave(data, buffer.size());
    }

    private static void skipFully(InputStream in, long count) throws IOException {
        long remaining = count;
        while (remaining > 0) {
            long skipped = in.skip(remaining);
            if (skipped > 0) {
                remaining -= skipped;
            } else if (in.read() >= 0) {
                remaining--;
            } else {
                throw new EOFException("Archive ended before offset " + count);
            }
        }
    }

    /** Output buffer with the installer header reserved and its backing array exposed. */
    private static final class WaveBuffer extends ByteArrayOutputStream {
        WaveBuffer(long expected) {
            super((int) Math.min(Math.max(expected, 4096), 16L * 1024 * 1024));
            count = WaveSaver.INSTALLER_HEADER_SIZE;
        }

        byte[] array() {
            return buf;
        }
    }

    /** Limits reads to one record's payload without closing the archive stream. */
    private static final class BoundedInputStream extends InputStream {
        private final InputStream in;
        private long remaining;

        BoundedInputStream(InputStream in, long length) {
            this.in = in;
            this.remaining = length;
        }

        @Override
        public int read() throws IOException {
            if (remaining <= 0) return -1;
            int value = in.read();
            if (value >= 0) remaining--;
            return value;
        }

        @Override
        public int read(byte[] b, int off, int len) throws IOException {
            if (len == 0) return 0;
            if (remaining <= 0) return -1;
            int read = in.read(b, off, (int) Math.min(len, remaining));
            if (read > 0) remaining -= read;
            return read;
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
}
