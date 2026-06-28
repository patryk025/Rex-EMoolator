package pl.genschu.bloomooemulator.patch;

import java.io.File;

/**
 * An on-disk patch the manager has discovered: the parsed manifest plus
 * the directory it lives in. Treat as immutable — re-scan to pick up changes.
 */
public final class InstalledPatch {
    private final PatchManifest manifest;
    private final File rootDir;

    public InstalledPatch(PatchManifest manifest, File rootDir) {
        this.manifest = manifest;
        this.rootDir = rootDir;
    }

    public PatchManifest getManifest() { return manifest; }
    public File getRootDir() { return rootDir; }

    /** Resolves the overlay-files directory ({@code <rootDir>/<filesRoot>}). */
    public File getFilesDir() {
        return new File(rootDir, manifest.getFilesRoot());
    }
}
