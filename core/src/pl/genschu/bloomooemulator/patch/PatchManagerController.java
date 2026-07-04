package pl.genschu.bloomooemulator.patch;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;

/**
 * UI-agnostic orchestration of the per-game patch manager. Both the Swing dialog
 * ({@code Dialogs.showPatchesDialog}) and the Android {@code PatchManagerActivity}
 * drive this; the views only render {@link PatchRowVM}s and forward user actions.
 *
 * <p>Holds one {@link PatchRegistry}, {@link PatchManager} and {@link PatchCatalog}
 * for the lifetime of a dialog/activity, so state stays consistent across actions.
 * Network and disk work is synchronous — callers must run {@link #install} and
 * {@link #refreshRemote} off the UI thread.
 *
 * <p>Deliberately free of any {@code Gdx}/Swing/Android dependency: it is created
 * with a plain {@link File} patches root and a pre-built {@link PatchCatalog}
 * (desktop uses {@link PatchCatalog#loadBundled()}; Android reads the bundled JSON
 * via its {@code AssetManager} since it has no initialised libGDX in the launcher).
 */
public class PatchManagerController {
    private final String gameHash;
    private final String gameFamily;
    private final File patchesRoot;
    private final PatchRegistry registry;
    private final PatchManager manager;
    private final PatchCatalog catalog;

    public PatchManagerController(String gameHash, String gameFamily, File patchesRoot,
                                  File indexFile, PatchCatalog catalog) {
        this.gameHash = gameHash;
        this.gameFamily = gameFamily;
        this.patchesRoot = patchesRoot;
        this.registry = new PatchRegistry(indexFile);
        this.manager = new PatchManager(patchesRoot, registry);
        this.catalog = catalog != null ? catalog : new PatchCatalog();
    }

    public String getGameHash() { return gameHash; }
    public String getGameFamily() { return gameFamily; }

    /**
     * Rows to render: catalog ∪ installed patches relevant to this game, filtered to
     * those that match (EXACT/FAMILY) or are already installed, sorted by mount order
     * (registry-tracked first in ascending order, the rest last).
     */
    public List<PatchRowVM> rows() {
        LinkedHashMap<String, PatchManifest> byId = new LinkedHashMap<>();
        for (PatchManifest m : catalog.all()) {
            byId.put(m.getId(), m);
        }
        for (InstalledPatch p : manager.allInstalled()) {
            byId.put(p.getManifest().getId(), p.getManifest()); // installed manifest is authoritative
        }
        List<PatchRowVM> rows = new ArrayList<>();
        for (PatchManifest m : byId.values()) {
            PatchCompatibility compat = m.compatibilityFor(gameHash, gameFamily);
            boolean installed = manager.byId(m.getId()) != null;
            if (compat == PatchCompatibility.NONE && !installed) {
                continue; // not for this game
            }
            PatchRegistryEntry entry = registry.find(gameHash, m.getId());
            boolean enabled = entry != null && entry.isEnabled();
            int order = entry != null ? entry.getOrder() : Integer.MAX_VALUE;
            InstalledPatch installedPatch = manager.byId(m.getId());
            boolean linkedLocal = installedPatch != null && installedPatch.isLinkedLocal();
            rows.add(new PatchRowVM(m, compat, installed, enabled, entry != null, order, linkedLocal));
        }
        rows.sort((a, b) -> Integer.compare(a.getOrder(), b.getOrder()));
        return rows;
    }

    /**
     * Downloads and installs the patch with the given id from its manifest source,
     * then rescans on-disk state.
     *
     * @throws IllegalStateException if the patch is unknown, already installed, or has
     *                               no direct-download source
     * @throws IOException           on download/extract failure
     */
    public void install(String patchId) throws IOException {
        install(patchId, null);
    }

    /** {@link #install(String)} reporting download progress to {@code progress} (may be null). */
    public void install(String patchId, DownloadProgress progress) throws IOException {
        if (manager.byId(patchId) != null) {
            throw new IllegalStateException("Patch already installed: " + patchId);
        }
        PatchManifest manifest = manifestById(patchId);
        if (manifest == null) {
            throw new IllegalStateException("Unknown patch: " + patchId);
        }
        PatchSource source = manifest.getSource();
        if (source == null || (source.getType() != PatchSourceType.URL
                && source.getType() != PatchSourceType.GDRIVE)) {
            throw new IllegalStateException("Patch is not downloadable: " + patchId);
        }
        PatchInstaller.installFromSource(manifest, patchesRoot, progress);
        manager.rescan();
    }

