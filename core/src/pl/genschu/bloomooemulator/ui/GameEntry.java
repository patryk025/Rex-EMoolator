package pl.genschu.bloomooemulator.ui;

public class GameEntry {
    private String name;
    private String version;
    private String path;
    // Add other game settings here

    public GameEntry(String name, String version) {
        this.name = name;
        this.version = version;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
    
    @Override
    public String toString() {
        return name + " - " + version;
    }
}
