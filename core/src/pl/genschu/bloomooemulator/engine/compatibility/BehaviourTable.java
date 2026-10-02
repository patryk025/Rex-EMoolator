package pl.genschu.bloomooemulator.engine.compatibility;

import pl.genschu.bloomooemulator.engine.compatibility.EngineBehaviour.ArchiveDouble;
import pl.genschu.bloomooemulator.engine.compatibility.EngineBehaviour.DoubleToInteger;
import pl.genschu.bloomooemulator.engine.compatibility.EngineBehaviour.DoubleToString;
import pl.genschu.bloomooemulator.logic.GameFamilies;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.UnaryOperator;

/**
 * The single place that says which original build behaves how.
 *
 * <p>A behaviour is resolved in two steps. The engine variant gives the defaults; it is all
 * that is known from the DLL file name. A game whose libraries deviate from those defaults —
 * an in-between build of the engine, or its own World.dll/Sekai.dll — then overrides the
 * entries that differ. A newly found difference becomes a field of {@link EngineBehaviour}
 * and a line here, not a condition at the call site.</p>
 */
final class BehaviourTable {
    private BehaviourTable() {}

    private static final EngineBehaviour PIKLIB = new EngineBehaviour(
            DoubleToInteger.TRUNCATE,
            DoubleToString.PIKLIB,
            ArchiveDouble.FIXED_POINT_1000,
            LegacyFrameOrder.INPUT_MANAGERS_RENDER,
            true,
            false);

    /** The rewritten BlooMooDLL (Visual Studio 2005 build), first shipped with Reksio i Kapitan Nemo. */
    private static final EngineBehaviour BLOOMOO = new EngineBehaviour(
            DoubleToInteger.ROUND_HALF_AWAY_FROM_ZERO,
            DoubleToString.BLOOMOO,
            ArchiveDouble.FIXED_POINT_10000,
            LegacyFrameOrder.RENDER_INPUT_MANAGERS,
            true,
            false);

    private static final Map<EngineVariant, EngineBehaviour> BY_ENGINE = new EnumMap<>(EngineVariant.class);
    static {
        // 6.1 has no binary ARRAY I/O at all, so its archive encoding is never exercised.
        BY_ENGINE.put(EngineVariant.PIKLIB_6_1, PIKLIB);
        // 7.1 writes a DOUBLE through the same routine as an __int64; the scale arrives with 7.2.
        BY_ENGINE.put(EngineVariant.PIKLIB_7_1, PIKLIB.withArchiveDouble(ArchiveDouble.IEEE_754));
        BY_ENGINE.put(EngineVariant.PIKLIB_7_2, PIKLIB);
        BY_ENGINE.put(EngineVariant.PIKLIB_8,
                PIKLIB.withFrameOrder(LegacyFrameOrder.INPUT_RENDER_MANAGERS));
        // Every Piklib seen so far shares these, which makes them a far better guess for an
        // uncatalogued piklib*.dll than UNKNOWN.
        BY_ENGINE.put(EngineVariant.PIKLIB_OTHER, PIKLIB);
        BY_ENGINE.put(EngineVariant.BLOOMOO, BLOOMOO);
        // No GameEntry (mostly isolated unit tests): the emulator's former mixed defaults.
        BY_ENGINE.put(EngineVariant.UNKNOWN, BLOOMOO
                .withDoubleToString(DoubleToString.PIKLIB)
                .withFrameOrder(LegacyFrameOrder.INPUT_MANAGERS_RENDER));
    }

    private static final Map<String, UnaryOperator<EngineBehaviour>> BY_GAME_FAMILY = Map.of(
            // World.dll misses a return in SETACTIVE and Sekai.dll predates substepping.
            GameFamilies.REKSIO_CZARODZIEJE, behaviour -> behaviour
                    .withPhysicsSubsteps(false)
                    .withSetActiveFallsThrough(true),

            // The first BlooMooDLL: built with the Piklib toolchain five months after Piklib 8
            // and still carrying its number conversions. Its window loop is already BlooMoo's.
            GameFamilies.REKSIO_WEHIKUL_CZASU, behaviour -> behaviour
                    .withDoubleToInteger(DoubleToInteger.TRUNCATE)
                    .withDoubleToString(DoubleToString.PIKLIB)
                    .withArchiveDouble(ArchiveDouble.FIXED_POINT_1000)
    );

    static EngineBehaviour resolve(EngineVariant engine, String gameFamily) {
        EngineBehaviour behaviour = BY_ENGINE.get(engine != null ? engine : EngineVariant.UNKNOWN);
        UnaryOperator<EngineBehaviour> override =
                gameFamily != null ? BY_GAME_FAMILY.get(gameFamily) : null;
        return override != null ? override.apply(behaviour) : behaviour;
    }
}