    /**
     * Installs a self-contained local patch archive ({@code patch.json} plus its
     * {@code filesRoot} directory) and rescans on-disk state.
     *
     * @return the freshly installed patch.
     * @throws IllegalStateException if a patch with the same id is already installed.
     * @throws IOException           if the archive is unsupported or malformed.
     */
    public InstalledPatch importLocalArchive(File archive) throws IOException {
        PatchManifest manifest = PatchInstaller.readManifestFromArchive(archive);
        if (manager.byId(manifest.getId()) != null) {
            throw new IllegalStateException("Patch already installed: " + manifest.getId());
        }
        InstalledPatch installed = PatchInstaller.installFromPackagedArchive(archive, patchesRoot);
        manager.rescan();
        return installed;
    }

    /**
     * Links a local development folder as an enabled patch for the current game.
     *
     * <p>If the folder contains {@code patch.json}, that manifest is used and its
     * {@code filesRoot} is mounted. Without a manifest, the folder itself is treated
     * as the overlay root and a game-specific temporary manifest is synthesized.
     * Files are not copied, so edits become visible on the next game load.
     */
    public InstalledPatch linkLocalFolder(File folder) throws IOException {
        if (folder == null || !folder.isDirectory()) {
            throw new IOException("Selected path is not a folder");
        }
        File canonicalFolder = folder.getCanonicalFile();
        PatchManifest manifest = PatchInstaller.readManifestFromDirectory(canonicalFolder);
        if (manifest == null) {
            manifest = syntheticLocalFolderManifest(canonicalFolder);
        }
        validateLinkedFolder(canonicalFolder, manifest);

        String patchId = manifest.getId();
        InstalledPatch existingPatch = manager.byId(patchId);
        if (existingPatch != null && (!existingPatch.isLinkedLocal()
                || !sameFile(existingPatch.getRootDir(), canonicalFolder))) {
            throw new IllegalStateException("Patch already installed: " + patchId);
        }

        PatchCompatibility compat = manifest.compatibilityFor(gameHash, gameFamily);
        PatchRegistryEntry existingEntry = registry.find(gameHash, patchId);
        int order = existingEntry != null ? existingEntry.getOrder() : nextOrder();
        PatchRegistryEntry linked = new PatchRegistryEntry(
                gameHash, patchId, true, compat != PatchCompatibility.EXACT, order);
        linked.setLocalPath(canonicalFolder.getPath());
        registry.upsert(linked);
        registry.save();
        manager.rescan();
        InstalledPatch installed = manager.byId(patchId);
        if (installed == null) {
            throw new IOException("Linked patch could not be loaded: " + patchId);
        }
        return installed;
    }

    /**
     * Enables or disables an installed patch. Non-EXACT matches (FAMILY) are
     * auto-marked {@code forceEnable} when enabled, since {@link PatchManager#activeFor}
     * only mounts EXACT matches otherwise. New pairings get the next mount order.
     *
     * @throws IllegalStateException if the patch is not installed
     */
    public void setEnabled(String patchId, boolean enabled) {
        InstalledPatch patch = manager.byId(patchId);
        if (patch == null) {
            throw new IllegalStateException("Patch not installed: " + patchId);
        }
        boolean force = patch.getManifest().compatibilityFor(gameHash, gameFamily) != PatchCompatibility.EXACT;
        PatchRegistryEntry entry = registry.find(gameHash, patchId);
        int order = entry != null ? entry.getOrder() : nextOrder();
        PatchRegistryEntry updated = new PatchRegistryEntry(gameHash, patchId, enabled, enabled && force, order);
        if (entry != null) {
            updated.setLocalPath(entry.getLocalPath());
        }
        registry.upsert(updated);
        registry.save();
    }

    /** Flips the current enabled state of an installed patch. */
    public void toggle(String patchId) {
        PatchRegistryEntry entry = registry.find(gameHash, patchId);
        setEnabled(patchId, !(entry != null && entry.isEnabled()));
    }

    /**
     * Deletes the patch's on-disk folder and clears its registry entry for this game.
     *
     * @return {@code true} if the patch was installed and removed.
     */
    public boolean uninstall(String patchId) {
        boolean removed = manager.uninstall(patchId);
        registry.remove(gameHash, patchId);
        registry.save();
        manager.rescan();
        return removed;
    }

