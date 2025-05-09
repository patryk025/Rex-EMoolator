package pl.genschu.bloomooemulator.engine.ini;

import org.ini4j.Config;
import org.ini4j.Wini;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class INIManager {
    private Wini ini = new Wini();

    private Map<String, Map<String, String>> sectionCache = new LinkedHashMap<>();

    public void loadFile(String path) throws IOException {
        ini = new Wini();

        Config config = ini.getConfig();
        config.setStrictOperator(true);
        ini.setConfig(config);

        ini.load(new File(path));

        sectionCache.clear();
    }

    public void saveFile(String path) throws IOException {
        ini.store(new File(path));
    }

    public String get(String section, String key) {
        if (section == null || key == null) {
            return null;
        }

        // use cache if contains key
        if (sectionCache.containsKey(section) && sectionCache.get(section).containsKey(key)) {
            return sectionCache.get(section).get(key);
        }

        // if not, get value
        String value = ini.get(section, key);

        if (!sectionCache.containsKey(section)) {
            sectionCache.put(section, new LinkedHashMap<>());
        }
        sectionCache.get(section).put(key, value);

        return value;
    }

    public void put(String section, String key, String value) {
        if (section == null || key == null) {
            return;
        }

        ini.put(section, key, value);

        // update cache
        if (!sectionCache.containsKey(section)) {
            sectionCache.put(section, new LinkedHashMap<>());
        }
        sectionCache.get(section).put(key, value);
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