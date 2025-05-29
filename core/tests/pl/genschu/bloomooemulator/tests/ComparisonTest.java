package pl.genschu.bloomooemulator.tests;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import pl.genschu.bloomooemulator.TestEnvironment;
import pl.genschu.bloomooemulator.builders.ContextBuilder;
import pl.genschu.bloomooemulator.interpreter.Context;
import pl.genschu.bloomooemulator.interpreter.arithmetic.ArithmeticSolver;
import pl.genschu.bloomooemulator.interpreter.logic.LogicSolver;
import pl.genschu.bloomooemulator.interpreter.variable.Variable;
import pl.genschu.bloomooemulator.interpreter.variable.types.BoolVariable;
import pl.genschu.bloomooemulator.interpreter.variable.types.DoubleVariable;
import pl.genschu.bloomooemulator.interpreter.variable.types.IntegerVariable;
import pl.genschu.bloomooemulator.interpreter.variable.types.StringVariable;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ComparisonTest {
    private Context ctx;

    private Map<String, Object[][]> expectedResults;

    @BeforeAll
    static void boot() {
        TestEnvironment.init();
    }

    @BeforeEach
    void setUp() {
        ctx = new ContextBuilder().build();
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

    private Variable createVariable(String type, Object value) {
        try {
            switch (type) {
                case "S":
                    return new StringVariable("", (String) value, ctx);
                case "I":
                    return new IntegerVariable("", (Integer) value, ctx);
                case "D":
                    return new DoubleVariable("", (Double) value, ctx);
                case "B":
                    return new BoolVariable("", (Boolean) value, ctx);
                default:
                    throw new IllegalArgumentException("Unknown type: " + type);
            }
        } catch (ClassCastException e) {
            String expectedType;
            switch (type) {
                case "S":
                    expectedType = "String";
                    break;
                case "I":
                    expectedType = "Integer";
                    break;
                case "D":
                    expectedType = "Double";
                    break;
                case "B":
                    expectedType = "Boolean";
                    break;
                default:
                    expectedType = type;
            };
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
        testLogicOperation("EQUALS", setIndex, LogicSolver::equals);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 3})
    void testLess(int setIndex) {
        testLogicOperation("LESS", setIndex, LogicSolver::less);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 3})
    void testGreater(int setIndex) {
        testLogicOperation("GREATER", setIndex, LogicSolver::greater);
    }

    private void testLogicOperation(String operation, int setIndex,
                                         LogicOperation logicOperation) {
        Object[][] expected = expectedResults.get(operation);
        if (expected == null || expected.length <= setIndex * 4) {
            return; // Skip if no expected results defined
        }

        Object[] set1 = TEST_SETS[setIndex][0];
        Object[] set2 = TEST_SETS[setIndex][1];

        // Create test variables
        Variable[] vars1 = {
                new StringVariable("", (String) set1[0], ctx),
                new BoolVariable("", (Boolean) set1[1], ctx),
                new DoubleVariable("", (Double) set1[2], ctx),
                new IntegerVariable("", (Integer) set1[3], ctx)
        };

        Variable[] vars2 = {
                new StringVariable("", (String) set2[0], ctx),
                new BoolVariable("", (Boolean) set2[1], ctx),
                new DoubleVariable("", (Double) set2[2], ctx),
                new IntegerVariable("", (Integer) set2[3], ctx)
        };

        // Test all combinations: STRING+all, INTEGER+all, DOUBLE+all, BOOLEAN+all
        String[] types = {"STRING", "INTEGER", "DOUBLE", "BOOLEAN"};
        int[] varIndexes = {0, 3, 2, 1}; // Map to correct variable positions

        for (int i = 0; i < 4; i++) { // For each type (STRING, INTEGER, DOUBLE, BOOLEAN)
            for (int j = 0; j < 4; j++) { // Against each type
                Variable var1 = vars1[varIndexes[i]];
                Variable var2 = vars2[varIndexes[j]];

                Variable result = logicOperation.apply(var1, var2);

                // Get expected result
                int expectedIndex = setIndex * 4 + i; // Which type group
                if (expectedIndex < expected.length && j < expected[expectedIndex].length) {
                    Object[] expectedData = (Object[]) expected[expectedIndex][j];
                    String expectedType = (String) expectedData[0];
                    Object expectedValue = expectedData[1];

                    Variable expectedVar = createVariable(expectedType, expectedValue);

                    assertNotNull(result, String.format("%s %s + %s should return result",
                            operation, var1.getType(), var2.getType()));

                    assertEquals(((BoolVariable)expectedVar).GET(),
                            ((BoolVariable)result).GET());
                }
            }
        }
    }

    @FunctionalInterface
    interface LogicOperation {
        Variable apply(Variable a, Variable b);
    }
}