    /**
     * Moves a registry-tracked patch up ({@code -1}) or down ({@code +1}) in mount
     * order by swapping its {@code order} with the nearest registry-tracked neighbour.
     *
     * @return {@code true} if a swap happened.
     */
    public boolean move(String patchId, int direction) {
        List<PatchRowVM> rows = rows();
        int sel = indexOf(rows, patchId);
        if (sel < 0 || !rows.get(sel).hasEntry()) {
            return false;
        }
        int target = sel + direction;
        while (target >= 0 && target < rows.size() && !rows.get(target).hasEntry()) {
            target += direction;
        }
        if (target < 0 || target >= rows.size()) {
            return false;
        }
        PatchRegistryEntry a = registry.find(gameHash, patchId);
        PatchRegistryEntry b = registry.find(gameHash, rows.get(target).getId());
        if (a == null || b == null) {
            return false;
        }
        int tmp = a.getOrder();
        a.setOrder(b.getOrder());
        b.setOrder(tmp);
        registry.save();
        return true;
    }

    /** Validation findings (conflicts/requires/supersedes) for the active set. */
    public List<PatchIssue> issues() {
        return manager.validate(manager.activeFor(gameHash, gameFamily));
    }

    /** Merges the default remote catalog on top of the bundled one (best-effort). */
    public void refreshRemote() {
        refreshRemote(PatchCatalog.DEFAULT_REMOTE_URL);
    }

    public void refreshRemote(String url) {
        catalog.mergeFrom(PatchCatalog.loadRemote(url));
    }

    /** Looks up a manifest by id across catalog ∪ installed (installed wins). */
    private PatchManifest manifestById(String id) {
        InstalledPatch installed = manager.byId(id);
        if (installed != null) {
            return installed.getManifest();
        }
        return catalog.byId(id);
    }

    private int nextOrder() {
        int max = -1;
        for (PatchRegistryEntry e : registry.entriesFor(gameHash)) {
            max = Math.max(max, e.getOrder());
        }
        return max + 1;
    }

    private static int indexOf(List<PatchRowVM> rows, String patchId) {
        for (int i = 0; i < rows.size(); i++) {
            if (rows.get(i).getId().equals(patchId)) {
                return i;
            }
        }
        return -1;
    }

    private PatchManifest syntheticLocalFolderManifest(File folder) {
        PatchManifest manifest = new PatchManifest();
        manifest.setId(localFolderPatchId(folder, gameHash));
        manifest.setName(folder.getName());
        manifest.setTargetHashes(new String[]{gameHash});
        manifest.setFilesRoot(".");
        PatchSource source = new PatchSource();
        source.setType(PatchSourceType.LOCAL);
        manifest.setSource(source);
        return manifest;
    }

    private static String localFolderPatchId(File folder, String gameHash) {
        String base = folder.getName();
        if (base == null || base.isBlank()) {
            base = "folder";
        }
        String slug = base.toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9._-]+", "-")
                .replaceAll("^-+|-+$", "");
        if (slug.isBlank() || slug.equals(".") || slug.equals("..")) {
            slug = "folder";
        }
        String suffix = Integer.toUnsignedString(folder.getPath().hashCode(), 36);
        String hashSuffix = gameHash == null || gameHash.length() < 8 ? "" : "-" + gameHash.substring(0, 8).toLowerCase(Locale.ROOT);
        return "local-" + slug + "-" + suffix + hashSuffix;
    }

    private static void validateLinkedFolder(File folder, PatchManifest manifest) throws IOException {
        if (!PatchInstaller.isSafePatchId(manifest.getId())) {
            throw new IOException("Patch id must be a simple directory name: " + manifest.getId());
        }
        File root = folder.getCanonicalFile();
        File files = new File(root, manifest.getFilesRoot()).getCanonicalFile();
        if (!files.equals(root)) {
            String rootPath = root.getPath() + File.separator;
            if (!files.getPath().startsWith(rootPath)) {
                throw new IOException("Patch filesRoot escapes source folder: " + manifest.getFilesRoot());
            }
        }
        if (!files.isDirectory()) {
            throw new IOException("Patch folder has no filesRoot directory: " + manifest.getFilesRoot());
        }
    }

    private static boolean sameFile(File a, File b) throws IOException {
        return a.getCanonicalFile().equals(b.getCanonicalFile());
    }
}
