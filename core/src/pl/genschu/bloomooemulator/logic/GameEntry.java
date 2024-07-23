package pl.genschu.bloomooemulator.logic;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static pl.genschu.bloomooemulator.logic.KnownHashes.checkHash;

public class GameEntry implements Serializable {
    private int id;
    private String name;
    private String version;
    private String gameName;
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
        this.path = path;
        this.version = searchForDllFiles(path);
        this.mouseMode = mouseMode;
        this.mouseVirtualJoystick = mouseVirtualJoystick;
        this.skipLicenceCode = skipLicenceCode;
        this.maintainAspectRatio = maintainAspectRatio;
    }

    private String searchForDllFiles(String path) {
        File folder = new File(path);
        File[] files = folder.listFiles((dir, name) -> name.toLowerCase().matches("bloomoodll\\.dll|piklib\\d+\\.dll"));

        if (files != null) {
            for (File file : files) {
                try {
                    //this.gameName = checkHash(computeFileSHA1(file));
                    this.gameName = checkHash(calculateSHA1(file.getPath()));
                } catch (IOException e) {
                    this.gameName = "Nieznana gra";
                } catch (NoSuchAlgorithmException e) {
                    throw new RuntimeException(e);
                }
                String name = file.getName().toLowerCase();
                if(name.matches("bloomoodll\\.dll")) return "BlooMoo";
                if(name.matches("piklib\\d+\\.dll")) {
                    String version = name.split("piklib")[1].split("\\.dll")[0];
                    if(version.length() > 1)
                        version = version.charAt(0) + "." + version.substring(1);

                    return "Piklib v" + version;
                }
            }
        }

        return "DLL not found";
    }

    public static String calculateSHA1(String filePath) throws IOException, NoSuchAlgorithmException {
        MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
        FileInputStream inputStream = new FileInputStream(filePath);

        byte[] dataBytes = new byte[8192];
        int bytesRead;

        while ((bytesRead = inputStream.read(dataBytes)) != -1) {
            sha1.update(dataBytes, 0, bytesRead);
        }

        byte[] hashBytes = sha1.digest();

        // Convert hash bytes to hex format
        StringBuilder sb = new StringBuilder();
        for (byte b : hashBytes) {
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
