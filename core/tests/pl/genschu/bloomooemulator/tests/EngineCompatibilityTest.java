package pl.genschu.bloomooemulator.tests;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import pl.genschu.bloomooemulator.TestEnvironment;
import pl.genschu.bloomooemulator.builders.ContextBuilder;
import pl.genschu.bloomooemulator.engine.Game;
import pl.genschu.bloomooemulator.engine.compatibility.Compatibility;
import pl.genschu.bloomooemulator.engine.compatibility.CompatibilityProfile;
import pl.genschu.bloomooemulator.engine.compatibility.EngineVariant;
import pl.genschu.bloomooemulator.interpreter.context.Context;
import pl.genschu.bloomooemulator.interpreter.helpers.ArgumentHelper;
import pl.genschu.bloomooemulator.interpreter.runtime.ASTInterpreter;
import pl.genschu.bloomooemulator.interpreter.serialization.ArrayValueCodec;
import pl.genschu.bloomooemulator.interpreter.values.BoolValue;
import pl.genschu.bloomooemulator.interpreter.values.DoubleValue;
import pl.genschu.bloomooemulator.interpreter.values.IntValue;
import pl.genschu.bloomooemulator.interpreter.values.StringValue;
import pl.genschu.bloomooemulator.interpreter.values.Value;
import pl.genschu.bloomooemulator.interpreter.variable.MultiArrayVariable;
import pl.genschu.bloomooemulator.interpreter.variable.TextVariable;
import pl.genschu.bloomooemulator.loader.MultiArrayLoader;
import pl.genschu.bloomooemulator.loader.helpers.InputStreamBinaryReader;
import pl.genschu.bloomooemulator.saver.MultiArraySaver;
import pl.genschu.bloomooemulator.logic.GameEntry;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HexFormat;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class EngineCompatibilityTest {
    private static final HexFormat HEX = HexFormat.of();

    // Generated in Reksio i Kapitan Nemo (BlooMooDLL).
    private static final byte[] BLOOMOO_ARR = hex(
            "0800000001000000010000000200000004000000546573740300000001000000",
            "04000000a8610000040000000a00000004000000393000000400000078ecffff",
            "0300000000000000");

    // Generated in Reksio i Czarodzieje (Piklib8).
    private static final byte[] PIKLIB8_ARR = hex(
            "0800000001000000010000000200000004000000546573740300000001000000",
            "04000000c4090000040000000100000004000000d2040000040000000cfeffff",
            "0300000000000000");

    private static final byte[] BLOOMOO_MAR = hex(
            "0200000010000000100000000000000001000000000000000100000001000000",
            "0300000002000000010000000600000010000000010000000100000011000000",
            "0100000004000000120000000100000007000000200000000100000002000000",
            "2100000001000000050000002200000001000000080000005500000004000000",
            "a861000056000000020000000400000054455354570000000300000001000000",
            "58000000040000000a00000059000000030000000000000000010000");

    private static final byte[] PIKLIB8_MAR = hex(
            "0200000010000000100000000000000001000000000000000100000001000000",
            "0300000002000000010000000600000010000000010000000100000011000000",
            "0100000004000000120000000100000007000000200000000100000002000000",
            "2100000001000000050000002200000001000000080000005500000004000000",
            "c409000056000000020000000400000054455354570000000300000001000000",
            "58000000040000000100000059000000030000000000000000010000");

    @BeforeAll
    static void boot() {
        TestEnvironment.init();
    }

    static Stream<Arguments> arrayFixtures() {
        return Stream.of(
                Arguments.of(EngineVariant.BLOOMOO, BLOOMOO_ARR),
                Arguments.of(EngineVariant.PIKLIB_8, PIKLIB8_ARR)
        );
    }

    static Stream<Arguments> multiArrayFixtures() {
        return Stream.of(
                Arguments.of(EngineVariant.BLOOMOO, BLOOMOO_MAR),
                Arguments.of(EngineVariant.PIKLIB_8, PIKLIB8_MAR)
        );
    }

    @AfterEach
    void clearAmbientProfile() {
        Compatibility.reset();
    }

    @Test
    void resolvesEngineVariantFromStoredGameVersion() {
        assertEquals(EngineVariant.BLOOMOO, EngineVariant.fromVersion("BlooMoo"));
        assertEquals(EngineVariant.PIKLIB_6_1, EngineVariant.fromVersion("Piklib v6.1"));
        assertEquals(EngineVariant.PIKLIB_7_2, EngineVariant.fromVersion("Piklib v7.2"));
        assertEquals(EngineVariant.PIKLIB_8, EngineVariant.fromVersion("Piklib v8"));
    }

    @Test
    void unlistedPiklibVersionKeepsPiklibQuirksInsteadOfFallingBackToUnknown() {
        // A piklib*.dll we have not catalogued is still Piklib: guessing BlooMoo
        // here would read every stored DOUBLE ten times too small.
        EngineVariant variant = EngineVariant.fromVersion("Piklib v7");
        assertEquals(EngineVariant.PIKLIB_OTHER, variant);
        assertEquals(1_000, variant.arrayDoubleScale());
        assertTrue(variant.hasPiklibDoubleStringQuirk());

        assertEquals(EngineVariant.UNKNOWN, EngineVariant.fromVersion("DLL not found"));
    }

    @Test
    void loadingAGameInstallsItsProfileForPlainValueConversions() {
        // No MethodContext involved: conversions must pick the engine up on their own.
        newGame("Piklib v8");
        assertEquals("0.-50000", ArgumentHelper.getString(new DoubleValue(-0.5)));

        newGame("BlooMoo");
        assertEquals("-0.50000", ArgumentHelper.getString(new DoubleValue(-0.5)));
    }

    @Test
    void arrayIoUsesTheRunningGamesDoubleScale() {
        Game game = newGame("BlooMoo");
        assertEquals(10_000, game.getCompatibilityProfile().arrayDoubleScale());

        game = newGame("Piklib v8");
        assertEquals(1_000, game.getCompatibilityProfile().arrayDoubleScale());
    }

    @ParameterizedTest
    @MethodSource("arrayFixtures")
    void readsAndReproducesOriginalArrayFixture(EngineVariant engine, byte[] fixture)
            throws IOException {
        CompatibilityProfile profile = CompatibilityProfile.forEngine(engine);
        InputStreamBinaryReader reader =
                new InputStreamBinaryReader(new ByteArrayInputStream(fixture));

        int count = reader.readI32LE();
        assertEquals(8, count);
        Value[] values = new Value[count];
        for (int i = 0; i < count; i++) {
            values[i] = ArrayValueCodec.read(reader, profile);
        }

        assertEquals(1, ((IntValue) values[0]).value());
        assertEquals("Test", ((StringValue) values[1]).value());
        assertTrue(((BoolValue) values[2]).value());
        assertEquals(2.5, ((DoubleValue) values[3]).value());
        assertEquals(0.001, ((DoubleValue) values[4]).value());
        assertEquals(engine == EngineVariant.BLOOMOO ? 1.2345 : 1.234,
                ((DoubleValue) values[5]).value());
        assertEquals(-0.5, ((DoubleValue) values[6]).value());
        assertFalse(((BoolValue) values[7]).value());

        ByteArrayOutputStream saved = new ByteArrayOutputStream();
        ArrayValueCodec.writeInt(saved, values.length);
        for (Value value : values) {
            ArrayValueCodec.write(saved, value, profile);
        }
        assertArrayEquals(fixture, saved.toByteArray());
    }

    @ParameterizedTest
    @MethodSource("multiArrayFixtures")
    void readsAndReproducesOriginalMultiArrayFixture(
            EngineVariant engine, byte[] fixture) {
        CompatibilityProfile profile = CompatibilityProfile.forEngine(engine);
        MultiArrayVariable variable = new MultiArrayVariable("TEST");

        MultiArrayLoader.loadMultiArray(
                variable, new ByteArrayInputStream(fixture), profile);

        assertArrayEquals(new int[]{16, 16}, variable.getDimensions());
        assertEquals(2.5, doubleAt(variable, 5, 5));
        assertEquals("TEST", stringAt(variable, 6, 5));
        assertTrue(boolAt(variable, 7, 5));
        assertEquals(0.001, doubleAt(variable, 8, 5));
        assertFalse(boolAt(variable, 9, 5));

        ByteArrayOutputStream saved = new ByteArrayOutputStream();
        MultiArraySaver.saveMultiArray(variable, saved, profile);
        assertArrayEquals(fixture, saved.toByteArray());
    }

    @Test
    void formatsDoubleLikeTheSelectedOriginalEngine() {
        CompatibilityProfile bloomoo =
                CompatibilityProfile.forEngine(EngineVariant.BLOOMOO);
        CompatibilityProfile piklib =
                CompatibilityProfile.forEngine(EngineVariant.PIKLIB_8);

        List<Double> values = List.of(0.0, 2.5, 0.001, 1.2345, -0.5);
        List<String> bloomooExpected =
                List.of("0", "2.50000", "0.00100", "1.23450", "-0.50000");
        List<String> piklibExpected =
                List.of("00000", "2.50000", "0.00100", "1.23450", "0.-50000");

        for (int i = 0; i < values.size(); i++) {
            DoubleValue value = new DoubleValue(values.get(i));
            assertEquals(bloomooExpected.get(i), value.toStringValue(bloomoo).value());
            assertEquals(piklibExpected.get(i), value.toStringValue(piklib).value());
        }
    }

    @Test
    void methodCoercionUsesTheRunningGamesEngineProfile() {
        assertEquals("-0.50000", setTextWithEngine("BlooMoo"));
        assertEquals("0.-50000", setTextWithEngine("Piklib v8"));
    }

    private static double doubleAt(MultiArrayVariable variable, int first, int second) {
        return ((DoubleValue) variable.callMethod(
                "GET", List.of(new IntValue(first), new IntValue(second)))
                .getReturnValue()).value();
    }

    private static String stringAt(MultiArrayVariable variable, int first, int second) {
        return ((StringValue) variable.callMethod(
                "GET", List.of(new IntValue(first), new IntValue(second)))
                .getReturnValue()).value();
    }

    private static boolean boolAt(MultiArrayVariable variable, int first, int second) {
        return ((BoolValue) variable.callMethod(
                "GET", List.of(new IntValue(first), new IntValue(second)))
                .getReturnValue()).value();
    }

    /** Creates a Game for {@code version}, which also installs its ambient profile. */
    private static Game newGame(String version) {
        GameEntry entry = new GameEntry();
        entry.setVersion(version);
        return new Game(entry, null);
    }

    private static String setTextWithEngine(String version) {
        Game game = newGame(version);
        Context context = new ContextBuilder().build();
        context.setGame(game);

        TextVariable text = new TextVariable("TEXT");
        text.callMethod(
                "SETTEXT",
                List.of(new DoubleValue(-0.5)),
                new ASTInterpreter(context).getMethodContext());
        return ((StringValue) text.value()).value();
    }

    private static byte[] hex(String... chunks) {
        return HEX.parseHex(String.join("", chunks));
    }
}
