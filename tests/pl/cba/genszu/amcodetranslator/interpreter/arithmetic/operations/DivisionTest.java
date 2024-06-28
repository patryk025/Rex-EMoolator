package pl.cba.genszu.amcodetranslator.interpreter.arithmetic.operations;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import pl.cba.genszu.amcodetranslator.interpreter.Context;
import pl.cba.genszu.amcodetranslator.interpreter.Interpreter;
import pl.cba.genszu.amcodetranslator.interpreter.antlr.AidemMediaLexer;
import pl.cba.genszu.amcodetranslator.interpreter.antlr.AidemMediaParser;
import pl.cba.genszu.amcodetranslator.interpreter.ast.ASTBuilderVisitor;
import pl.cba.genszu.amcodetranslator.interpreter.ast.Node;
import pl.cba.genszu.amcodetranslator.interpreter.variable.Variable;
import pl.cba.genszu.amcodetranslator.interpreter.variable.types.BoolVariable;
import pl.cba.genszu.amcodetranslator.interpreter.variable.types.DoubleVariable;
import pl.cba.genszu.amcodetranslator.interpreter.variable.types.IntegerVariable;
import pl.cba.genszu.amcodetranslator.interpreter.variable.types.StringVariable;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class DivisionTest {

    private Context context;
    private String testSetName;
    private Variable[] expectedBoolDivision;
    private Variable[] expectedStringDivision;
    private Variable[] expectedIntegerDivision;
    private Variable[] expectedDoubleDivision;

    public DivisionTest(String testSetName, Context context, Variable[] expectedBoolDivision, Variable[] expectedStringDivision, Variable[] expectedIntegerDivision, Variable[] expectedDoubleDivision) {
        this.testSetName = testSetName;
        this.context = context;
        this.expectedBoolDivision = expectedBoolDivision;
        this.expectedStringDivision = expectedStringDivision;
        this.expectedIntegerDivision = expectedIntegerDivision;
        this.expectedDoubleDivision = expectedDoubleDivision;
    }

    @Parameterized.Parameters(name = "{index}: Test with {0}")
    public static Collection<Object[]> data() {
        int numberOfTestSets = 4;
        Context[] contexts = new Context[numberOfTestSets];

        for(int i = 0; i < numberOfTestSets; i++) {
            contexts[i] = new Context();
            // Setup initial variables
            if(i < 3) {
                contexts[i].setVariable("TEST", new StringVariable("TEST", "Test", contexts[i]));
                contexts[i].setVariable("TEST2", new BoolVariable("TEST2", true, contexts[i]));
                contexts[i].setVariable("TEST3", new DoubleVariable("TEST3", 1.5, contexts[i]));
                contexts[i].setVariable("TEST4", new IntegerVariable("TEST4", 1, contexts[i]));
            } else {
                contexts[i].setVariable("TEST", new StringVariable("TEST", "Zzz", contexts[i]));
                contexts[i].setVariable("TEST2", new BoolVariable("TEST2", false, contexts[i]));
                contexts[i].setVariable("TEST3", new DoubleVariable("TEST3", 6.5, contexts[i]));
                contexts[i].setVariable("TEST4", new IntegerVariable("TEST4", 1, contexts[i]));
            }

            if(i == 0) {
                contexts[i].setVariable("TEST_COMPARE", new StringVariable("TEST_COMPARE", "Test", contexts[i]));
                contexts[i].setVariable("TEST2_COMPARE", new BoolVariable("TEST2_COMPARE", true, contexts[i]));
                contexts[i].setVariable("TEST3_COMPARE", new DoubleVariable("TEST3_COMPARE", 1.5, contexts[i]));
                contexts[i].setVariable("TEST4_COMPARE", new IntegerVariable("TEST4_COMPARE", 1, contexts[i]));
            } else if(i == 1) {
                contexts[i].setVariable("TEST_COMPARE", new StringVariable("TEST_COMPARE", "Test2", contexts[i]));
                contexts[i].setVariable("TEST2_COMPARE", new BoolVariable("TEST2_COMPARE", false, contexts[i]));
                contexts[i].setVariable("TEST3_COMPARE", new DoubleVariable("TEST3_COMPARE", 2.5, contexts[i]));
                contexts[i].setVariable("TEST4_COMPARE", new IntegerVariable("TEST4_COMPARE", 3, contexts[i]));
            } else if(i == 2) {
                contexts[i].setVariable("TEST_COMPARE", new StringVariable("TEST_COMPARE", "3", contexts[i]));
                contexts[i].setVariable("TEST2_COMPARE", new BoolVariable("TEST2_COMPARE", 2, contexts[i]));
                contexts[i].setVariable("TEST3_COMPARE", new DoubleVariable("TEST3_COMPARE", -2.5, contexts[i]));
                contexts[i].setVariable("TEST4_COMPARE", new IntegerVariable("TEST4_COMPARE", -3, contexts[i]));
            } else if(i == 3) {
                contexts[i].setVariable("TEST_COMPARE", new StringVariable("TEST_COMPARE", "TEST3", contexts[i]));
                contexts[i].setVariable("TEST2_COMPARE", new BoolVariable("TEST2_COMPARE", 0, contexts[i]));
                contexts[i].setVariable("TEST3_COMPARE", new DoubleVariable("TEST3_COMPARE", 1.00001, contexts[i]));
                contexts[i].setVariable("TEST4_COMPARE", new IntegerVariable("TEST4_COMPARE", -5, contexts[i]));
            }
        }

        return Arrays.asList(new Object[][] {
                {
                    "TestSet 0",
                    contexts[0],
                    new Variable[] {
                        new BoolVariable("", true, contexts[0]),
                        new StringVariable("", "NULL", contexts[0]),
                        new BoolVariable("", true, contexts[0]),
                        new BoolVariable("", true, contexts[0])
                    },
                    new Variable[] {
                        new StringVariable("", "NULL", contexts[0]),
                        new StringVariable("", "Test", contexts[0]),
                        new StringVariable("", "Test", contexts[0]),
                        new StringVariable("", "Test", contexts[0])
                    },
                    new Variable[] {
                        new StringVariable("", "NULL", contexts[0]),
                        new IntegerVariable("", 1, contexts[0]),
                        new IntegerVariable("", 0, contexts[0]),
                        new IntegerVariable("", 1, contexts[0])
                    },
                    new Variable[] {
                        new StringVariable("", "NULL", contexts[0]),
                        new DoubleVariable("", 1.5, contexts[0]),
                        new DoubleVariable("", 1.0, contexts[0]),
                        new DoubleVariable("", 1.5, contexts[0])
                    }
                },
                {
                    "TestSet 1",
                    contexts[1],
                    new Variable[] {
                            new StringVariable("", "NULL", contexts[1]),
                            new StringVariable("", "NULL", contexts[1]),
                            new BoolVariable("", true, contexts[1]),
                            new BoolVariable("", true, contexts[1])
                    },
                    new Variable[] {
                            new StringVariable("", "NULL", contexts[1]),
                            new StringVariable("", "Test", contexts[1]),
                            new StringVariable("", "Test", contexts[1]),
                            new StringVariable("", "NULL", contexts[1])
                    },
                    new Variable[] {
                            new StringVariable("", "NULL", contexts[1]),
                            new IntegerVariable("", 0, contexts[1]),
                            new IntegerVariable("", 0, contexts[1]),
                            new StringVariable("", "NULL", contexts[1])
                    },
                    new Variable[] {
                            new StringVariable("", "NULL", contexts[1]),
                            new DoubleVariable("", 0.5, contexts[1]),
                            new DoubleVariable("", 0.6, contexts[1]),
                            new StringVariable("", "NULL", contexts[1])
                    }
                },
                {
                    "TestSet 2",
                    contexts[2],
                    new Variable[] {
                            new BoolVariable("", true, contexts[2]),
                            new BoolVariable("", true, contexts[2]),
                            new BoolVariable("", true, contexts[2]),
                            new BoolVariable("", true, contexts[2])
                    },
                    new Variable[] {
                            new StringVariable("", "Test", contexts[2]),
                            new StringVariable("", "Test", contexts[2]),
                            new StringVariable("", "Test", contexts[2]),
                            new StringVariable("", "Test", contexts[2])
                    },
                    new Variable[] {
                            new IntegerVariable("", 0, contexts[2]),
                            new IntegerVariable("", 0, contexts[2]),
                            new IntegerVariable("", 0, contexts[2]),
                            new IntegerVariable("", 1, contexts[2])
                    },
                    new Variable[]{
                            new DoubleVariable("", 0.5, contexts[2]),
                            new DoubleVariable("", -0.5, contexts[2]),
                            new DoubleVariable("", -0.6, contexts[2]),
                            new DoubleVariable("", 1.5, contexts[2])
                    }
                },
                {
                    "TestSet 3",
                    contexts[3],
                    new Variable[] {
                            new StringVariable("", "NULL", contexts[3]),
                            new StringVariable("", "NULL", contexts[3]),
                            new BoolVariable("", false, contexts[3]),
                            new BoolVariable("", false, contexts[3])
                    },
                    new Variable[] {
                            new StringVariable("", "NULL", contexts[3]),
                            new StringVariable("", "Zzz", contexts[3]),
                            new StringVariable("", "Zzz", contexts[3]),
                            new StringVariable("", "NULL", contexts[3])
                    },
                    new Variable[] {
                            new StringVariable("", "NULL", contexts[3]),
                            new IntegerVariable("", 0, contexts[3]),
                            new IntegerVariable("", 1, contexts[3]),
                            new StringVariable("", "NULL", contexts[3])
                    },
                    new Variable[] {
                            new StringVariable("", "NULL", contexts[3]),
                            new DoubleVariable("", -1.3, contexts[3]),
                            new DoubleVariable("", 6.49994, contexts[3]),
                            new StringVariable("", "NULL", contexts[3])
                    }
                }
        });
    }

    @Test
    public void testBoolDivision() {
        // Test BOOL + BOOL
        Variable result = executeExpression("TEST2 @ TEST2_COMPARE", "BOOL");
        assertEquals(expectedBoolDivision[0].toString(), result.toString());

        // Test BOOL + STRING
        result = executeExpression("TEST2 @ TEST_COMPARE", "BOOL");
        assertEquals(expectedBoolDivision[1].toString(), result.toString());

        // Test BOOL + INTEGER
        result = executeExpression("TEST2 @ TEST4_COMPARE", "BOOL");
        assertEquals(expectedBoolDivision[2].toString(), result.toString());

        // Test BOOL + DOUBLE
        result = executeExpression("TEST2 @ TEST3_COMPARE", "BOOL");
        assertEquals(expectedBoolDivision[3].toString(), result.toString());
    }

    @Test
    public void testStringDivision() {
        // Test STRING + STRING
        Variable result = executeExpression("TEST @ TEST_COMPARE", "STRING");
        assertEquals(expectedStringDivision[0].toString(), result.toString());

        // Test STRING + INTEGER
        result = executeExpression("TEST @ TEST4_COMPARE", "STRING");
        assertEquals(expectedStringDivision[1].toString(), result.toString());

        // Test STRING + DOUBLE
        result = executeExpression("TEST @ TEST3_COMPARE", "STRING");
        assertEquals(expectedStringDivision[2].toString(), result.toString());

        // Test STRING + BOOL
        result = executeExpression("TEST @ TEST2_COMPARE", "STRING");
        assertEquals(expectedStringDivision[3].toString(), result.toString());
    }

    @Test
    public void testIntegerDivision() {
        // Test INTEGER + STRING
        Variable result = executeExpression("TEST4 @ TEST_COMPARE", "INT");
        assertEquals(expectedIntegerDivision[0].toString(), result.toString());

        // Test INTEGER + INTEGER
        result = executeExpression("TEST4 @ TEST4_COMPARE", "INT");
        assertEquals(expectedIntegerDivision[1].toString(), result.toString());

        // Test INTEGER + DOUBLE
        result = executeExpression("TEST4 @ TEST3_COMPARE", "INT");
        assertEquals(expectedIntegerDivision[2].toString(), result.toString());

        // Test INTEGER + BOOL
        result = executeExpression("TEST4 @ TEST2_COMPARE", "INT");
        assertEquals(expectedIntegerDivision[3].toString(), result.toString());
    }

    @Test
    public void testDoubleDivision() {
        // Test DOUBLE + STRING
        Variable result = executeExpression("TEST3 @ TEST_COMPARE", "DOUBLE");
        assertEquals(expectedDoubleDivision[0].toString(), result.toString());

        // Test DOUBLE + INTEGER
        result = executeExpression("TEST3 @ TEST4_COMPARE", "DOUBLE");
        assertEquals(expectedDoubleDivision[1].toString(), result.toString());

        // Test DOUBLE + DOUBLE
        result = executeExpression("TEST3 @ TEST3_COMPARE", "DOUBLE");
        assertEquals(expectedDoubleDivision[2].toString(), result.toString());

        // Test DOUBLE + BOOL
        result = executeExpression("TEST3 @ TEST2_COMPARE", "DOUBLE");
        assertEquals(expectedDoubleDivision[3].toString(), result.toString());
    }

    private Variable executeExpression(String expression, String type) {
        // prepare expression for test
        expression = "{@" + type + "(TMP_RESULT, [" + expression + "]);@RETURN(TMP_RESULT);}";

        AidemMediaLexer lexer = new AidemMediaLexer(CharStreams.fromString(expression));
        AidemMediaParser parser = new AidemMediaParser(new CommonTokenStream(lexer));
        ParseTree tree = parser.script();

        ASTBuilderVisitor astBuilder = new ASTBuilderVisitor(context);
        Node astRoot = astBuilder.visit(tree);

        Interpreter interpreter = new Interpreter(astRoot, context);
        interpreter.interpret();

        return (Variable) interpreter.getReturnValue();
    }
}