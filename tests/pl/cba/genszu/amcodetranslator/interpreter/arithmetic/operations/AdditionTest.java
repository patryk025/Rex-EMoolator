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
public class AdditionTest {

    private Context context;
    private String testSetName;
    private Variable[] expectedBoolAddition;
    private Variable[] expectedStringAddition;
    private Variable[] expectedIntegerAddition;
    private Variable[] expectedDoubleAddition;

    public AdditionTest(String testSetName, Context context, Variable[] expectedBoolAddition, Variable[] expectedStringAddition, Variable[] expectedIntegerAddition, Variable[] expectedDoubleAddition) {
        this.testSetName = testSetName;
        this.context = context;
        this.expectedBoolAddition = expectedBoolAddition;
        this.expectedStringAddition = expectedStringAddition;
        this.expectedIntegerAddition = expectedIntegerAddition;
        this.expectedDoubleAddition = expectedDoubleAddition;
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
                contexts[i].setVariable("TEST2_COMPARE", new IntegerVariable("TEST2_COMPARE", 2, contexts[i]));
                contexts[i].setVariable("TEST3_COMPARE", new DoubleVariable("TEST3_COMPARE", -2.5, contexts[i]));
                contexts[i].setVariable("TEST4_COMPARE", new IntegerVariable("TEST4_COMPARE", -3, contexts[i]));
            } else if(i == 3) {
                contexts[i].setVariable("TEST_COMPARE", new StringVariable("TEST_COMPARE", "TEST3", contexts[i]));
                contexts[i].setVariable("TEST2_COMPARE", new IntegerVariable("TEST2_COMPARE", 0, contexts[i]));
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
                        new BoolVariable("", false, contexts[0]),
                        new BoolVariable("", true, contexts[0]),
                        new BoolVariable("", true, contexts[0])
                    },
                    new Variable[] {
                        new StringVariable("", "TestTest", contexts[0]),
                        new StringVariable("", "Test1", contexts[0]),
                        new StringVariable("", "Test1.50000", contexts[0]),
                        new StringVariable("", "TestTRUE", contexts[0])
                    },
                    new Variable[] {
                        new IntegerVariable("", 1, contexts[0]),
                        new IntegerVariable("", 2, contexts[0]),
                        new IntegerVariable("", 3, contexts[0]),
                        new IntegerVariable("", 2, contexts[0])
                    },
                    new Variable[] {
                        new DoubleVariable("", 1.5, contexts[0]),
                        new DoubleVariable("", 2.5, contexts[0]),
                        new DoubleVariable("", 3.0, contexts[0]),
                        new DoubleVariable("", 2.5, contexts[0])
                    }
                },
                {
                    "TestSet 1",
                    contexts[1],
                    new Variable[] {
                            new BoolVariable("", false, contexts[1]),
                            new BoolVariable("", false, contexts[1]),
                            new BoolVariable("", true, contexts[1]),
                            new BoolVariable("", true, contexts[1])
                    },
                    new Variable[] {
                            new StringVariable("", "TestTest2", contexts[1]),
                            new StringVariable("", "Test3", contexts[1]),
                            new StringVariable("", "Test2.50000", contexts[1]),
                            new StringVariable("", "TestFALSE", contexts[1])
                    },
                    new Variable[] {
                            new IntegerVariable("", 1, contexts[1]),
                            new IntegerVariable("", 4, contexts[1]),
                            new IntegerVariable("", 4, contexts[1]),
                            new IntegerVariable("", 1, contexts[1])
                    },
                    new Variable[] {
                            new DoubleVariable("", 1.5, contexts[1]),
                            new DoubleVariable("", 4.5, contexts[1]),
                            new DoubleVariable("", 4.0, contexts[1]),
                            new DoubleVariable("", 1.5, contexts[1])
                    }
                },
                {
                    "TestSet 2",
                    contexts[2],
                    new Variable[] {
                            new BoolVariable("", true, contexts[2]),
                            new BoolVariable("", false, contexts[2]),
                            new BoolVariable("", true, contexts[2]),
                            new BoolVariable("", true, contexts[2])
                    },
                    new Variable[] {
                            new StringVariable("", "Test3", contexts[2]),
                            new StringVariable("", "Test-3", contexts[2]),
                            new StringVariable("", "Test-2.50000", contexts[2]),
                            new StringVariable("", "TestTRUE", contexts[2])
                    },
                    new Variable[] {
                            new IntegerVariable("", 4, contexts[2]),
                            new IntegerVariable("", -2, contexts[2]),
                            new IntegerVariable("", -2, contexts[2]),
                            new IntegerVariable("", 2, contexts[2])
                    },
                    new Variable[]{
                            new DoubleVariable("", 4.5, contexts[2]),
                            new DoubleVariable("", -1.5, contexts[2]),
                            new DoubleVariable("", -1.0, contexts[2]),
                            new DoubleVariable("", 2.5, contexts[2])
                    }
                },
                {
                    "TestSet 3",
                    contexts[3],
                    new Variable[] {
                            new BoolVariable("", false, contexts[3]),
                            new BoolVariable("", false, contexts[3]),
                            new BoolVariable("", false, contexts[3]),
                            new BoolVariable("", false, contexts[3])
                    },
                    new Variable[] {
                            new StringVariable("", "ZzzTEST3", contexts[3]),
                            new StringVariable("", "Zzz-5", contexts[3]),
                            new StringVariable("", "Zzz1.00001", contexts[3]),
                            new StringVariable("", "ZzzFALSE", contexts[3])
                    },
                    new Variable[] {
                            new IntegerVariable("", 1, contexts[3]),
                            new IntegerVariable("", -4, contexts[3]),
                            new IntegerVariable("", 2, contexts[3]),
                            new IntegerVariable("", 1, contexts[3])
                    },
                    new Variable[] {
                            new DoubleVariable("", 6.5, contexts[3]),
                            new DoubleVariable("", 1.5, contexts[3]),
                            new DoubleVariable("", 7.50001, contexts[3]),
                            new DoubleVariable("", 6.5, contexts[3])
                    }
                }
        });
    }

    @Test
    public void testBoolAddition() {
        // Test BOOL + BOOL
        Variable result = executeExpression("TEST2 + TEST2_COMPARE", "BOOL");
        assertEquals(expectedBoolAddition[0].toString(), result.toString());

        // Test BOOL + STRING
        result = executeExpression("TEST2 + TEST_COMPARE", "BOOL");
        assertEquals(expectedBoolAddition[1].toString(), result.toString());

        // Test BOOL + INTEGER
        result = executeExpression("TEST2 + TEST4_COMPARE", "BOOL");
        assertEquals(expectedBoolAddition[2].toString(), result.toString());

        // Test BOOL + DOUBLE
        result = executeExpression("TEST2 + TEST3_COMPARE", "BOOL");
        assertEquals(expectedBoolAddition[3].toString(), result.toString());
    }

    @Test
    public void testStringAddition() {
        // Test STRING + STRING
        Variable result = executeExpression("TEST + TEST_COMPARE", "STRING");
        assertEquals(expectedStringAddition[0].toString(), result.toString());

        // Test STRING + INTEGER
        result = executeExpression("TEST + TEST4_COMPARE", "STRING");
        assertEquals(expectedStringAddition[1].toString(), result.toString());

        // Test STRING + DOUBLE
        result = executeExpression("TEST + TEST3_COMPARE", "STRING");
        assertEquals(expectedStringAddition[2].toString(), result.toString());

        // Test STRING + BOOL
        result = executeExpression("TEST + TEST2_COMPARE", "STRING");
        assertEquals(expectedStringAddition[3].toString(), result.toString());
    }

    @Test
    public void testIntegerAddition() {
        // Test INTEGER + STRING
        Variable result = executeExpression("TEST4 + TEST_COMPARE", "INT");
        assertEquals(expectedIntegerAddition[0].toString(), result.toString());

        // Test INTEGER + INTEGER
        result = executeExpression("TEST4 + TEST4_COMPARE", "INT");
        assertEquals(expectedIntegerAddition[1].toString(), result.toString());

        // Test INTEGER + DOUBLE
        result = executeExpression("TEST4 + TEST3_COMPARE", "INT");
        assertEquals(expectedIntegerAddition[2].toString(), result.toString());

        // Test INTEGER + BOOL
        result = executeExpression("TEST4 + TEST2_COMPARE", "INT");
        assertEquals(expectedIntegerAddition[3].toString(), result.toString());
    }

    @Test
    public void testDoubleAddition() {
        // Test DOUBLE + STRING
        Variable result = executeExpression("TEST3 + TEST_COMPARE", "DOUBLE");
        assertEquals(expectedDoubleAddition[0].toString(), result.toString());

        // Test DOUBLE + INTEGER
        result = executeExpression("TEST3 + TEST4_COMPARE", "DOUBLE");
        assertEquals(expectedDoubleAddition[1].toString(), result.toString());

        // Test DOUBLE + DOUBLE
        result = executeExpression("TEST3 + TEST3_COMPARE", "DOUBLE");
        assertEquals(expectedDoubleAddition[2].toString(), result.toString());

        // Test DOUBLE + BOOL
        result = executeExpression("TEST3 + TEST2_COMPARE", "DOUBLE");
        assertEquals(expectedDoubleAddition[3].toString(), result.toString());
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