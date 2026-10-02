package pl.genschu.bloomooemulator.engine.compatibility;

import java.util.Locale;

/**
 * Identifies the original script-engine binary used by a game.
 *
 * <p>The variants are intentionally kept separate even when they currently
 * behave alike. This is only an identity: how each variant behaves is listed in
 * {@link BehaviourTable} and reaches engine code through {@link EngineBehaviour}.</p>
 */
public enum EngineVariant {
    BLOOMOO,
    PIKLIB_6_1,
    PIKLIB_7_1,
    PIKLIB_7_2,
    PIKLIB_8,

    /** A Piklib DLL whose exact version is not one of the above. */
    PIKLIB_OTHER,

    /** No GameEntry is available (mostly isolated unit tests). */
    UNKNOWN;

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
