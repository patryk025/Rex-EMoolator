package pl.genschu.bloomooemulator.engine.compatibility;

import pl.genschu.bloomooemulator.logic.GameEntry;
import pl.genschu.bloomooemulator.logic.GameFamilies;

/**
 * Immutable compatibility information for one running game.
 *
 * @param engine engine DLL variant
 * @param gameFamily stable game-family slug, or {@code null} when unknown
 */
public record CompatibilityProfile(
        EngineVariant engine,
        String gameFamily
) {
    private static final CompatibilityProfile UNKNOWN =
            new CompatibilityProfile(EngineVariant.UNKNOWN, null);

    public CompatibilityProfile {
        if (engine == null) {
            engine = EngineVariant.UNKNOWN;
        }
        if (gameFamily != null && gameFamily.isBlank()) {
            gameFamily = null;
        }
    }

    public static CompatibilityProfile from(GameEntry entry) {
        if (entry == null) {
            return unknown();
        }
        return new CompatibilityProfile(
                EngineVariant.fromVersion(entry.getVersion()),
                entry.resolveFamily()
        );
    }

    public static CompatibilityProfile forEngine(EngineVariant engine) {
        return new CompatibilityProfile(engine, null);
    }

    public static CompatibilityProfile unknown() {
        return UNKNOWN;
    }

    public int arrayDoubleScale() {
        return engine.arrayDoubleScale();
    }

    public boolean hasPiklibDoubleStringQuirk() {
        return engine.hasPiklibDoubleStringQuirk();
    }

    /** Whether DOUBLE to INTEGER rounds half away from zero rather than truncating. */
    public boolean roundsDoubleToInteger() {
        return engine.roundsDoubleToInteger() && !isFirstBlooMoo();
    }

    /**
     * Reksio i Wehikuł Czasu ships the first BlooMooDLL, which in these respects still
     * behaves like Piklib; the differences arrive with Reksio i Kapitan Nemo.
     */
    private boolean isFirstBlooMoo() {
        return engine == EngineVariant.BLOOMOO
                && isGameFamily(GameFamilies.REKSIO_WEHIKUL_CZASU);
    }

    public boolean isGameFamily(String family) {
        return family != null && family.equals(gameFamily);
    }
}
