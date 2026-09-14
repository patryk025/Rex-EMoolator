package pl.genschu.bloomooemulator.logic;

import org.ini4j.Ini;
import pl.genschu.bloomooemulator.engine.filesystem.IFileSystem;
import pl.genschu.bloomooemulator.engine.filesystem.SubdirectoryFileSystem;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/** Disambiguates known shared engine DLLs using files in the selected game root. */
public final class GameIdentityResolver {
    public static final String HERKULES_ODYSEUSZ_HASH = "E80093DE96363524A209BBB80901458D9D524925";

    private record Rule(String executable, String ini, String name) {}

    private static final Map<String, List<Rule>> RULES = Map.of(
            HERKULES_ODYSEUSZ_HASH, List.of(
                    new Rule("herkules.exe", "herkules.ini", "Poznaj Mity: Herkules"),
                    new Rule("odyseusz.exe", "odyseusz.ini", "Poznaj Mity: Odyseusz")));

    private GameIdentityResolver() {}

    public static boolean hasRules(String hash) {
        return hash != null && RULES.containsKey(hash.toUpperCase(Locale.ROOT));
    }

    public static boolean isResolved(String hash, String name) {
        if (!hasRules(hash)) return false;
        return RULES.get(hash.toUpperCase(Locale.ROOT)).stream().anyMatch(rule -> rule.name().equals(name));
    }

    public static String resolve(String hash, IFileSystem fs) {
        String fallback = hash == null ? "Nieznana gra" : KnownHashes.checkHash(hash);
        if (!hasRules(hash)) return fallback;
        List<Rule> rules = RULES.get(hash.toUpperCase(Locale.ROOT));
        try {
            Rule match = null;
            int count = 0;
            for (Rule rule : rules) {
                if (isFile(fs, rule.executable())) {
                    match = rule;
                    count++;
                }
            }
            if (count == 1) return match.name();

            // Only an explicit main INI can break a tie. An arbitrary INI found
            // beside the EXE is unsafe: Odyseusz also ships with Herkules.ini.
            if (isFile(fs, "bloomoo.ini")) {
                Ini ini = new Ini();
                // Legacy Windows INIs use backslashes as path separators.
                ini.setConfig(new org.ini4j.Config());
                ini.getConfig().setEscape(false);
                byte[] bytes = GameIniResolver.boundedReader(fs::length, fs::open).read("bloomoo.ini");
                ini.load(new ByteArrayInputStream(bytes));
                String main = ini.get("MAIN", "INI");
                if (main != null && !main.isBlank()) {
                    String normalized = SubdirectoryFileSystem.normalize(main.trim());
                    for (Rule rule : rules) {
                        if (rule.ini().equalsIgnoreCase(normalized) && isFile(fs, normalized)) return rule.name();
                    }
                }
            }
        } catch (IOException | RuntimeException ignored) {
            // Missing media, malformed configuration or unreadable files must not
            // turn an ambiguous identity into a guess (or prevent library loading).
        }
        return fallback;
    }

    private static boolean isFile(IFileSystem fs, String path) {
        return fs.exists(path) && !fs.isDirectory(path);
    }
}
