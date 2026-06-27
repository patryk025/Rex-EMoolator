package pl.genschu.bloomooemulator.patch;

/**
 * UI-agnostic view of a single patch row in the per-game patch manager: the
 * manifest plus everything a view needs to render it (compatibility, install /
 * enable state, mount order). Produced by {@link PatchManagerController#rows()}
 * and consumed by both the Swing dialog and the Android list.
 *
 * <p>Immutable snapshot — re-query {@link PatchManagerController#rows()} after any
 * mutation to pick up changes.
 */
public final class PatchRowVM {
    private final PatchManifest manifest;
    private final PatchCompatibility compat;
    private final boolean installed;
    private final boolean enabled;
    private final boolean hasEntry;
    private final int order;

    public PatchRowVM(PatchManifest manifest, PatchCompatibility compat, boolean installed,
                      boolean enabled, boolean hasEntry, int order) {
        this.manifest = manifest;
        this.compat = compat;
        this.installed = installed;
        this.enabled = enabled;
        this.hasEntry = hasEntry;
        this.order = order;
    }

    public PatchManifest getManifest() { return manifest; }
    public PatchCompatibility getCompat() { return compat; }
    public boolean isInstalled() { return installed; }
    public boolean isEnabled() { return enabled; }

    /** Whether the registry already tracks this (game, patch) pairing — gates reordering. */
    public boolean hasEntry() { return hasEntry; }

    public int getOrder() { return order; }

    public String getId() { return manifest.getId(); }

    public String getDisplayName() {
        return manifest.getName() != null ? manifest.getName() : manifest.getId();
    }

    public String getVersion() {
        return manifest.getVersion() != null ? manifest.getVersion() : "";
    }

    public String getDescription() { return manifest.getDescription(); }

    /** True when the patch declares a direct-download source the installer can fetch. */
    public boolean isDownloadable() {
        PatchSource source = manifest.getSource();
        return source != null && source.getType() == PatchSourceType.URL;
    }
}
