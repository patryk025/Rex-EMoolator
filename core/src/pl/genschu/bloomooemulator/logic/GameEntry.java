package pl.genschu.bloomooemulator.logic;

public class GameEntry {
    private int id;
    private String name;
    private String version;
    private String path;
    private String mouseMode;
    private boolean mouseVirtualJoystick;
    private boolean skipLicenceCode;
    private boolean maintainAspectRatio;

    public GameEntry() {
    }

    public GameEntry(String name, String path, String mouseMode, boolean mouseVirtualJoystick, boolean skipLicenceCode, boolean maintainAspectRatio) {
        this.id = -1;
        this.name = name;
        this.version = "Unknown";
        this.path = path;
        this.mouseMode = mouseMode;
        this.mouseVirtualJoystick = mouseVirtualJoystick;
        this.skipLicenceCode = skipLicenceCode;
        this.maintainAspectRatio = maintainAspectRatio;
    }

    public GameEntry(String name, String version, String path, String mouseMode, boolean mouseVirtualJoystick, boolean skipLicenceCode, boolean maintainAspectRatio) {
        this.name = name;
        this.version = version;
        this.path = path;
        this.mouseMode = mouseMode;
        this.mouseVirtualJoystick = mouseVirtualJoystick;
        this.skipLicenceCode = skipLicenceCode;
        this.maintainAspectRatio = maintainAspectRatio;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public void setVersion(String version) {
        this.version = version;
    }

    public String getMouseMode() {
        return mouseMode;
    }

    public void setMouseMode(String mouseMode) {
        this.mouseMode = mouseMode;
    }

    public boolean isMouseVirtualJoystick() {
        return mouseVirtualJoystick;
    }

    public void setMouseVirtualJoystick(boolean mouseVirtualJoystick) {
        this.mouseVirtualJoystick = mouseVirtualJoystick;
    }

    public boolean isSkipLicenceCode() {
        return skipLicenceCode;
    }

    public void setSkipLicenceCode(boolean skipLicenceCode) {
        this.skipLicenceCode = skipLicenceCode;
    }

    public boolean isMaintainAspectRatio() {
        return maintainAspectRatio;
    }

    public void setMaintainAspectRatio(boolean maintainAspectRatio) {
        this.maintainAspectRatio = maintainAspectRatio;
    }

    @Override
    public String toString() {
        return name + " - " + version;
    }
}
