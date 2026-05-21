package pl.genschu.bloomooemulator.patch;

import java.io.Serializable;

/**
 * Per-(game, patch) enable state, persisted in {@code <userDir>/patches/index.json}.
 *
 * One entry per pairing: the same patch can be enabled for game A and disabled for B,
 * or enabled with {@code forceEnable=true} for a game whose hash isn't on the
 * patch's target list (yellow/red opt-in).
 *
 * Kept as a flat list rather than a nested map so libGDX {@code Json} can
 * round-trip it without {@code @ElementType} hints.
 */
public class PatchRegistryEntry implements Serializable {
    private String gameHash;
    private String patchId;
    private boolean enabled;
    private boolean forceEnable;
    /** Free-form ordering hint; lower numbers mount first (= lower priority). */
    private int order;

    public PatchRegistryEntry() {}

    public PatchRegistryEntry(String gameHash, String patchId, boolean enabled, boolean forceEnable, int order) {
        this.gameHash = gameHash;
        this.patchId = patchId;
        this.enabled = enabled;
        this.forceEnable = forceEnable;
        this.order = order;
    }

    public String getGameHash() { return gameHash; }
    public void setGameHash(String gameHash) { this.gameHash = gameHash; }

    public String getPatchId() { return patchId; }
    public void setPatchId(String patchId) { this.patchId = patchId; }

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }

    public boolean isForceEnable() { return forceEnable; }
    public void setForceEnable(boolean forceEnable) { this.forceEnable = forceEnable; }

    public int getOrder() { return order; }
    public void setOrder(int order) { this.order = order; }
}
