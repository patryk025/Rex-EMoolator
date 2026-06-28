package pl.genschu.bloomooemulator.logic;

/**
 * How pointer input is handled for a game.
 *
 * <p>Persisted in {@code games.json} (via {@link GameEntry#getMouseMode()}) as the
 * locale-independent {@link #key()}, deliberately decoupled from the human-readable
 * label shown in the UI — so the label can be translated freely without corrupting
 * stored configs. {@link #fromStored(String)} additionally recognises the legacy
 * values older builds wrote (the then-displayed PL/EN label), keeping existing
 * {@code games.json} files working.
 *
 * <p>The enum order is the canonical UI ordering (spinner / combo-box positions);
 * persistence uses {@link #key()}, never the ordinal, so reordering stays safe on disk.
 */
public enum MouseMode {
    TOUCH("TOUCH", "Dotykowo", "Touchscreen"),
    PHYSICAL("PHYSICAL", "Fizyczny kursor", "Physical cursor");

    private final String key;
    private final String[] legacyAliases;

    MouseMode(String key, String... legacyAliases) {
        this.key = key;
        this.legacyAliases = legacyAliases;
    }

    /** Stable, locale-independent identifier written to disk. */
    public String key() {
        return key;
    }

    /** Fallback used when nothing is stored or the stored value is unrecognised. */
    public static MouseMode defaultMode() {
        return TOUCH;
    }

    /**
     * Resolves a persisted value to a mode: matches the canonical {@link #key()}
     * first, then legacy display labels (case-insensitive, trimmed). Unknown or
     * {@code null} input yields {@link #defaultMode()}.
     */
    public static MouseMode fromStored(String stored) {
        if (stored != null) {
            String trimmed = stored.trim();
            for (MouseMode mode : values()) {
                if (mode.key.equalsIgnoreCase(trimmed)) {
                    return mode;
                }
                for (String alias : mode.legacyAliases) {
                    if (alias.equalsIgnoreCase(trimmed)) {
                        return mode;
                    }
                }
            }
        }
        return defaultMode();
    }
}
