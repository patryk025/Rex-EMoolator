package pl.genschu.bloomooemulator.engine.compatibility;

import pl.genschu.bloomooemulator.logic.GameEntry;

/**
 * Immutable compatibility information for one running game: which original it is and,
 * resolved from that once, how its libraries behave.
 *
 * @param engine engine DLL variant
 * @param gameFamily stable game-family slug, or {@code null} when unknown
 * @param behaviour how the original behaves; {@code null} resolves it from the two above
 */
public record CompatibilityProfile(
        EngineVariant engine,
        String gameFamily,
        EngineBehaviour behaviour
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
        if (behaviour == null) {
            behaviour = BehaviourTable.resolve(engine, gameFamily);
        }
    }

    public CompatibilityProfile(EngineVariant engine, String gameFamily) {
        this(engine, gameFamily, null);
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
}
