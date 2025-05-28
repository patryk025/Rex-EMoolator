package pl.genschu.bloomooemulator.tests;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import pl.genschu.bloomooemulator.TestEnvironment;
import pl.genschu.bloomooemulator.builders.ContextBuilder;
import pl.genschu.bloomooemulator.interpreter.Context;
import pl.genschu.bloomooemulator.interpreter.arithmetic.ArithmeticSolver;
import pl.genschu.bloomooemulator.interpreter.variable.Variable;
import pl.genschu.bloomooemulator.interpreter.variable.types.*;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class ArithmeticTest {
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
                "ADDITION", new Object[][][] {
                        // Set 1: STRING, INTEGER, DOUBLE, BOOLEAN + corresponding types
                        {{"S", "TestTest"}, {"S", "Test1"}, {"S", "Test1.50000"}, {"S", "TestTRUE"}},
                        {{"I", 1}, {"I", 2}, {"I", 3}, {"I", 2}},
                        {{"D", 1.50000}, {"D", 2.50000}, {"D", 3.00000}, {"D", 2.50000}},
                        {{"B", false}, {"B", true}, {"B", true}, {"B", true}},

                        // Set 2
                        {{"S", "TestTest2"}, {"S", "Test3"}, {"S", "Test2.50000"}, {"S", "TestFALSE"}},
                        {{"I", 1}, {"I", 4}, {"I", 4}, {"I", 1}},
                        {{"D", 1.50000}, {"D", 4.50000}, {"D", 4.00000}, {"D", 1.50000}},
                        {{"B", false}, {"B", true}, {"B", true}, {"B", false}},

                        // Set 3
                        {{"S", "Test3"}, {"S", "Test-3"}, {"S", "Test-2.50000"}, {"S", "TestTRUE"}},
                        {{"I", 4}, {"I", -2}, {"I", -2}, {"I", 2}},
                        {{"D", 4.50000}, {"D", -1.50000}, {"D", -1.00000}, {"D", 2.50000}},
                        {{"B", false}, {"B", true}, {"B", true}, {"B", true}},

                        // Set 4
                        {{"S", "ZzzTEST3"}, {"S", "Zzz-5"}, {"S", "Zzz1.00001"}, {"S", "ZzzFALSE"}},
                        {{"I", 1}, {"I", -4}, {"I", 2}, {"I", 1}},
                        {{"D", 6.50000}, {"D", 1.50000}, {"D", 7.50001}, {"D", 6.50000}},
                        {{"B", false}, {"B", false}, {"B", false}, {"B", false}}
                },

                "SUBTRACTION", new Object[][][] {
                        // Set 1
                        {{"S", "Test"}, {"S", "Test"}, {"S", "Test"}, {"S", "Test"}},
                        {{"I", 1}, {"I", 0}, {"I", -1}, {"I", 0}},
                        {{"D", 1.50000}, {"D", 0.50000}, {"D", 0}, {"D", 0.50000}},
                        {{"B", true}, {"B", true}, {"B", true}, {"B", true}},

                        // Set 2
                        {{"S", "Test"}, {"S", "Test"}, {"S", "Test"}, {"S", "Test"}},
                        {{"I", 1}, {"I", -2}, {"I", -2}, {"I", 1}},
                        {{"D", 1.50000}, {"D", -1.50000}, {"D", -1.00000}, {"D", 1.50000}},
                        {{"B", true}, {"B", true}, {"B", true}, {"B", true}},

                        // Set 3
                        {{"S", "Test"}, {"S", "Test"}, {"S", "Test"}, {"S", "Test"}},
                        {{"I", -2}, {"I", 4}, {"I", 4}, {"I", 0}},
                        {{"D", -1.50000}, {"D", 4.50000}, {"D", 4.00000}, {"D", 0.50000}},
                        {{"B", true}, {"B", true}, {"B", true}, {"B", true}},

                        // Set 4
                        {{"S", "Zzz"}, {"S", "Zzz"}, {"S", "Zzz"}, {"S", "Zzz"}},
                        {{"I", 1}, {"I", 6}, {"I", 0}, {"I", 1}},
                        {{"D", 6.50000}, {"D", 11.50000}, {"D", 5.49999}, {"D", 6.50000}},
                        {{"B", false}, {"B", false}, {"B", false}, {"B", false}}
                },

                "MULTIPLICATION", new Object[][][] {
                        // Set 1
                        {{"S", "Test"}, {"S", "Test"}, {"S", "Test"}, {"S", "Test"}},
                        {{"I", 0}, {"I", 1}, {"I", 2}, {"I", 1}},
                        {{"D", 0}, {"D", 1.50000}, {"D", 2.25000}, {"D", 1.50000}},
                        {{"B", true}, {"B", true}, {"B", true}, {"B", true}},

                        // Set 2
                        {{"S", "Test"}, {"S", "Test"}, {"S", "Test"}, {"S", "Test"}},
                        {{"I", 0}, {"I", 3}, {"I", 3}, {"I", 0}},
                        {{"D", 0}, {"D", 4.50000}, {"D", 3.75000}, {"D", 0}},
                        {{"B", true}, {"B", true}, {"B", true}, {"B", true}},

                        // Set 3
                        {{"S", "Test"}, {"S", "Test"}, {"S", "Test"}, {"S", "Test"}},
                        {{"I", 3}, {"I", -3}, {"I", -3}, {"I", 1}},
                        {{"D", 4.50000}, {"D", -4.50000}, {"D", -3.75000}, {"D", 1.50000}},
                        {{"B", true}, {"B", true}, {"B", true}, {"B", true}},

                        // Set 4
                        {{"S", "Zzz"}, {"S", "Zzz"}, {"S", "Zzz"}, {"S", "Zzz"}},
                        {{"I", 0}, {"I", -5}, {"I", 1}, {"I", 0}},
                        {{"D", 0}, {"D", -32.50000}, {"D", 6.50007}, {"D", 0}},
                        {{"B", false}, {"B", true}, {"B", true}, {"B", false}}
                },

                "DIVISION", new Object[][][] {
                        // Set 1
                        {{"S", "NULL"}, {"S", "Test"}, {"S", "Test"}, {"S", "Test"}},
                        {{"S", "NULL"}, {"I", 1}, {"I", 0}, {"I", 1}},
                        {{"S", "NULL"}, {"D", 1.50000}, {"D", 1.00000}, {"D", 1.50000}},
                        {{"S", "NULL"}, {"B", true}, {"B", true}, {"B", true}},

                        // Set 2
                        {{"S", "NULL"}, {"S", "Test"}, {"S", "Test"}, {"S", "NULL"}},
                        {{"S", "NULL"}, {"I", 0}, {"I", 0}, {"S", "NULL"}},
                        {{"S", "NULL"}, {"D", 0.50000}, {"D", 0.60000}, {"S", "NULL"}},
                        {{"S", "NULL"}, {"B", true}, {"B", true}, {"S", "NULL"}},

                        // Set 3
                        {{"S", "Test"}, {"S", "Test"}, {"S", "Test"}, {"S", "Test"}},
                        {{"I", 0}, {"I", 0}, {"I", 0}, {"I", 1}},
                        {{"D", 0.50000}, {"D", -0.50000}, {"D", -0.60000}, {"D", 1.50000}},
                        {{"B", true}, {"B", true}, {"B", true}, {"B", true}},

                        // Set 4
                        {{"S", "NULL"}, {"S", "Zzz"}, {"S", "Zzz"}, {"S", "NULL"}},
                        {{"S", "NULL"}, {"I", 0}, {"I", 1}, {"S", "NULL"}},
                        {{"S", "NULL"}, {"D", -1.30000}, {"D", 6.49994}, {"S", "NULL"}},
                        {{"S", "NULL"}, {"B", false}, {"B", false}, {"S", "NULL"}}
                },

                "MODULO", new Object[][][] {
                        // Set 1
                        {{"S", "NULL"}, {"S", "Test"}, {"S", "Test"}, {"S", "Test"}},
                        {{"S", "NULL"}, {"I", 0}, {"I", 1}, {"I", 0}},
                        {{"S", "NULL"}, {"D", 0}, {"D", 0}, {"D", 0}},
                        {{"S", "NULL"}, {"B", true}, {"B", true}, {"B", true}},

                        // Set 2
                        {{"S", "NULL"}, {"S", "Test"}, {"S", "Test"}, {"S", "NULL"}},
                        {{"S", "NULL"}, {"I", 1}, {"I", 1}, {"S", "NULL"}},
                        {{"S", "NULL"}, {"D", 1.00000}, {"D", 1.00000}, {"S", "NULL"}},
                        {{"S", "NULL"}, {"B", true}, {"B", true}, {"S", "NULL"}},

                        // Set 3
                        {{"S", "Test"}, {"S", "Test"}, {"S", "Test"}, {"S", "Test"}},
                        {{"I", 1}, {"I", 1}, {"I", 1}, {"I", 0}},
                        {{"D", 1.00000}, {"D", 1.00000}, {"D", 1.00000}, {"D", 0}},
                        {{"B", true}, {"B", true}, {"B", true}, {"B", true}},

                        // Set 4
                        {{"S", "NULL"}, {"S", "Zzz"}, {"S", "Zzz"}, {"S", "NULL"}},
                        {{"S", "NULL"}, {"I", 1}, {"I", 0}, {"S", "NULL"}},
                        {{"S", "NULL"}, {"D", 1.00000}, {"D", 0}, {"S", "NULL"}},
                        {{"S", "NULL"}, {"B", false}, {"B", false}, {"S", "NULL"}}
                },

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

                "LOWER", new Object[][][] {
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

    // Test data sets (4 zestawy danych z Math_test.cnv)
    private static final Object[][][] TEST_SETS = {
            // Set 1: Test, true, 1.5, 1 vs Test, true, 1.5, 1
            {{"Test", true, 1.5, 1}, {"Test", true, 1.5, 1}},
            // Set 2: Test, true, 1.5, 1 vs Test2, false, 2.5, 3
            {{"Test", true, 1.5, 1}, {"Test2", false, 2.5, 3}},
            // Set 3: Test, true, 1.5, 1 vs 3, 2, -2.5, -3
            {{"Test", true, 1.5, 1}, {"3", 2, -2.5, -3}},
            // Set 4: Zzz, false, 6.5, 1 vs TEST3, 0, 1.00001, -5
            {{"Zzz", false, 6.5, 1}, {"TEST3", 0, 1.00001, -5}}
    };

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 3})
    void testAddition(int setIndex) {
        testArithmeticOperation("ADDITION", setIndex, ArithmeticSolver::add);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 3})
    void testSubtraction(int setIndex) {
        testArithmeticOperation("SUBTRACTION", setIndex, ArithmeticSolver::subtract);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 3})
    void testMultiplication(int setIndex) {
        testArithmeticOperation("MULTIPLICATION", setIndex, ArithmeticSolver::multiply);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 3})
    void testDivision(int setIndex) {
        testArithmeticOperation("DIVISION", setIndex, ArithmeticSolver::divide);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 3})
    void testModulo(int setIndex) {
        testArithmeticOperation("MODULO", setIndex, ArithmeticSolver::modulo);
    }

    private void testArithmeticOperation(String operation, int setIndex,
                                         ArithmeticOperation arithmeticOp) {
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

                Variable result = arithmeticOp.apply(var1, var2);

                // Get expected result
                int expectedIndex = setIndex * 4 + i; // Which type group
                if (expectedIndex < expected.length && j < expected[expectedIndex].length) {
                    Object[] expectedData = (Object[]) expected[expectedIndex][j];
                    String expectedType = (String) expectedData[0];
                    Object expectedValue = expectedData[1];

                    Variable expectedVar = createVariable(expectedType, expectedValue);

                    assertNotNull(result, String.format("%s %s + %s should return result",
                            operation, var1.getType(), var2.getType()));

                    assertEquals(expectedVar.getType(), result.getType(),
                            String.format("%s: %s + %s should return %s",
                                    operation, var1.getType(), var2.getType(), expectedVar.getType()));

                    // Compare values based on type
                    switch (expectedType) {
                        case "S":
                            assertEquals(((StringVariable)expectedVar).GET(), ((StringVariable)result).GET());
                            break;
                        case "I":
                            assertEquals(((IntegerVariable)expectedVar).GET(),
                                    ((IntegerVariable)result).GET());
                            break;
                        case "D":
                            assertEquals(((DoubleVariable)expectedVar).GET(),
                                    ((DoubleVariable)result).GET(), 0.00001);
                            break;
                        case "B":
                            assertEquals(((BoolVariable)expectedVar).GET(),
                                    ((BoolVariable)result).GET());
                            break;
                    }
                }
            }
        }
    }

    @FunctionalInterface
    interface ArithmeticOperation {
        Variable apply(Variable a, Variable b);
    }
}