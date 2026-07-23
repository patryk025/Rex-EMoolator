package pl.genschu.bloomooemulator.engine.compatibility;

import java.util.Locale;

/**
 * Identifies the original script-engine binary used by a game.
 *
 * <p>The variants are intentionally kept separate even when they currently
 * share quirks. This lets a newly discovered difference be represented without
 * changing call sites or guessing from the game name.</p>
 */
public enum EngineVariant {
    BLOOMOO(10_000, false),
    PIKLIB_6_1(1_000, true),
    PIKLIB_7_1(1_000, true),
    PIKLIB_7_2(1_000, true),
    PIKLIB_8(1_000, true),

    /**
     * A Piklib DLL whose exact version is not one of the above. The quirks are
     * shared by every Piklib version seen so far, so this is a far better guess
     * than {@link #UNKNOWN} — which would silently read arrays at BlooMoo scale.
     */
    PIKLIB_OTHER(1_000, true),

    /**
     * Compatibility fallback for contexts which do not have a GameEntry
     * (mostly isolated unit tests). It preserves the emulator's former mixed
     * defaults: BlooMoo ARRAY precision and Piklib double formatting.
     */
    UNKNOWN(10_000, true);

    private final int arrayDoubleScale;
    private final boolean piklibDoubleStringQuirk;

    EngineVariant(int arrayDoubleScale, boolean piklibDoubleStringQuirk) {
        this.arrayDoubleScale = arrayDoubleScale;
        this.piklibDoubleStringQuirk = piklibDoubleStringQuirk;
    }

    public int arrayDoubleScale() {
        return arrayDoubleScale;
    }

    public boolean hasPiklibDoubleStringQuirk() {
        return piklibDoubleStringQuirk;
    }

    public static EngineVariant fromVersion(String version) {
        if (version == null || version.isBlank()) {
            return UNKNOWN;
        }

        String normalized = version.trim().toUpperCase(Locale.ROOT);
        if (normalized.equals("BLOOMOO")) {
            return BLOOMOO;
        }
        if (!normalized.startsWith("PIKLIB")) {
            return UNKNOWN;
        }

        String number = normalized
                .replace("PIKLIB", "")
                .replace("V", "")
                .trim();
        return switch (number) {
            case "6.1", "61" -> PIKLIB_6_1;
            case "7.1", "71" -> PIKLIB_7_1;
            case "7.2", "72" -> PIKLIB_7_2;
            case "8", "8.0", "80" -> PIKLIB_8;
            // GameEntry derives the version from the DLL file name, so an
            // unlisted piklib*.dll is still unambiguously Piklib.
            default -> PIKLIB_OTHER;
        };
    }
}
