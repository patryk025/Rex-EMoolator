package pl.genschu.bloomooemulator.logic;

import java.io.File;

public final class AppPaths {
    private static final String APP_DIR = ".rexemoolator";
    private static final String STORAGE_DIR = "storage";

    private AppPaths() {
    }

    public static File userDataDir() {
        String osName = System.getProperty("os.name", "").toLowerCase();
        String userHome = System.getProperty("user.home", ".");

        if (osName.contains("win")) {
            String userProfile = System.getenv("USERPROFILE");
            if (userProfile != null && !userProfile.isBlank()) {
                return new File(userProfile, APP_DIR);
            }
        }

        return new File(userHome, APP_DIR);
    }

    public static File storageRootDir() {
        return new File(userDataDir(), STORAGE_DIR);
    }

    public static File storageDirFor(GameEntry game) {
        return new File(storageRootDir(), game.getStorageId());
    }
}
