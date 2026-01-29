package pl.genschu.bloomooemulator.tests;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import pl.genschu.bloomooemulator.TestEnvironment;
import pl.genschu.bloomooemulator.interpreter.ast.ComparisonNode;
import pl.genschu.bloomooemulator.interpreter.helpers.ArgumentHelper;
import pl.genschu.bloomooemulator.interpreter.ops.ValueOps;
import pl.genschu.bloomooemulator.interpreter.values.*;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ComparisonTest {
    private Map<String, Object[][]> expectedResults;

    @BeforeAll
    static void boot() {
        TestEnvironment.init();
    }

    @BeforeEach
    void setUp() {
        initExpectedResults();
    }

    private void initExpectedResults() {
        // Format: [type, value] - "S"=String, "I"=Integer, "D"=Double, "B"=Boolean
        expectedResults = Map.of(
                "EQUALS", new Object[][][] {
                        // Set 1
                        {{"B", true}, {"B", false}, {"B", false}, {"B", false}},
                        {{"B", false}, {"B", true}, {"B", false}, {"B", true}},
                        {{"B", false}, {"B", false}, {"B", true}, {"B", false}},
                        {{"B", false}, {"B", true}, {"B", true}, {"B", true}},

                        // Set 2
                        {{"B", false}, {"B", false}, {"B", false}, {"B", false}},
                        {{"B", false}, {"B", false}, {"B", false}, {"B", false}},
                        {{"B", false}, {"B", false}, {"B", false}, {"B", false}},
                        {{"B", false}, {"B", true}, {"B", true}, {"B", false}},

                        // Set 3
                        {{"B", false}, {"B", false}, {"B", false}, {"B", false}},
                        {{"B", false}, {"B", false}, {"B", false}, {"B", true}},
                        {{"B", false}, {"B", false}, {"B", false}, {"B", false}},
                        {{"B", false}, {"B", true}, {"B", true}, {"B", true}},

                        // Set 4
                        {{"B", false}, {"B", false}, {"B", false}, {"B", false}},
                        {{"B", false}, {"B", false}, {"B", true}, {"B", false}},
                        {{"B", false}, {"B", false}, {"B", false}, {"B", false}},
                        {{"B", true}, {"B", false}, {"B", false}, {"B", true}}
                },

                "LESS", new Object[][][] {
                        // Set 1
                        {{"B", false}, {"B", false}, {"B", false}, {"B", false}},
                        {{"B", false}, {"B", false}, {"B", true}, {"B", false}},
                        {{"B", false}, {"B", false}, {"B", false}, {"B", false}},
                        {{"B", false}, {"B", false}, {"B", false}, {"B", false}},

                        // Set 2
                        {{"B", true}, {"B", false}, {"B", false}, {"B", false}},
                        {{"B", false}, {"B", true}, {"B", true}, {"B", false}},
                        {{"B", false}, {"B", true}, {"B", true}, {"B", false}},
                        {{"B", false}, {"B", false}, {"B", false}, {"B", false}},

                        // Set 3
                        {{"B", false}, {"B", false}, {"B", false}, {"B", false}},
                        {{"B", true}, {"B", false}, {"B", false}, {"B", false}},
                        {{"B", true}, {"B", false}, {"B", false}, {"B", false}},
                        {{"B", false}, {"B", false}, {"B", false}, {"B", false}},

                        // Set 4
                        {{"B", false}, {"B", false}, {"B", false}, {"B", false}},
                        {{"B", false}, {"B", false}, {"B", false}, {"B", false}},
                        {{"B", false}, {"B", false}, {"B", false}, {"B", false}},
                        {{"B", false}, {"B", true}, {"B", true}, {"B", false}}
                },

                "GREATER", new Object[][][] {
                        // Set 1
                        {{"B", false}, {"B", true}, {"B", true}, {"B", true}},
                        {{"B", true}, {"B", false}, {"B", false}, {"B", false}},
                        {{"B", true}, {"B", true}, {"B", false}, {"B", true}},
                        {{"B", true}, {"B", false}, {"B", false}, {"B", false}},

                        // Set 2
                        {{"B", false}, {"B", true}, {"B", true}, {"B", true}},
                        {{"B", true}, {"B", false}, {"B", false}, {"B", true}},
                        {{"B", true}, {"B", false}, {"B", false}, {"B", true}},
                        {{"B", true}, {"B", false}, {"B", false}, {"B", true}},

                        // Set 3
                        {{"B", true}, {"B", true}, {"B", true}, {"B", true}},
                        {{"B", false}, {"B", true}, {"B", true}, {"B", false}},
                        {{"B", false}, {"B", true}, {"B", true}, {"B", true}},
                        {{"B", true}, {"B", false}, {"B", false}, {"B", false}},

                        // Set 4
                        {{"B", true}, {"B", true}, {"B", true}, {"B", true}},
                        {{"B", true}, {"B", true}, {"B", false}, {"B", true}},
                        {{"B", true}, {"B", true}, {"B", true}, {"B", true}},
                        {{"B", false}, {"B", false}, {"B", false}, {"B", false}}
                }
        );
    }

    private Value createVariable(String type, Object value) {
        try {
            return switch (type) {
                case "S" -> new StringValue((String) value);
                case "I" -> new IntValue((Integer) value);
                case "D" -> new DoubleValue((Double) value);
                case "B" -> new BoolValue((Boolean) value);
                default -> throw new IllegalArgumentException("Unknown type: " + type);
            };
        } catch (ClassCastException e) {
            String expectedType = switch (type) {
                case "S" -> "String";
                case "I" -> "Integer";
                case "D" -> "Double";
                case "B" -> "Boolean";
                default -> type;
            };
            ;
            throw new IllegalArgumentException("Value " + value + " of type " + value.getClass().getName() + " does not match type " + expectedType, e);
        }
    }

    // Test data sets
    private static final Object[][][] TEST_SETS = {
            // Set 1: Test, true, 1.5, 1 vs Test, true, 1.5, 1
            {{"Test", true, 1.5, 1}, {"Test", true, 1.5, 1}},
            // Set 2: Test, true, 1.5, 1 vs Test2, false, 2.5, 3
            {{"Test", true, 1.5, 1}, {"Test2", false, 2.5, 3}},
            // Set 3: Test, true, 1.5, 1 vs 3, 2, -2.5, -3
            {{"Test", true, 1.5, 1}, {"3", true, -2.5, -3}}, // 2 == TRUE
            // Set 4: Zzz, false, 6.5, 1 vs TEST3, 0, 1.00001, -5
            {{"Zzz", false, 6.5, 1}, {"TEST3", false, 1.00001, -5}} // 0 == FALSE
    };

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 3})
    void testEquals(int setIndex) {
        testLogicOperation("EQUALS", setIndex, ComparisonNode.ComparisonOp.EQUAL);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 3})
    void testLess(int setIndex) {
        testLogicOperation("LESS", setIndex, ComparisonNode.ComparisonOp.LESS);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 3})
    void testGreater(int setIndex) {
        testLogicOperation("GREATER", setIndex, ComparisonNode.ComparisonOp.GREATER);
    }

    private void testLogicOperation(String operation, int setIndex,
                                    ComparisonNode.ComparisonOp op) {
        Object[][] expected = expectedResults.get(operation);
        if (expected == null || expected.length <= setIndex * 4) {
            return; // Skip if no expected results defined
        }

        Object[] set1 = TEST_SETS[setIndex][0];
        Object[] set2 = TEST_SETS[setIndex][1];

        // Create test variables
        Value[] vars1 = {
                new StringValue((String) set1[0]),
                new BoolValue((Boolean) set1[1]),
                new DoubleValue((Double) set1[2]),
                new IntValue((Integer) set1[3])
        };

        Value[] vars2 = {
                new StringValue((String) set2[0]),
                new BoolValue((Boolean) set2[1]),
                new DoubleValue((Double) set2[2]),
                new IntValue((Integer) set2[3])
        };

        // Test all combinations: STRING+all, INTEGER+all, DOUBLE+all, BOOLEAN+all
        String[] types = {"STRING", "INTEGER", "DOUBLE", "BOOLEAN"};
        int[] varIndexes = {0, 3, 2, 1}; // Map to correct variable positions

        for (int i = 0; i < 4; i++) { // For each type (STRING, INTEGER, DOUBLE, BOOLEAN)
            for (int j = 0; j < 4; j++) { // Against each type
                Value var1 = vars1[varIndexes[i]];
                Value var2 = vars2[varIndexes[j]];

                Value result = ValueOps.compare(var1, var2, op);

                // Get expected result
                int expectedIndex = setIndex * 4 + i; // Which type group
                if (expectedIndex < expected.length && j < expected[expectedIndex].length) {
                    Object[] expectedData = (Object[]) expected[expectedIndex][j];
                    String expectedType = (String) expectedData[0];
                    Object expectedValue = expectedData[1];

                    Value expectedVar = createVariable(expectedType, expectedValue);

                    assertNotNull(result, String.format("%s %s + %s should return result",
                            operation, var1.getType(), var2.getType()));

                    assertEquals(ArgumentHelper.getBoolean(expectedVar),
                            ArgumentHelper.getBoolean(result));
                }
            }
        }
    }
}
