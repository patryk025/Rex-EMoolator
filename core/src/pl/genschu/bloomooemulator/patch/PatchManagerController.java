package pl.genschu.bloomooemulator.patch;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

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
            rows.add(new PatchRowVM(m, compat, installed, enabled, entry != null, order));
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
        PatchInstaller.installFromSource(manifest, patchesRoot);
        manager.rescan();
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
        registry.upsert(new PatchRegistryEntry(gameHash, patchId, enabled, enabled && force, order));
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
}
