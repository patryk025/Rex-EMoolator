package pl.genschu.bloomooemulator.engine.compatibility;

/**
 * Ambient {@link CompatibilityProfile} of the game currently being emulated.
 *
 * <p>Which original binary a value came from is a property of the running
 * emulator, not of the individual call — exactly like the settings in
 * {@link pl.genschu.bloomooemulator.engine.config.EngineConfig}. Threading the
 * profile through every conversion instead would make it opt-in, and any
 * forgotten call site would silently fall back to a profile matching no real
 * engine.</p>
 *
 * <p>The emulator runs one game at a time on the LibGDX render thread, so a
 * single holder is enough. {@link pl.genschu.bloomooemulator.engine.Game}
 * installs its profile on construction; tests that need a specific engine use
 * {@link #install} and restore with {@link #reset}.</p>
 */
public final class Compatibility {
    private static volatile CompatibilityProfile current = CompatibilityProfile.unknown();

    private Compatibility() {}

    /** The profile of the running game, never {@code null}. */
    public static CompatibilityProfile current() {
        return current;
    }

    /** Makes {@code profile} the ambient one; {@code null} resets to unknown. */
    public static void install(CompatibilityProfile profile) {
        current = profile != null ? profile : CompatibilityProfile.unknown();
    }

    /** Drops back to the neutral profile (no game loaded). */
    public static void reset() {
        current = CompatibilityProfile.unknown();
    }
}
