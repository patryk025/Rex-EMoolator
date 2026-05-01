package pl.genschu.bloomooemulator.logic;

import pl.genschu.bloomooemulator.engine.filesystem.AssetSourceDispatcher;
import pl.genschu.bloomooemulator.engine.filesystem.IFileSystem;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;
import java.util.UUID;

import static pl.genschu.bloomooemulator.logic.KnownHashes.checkHash;

public class GameEntry implements Serializable {
    private int id;
    private String name;
    private String version;
    private String gameName;
    private String path;
    private String storageId;
    private String mouseMode;
    private boolean mouseVirtualJoystick;
    private boolean skipLicenceCode;
    private boolean maintainAspectRatio;

    public GameEntry() {
    }

    public GameEntry(String name, String path, String mouseMode, boolean mouseVirtualJoystick, boolean skipLicenceCode, boolean maintainAspectRatio) {
        this.id = -1;
        this.name = name;
        this.path = path;
        this.storageId = UUID.randomUUID().toString();
        this.version = searchForDllFiles(path);
        this.mouseMode = mouseMode;
        this.mouseVirtualJoystick = mouseVirtualJoystick;
        this.skipLicenceCode = skipLicenceCode;
        this.maintainAspectRatio = maintainAspectRatio;
    }

    private String searchForDllFiles(String path) {
        File source = new File(path);
        if (!source.exists()) {
            return "DLL not found";
        }

        IFileSystem fs;
        try {
            fs = AssetSourceDispatcher.openAssets(source);
        } catch (IOException e) {
            return "DLL not found";
        }

        String[] entries = fs.list("");
        if (entries == null) {
            return "DLL not found";
        }

        for (String entry : entries) {
            String name = entry.toLowerCase(Locale.ROOT);
            if (!name.matches("bloomoodll\\.dll|piklib\\d+\\.dll")) {
                continue;
            }
            try (InputStream is = fs.open(entry)) {
                this.gameName = checkHash(calculateSHA1(is));
            } catch (IOException e) {
                this.gameName = "Nieznana gra";
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
            if (name.equals("bloomoodll.dll")) {
                return "BlooMoo";
            }
            String version = name.substring("piklib".length(), name.length() - ".dll".length());
            if (version.length() > 1) {
                version = version.charAt(0) + "." + version.substring(1);
            }
            return "Piklib v" + version;
        }

        return "DLL not found";
    }

    public static String calculateSHA1(InputStream input) throws IOException, NoSuchAlgorithmException {
        MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
        byte[] buffer = new byte[8192];
        int read;
        while ((read = input.read(buffer)) != -1) {
            sha1.update(buffer, 0, read);
        }

        StringBuilder sb = new StringBuilder();
        for (byte b : sha1.digest()) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
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

    public String getStorageId() {
        ensureStorageId();
        return storageId;
    }

    public void setStorageId(String storageId) {
        this.storageId = storageId;
    }

    public boolean ensureStorageId() {
        if (storageId == null || storageId.isBlank()) {
            storageId = UUID.randomUUID().toString();
            return true;
        }
        try {
            UUID.fromString(storageId);
            return false;
        } catch (IllegalArgumentException ignored) {
            storageId = UUID.randomUUID().toString();
            return true;
        }
    }

    public String getGameName() {
        return gameName;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
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
        return name + " (" + gameName + ", silnik " + version + ")";
    }
}
