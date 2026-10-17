package pl.genschu.bloomooemulator.engine.filesystem;

import pl.genschu.bloomooemulator.encoding.OggVorbisDecoder;
import pl.genschu.bloomooemulator.loader.helpers.SeekableBinaryReader;
import pl.genschu.bloomooemulator.saver.WaveSaver;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Read-only view of an installer sound archive ({@code *.snd}, e.g.
 * {@code wavs/wav.snd} in Reksio i Kapitan Nemo) as the WAV files the
 * installer would have unpacked next to it.
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
 * Mount this below the source it was found in, so files that really exist
 * (an installed copy) keep priority over the decoded ones.
 */
public final class SndArchiveFileSystem implements IFileSystem {
    private static final Logger LOGGER = Logger.getLogger(SndArchiveFileSystem.class.getName());
    private static final Charset NAME_CHARSET = Charset.forName("windows-1250");
    private static final String EXTENSION = ".snd";

    static final int TYPE_OGG_VORBIS = 0;
    static final int TYPE_SPEEX = 1;

    /** MAX_PATH: setup.exe copies the name into a 0x104-byte stack buffer. */
    private static final int MAX_NAME_LENGTH = 0x104;
    /** Decoded WAVs kept in memory; a sound is typically opened twice in a row (data + header). */
    private static final long CACHE_BUDGET_BYTES = 32L * 1024 * 1024;

    private final DataSource source;
    private final String directory;
    private final Map<String, Entry> entries = new LinkedHashMap<>();
    private final Map<Entry, Long> decodedLengths = new ConcurrentHashMap<>();
    private final LinkedHashMap<Entry, byte[]> cache = new LinkedHashMap<>(16, 0.75f, true);
    private long cachedBytes;

    private record Entry(String name, int type, long offset, long size) {}

    /**
     * @param source    the archive bytes
     * @param directory directory the archive sits in, relative to the mount root;
     *                  entries are exposed inside it
     */
    public SndArchiveFileSystem(DataSource source, String directory) throws IOException {
        this.source = source;
        this.directory = normalize(directory);
        index();
    }

    /**
     * Finds {@code *.snd} archives in the root of {@code base} and its immediate
     * subdirectories. Files that do not parse as a complete archive are skipped,
     * so an unrelated file with the same extension cannot break mounting.
     */
    public static List<SndArchiveFileSystem> discover(IFileSystem base) {
        List<SndArchiveFileSystem> found = new ArrayList<>();
        String[] rootNames = base.list("");
        if (rootNames == null) {
            return found;
        }
        for (String rootName : rootNames) {
            if (base.isDirectory(rootName)) {
                String[] children = base.list(rootName);
                if (children == null) continue;
                for (String child : children) {
                    tryOpen(base, rootName + "/" + child, rootName, found);
                }
            } else {
                tryOpen(base, rootName, "", found);
            }
        }
        return found;
    }

    private static void tryOpen(IFileSystem base, String path, String directory, List<SndArchiveFileSystem> found) {
        if (!path.toLowerCase(Locale.ROOT).endsWith(EXTENSION) || base.isDirectory(path)) {
            return;
        }
        try {
            SndArchiveFileSystem archive = new SndArchiveFileSystem(base.openSource(path), directory);
            LOGGER.info("Mounted sound archive " + path + " (" + archive.entries.size() + " sounds)");
            found.add(archive);
        } catch (IOException | RuntimeException e) {
            LOGGER.log(Level.WARNING, "Ignoring " + path + ": not a sound archive", e);
        }
    }

    private void index() throws IOException {
        int skipped = 0;
        try (SeekableBinaryReader reader = source.openReader()) {
            long length = reader.length();
            long position = 0;
            while (position < length) {
                if (length - position < 12) {
                    throw new IOException("Truncated record header at " + position);
                }
                reader.seek(position);
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
                String name = reader.readFixedString((int) nameLength, NAME_CHARSET, true);
                validateName(name);
                if (type == TYPE_OGG_VORBIS) {
                    Entry entry = new Entry(name, (int) type, dataOffset, size);
                    entries.put(normalize(entryPath(name)), entry);
                } else {
                    skipped++;
                }
                position = dataOffset + size;
            }
        }
        if (skipped > 0) {
            LOGGER.warning(source.name() + ": " + skipped + " Speex record(s) not supported");
        }
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

    @Override
    public InputStream open(String path) throws IOException {
        Entry entry = entries.get(normalize(path));
        if (entry == null) {
            throw new FileNotFoundException(path);
        }
        return new ByteArrayInputStream(decoded(entry));
    }

    @Override
    public boolean exists(String path) {
        String normalized = normalize(path);
        return entries.containsKey(normalized) || isDirectory(normalized);
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
            List<String> names = new ArrayList<>(entries.size());
            for (Entry entry : entries.values()) {
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

    /** Decodes the entry on first use: the PCM size is not stored in the archive. */
    @Override
    public long length(String path) {
        Entry entry = entries.get(normalize(path));
        if (entry == null) {
            return 0;
        }
        Long known = decodedLengths.get(entry);
        if (known != null) {
            return known;
        }
        try {
            return decoded(entry).length;
        } catch (IOException e) {
            throw new IllegalStateException("Failed to decode " + path + " from " + source.name(), e);
        }
    }

    private byte[] decoded(Entry entry) throws IOException {
        synchronized (cache) {
            byte[] cached = cache.get(entry);
            if (cached != null) {
                return cached;
            }
        }

        OggVorbisDecoder.Pcm pcm;
        try (SeekableBinaryReader reader = source.openReader()) {
            reader.seek(entry.offset());
            pcm = OggVorbisDecoder.decode(new BoundedReaderInputStream(reader, entry.size()));
        }
        byte[] wave = WaveSaver.saveInstallerWave(pcm.channels(), pcm.sampleRate(), pcm.bitsPerSample(),
                pcm.samples());
        decodedLengths.put(entry, (long) wave.length);

        synchronized (cache) {
            if (wave.length <= CACHE_BUDGET_BYTES && cache.put(entry, wave) == null) {
                cachedBytes += wave.length;
                Iterator<byte[]> eldest = cache.values().iterator();
                while (cachedBytes > CACHE_BUDGET_BYTES && eldest.hasNext()) {
                    byte[] evicted = eldest.next();
                    if (evicted == wave) break;
                    cachedBytes -= evicted.length;
                    eldest.remove();
                }
            }
        }
        return wave;
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
