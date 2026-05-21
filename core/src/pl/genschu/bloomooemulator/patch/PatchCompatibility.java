package pl.genschu.bloomooemulator.patch;

/**
 * How well a patch matches the currently loaded game.
 *
 * EXACT  - the game's DLL hash is in {@code targetHashes}; safe to enable by default.
 * FAMILY - hash differs but {@code targetFamily} matches the game; user opt-in with warning.
 * NONE   - no match on either axis; show only via force-enable in the UI.
 */
public enum PatchCompatibility {
    EXACT,
    FAMILY,
    NONE
}
