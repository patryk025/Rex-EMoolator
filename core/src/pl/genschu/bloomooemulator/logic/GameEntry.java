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
    private String dllHash;
    private String familyOverride;
    private String path;
    private String iniPath;
    private String storageId;
    private String mouseMode;
    private boolean mouseVirtualJoystick;
    private boolean skipLicenceCode;
    private boolean maintainAspectRatio;
    private boolean showFpsCounter;

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

        this.iniPath = resolveIniPath(fs, entries);

        for (String entry : entries) {
            String name = entry.toLowerCase(Locale.ROOT);
            if (!name.matches("bloomoodll\\.dll|piklib\\d+\\.dll")) {
                continue;
            }
            try (InputStream is = fs.open(entry)) {
                String hash = calculateSHA1(is).toUpperCase(Locale.ROOT);
                this.dllHash = hash;
                this.gameName = checkHash(hash);
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

    /**
     * Lazily resolves and caches {@link #iniPath} for legacy entries that
     * predate INI caching. Opens the assets, resolves once; the caller is
     * responsible for persisting (e.g. via GameManager) when this returns true.
     */
    public boolean ensureIniPath() {
        if (iniPath != null && !iniPath.isBlank()) {
            return false;
        }
        if (path == null) {
            return false;
        }
        File source = new File(path);
        if (!source.exists()) {
            return false;
        }
        IFileSystem fs;
        try {
            fs = AssetSourceDispatcher.openAssets(source);
        } catch (IOException e) {
            return false;
        }
        String[] entries = fs.list("");
        if (entries == null) {
            return false;
        }
        String resolved = resolveIniPath(fs, entries);
        if (resolved != null) {
            this.iniPath = resolved;
            return true;
        }
        return false;
    }

    /**
     * Lazily computes and caches {@link #dllHash} for legacy entries that
     * predate hash caching. Opens the assets, hashes the engine DLL once; the
     * caller is responsible for persisting (e.g. via GameManager) when this
     * returns true.
     */
    public boolean ensureDllHash() {
        if (dllHash != null && !dllHash.isBlank()) {
            return false;
        }
        if (path == null) {
            return false;
        }
        File source = new File(path);
        if (!source.exists()) {
            return false;
        }
        IFileSystem fs;
        try {
            fs = AssetSourceDispatcher.openAssets(source);
        } catch (IOException e) {
            return false;
        }
        String[] entries = fs.list("");
        if (entries == null) {
            return false;
        }
        for (String entry : entries) {
            String name = entry.toLowerCase(Locale.ROOT);
            if (!name.matches("bloomoodll\\.dll|piklib\\d+\\.dll")) {
                continue;
            }
            try (InputStream is = fs.open(entry)) {
                this.dllHash = calculateSHA1(is).toUpperCase(Locale.ROOT);
                return true;
            } catch (IOException e) {
                return false;
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
        }
        return false;
    }

    private static String resolveIniPath(IFileSystem fs, String[] entries) {
        return GameIniResolver.resolve(fs::exists, entries, p -> {
            try (InputStream is = fs.open(p)) {
                return readAllBytes(is);
            }
        });
    }

    private static byte[] readAllBytes(InputStream input) throws IOException {
        java.io.ByteArrayOutputStream buffer = new java.io.ByteArrayOutputStream();
        byte[] data = new byte[8192];
        int read;
        while ((read = input.read(data)) != -1) {
            buffer.write(data, 0, read);
        }
        return buffer.toByteArray();
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

    public String getIniPath() {
        return iniPath;
    }

    public void setIniPath(String iniPath) {
        this.iniPath = iniPath;
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

    public String getDllHash() {
        return dllHash;
    }

    public void setDllHash(String dllHash) {
        this.dllHash = dllHash;
    }

    public String getFamilyOverride() {
        return familyOverride;
    }

    public void setFamilyOverride(String familyOverride) {
        this.familyOverride = (familyOverride == null || familyOverride.isBlank()) ? null : familyOverride.trim();
    }

    /**
     * Resolves the patch family: an explicit {@link #familyOverride} wins, otherwise
     * it is derived from the engine DLL hash, falling back to the cached display name
     * (which survives no-CD / DLL edits). May be {@code null} if nothing resolves.
     */
    public String resolveFamily() {
        if (familyOverride != null && !familyOverride.isBlank()) {
            return familyOverride;
        }
        return GameFamilies.familyFor(dllHash, gameName);
    }

    public String getMouseMode() {
        return mouseMode;
    }

    public void setMouseMode(String mouseMode) {
        this.mouseMode = mouseMode;
    }

    /** The mouse mode as a typed value, resolving legacy/blank stored values. */
    public MouseMode getMouseModeEnum() {
        return MouseMode.fromStored(mouseMode);
    }

    /** Stores {@code mode} by its stable {@link MouseMode#key()}. */
    public void setMouseMode(MouseMode mode) {
        this.mouseMode = (mode == null ? MouseMode.defaultMode() : mode).key();
    }

    /**
     * Rewrites a legacy {@code mouseMode} (stored as a localized label) to the
     * stable {@link MouseMode#key()}. Returns {@code true} if the value changed,
     * so the caller can persist; mirrors {@link #ensureStorageId()} / {@link #ensureDllHash()}.
     */
    public boolean normalizeMouseMode() {
        String normalized = MouseMode.fromStored(mouseMode).key();
        if (!normalized.equals(mouseMode)) {
            this.mouseMode = normalized;
            return true;
        }
        return false;
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

    public boolean isShowFpsCounter() {
        return showFpsCounter;
    }

    public void setShowFpsCounter(boolean showFpsCounter) {
        this.showFpsCounter = showFpsCounter;
    }

    @Override
    public String toString() {
        return name + " (" + gameName + ", silnik " + version + ")";
    }
}
