package pl.genschu.bloomooemulator.tests;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import pl.genschu.bloomooemulator.TestEnvironment;
import pl.genschu.bloomooemulator.interpreter.values.DoubleValue;
import pl.genschu.bloomooemulator.interpreter.values.IntValue;
import pl.genschu.bloomooemulator.interpreter.variable.DoubleVariable;
import pl.genschu.bloomooemulator.interpreter.variable.IntegerVariable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class DoubleTest {

    private static final double[] TEST_VECTORS = {
            -4.65000, 0.00000, 2.90000, 90.00000, 421.00000, -176.00000, 0.00001
    };

    private static final double[] TEST_STRING_VECTORS = {
            2.5, 0.0, 0.012345, -123.45, -0.12415, 0.00000123, 123456789.987654321, 1.0, 0.09999, 1e-6, -1e-6, 0.999999
    };

    private static final Map<String, String[]> EXPECTED_RESULTS = new HashMap<>();

    @BeforeAll
    static void boot() {
        TestEnvironment.init();
        initializeExpectedResults();
    }

    private static void initializeExpectedResults() {
        EXPECTED_RESULTS.put("ADD", new String[]{"0.35000", "5.00000", "7.90000", "95.00000", "426.00000", "-171.00000", "5.00001"});
        EXPECTED_RESULTS.put("ARCTAN", new String[]{"-77.86318", "0", "70.97439", "89.36341", "89.86391", "-89.67446", "0.00057"});
        EXPECTED_RESULTS.put("ARCTANEX", new String[]{"38.65981", "38.65981", "38.65981", "38.65981", "38.65981", "38.65981", "38.65981"});
        EXPECTED_RESULTS.put("CLAMP", new String[]{"0", "0", "2.90000", "20.00000", "20.00000", "0", "0.00001"});
        EXPECTED_RESULTS.put("COSINUS", new String[]{"0.99671", "1.00000", "0.99872", "0", "0.48481", "-0.99756", "1.00000"});
        EXPECTED_RESULTS.put("DEC", new String[]{"-5.65000", "-1.00000", "1.90000", "89.00000", "420.00000", "-177.00000", "-0.99999"});
        EXPECTED_RESULTS.put("DIV", new String[]{"-0.93000", "0", "0.58000", "18.00000", "84.20000", "-35.20000", "0"});
        EXPECTED_RESULTS.put("INC", new String[]{"-3.65000", "1.00000", "3.90000", "91.00000", "422.00000", "-175.00000", "1.00001"});
        EXPECTED_RESULTS.put("LENGTH", new String[]{"6.40312", "6.40312", "6.40312", "6.40312", "6.40312", "6.40312", "6.40312"});
        EXPECTED_RESULTS.put("LOG", new String[]{"1.#IND0", "-1.#INF0", "1.06471", "4.49981", "6.04263", "1.#IND0", "-11.51293"});
        EXPECTED_RESULTS.put("MAXA", new String[]{"421.00000", "421.00000", "421.00000", "421.00000", "421.00000", "421.00000", "421.00000"});
        EXPECTED_RESULTS.put("MINA", new String[]{"-176.00000", "-176.00000", "-176.00000", "-176.00000", "-176.00000", "-176.00000", "-176.00000"});
        EXPECTED_RESULTS.put("MOD", new String[]{"-4.00000", "0", "2.00000", "0", "1.00000", "-1.00000", "0"});
        EXPECTED_RESULTS.put("MUL", new String[]{"-23.25000", "0", "14.50000", "450.00000", "2105.00000", "-880.00000", "0.00005"});
        EXPECTED_RESULTS.put("POWER", new String[]{"-2174.02637", "0", "205.11153", "5904900096.00000", "13225451061248.00000", "-168874213376.00000", "0"});
        EXPECTED_RESULTS.put("RANDOM", new String[]{"-4.65000", "0", "2.90000", "90.00000", "421.00000", "-176.00000", "0.00001"});
        EXPECTED_RESULTS.put("ROUND", new String[]{"-4.65000", "0", "2.90000", "90.00000", "421.00000", "-176.00000", "0.00001"});
        EXPECTED_RESULTS.put("SGN", new String[]{"-1", "0", "1", "1", "1", "-1", "1"});
        EXPECTED_RESULTS.put("SINUS", new String[]{"-0.08107", "0", "0.05059", "1.00000", "0.87462", "-0.06976", "0"});
        EXPECTED_RESULTS.put("SQRT", new String[]{"1.#IND0", "0", "1.70294", "9.48683", "20.51828", "1.#IND0", "0.00316"});
        EXPECTED_RESULTS.put("SUB", new String[]{"-9.65000", "-5.00000", "-2.10000", "85.00000", "416.00000", "-181.00000", "-4.99999"});
        EXPECTED_RESULTS.put("<TO_STRING>", new String[]{"2.50000", "00000", "0.01235", "-123.45000", "0.-12415", "0.000000", "123456789.98765", "1.00000", "0.09999", "0.000000", "0.-0", "1.00000"});
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 3, 4, 5, 6})
    void testAdd(int vectorIndex) {
        double testValue = TEST_VECTORS[vectorIndex];

        DoubleVariable testVar;
        double result;

        testVar = new DoubleVariable("TEST", testValue);
        testVar = (DoubleVariable) testVar.callMethod("ADD", List.of(new DoubleValue(5.0))).newSelf();
        result = testVar.get();
        assertEquals(parseDoubleOrSpecial(EXPECTED_RESULTS.get("ADD")[vectorIndex]), result, 0.00001,
                "ADD failed for vector " + testValue);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 3, 4, 5, 6})
    void testAtan(int vectorIndex) {
        double testValue = TEST_VECTORS[vectorIndex];

        DoubleVariable testVar;
        double result;

        testVar = new DoubleVariable("TEST", testValue);
        testVar = (DoubleVariable) testVar.callMethod("ARCTAN", List.of(new DoubleValue(testValue))).newSelf();
        result = testVar.get();
        assertEquals(parseDoubleOrSpecial(EXPECTED_RESULTS.get("ARCTAN")[vectorIndex]), result, 0.00001,
                "ARCTAN failed for vector " + testValue);

    }

    @Test
    void testAtan2() {
        DoubleVariable testVar;
        double result;

        testVar = new DoubleVariable("TEST", 0);
        testVar = (DoubleVariable) testVar.callMethod("ARCTANEX", List.of(new DoubleValue(4.0), new DoubleValue(5.0))).newSelf();
        result = testVar.get();
        assertEquals(parseDoubleOrSpecial(EXPECTED_RESULTS.get("ARCTANEX")[0]), result, 0.00001,
                "ARCTANEX failed for vector (4,5)");
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 3, 4, 5, 6})
    void testClamp(int vectorIndex) {
        double testValue = TEST_VECTORS[vectorIndex];

        DoubleVariable testVar;
        double result;

        testVar = new DoubleVariable("TEST", testValue);
        testVar = (DoubleVariable) testVar.callMethod("CLAMP", List.of(new DoubleValue(0.0), new DoubleValue(20.0))).newSelf();
        result = testVar.get();
        assertEquals(parseDoubleOrSpecial(EXPECTED_RESULTS.get("CLAMP")[vectorIndex]), result, 0.00001,
                "CLAMP failed for vector " + testValue);
    }

    @Test
    void testClear() {
        double testValue = TEST_VECTORS[0];

        DoubleVariable testVar;
        double result;

        testVar = new DoubleVariable("TEST", testValue);
        testVar = (DoubleVariable) testVar.callMethod("CLEAR", List.of()).newSelf();
        result = testVar.get();
        assertEquals(0, result, 0.00001,
                "CLEAR failed for vector " + testValue);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 3, 4, 5, 6})
    void testCos(int vectorIndex) {
        double testValue = TEST_VECTORS[vectorIndex];

        DoubleVariable testVar;
        double result;

        testVar = new DoubleVariable("TEST", testValue);
        testVar = (DoubleVariable) testVar.callMethod("COSINUS", List.of(new DoubleValue(testValue))).newSelf();
        result = testVar.get();
        assertEquals(parseDoubleOrSpecial(EXPECTED_RESULTS.get("COSINUS")[vectorIndex]), result, 0.00001,
                "COSINUS failed for vector " + testValue);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 3, 4, 5, 6})
    void testDec(int vectorIndex) {
        double testValue = TEST_VECTORS[vectorIndex];

        DoubleVariable testVar;
        double result;

        testVar = new DoubleVariable("TEST", testValue);
        testVar = (DoubleVariable) testVar.callMethod("DEC", List.of()).newSelf();
        result = testVar.get();
        assertEquals(parseDoubleOrSpecial(EXPECTED_RESULTS.get("DEC")[vectorIndex]), result, 0.00001,
                "DEC failed for vector " + testValue);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 3, 4, 5, 6})
    void testDiv(int vectorIndex) {
        double testValue = TEST_VECTORS[vectorIndex];

        DoubleVariable testVar;
        double result;

        testVar = new DoubleVariable("TEST", testValue);
        testVar = (DoubleVariable) testVar.callMethod("DIV", List.of(new DoubleValue(5.0))).newSelf();
        result = testVar.get();
        assertEquals(parseDoubleOrSpecial(EXPECTED_RESULTS.get("DIV")[vectorIndex]), result, 0.00001,
                "DIV failed for vector " + testValue);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 3, 4, 5, 6})
    void testInc(int vectorIndex) {
        double testValue = TEST_VECTORS[vectorIndex];

        DoubleVariable testVar;
        double result;

        testVar = new DoubleVariable("TEST", testValue);
        testVar = (DoubleVariable) testVar.callMethod("INC", List.of()).newSelf();
        result = testVar.get();
        assertEquals(parseDoubleOrSpecial(EXPECTED_RESULTS.get("INC")[vectorIndex]), result, 0.00001,
                "INC failed for vector " + testValue);
    }

    @Test
    void testLength() {
        DoubleVariable testVar;
        double result;

        testVar = new DoubleVariable("TEST", 0);
        testVar = (DoubleVariable) testVar.callMethod("LENGTH", List.of(new DoubleValue(4.0), new DoubleValue(5.0))).newSelf();
        result = testVar.get();
        assertEquals(parseDoubleOrSpecial(EXPECTED_RESULTS.get("LENGTH")[0]), result, 0.00001,
                "LENGTH failed for vector (4,5)");
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 3, 4, 5, 6})
    void testLog(int vectorIndex) {
        double testValue = TEST_VECTORS[vectorIndex];

        DoubleVariable testVar;
        double result;

        testVar = new DoubleVariable("TEST", testValue);
        testVar = (DoubleVariable) testVar.callMethod("LOG", List.of(new DoubleValue(testValue))).newSelf();
        result = testVar.get();
        if (EXPECTED_RESULTS.get("LOG")[vectorIndex].contains("IND")) {
            assertTrue(Double.isNaN(result), "LOG should return NaN for vector " + testValue);
        } else if (EXPECTED_RESULTS.get("LOG")[vectorIndex].contains("INF")) {
            assertTrue(Double.isInfinite(result), "LOG should return Infinity for vector " + testValue);
        } else {
            assertEquals(parseDoubleOrSpecial(EXPECTED_RESULTS.get("LOG")[vectorIndex]), result, 0.00001,
                    "LOG failed for vector " + testValue);
        }
    }

    @Test
    void testMaxA() {
        DoubleVariable testVar;
        double result;

        testVar = new DoubleVariable("TEST", 0);
        testVar = (DoubleVariable) testVar.callMethod("MAXA", List.of(
                new DoubleValue(-4.65),
                new DoubleValue(0.0),
                new DoubleValue(2.9),
                new DoubleValue(90.0),
                new DoubleValue(421.0),
                new DoubleValue(-176.0),
                new DoubleValue(0.00001))).newSelf();
        result = testVar.get();
        assertEquals(parseDoubleOrSpecial(EXPECTED_RESULTS.get("MAXA")[0]), result, 0.00001,
                "MAXA failed for vector " + Arrays.toString(TEST_VECTORS));
    }

    @Test
    void testMinA() {
        DoubleVariable testVar;
        double result;

        testVar = new DoubleVariable("TEST", 0);
        testVar = (DoubleVariable) testVar.callMethod("MINA", List.of(
                new DoubleValue(-4.65),
                new DoubleValue(0.0),
                new DoubleValue(2.9),
                new DoubleValue(90.0),
                new DoubleValue(421.0),
                new DoubleValue(-176.0),
                new DoubleValue(0.00001))).newSelf();
        result = testVar.get();
        assertEquals(parseDoubleOrSpecial(EXPECTED_RESULTS.get("MINA")[0]), result, 0.00001,
                "MINA failed for vector " + Arrays.toString(TEST_VECTORS));
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 3, 4, 5, 6})
    void testMod(int vectorIndex) {
        double testValue = TEST_VECTORS[vectorIndex];

        DoubleVariable testVar;
        double result;

        testVar = new DoubleVariable("TEST", testValue);
        testVar = (DoubleVariable) testVar.callMethod("MOD", List.of(new DoubleValue(5.0))).newSelf();
        result = testVar.get();
        assertEquals(parseDoubleOrSpecial(EXPECTED_RESULTS.get("MOD")[vectorIndex]), result, 0.00001,
                "MOD failed for vector " + testValue);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 3, 4, 5, 6})
    void testMul(int vectorIndex) {
        double testValue = TEST_VECTORS[vectorIndex];

        DoubleVariable testVar;
        double result;

        testVar = new DoubleVariable("TEST", testValue);
        testVar = (DoubleVariable) testVar.callMethod("MUL", List.of(new DoubleValue(5.0))).newSelf();
        result = testVar.get();
        assertEquals(parseDoubleOrSpecial(EXPECTED_RESULTS.get("MUL")[vectorIndex]), result, 0.00001,
                "MUL failed for vector " + testValue);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 3, 4, 5, 6})
    void testSgn(int vectorIndex) {
        double testValue = TEST_VECTORS[vectorIndex];

        DoubleVariable testVar;
        double result;

        testVar = new DoubleVariable("TEST", testValue);
        IntValue varResult = (IntValue) testVar.callMethod("SGN", List.of(new DoubleValue(testValue))).getReturnValue();
        result = varResult.value();
        assertEquals(parseDoubleOrSpecial(EXPECTED_RESULTS.get("SGN")[vectorIndex]), result, 0.00001,
                "SGN failed for vector " + testValue);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 3, 4, 5, 6})
    void testSinus(int vectorIndex) {
        double testValue = TEST_VECTORS[vectorIndex];

        DoubleVariable testVar;
        double result;

        testVar = new DoubleVariable("TEST", testValue);
        testVar = (DoubleVariable) testVar.callMethod("SINUS", List.of(new DoubleValue(testValue))).newSelf();
        result = testVar.get();
        assertEquals(parseDoubleOrSpecial(EXPECTED_RESULTS.get("SINUS")[vectorIndex]), result, 0.00001,
                "SINUS failed for vector " + testValue);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 3, 4, 5, 6})
    void testSqrt(int vectorIndex) {
        double testValue = TEST_VECTORS[vectorIndex];

        DoubleVariable testVar;
        double result;

        testVar = new DoubleVariable("TEST", testValue);
        testVar = (DoubleVariable) testVar.callMethod("SQRT", List.of()).newSelf();
        result = testVar.get();
        if (EXPECTED_RESULTS.get("SQRT")[vectorIndex].contains("IND")) {
            assertTrue(Double.isNaN(result), "SQRT should return NaN for vector " + testValue);
        } else {
            assertEquals(parseDoubleOrSpecial(EXPECTED_RESULTS.get("SQRT")[vectorIndex]), result, 0.00001,
                    "SQRT failed for vector " + testValue);
        }
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 3, 4, 5, 6})
    void testSub(int vectorIndex) {
        double testValue = TEST_VECTORS[vectorIndex];

        DoubleVariable testVar;
        double result;

        testVar = new DoubleVariable("TEST", testValue);
        testVar = (DoubleVariable) testVar.callMethod("SUB", List.of(new DoubleValue(5.0))).newSelf();
        result = testVar.get();
        assertEquals(parseDoubleOrSpecial(EXPECTED_RESULTS.get("SUB")[vectorIndex]), result, 0.00001,
                "SUB failed for vector " + testValue);
    }

    private double parseDoubleOrSpecial(String value) {
        if (value.contains("IND")) {
            return Double.NaN;
        } else if (value.contains("INF")) {
            return value.contains("-") ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY;
        } else {
            return Double.parseDouble(value);
        }
    }

    @Test
    void testSwitch() {
        DoubleVariable switchVar1 = new DoubleVariable("TEST", 0);
        switchVar1 = (DoubleVariable) switchVar1.callMethod("SWITCH", List.of(
                new DoubleValue(0),
                new DoubleValue(15))).newSelf();
        assertEquals(15, switchVar1.get(), 0.00001);

        DoubleVariable switchVar2 = new DoubleVariable("TEST", 5);
        switchVar2 = (DoubleVariable) switchVar2.callMethod("SWITCH", List.of(
                new DoubleValue(0),
                new DoubleValue(15))).newSelf();
        assertEquals(0, switchVar2.get(), 0.00001);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11})
    void testToString(int vectorIndex) {
        double testValue = TEST_STRING_VECTORS[vectorIndex];
        DoubleVariable testVar;
        String result;
        testVar = new DoubleVariable("TEST", testValue);
        result = testVar.value().toDisplayString();
        assertEquals(EXPECTED_RESULTS.get("<TO_STRING>")[vectorIndex], result,
                "conversion failed for vector " + testValue);
    }
}