package pl.genschu.bloomooemulator.objects;

import org.ini4j.Ini;

import java.io.File;
import java.io.IOException;

public class INIManager {
    private Ini ini = new Ini();

    public void loadFile(String path) throws IOException {
        ini = new Ini();
        ini.load(new File(path));
    }

    public void saveFile(String path) throws IOException {
        ini.store(new File(path));
    }

    public String get(String section, String key) {
        return ini.get(section, key);
    }
}
