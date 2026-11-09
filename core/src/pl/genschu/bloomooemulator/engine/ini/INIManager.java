package pl.genschu.bloomooemulator.engine.ini;

import org.ini4j.Config;
import org.ini4j.Wini;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedHashMap;
import java.util.Map;

public class INIManager {
    private Wini ini = new Wini();

    private final Map<String, Map<String, String>> sectionCache = new LinkedHashMap<>();

    public void load(InputStream input) throws IOException {
        ini = new Wini();

        Config config = ini.getConfig();
        config.setStrictOperator(true);
        ini.setConfig(config);

        ini.load(input);

        sectionCache.clear();
    }

    public void store(OutputStream output) throws IOException {
        ini.store(output);
    }

    public String get(String section, String key) {
        if (section == null || key == null) {
            return null;
        }

        // use cache if contains key (cache keys are upper-cased: lookups are case-insensitive)
        String cacheSection = section.toUpperCase();
        String cacheKey = key.toUpperCase();
        if (sectionCache.containsKey(cacheSection) && sectionCache.get(cacheSection).containsKey(cacheKey)) {
            return sectionCache.get(cacheSection).get(cacheKey);
        }

        // if not, get value (case-insensitive like GetPrivateProfileString)
        String value = ini.get(section, key);
        if (value == null) {
            String realSection = matchSection(section);
            if (realSection != null) {
                String realKey = matchKey(realSection, key);
                if (realKey != null) value = ini.get(realSection, realKey);
            }
        }

        sectionCache.computeIfAbsent(cacheSection, k -> new LinkedHashMap<>()).put(cacheKey, value);

        return value;
    }

    public void put(String section, String key, String value) {
        if (section == null || key == null) {
            return;
        }

        // Reuse an existing spelling so a differently-cased entry is updated, not duplicated
        String realSection = matchSection(section);
        String targetSection = realSection != null ? realSection : section;
        String realKey = realSection != null ? matchKey(realSection, key) : null;
        ini.put(targetSection, realKey != null ? realKey : key, value);

        // update cache
        sectionCache.computeIfAbsent(section.toUpperCase(), k -> new LinkedHashMap<>())
                .put(key.toUpperCase(), value);
    }

    private String matchSection(String section) {
        if (ini.containsKey(section)) return section;
        for (String existing : ini.keySet()) {
            if (existing.equalsIgnoreCase(section)) return existing;
        }
        return null;
    }

    private String matchKey(String realSection, String key) {
        Map<String, String> entries = ini.get(realSection);
        if (entries == null) return null;
        if (entries.containsKey(key)) return key;
        for (String existing : entries.keySet()) {
            if (existing.equalsIgnoreCase(key)) return existing;
        }
        return null;
    }

    /**
     * Checks if the variable value exists in any of the specified sections.
     * @param sections list of sections to check.
     * @param key name of the variable.
     * @return the name of the section in which the variable was found, or null if not found.
     */
    public String findSectionForKey(String[] sections, String key) {
        if (sections == null || key == null) {
            return null;
        }

        for (String section : sections) {
            if (section != null && get(section, key) != null) {
                return section;
            }
        }

        return null;
    }

    /**
     * Retrieves a list of all available sections.
     * @return an array of section names
     */
    public String[] getSections() {
        return ini.keySet().toArray(new String[0]);
    }
}