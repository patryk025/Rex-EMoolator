package pl.genschu.bloomooemulator.engine.compatibility;

/**
 * Every script-observable behaviour that differs between the original builds.
 *
 * <p>Engine code asks this record how to behave and never tests the engine variant or the
 * game itself. Which build behaves how is decided in one place, {@link BehaviourTable}.</p>
 *
 * @param doubleToInteger       {@code CMC_Double::convert(INTEGER)}: method arguments, the right
 *                              operand of an expression, {@code INTEGER^SET}
 * @param doubleToString        {@code CXString::toString(double)}
 * @param archiveDouble         how {@code CXArchive} stores a DOUBLE (ARRAY and MULTIARRAY files)
 * @param frameOrder            order of script input, canvas presentation and managers in a frame
 * @param physicsSubsteps       whether Sekai splits a long WORLD step into ~60 Hz substeps
 * @param setActiveFallsThrough whether the three-argument {@code WORLD^SETACTIVE} runs on into
 *                              the default {@code SetActive(id, TRUE, TRUE)} (a missing return)
 */
public record EngineBehaviour(
        DoubleToInteger doubleToInteger,
        DoubleToString doubleToString,
        ArchiveDouble archiveDouble,
        LegacyFrameOrder frameOrder,
        boolean physicsSubsteps,
        boolean setActiveFallsThrough
) {
    public enum DoubleToInteger {
        /** Plain {@code _ftol}: 0.5 -> 0, -0.5 -> 0, 7.99 -> 7. */
        TRUNCATE,
        /** {@code CMC_Double::round}: 0.5 -> 1, -0.5 -> -1. */
        ROUND_HALF_AWAY_FROM_ZERO
    }

    public enum DoubleToString {
        /** Malformed for zero and negative fractions: "00000", "0.-50000". */
        PIKLIB,
        BLOOMOO
    }

    public enum ArchiveDouble {
        /** The raw eight bytes of the double. */
        IEEE_754(0),
        /** {@code int32 = value * 1000}. */
        FIXED_POINT_1000(1_000),
        /** {@code int32 = value * 10000}. */
        FIXED_POINT_10000(10_000);

        private final int scale;

        ArchiveDouble(int scale) {
            this.scale = scale;
        }

        /** Fixed-point scale, or 0 for {@link #IEEE_754}. */
        public int scale() {
            return scale;
        }
    }

    public EngineBehaviour withDoubleToInteger(DoubleToInteger value) {
        return new EngineBehaviour(value, doubleToString, archiveDouble, frameOrder,
                physicsSubsteps, setActiveFallsThrough);
    }

    public EngineBehaviour withDoubleToString(DoubleToString value) {
        return new EngineBehaviour(doubleToInteger, value, archiveDouble, frameOrder,
                physicsSubsteps, setActiveFallsThrough);
    }

    public EngineBehaviour withArchiveDouble(ArchiveDouble value) {
        return new EngineBehaviour(doubleToInteger, doubleToString, value, frameOrder,
                physicsSubsteps, setActiveFallsThrough);
    }

    public EngineBehaviour withFrameOrder(LegacyFrameOrder value) {
        return new EngineBehaviour(doubleToInteger, doubleToString, archiveDouble, value,
                physicsSubsteps, setActiveFallsThrough);
    }

    public EngineBehaviour withPhysicsSubsteps(boolean value) {
        return new EngineBehaviour(doubleToInteger, doubleToString, archiveDouble, frameOrder,
                value, setActiveFallsThrough);
    }

    public EngineBehaviour withSetActiveFallsThrough(boolean value) {
        return new EngineBehaviour(doubleToInteger, doubleToString, archiveDouble, frameOrder,
                physicsSubsteps, value);
    }
}
