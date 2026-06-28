package pl.genschu.bloomooemulator.logic;

import com.badlogic.gdx.Gdx;
import org.ini4j.Ini;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.ToLongFunction;

/**
 * Resolves the path (relative to the assets root) of a game's main INI file.
 *
 * Order of trust:
 * 1. bloomoo.ini -> [MAIN].INI (what the original engine itself reads).
 * 2. The game .exe embeds the name of its INI as a null-terminated string;
 *    we extract every "*.ini" candidate and return the first that actually
 *    exists in the filesystem. This is the source of truth when (1) is absent
 *    and the exe name differs from the ini name (e.g. ReksioPiraci.exe ->
 *    Piraci.ini).
 * 3. baseName.ini next to the exe (cheap guess for exe == ini cases).
 * 4. First non-installer .ini in the root.
 *
 * The resolver is decoupled from any concrete filesystem so it can run both at
 * registration time (over an IFileSystem) and at load time (over the VFS).
 */
public final class GameIniResolver {
    private GameIniResolver() {}

    /**
     * Upper bound on bytes a {@link ByteReader} should materialize when scanning a
     * file for an embedded INI name. Piklib/BlooMoo game launchers are tiny (tens
     * to hundreds of KB); a much larger ".exe" is a bundled video demo (e.g.
     * {@code Reksio_ufo.exe} / {@code czarodemo1.exe} on the Tezeusz disc) that never
     * embeds the current game's INI name. Reading such a file whole OOMs on Android's
     * capped heap, so callers skip oversized entries and bound the read.
     */
    public static final long MAX_SCAN_BYTES = 24L * 1024 * 1024;

    /** Reads (up to {@link #MAX_SCAN_BYTES}) the contents of a path. */
    public interface ByteReader {
        byte[] read(String path) throws IOException;
    }

    /** Opens a path for reading (filesystem-agnostic). */
    public interface Opener {
        InputStream open(String path) throws IOException;
    }

    /**
     * Builds a {@link ByteReader} that skips entries larger than {@link #MAX_SCAN_BYTES}
     * and never materializes more than that many bytes, so a bundled video-demo ".exe"
     * (e.g. {@code Reksio_ufo.exe}) can't OOM the INI scan. {@code length} reports an
     * entry's size (anything bigger is skipped without opening it); {@code opener}
     * supplies the bytes, and the read is still bounded as a backstop for filesystems
     * that can't report a length.
     */
    public static ByteReader boundedReader(ToLongFunction<String> length, Opener opener) {
        return path -> {
            if (length.applyAsLong(path) > MAX_SCAN_BYTES) {
                return new byte[0];
            }
            try (InputStream is = opener.open(path)) {
                return readBounded(is, (int) MAX_SCAN_BYTES);
            }
        };
    }

    static byte[] readBounded(InputStream input, int maxBytes) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        byte[] data = new byte[8192];
        int read;
        int total = 0;
        while (total < maxBytes
                && (read = input.read(data, 0, Math.min(data.length, maxBytes - total))) != -1) {
            buffer.write(data, 0, read);
            total += read;
        }
        return buffer.toByteArray();
    }

    public static String resolve(Predicate<String> exists, String[] rootEntries, ByteReader reader) {
        if (rootEntries == null) rootEntries = new String[0];

        // 1. bloomoo.ini points at the real INI via [MAIN].INI.
        if (exists.test("bloomoo.ini")) {
            try {
                Ini ini = new Ini();
                ini.load(new ByteArrayInputStream(reader.read("bloomoo.ini")));
                String mainIni = ini.get("MAIN", "INI");
                if (mainIni != null && !mainIni.isBlank()) {
                    return normalize(mainIni);
                }
            } catch (IOException e) {
                Gdx.app.error("GameIniResolver", "Failed to read bloomoo.ini: " + e.getMessage());
            }
        }

        List<String> exeFiles = new ArrayList<>();
        for (String name : rootEntries) {
            if (isGameExe(name)) exeFiles.add(name);
        }

        // 2. The exe names its own INI; pick the embedded candidate that exists.
        for (String exe : exeFiles) {
            byte[] exeBytes;
            try {
                exeBytes = reader.read(exe);
            } catch (IOException e) {
                Gdx.app.error("GameIniResolver", "Failed to read exe " + exe + ": " + e.getMessage());
                continue;
            }
            for (String candidate : extractIniCandidates(exeBytes)) {
                if (!isInstallerIni(candidate) && exists.test(candidate)) {
                    return candidate;
                }
            }
        }

        // 3. baseName.ini next to the exe.
        for (String exe : exeFiles) {
            String base = stripExtension(exe) + ".ini";
            if (exists.test(base)) return base;
        }

        // 4. First non-installer .ini in the root.
        for (String name : rootEntries) {
            if (name.toLowerCase(Locale.ROOT).endsWith(".ini") && !isInstallerIni(name)) {
                return name;
            }
        }

        return null;
    }

    private static boolean isGameExe(String name) {
        String lower = name.toLowerCase(Locale.ROOT);
        return lower.endsWith(".exe")
                && !lower.equals("setup.exe")
                && !lower.equals("install.exe")
                && !lower.equals("uninstall.exe")
                && !lower.startsWith("unins");
    }

    private static boolean isInstallerIni(String path) {
        String base = baseName(path).toLowerCase(Locale.ROOT);
        return base.equals("setup.ini")
                || base.equals("install.ini")
                || base.equals("uninstall.ini")
                || base.startsWith("unins");
    }

    /**
     * Scans raw exe bytes for null-terminated ASCII strings ending in ".ini".
     * Walking backwards from each ".ini" to the previous non-printable byte
     * recovers the full embedded name (e.g. "Czarodzieje.ini") rather than the
     * first stray ".ini" occurrence in the binary.
     */
    static List<String> extractIniCandidates(byte[] bytes) {
        Set<String> out = new LinkedHashSet<>();
        if (bytes == null) return new ArrayList<>(out);
        for (int i = 0; i + 4 <= bytes.length; i++) {
            if ((bytes[i] == '.')
                    && (bytes[i + 1] == 'i' || bytes[i + 1] == 'I')
                    && (bytes[i + 2] == 'n' || bytes[i + 2] == 'N')
                    && (bytes[i + 3] == 'i' || bytes[i + 3] == 'I')) {
                int end = i + 4;
                int start = i;
                while (start > 0 && isPrintable(bytes[start - 1])) {
                    start--;
                }
                if (start < i) {
                    out.add(normalize(new String(bytes, start, end - start, java.nio.charset.StandardCharsets.US_ASCII)));
                }
            }
        }
        return new ArrayList<>(out);
    }

    private static boolean isPrintable(byte b) {
        return b >= 0x20 && b < 0x7F;
    }

    private static String normalize(String path) {
        return path.replace('\\', '/').replaceFirst("^/+", "");
    }

    private static String baseName(String path) {
        String p = path.replace('\\', '/');
        int slash = p.lastIndexOf('/');
        return slash < 0 ? p : p.substring(slash + 1);
    }

    private static String stripExtension(String name) {
        int dot = name.lastIndexOf('.');
        return dot < 0 ? name : name.substring(0, dot);
    }
}
