package pl.genschu.bloomooemulator.tests;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import pl.genschu.bloomooemulator.TestEnvironment;
import pl.genschu.bloomooemulator.interpreter.ast.ArithmeticNode;
import pl.genschu.bloomooemulator.interpreter.helpers.ArgumentHelper;
import pl.genschu.bloomooemulator.interpreter.ops.ValueOps;
import pl.genschu.bloomooemulator.interpreter.values.*;
import pl.genschu.bloomooemulator.interpreter.variable.Variable;
import pl.genschu.bloomooemulator.interpreter.v1.variable.types.*;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class ArithmeticTest {
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
                        {{"D", 1.50000}, {"D", 0.50000}, {"D", 0.00000}, {"D", 0.50000}},
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
                        {{"D", 0.00000}, {"D", 1.50000}, {"D", 2.25000}, {"D", 1.50000}},
                        {{"B", true}, {"B", true}, {"B", true}, {"B", true}},

                        // Set 2
                        {{"S", "Test"}, {"S", "Test"}, {"S", "Test"}, {"S", "Test"}},
                        {{"I", 0}, {"I", 3}, {"I", 3}, {"I", 0}},
                        {{"D", 0.00000}, {"D", 4.50000}, {"D", 3.75000}, {"D", 0.00000}},
                        {{"B", true}, {"B", true}, {"B", true}, {"B", true}},

                        // Set 3
                        {{"S", "Test"}, {"S", "Test"}, {"S", "Test"}, {"S", "Test"}},
                        {{"I", 3}, {"I", -3}, {"I", -3}, {"I", 1}},
                        {{"D", 4.50000}, {"D", -4.50000}, {"D", -3.75000}, {"D", 1.50000}},
                        {{"B", true}, {"B", true}, {"B", true}, {"B", true}},

                        // Set 4
                        {{"S", "Zzz"}, {"S", "Zzz"}, {"S", "Zzz"}, {"S", "Zzz"}},
                        {{"I", 0}, {"I", -5}, {"I", 1}, {"I", 0}},
                        {{"D", 0.00000}, {"D", -32.50000}, {"D", 6.50007}, {"D", 0.00000}},
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
                        {{"S", "NULL"}, {"D", 0.00000}, {"D", 0.00000}, {"D", 0.00000}},
                        {{"S", "NULL"}, {"B", true}, {"B", true}, {"B", true}},

                        // Set 2
                        {{"S", "NULL"}, {"S", "Test"}, {"S", "Test"}, {"S", "NULL"}},
                        {{"S", "NULL"}, {"I", 1}, {"I", 1}, {"S", "NULL"}},
                        {{"S", "NULL"}, {"D", 1.00000}, {"D", 1.00000}, {"S", "NULL"}},
                        {{"S", "NULL"}, {"B", true}, {"B", true}, {"S", "NULL"}},

                        // Set 3
                        {{"S", "Test"}, {"S", "Test"}, {"S", "Test"}, {"S", "Test"}},
                        {{"I", 1}, {"I", 1}, {"I", 1}, {"I", 0}},
                        {{"D", 1.00000}, {"D", 1.00000}, {"D", 1.00000}, {"D", 0.00000}},
                        {{"B", true}, {"B", true}, {"B", true}, {"B", true}},

                        // Set 4
                        {{"S", "NULL"}, {"S", "Zzz"}, {"S", "Zzz"}, {"S", "NULL"}},
                        {{"S", "NULL"}, {"I", 1}, {"I", 0}, {"S", "NULL"}},
                        {{"S", "NULL"}, {"D", 1.00000}, {"D", 0.00000}, {"S", "NULL"}},
                        {{"S", "NULL"}, {"B", false}, {"B", false}, {"S", "NULL"}}
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
    void testAddition(int setIndex) {
        testArithmeticOperation("ADDITION", setIndex, ArithmeticNode.ArithmeticOp.ADD);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 3})
    void testSubtraction(int setIndex) {
        testArithmeticOperation("SUBTRACTION", setIndex, ArithmeticNode.ArithmeticOp.SUBTRACT);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 3})
    void testMultiplication(int setIndex) {
        testArithmeticOperation("MULTIPLICATION", setIndex, ArithmeticNode.ArithmeticOp.MULTIPLY);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 3})
    void testDivision(int setIndex) {
        testArithmeticOperation("DIVISION", setIndex, ArithmeticNode.ArithmeticOp.DIVIDE);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 3})
    void testModulo(int setIndex) {
        testArithmeticOperation("MODULO", setIndex, ArithmeticNode.ArithmeticOp.MODULO);
    }

    private void testArithmeticOperation(String operation, int setIndex, ArithmeticNode.ArithmeticOp op) {
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

                Value result = ValueOps.arithmetic(var1, var2, op);

                // Get expected result
                int expectedIndex = setIndex * 4 + i; // Which type group
                if (expectedIndex < expected.length && j < expected[expectedIndex].length) {
                    Object[] expectedData = (Object[]) expected[expectedIndex][j];
                    String expectedType = (String) expectedData[0];
                    Object expectedValue = expectedData[1];

                    Value expectedVar = createVariable(expectedType, expectedValue);

                    assertNotNull(result, String.format("%s %s + %s should return result",
                            operation, var1.getType(), var2.getType()));

                    assertEquals(expectedVar.getType(), result.getType(),
                            String.format("%s: %s + %s should return %s",
                                    operation, var1.getType(), var2.getType(), expectedVar.getType()));

                    // Compare values based on type
                    switch (expectedType) {
                        case "S":
                            assertEquals(ArgumentHelper.getString(expectedVar), ArgumentHelper.getString(result));
                            break;
                        case "I":
                            assertEquals(ArgumentHelper.getInt(expectedVar),
                                    ArgumentHelper.getInt(result));
                            break;
                        case "D":
                            assertEquals(ArgumentHelper.getDouble(expectedVar),
                                    ArgumentHelper.getDouble(result), 0.00001);
                            break;
                        case "B":
                            assertEquals(ArgumentHelper.getBoolean(expectedVar),
                                    ArgumentHelper.getBoolean(result));
                            break;
                    }
                }
            }
        }
    }
}