package pl.genschu.bloomooemulator.patch;

import java.io.Serializable;
import java.util.Locale;

/**
 * Contents of {@code patch.json}, sitting at the root of every installed patch.
 *
 * Example:
 * <pre>
 * {
 *   "id":            "reksio-ufo-s65-zamek",
 *   "name":          "Reksio i Ufo — naprawa zbugowanego fadingu,
 *   "description":   "Naprawia zbugowane przyciemnianie ekranu w scenie S65_ZAMEK",
 *   "author":        "Patryk Gensch",
 *   "version":       "1.0.0",
 *   "targetHashes":  ["5F6CBB33576D4F5493F141DDEE70CD108EA0F9D5",
 *                     "9FE73F7DCA5ABF7BEFE16216CF5BA54A9E68281B"],
 *   "targetFamily":  "reksio-ufo",
 *   "filesRoot":     "files",
 *   "source":        { "type": "LOCAL" }
 * }
 * </pre>
 *
 * Field semantics:
 * <ul>
 *   <li>{@code id} — slug, must be unique across the user's installation. Used as the on-disk folder name.</li>
 *   <li>{@code targetHashes} — SHA1s of {@code bloomoodll.dll}/{@code piklib*.dll} the author tested
 *       against (see {@link pl.genschu.bloomooemulator.logic.KnownHashes}). Empty array means
 *       "applies to any game" — discouraged, will only ever match as {@link PatchCompatibility#NONE}.</li>
 *   <li>{@code targetFamily} — optional logical group, e.g. {@code "reksio-ufo"}. Lets the user
 *       opt-in for editions the author did not test (yellow / FAMILY match).</li>
 *   <li>{@code filesRoot} — subdirectory under the patch root holding the overlay tree.
 *       Defaults to {@code "files"}. The tree mirrors the game's VFS layout
 *       (e.g. {@code files/DANE/scene12/music.cnv}).</li>
 *   <li>{@code archiveRoot} — optional subdirectory <em>inside the downloaded archive</em>
 *       whose contents are the overlay root. It is stripped during extraction (entries
 *       outside it are ignored), so the on-disk overlay stays clean and {@code filesRoot}
 *       remains {@code "files"}. Use it when an archive wraps everything in a folder
 *       (e.g. {@code "Reksio Piraci"}). Null/absent = extract the whole archive verbatim.</li>
 *   <li>{@code source} — provenance, used only by the patch manager for refresh logic.</li>
 *   <li>{@code conflicts} / {@code requires} / {@code supersedes} — relationships with other
 *       patches, evaluated by {@link PatchManager#validate}. Each entry is a patch {@code id}.
 *       Asymmetric declarations still count (if A lists B as conflicting, the conflict holds
 *       even if B's manifest is silent).</li>
 * </ul>
 */
public class PatchManifest implements Serializable {
    private String id;
    private String name;
    private String description;
    private String author;
    private String version;
    private String[] targetHashes = new String[0];
    private String targetFamily;
    private String filesRoot = "files";
    private String archiveRoot;
    private PatchSource source = new PatchSource();
    private String reference;
    private String[] conflicts = new String[0];
    private String[] requires = new String[0];
    private String[] supersedes = new String[0];

    public PatchManifest() {}

    /**
     * @return compatibility level of this patch against the given game.
     *         {@code gameFamily} may be null for games not present in
     *         {@link pl.genschu.bloomooemulator.logic.KnownHashes}.
     */
    public PatchCompatibility compatibilityFor(String gameHash, String gameFamily) {
        if (gameHash != null && targetHashes != null) {
            String upper = gameHash.toUpperCase(Locale.ROOT);
            for (String h : targetHashes) {
                if (h != null && upper.equals(h.toUpperCase(Locale.ROOT))) {
                    return PatchCompatibility.EXACT;
                }
            }
        }
        if (targetFamily != null && gameFamily != null && targetFamily.equalsIgnoreCase(gameFamily)) {
            return PatchCompatibility.FAMILY;
        }
        return PatchCompatibility.NONE;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }

    public String[] getTargetHashes() { return targetHashes; }
    public void setTargetHashes(String[] targetHashes) {
        this.targetHashes = targetHashes == null ? new String[0] : targetHashes;
    }

    public String getTargetFamily() { return targetFamily; }
    public void setTargetFamily(String targetFamily) { this.targetFamily = targetFamily; }

    public String getFilesRoot() { return filesRoot; }
    public void setFilesRoot(String filesRoot) {
        this.filesRoot = (filesRoot == null || filesRoot.isBlank()) ? "files" : filesRoot;
    }

    public String getArchiveRoot() { return archiveRoot; }
    public void setArchiveRoot(String archiveRoot) {
        this.archiveRoot = (archiveRoot == null || archiveRoot.isBlank()) ? null : archiveRoot.trim();
    }

    public PatchSource getSource() { return source; }
    public void setSource(PatchSource source) { this.source = source == null ? new PatchSource() : source; }

    public String[] getConflicts() { return conflicts; }
    public void setConflicts(String[] conflicts) {
        this.conflicts = conflicts == null ? new String[0] : conflicts;
    }

    public String[] getRequires() { return requires; }
    public void setRequires(String[] requires) {
        this.requires = requires == null ? new String[0] : requires;
    }

    public String[] getSupersedes() { return supersedes; }
    public void setSupersedes(String[] supersedes) {
        this.supersedes = supersedes == null ? new String[0] : supersedes;
    }
}
