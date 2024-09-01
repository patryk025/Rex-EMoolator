package pl.genschu.bloomooemulator.objects;

import org.ini4j.Config;
import org.ini4j.Wini;

import java.io.File;
import java.io.IOException;

public class INIManager {
    private Wini ini = new Wini();

    public void loadFile(String path) throws IOException {
        ini = new Wini();

        Config config = ini.getConfig();
        config.setStrictOperator(true);
        ini.setConfig(config);

        ini.load(new File(path));
    }

    public void saveFile(String path) throws IOException {
        ini.store(new File(path));
    }

    public String get(String section, String key) {
        return ini.get(section, key);
    }

    public void put(String section, String key, String value) {
        ini.put(section, key, value);
    }
}
