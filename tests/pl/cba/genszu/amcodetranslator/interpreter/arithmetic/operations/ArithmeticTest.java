package pl.cba.genszu.amcodetranslator.interpreter.arithmetic.operations;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.junit.Before;
import org.junit.Test;
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

import static org.junit.Assert.assertEquals;

public class ArithmeticTest {

    private Context context;

    @Before
    public void setUp() {
        context = new Context();
        // Setup initial variables
        context.setVariable("TEST", new StringVariable("TEST", "Test", context));
        context.setVariable("TEST2", new BoolVariable("TEST2", true, context));
        context.setVariable("TEST3", new DoubleVariable("TEST3", 1.5, context));
        context.setVariable("TEST4", new IntegerVariable("TEST4", 1, context));

        context.setVariable("TEST_COMPARE", new StringVariable("TEST_COMPARE", "Test", context));
        context.setVariable("TEST2_COMPARE", new BoolVariable("TEST2_COMPARE", true, context));
        context.setVariable("TEST3_COMPARE", new DoubleVariable("TEST3_COMPARE", 1.5, context));
        context.setVariable("TEST4_COMPARE", new IntegerVariable("TEST4_COMPARE", 1, context));
    }

    @Test
    public void testBoolAddition() {
        // Test BOOL + BOOL
        Variable result = executeExpression("TEST2 + TEST2_COMPARE", "BOOL");
        assertEquals(new BoolVariable("", true, context).toString(), result.toString());

        // Test BOOL + STRING
        result = executeExpression("TEST2 + TEST_COMPARE", "BOOL");
        assertEquals(new BoolVariable("", false, context).toString(), result.toString());

        // Test BOOL + INTEGER
        result = executeExpression("TEST2 + TEST4_COMPARE", "BOOL");
        assertEquals(new BoolVariable("", true, context).toString(), result.toString());

        // Test BOOL + DOUBLE
        result = executeExpression("TEST2 + TEST3_COMPARE", "BOOL");
        assertEquals(new BoolVariable("", true, context).toString(), result.toString());
    }

    @Test
    public void testStringAddition() {
        // Test STRING + STRING
        Variable result = executeExpression("TEST + TEST_COMPARE", "STRING");
        assertEquals(new StringVariable("", "TestTest", context).toString(), result.toString());

        // Test STRING + INTEGER
        result = executeExpression("TEST + TEST4_COMPARE", "STRING");
        assertEquals(new StringVariable("", "Test1", context).toString(), result.toString());

        // Test STRING + DOUBLE
        result = executeExpression("TEST + TEST3_COMPARE", "STRING");
        assertEquals(new StringVariable("", "Test1.50000", context).toString(), result.toString());

        // Test STRING + BOOL
        result = executeExpression("TEST + TEST2_COMPARE", "STRING");
        assertEquals(new StringVariable("", "TestTRUE", context).toString(), result.toString());
    }

    @Test
    public void testIntegerAddition() {
        // Test INTEGER + STRING
        Variable result = executeExpression("TEST4 + TEST_COMPARE", "INT");
        assertEquals(new IntegerVariable("", 1, context).toString(), result.toString());

        // Test INTEGER + INTEGER
        result = executeExpression("TEST4 + TEST4_COMPARE", "INT");
        assertEquals(new IntegerVariable("", 2, context).toString(), result.toString());

        // Test INTEGER + DOUBLE
        result = executeExpression("TEST4 + TEST3_COMPARE", "INT");
        assertEquals(new IntegerVariable("", 3, context).toString(), result.toString());

        // Test INTEGER + BOOL
        result = executeExpression("TEST4 + TEST2_COMPARE", "INT");
        assertEquals(new IntegerVariable("", 2, context).toString(), result.toString());
    }

    @Test
    public void testDoubleAddition() {
        // Test DOUBLE + STRING
        Variable result = executeExpression("TEST3 + TEST_COMPARE", "DOUBLE");
        assertEquals(new DoubleVariable("", 1.5, context).toString(), result.toString());

        // Test DOUBLE + INTEGER
        result = executeExpression("TEST3 + TEST4_COMPARE", "DOUBLE");
        assertEquals(new DoubleVariable("", 2.5, context).toString(), result.toString());

        // Test DOUBLE + DOUBLE
        result = executeExpression("TEST3 + TEST3_COMPARE", "DOUBLE");
        assertEquals(new DoubleVariable("", 3.0, context).toString(), result.toString());

        // Test DOUBLE + BOOL
        result = executeExpression("TEST3 + TEST2_COMPARE", "DOUBLE");
        assertEquals(new DoubleVariable("", 2.5, context).toString(), result.toString());
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