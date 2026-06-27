package pl.genschu.bloomooemulator.patch;

import com.badlogic.gdx.utils.Json;
import pl.genschu.bloomooemulator.engine.filesystem.PatchFileSystem;
import pl.genschu.bloomooemulator.engine.filesystem.VFS;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Top-level entry point for the patch system.
 *
 * Responsibilities:
 * <ul>
 *   <li>Scan {@code <userDir>/patches/} for {@link InstalledPatch}es (each is a folder
 *       with {@code patch.json} at the root).</li>
 *   <li>Combine on-disk discovery with the user's {@link PatchRegistry} state to
 *       answer "which patches apply to this game?" and "which should I mount?".</li>
 *   <li>Mount active patches into a {@link VFS} so their {@code files/} trees override
 *       the game's assets.</li>
 * </ul>
 *
 * Network sources ({@link PatchSourceType#GITHUB}, {@link PatchSourceType#URL}) are
 * currently out of scope — the install step is expected to materialise them
 * under {@code patches/<id>/} so this class only ever deals with local folders.
 */
public class PatchManager {
    private static final String MANIFEST_FILE = "patch.json";
    private static final Logger LOGGER = Logger.getLogger(PatchManager.class.getName());

    private final File patchesRoot;
    private final PatchRegistry registry;

    /** Cached scan result, keyed by patch id. Refresh via {@link #rescan()}. */
    private Map<String, InstalledPatch> installed = new LinkedHashMap<>();

    public PatchManager(File patchesRoot, PatchRegistry registry) {
        this.patchesRoot = patchesRoot;
        this.registry = registry;
        rescan();
    }

    public PatchRegistry getRegistry() { return registry; }

    /** Re-walks {@link #patchesRoot} and reloads every manifest. */
    public void rescan() {
        Map<String, InstalledPatch> next = new LinkedHashMap<>();
        if (patchesRoot != null && patchesRoot.isDirectory()) {
            File[] children = patchesRoot.listFiles(File::isDirectory);
            if (children != null) {
                for (File dir : children) {
                    InstalledPatch p = readPatch(dir);
                    if (p != null) next.put(p.getManifest().getId(), p);
                }
            }
        }
        installed = next;
    }

    /** All discovered patches, in scan order. */
    public List<InstalledPatch> allInstalled() {
        return new ArrayList<>(installed.values());
    }

    public InstalledPatch byId(String id) {
        return id == null ? null : installed.get(id);
    }

    /**
     * Deletes a patch's on-disk folder and refreshes the scan. The caller is
     * responsible for clearing the patch's {@link PatchRegistry} entries.
     *
     * @return {@code true} if the patch was installed and removed.
     */
    public boolean uninstall(String id) {
        InstalledPatch patch = byId(id);
        if (patch == null) {
            return false;
        }
        deleteRecursively(patch.getRootDir());
        rescan();
        return true;
    }

    private static void deleteRecursively(File file) {
        if (file == null || !file.exists()) {
            return;
        }
        File[] children = file.listFiles();
        if (children != null) {
            for (File child : children) {
                deleteRecursively(child);
            }
        }
        if (!file.delete()) {
            LOGGER.warning("Could not delete " + file);
        }
    }

    /**
     * Patches that match the given game at any compatibility level, paired with
     * the level. Used by UI to render the patch list grouped by exact/family/none.
     */
    public List<Match> matchesFor(String gameHash, String gameFamily) {
        List<Match> result = new ArrayList<>();
        for (InstalledPatch p : installed.values()) {
            PatchCompatibility level = p.getManifest().compatibilityFor(gameHash, gameFamily);
            result.add(new Match(p, level));
        }
        return result;
    }

    /**
     * Resolves the set of patches that should actually be mounted for the given game:
     * registry says enabled, and either compatibility is EXACT or the user has
     * explicitly set {@code forceEnable}.
     *
     * Result is ordered by {@link PatchRegistryEntry#getOrder()} ascending — same as
     * the registry, which matches the desired mount order (earlier = lower priority).
     */
    public List<InstalledPatch> activeFor(String gameHash, String gameFamily) {
        List<InstalledPatch> result = new ArrayList<>();
        for (PatchRegistryEntry entry : registry.entriesFor(gameHash)) {
            if (!entry.isEnabled()) continue;
            InstalledPatch patch = byId(entry.getPatchId());
            if (patch == null) continue;
            PatchCompatibility level = patch.getManifest().compatibilityFor(gameHash, gameFamily);
            if (level == PatchCompatibility.EXACT || entry.isForceEnable()) {
                result.add(patch);
            }
        }
        return result;
    }

    /**
     * Mounts every active patch into {@code vfs}. Call after the main asset source
     * has been mounted — VFS gives last-mounted highest priority, so each call
     * here stacks on top of the previous one.
     *
     * Mount order matches {@link #activeFor}: lower {@code order} first, so the
     * patch with the highest {@code order} ends up overriding everything else.
     */
    public void mountActiveFor(VFS vfs, String gameHash, String gameFamily) {
        for (InstalledPatch p : activeFor(gameHash, gameFamily)) {
            vfs.mountAssets(new PatchFileSystem(p));
        }
    }

    /**
     * Checks manifest-declared relationships against the given set of patches
     * (typically the result of {@link #activeFor} or a UI preview).
     *
     * Returns one issue per finding. CONFLICT is deduplicated across the pair —
     * if A lists B and B lists A, only one issue is emitted.
     */
    public List<PatchIssue> validate(List<InstalledPatch> active) {
        List<PatchIssue> issues = new ArrayList<>();
        if (active == null || active.isEmpty()) return issues;

        Set<String> activeIds = new HashSet<>();
        for (InstalledPatch p : active) activeIds.add(p.getManifest().getId());

        Set<String> conflictPairs = new HashSet<>();
        for (InstalledPatch p : active) {
            PatchManifest m = p.getManifest();
            String id = m.getId();

            for (String other : m.getConflicts()) {
                if (other == null || other.equals(id) || !activeIds.contains(other)) continue;
                String pairKey = id.compareTo(other) < 0 ? id + "\0" + other : other + "\0" + id;
                if (!conflictPairs.add(pairKey)) continue;
                issues.add(new PatchIssue(PatchIssueType.CONFLICT, PatchIssueSeverity.ERROR,
                        id, other, "Patch '" + id + "' is incompatible with '" + other + "'."));
            }

            for (String required : m.getRequires()) {
                if (required == null || required.equals(id) || activeIds.contains(required)) continue;
                PatchIssueSeverity severity = installed.containsKey(required)
                        ? PatchIssueSeverity.ERROR    // installed but disabled — user can fix
                        : PatchIssueSeverity.WARNING; // not installed at all — user may not be able to fix
                issues.add(new PatchIssue(PatchIssueType.MISSING_REQUIRED, severity,
                        id, required, "Patch '" + id + "' requires '" + required + "', which is not active."));
            }

            for (String superseded : m.getSupersedes()) {
                if (superseded == null || superseded.equals(id) || !activeIds.contains(superseded)) continue;
                issues.add(new PatchIssue(PatchIssueType.SUPERSEDED, PatchIssueSeverity.WARNING,
                        superseded, id, "Patch '" + superseded + "' is included in '" + id + "' — enabling both is redundant."));
            }
        }
        return issues;
    }

    private InstalledPatch readPatch(File dir) {
        File manifest = new File(dir, MANIFEST_FILE);
        if (!manifest.isFile()) return null;
        try (InputStreamReader reader = new InputStreamReader(new FileInputStream(manifest), StandardCharsets.UTF_8)) {
            StringBuilder sb = new StringBuilder();
            int c;
            while ((c = reader.read()) != -1) sb.append((char) c);
            PatchManifest parsed = new Json().fromJson(PatchManifest.class, sb.toString());
            if (parsed == null || parsed.getId() == null || parsed.getId().isBlank()) {
                LOGGER.warning("Skipping " + dir + ": manifest missing id");
                return null;
            }
            return new InstalledPatch(parsed, dir);
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Failed to read " + manifest, e);
            return null;
        }
    }

    /** Pairing of an installed patch with its compatibility level for a given game. */
    public static final class Match {
        private final InstalledPatch patch;
        private final PatchCompatibility level;

        public Match(InstalledPatch patch, PatchCompatibility level) {
            this.patch = patch;
            this.level = level;
        }

        public InstalledPatch getPatch() { return patch; }
        public PatchCompatibility getLevel() { return level; }
    }
}
