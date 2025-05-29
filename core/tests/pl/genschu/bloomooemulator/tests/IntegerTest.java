package pl.genschu.bloomooemulator.tests;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import pl.genschu.bloomooemulator.TestEnvironment;
import pl.genschu.bloomooemulator.builders.ContextBuilder;
import pl.genschu.bloomooemulator.interpreter.Context;
import pl.genschu.bloomooemulator.interpreter.variable.types.DoubleVariable;
import pl.genschu.bloomooemulator.interpreter.variable.types.IntegerVariable;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class IntegerTest {
    private Context ctx;

    private static final int[] TEST_VECTORS = {
            -5, 0, 3, 90, 421, -176, -348, 15181818, -54867938
    };

    private static final Map<String, String[]> EXPECTED_RESULTS = new HashMap<>();

    @BeforeAll
    static void boot() {
        TestEnvironment.init();
        initializeExpectedResults();
    }

    private static void initializeExpectedResults() {
        EXPECTED_RESULTS.put("ABS", new String[]{"5", "0", "3", "90", "421", "176", "348", "15181818", "54867938"});
        EXPECTED_RESULTS.put("ADD", new String[]{"0", "5", "8", "95", "426", "-171", "-343", "15181823", "-54867933"});
        EXPECTED_RESULTS.put("AND", new String[]{"555", "0", "3", "10", "33", "512", "544", "554", "10"});
        EXPECTED_RESULTS.put("CLAMP", new String[]{"0", "0", "3", "50", "50", "0", "0", "50", "0"});
        EXPECTED_RESULTS.put("CLEAR", new String[]{"0", "0", "0", "0", "0", "0", "0", "0", "0"});
        EXPECTED_RESULTS.put("DEC", new String[]{"-6", "-1", "2", "89", "420", "-177", "-349", "15181817", "-54867939"});
        EXPECTED_RESULTS.put("DIV", new String[]{"-1", "0", "0", "18", "84", "-35", "-69", "3036363", "-10973587"});
        EXPECTED_RESULTS.put("GET", new String[]{"-5", "0", "3", "90", "421", "-176", "-348", "15181818", "-54867938"});
        EXPECTED_RESULTS.put("INC", new String[]{"-4", "1", "4", "91", "422", "-175", "-347", "15181819", "-54867937"});
        EXPECTED_RESULTS.put("LENGTH", new String[]{"-1610612736", "-1610612736", "-1610612736", "-1610612736", "-1610612736", "-1610612736", "-1610612736", "-1610612736", "-1610612736"});
        EXPECTED_RESULTS.put("MOD", new String[]{"0", "0", "3", "0", "1", "-1", "-3", "3", "-3"});
        EXPECTED_RESULTS.put("MUL", new String[]{"-25", "0", "15", "450", "2105", "-880", "-1740", "75909090", "-274339690"});
        EXPECTED_RESULTS.put("NOT", new String[]{"4", "-1", "-4", "-91", "-422", "175", "347", "-15181819", "54867937"});
        EXPECTED_RESULTS.put("OR", new String[]{"-5", "555", "555", "635", "943", "-133", "-337", "15181819", "-54867393"});
        EXPECTED_RESULTS.put("POWER", new String[]{"25", "0", "9", "8100", "177241", "30976", "121104", "-2147483648", "-2147483648"});
        EXPECTED_RESULTS.put("RANDOM", new String[]{"1", "0", "0", "71", "238", "38", "280", "5720506", "1729754"});
        EXPECTED_RESULTS.put("SUB", new String[]{"-10", "-5", "-2", "85", "416", "-181", "-353", "15181813", "-54867943"});
        EXPECTED_RESULTS.put("SWITCH", new String[]{"0", "15", "0", "0", "0", "0", "0", "0", "0"});
        EXPECTED_RESULTS.put("XOR", new String[]{"-560", "555", "552", "625", "910", "-645", "-881", "15181265", "-54867403"});
    }

    @BeforeEach
    void setUp() {
        ctx = new ContextBuilder().build();
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 3, 4, 5, 6})
    void testAbs(int vectorIndex) {
        int testValue = TEST_VECTORS[vectorIndex];

        IntegerVariable testVar;
        int result;

        testVar = new IntegerVariable("TEST", testValue, ctx);
        testVar.fireMethod("ABS", new IntegerVariable("", testValue, ctx));
        result = testVar.GET();
        assertEquals(parseInt(EXPECTED_RESULTS.get("ABS")[vectorIndex]), result,
                "ABS failed for vector " + testValue);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 3, 4, 5, 6})
    void testAdd(int vectorIndex) {
        int testValue = TEST_VECTORS[vectorIndex];

        IntegerVariable testVar;
        int result;

        testVar = new IntegerVariable("TEST", testValue, ctx);
        testVar.fireMethod("ADD", new IntegerVariable("", 5, ctx));
        result = testVar.GET();
        assertEquals(parseInt(EXPECTED_RESULTS.get("ADD")[vectorIndex]), result,
                "ADD failed for vector " + testValue);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 3, 4, 5, 6})
    void testAND(int vectorIndex) {
        int testValue = TEST_VECTORS[vectorIndex];

        IntegerVariable testVar;
        int result;

        testVar = new IntegerVariable("TEST", testValue, ctx);
        testVar.fireMethod("AND", new IntegerVariable("", 555, ctx));
        result = testVar.GET();
        assertEquals(parseInt(EXPECTED_RESULTS.get("AND")[vectorIndex]), result,
                "AND failed for vector " + testValue);

    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 3, 4, 5, 6})
    void testClamp(int vectorIndex) {
        int testValue = TEST_VECTORS[vectorIndex];

        IntegerVariable testVar;
        int result;

        testVar = new IntegerVariable("TEST", testValue, ctx);
        testVar.fireMethod("CLAMP", new IntegerVariable("", 0, ctx), new IntegerVariable("", 50, ctx));
        result = testVar.GET();
        assertEquals(parseInt(EXPECTED_RESULTS.get("CLAMP")[vectorIndex]), result,
                "CLAMP failed for vector " + testValue);
    }

    @Test
    void testClear() {
        int testValue = TEST_VECTORS[0];

        IntegerVariable testVar;
        int result;

        testVar = new IntegerVariable("TEST", testValue, ctx);
        testVar.fireMethod("CLEAR");
        result = testVar.GET();
        assertEquals(0, result,
                "CLEAR failed for vector " + testValue);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 3, 4, 5, 6})
    void testDec(int vectorIndex) {
        int testValue = TEST_VECTORS[vectorIndex];

        IntegerVariable testVar;
        int result;

        testVar = new IntegerVariable("TEST", testValue, ctx);
        testVar.fireMethod("DEC");
        result = testVar.GET();
        assertEquals(parseInt(EXPECTED_RESULTS.get("DEC")[vectorIndex]), result,
                "DEC failed for vector " + testValue);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 3, 4, 5, 6})
    void testDiv(int vectorIndex) {
        int testValue = TEST_VECTORS[vectorIndex];

        IntegerVariable testVar;
        int result;

        testVar = new IntegerVariable("TEST", testValue, ctx);
        testVar.fireMethod("DIV", new IntegerVariable("", 5, ctx));
        result = testVar.GET();
        assertEquals(parseInt(EXPECTED_RESULTS.get("DIV")[vectorIndex]), result,
                "DIV failed for vector " + testValue);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 3, 4, 5, 6})
    void testInc(int vectorIndex) {
        int testValue = TEST_VECTORS[vectorIndex];

        IntegerVariable testVar;
        int result;

        testVar = new IntegerVariable("TEST", testValue, ctx);
        testVar.fireMethod("INC");
        result = testVar.GET();
        assertEquals(parseInt(EXPECTED_RESULTS.get("INC")[vectorIndex]), result,
                "INC failed for vector " + testValue);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 3, 4, 5, 6})
    void testMod(int vectorIndex) {
        int testValue = TEST_VECTORS[vectorIndex];

        IntegerVariable testVar;
        int result;

        testVar = new IntegerVariable("TEST", testValue, ctx);
        testVar.fireMethod("MOD", new IntegerVariable("", 5, ctx));
        result = testVar.GET();
        assertEquals(parseInt(EXPECTED_RESULTS.get("MOD")[vectorIndex]), result,
                "MOD failed for vector " + testValue);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 3, 4, 5, 6})
    void testMul(int vectorIndex) {
        int testValue = TEST_VECTORS[vectorIndex];

        IntegerVariable testVar;
        int result;

        testVar = new IntegerVariable("TEST", testValue, ctx);
        testVar.fireMethod("MUL", new IntegerVariable("", 5, ctx));
        result = testVar.GET();
        assertEquals(parseInt(EXPECTED_RESULTS.get("MUL")[vectorIndex]), result,
                "MUL failed for vector " + testValue);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 3, 4, 5, 6})
    void testNot(int vectorIndex) {
        int testValue = TEST_VECTORS[vectorIndex];

        IntegerVariable testVar;
        int result;

        testVar = new IntegerVariable("TEST", testValue, ctx);
        testVar.fireMethod("NOT", new IntegerVariable("", 555, ctx));
        result = testVar.GET();
        assertEquals(parseInt(EXPECTED_RESULTS.get("NOT")[vectorIndex]), result,
                "NOT failed for vector " + testValue);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 3, 4, 5, 6})
    void testOr(int vectorIndex) {
        int testValue = TEST_VECTORS[vectorIndex];

        IntegerVariable testVar;
        int result;

        testVar = new IntegerVariable("TEST", testValue, ctx);
        testVar.fireMethod("OR", new IntegerVariable("", 555, ctx));
        result = testVar.GET();
        assertEquals(parseInt(EXPECTED_RESULTS.get("OR")[vectorIndex]), result,
                "OR failed for vector " + testValue);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 3, 4, 5, 6})
    void testPower(int vectorIndex) {
        int testValue = TEST_VECTORS[vectorIndex];

        IntegerVariable testVar;
        int result;

        testVar = new IntegerVariable("TEST", testValue, ctx);
        testVar.fireMethod("POWER", new DoubleVariable("", 1.5, ctx));
        result = testVar.GET();
        assertEquals(parseInt(EXPECTED_RESULTS.get("POWER")[vectorIndex]), result,
                "POWER failed for vector " + testValue);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 3, 4, 5, 6})
    void testSub(int vectorIndex) {
        int testValue = TEST_VECTORS[vectorIndex];

        IntegerVariable testVar;
        int result;

        testVar = new IntegerVariable("TEST", testValue, ctx);
        testVar.fireMethod("SUB", new IntegerVariable("", 5, ctx));
        result = testVar.GET();
        assertEquals(parseInt(EXPECTED_RESULTS.get("SUB")[vectorIndex]), result,
                "SUB failed for vector " + testValue);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 3, 4, 5, 6})
    void testXor(int vectorIndex) {
        int testValue = TEST_VECTORS[vectorIndex];

        IntegerVariable testVar;
        int result;

        testVar = new IntegerVariable("TEST", testValue, ctx);
        testVar.fireMethod("XOR", new IntegerVariable("", 555, ctx));
        result = testVar.GET();
        assertEquals(parseInt(EXPECTED_RESULTS.get("XOR")[vectorIndex]), result,
                "XOR failed for vector " + testValue);
    }

    private int parseInt(String value) {
        return Integer.parseInt(value);
    }

    @Test
    void testSwitch() {
        IntegerVariable switchVar1 = new IntegerVariable("TEST", 0, ctx);
        switchVar1.fireMethod("SWITCH",
                new IntegerVariable("", 0, ctx),
                new IntegerVariable("", 15, ctx));
        assertEquals(15, switchVar1.GET(), 0.00001);

        IntegerVariable switchVar2 = new IntegerVariable("TEST", 5, ctx);
        switchVar2.fireMethod("SWITCH",
                new IntegerVariable("", 0, ctx),
                new IntegerVariable("", 15, ctx));
        assertEquals(0, switchVar2.GET(), 0.00001);
    }
}
