package pl.genschu.bloomooemulator.engine.filesystem;

import pl.genschu.bloomooemulator.patch.InstalledPatch;

import java.io.IOException;
import java.io.InputStream;

/**
 * Read-only overlay backed by a patch's {@code files/} directory.
 *
 * Behaves identically to {@link LocalFileSystem} for path resolution
 * (case-insensitive, same layout as the game's VFS); the dedicated type
 * exists so the VFS layer can identify which mount served a file —
 * useful for debug overlays ("this file is overridden by patch X")
 * and future scope restrictions (e.g. allow patches to override only
 * specific subtrees).
 */
public class PatchFileSystem implements IFileSystem {
    private final String patchId;
    private final LocalFileSystem inner;

    public PatchFileSystem(InstalledPatch patch) {
        this.patchId = patch.getManifest().getId();
        this.inner = new LocalFileSystem(patch.getFilesDir());
    }

    public String getPatchId() { return patchId; }

    @Override public InputStream open(String path) throws IOException { return inner.open(path); }
    @Override public boolean exists(String path) { return inner.exists(path); }
    @Override public boolean isDirectory(String path) { return inner.isDirectory(path); }
    @Override public String[] list(String path) { return inner.list(path); }
    @Override public long length(String path) { return inner.length(path); }
}
