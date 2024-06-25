package pl.cba.genszu.amcodetranslator;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import pl.cba.genszu.amcodetranslator.interpreter.*;
import pl.cba.genszu.amcodetranslator.interpreter.antlr.AidemMediaLexer;
import pl.cba.genszu.amcodetranslator.interpreter.antlr.AidemMediaParser;
import pl.cba.genszu.amcodetranslator.interpreter.ast.ASTBuilderVisitor;
import pl.cba.genszu.amcodetranslator.interpreter.ast.Node;
import pl.cba.genszu.amcodetranslator.interpreter.variable.types.*;

import java.util.ArrayList;
import java.util.List;

public class Main
{
    public static void main(String[] args)
	{
		//String testExpression = "{[2+2*2]}";
		//String testExpression = "{[TEST+ITERATOR*2]}";
		//String testExpression = "{@IF(\"ITERATOR'3\",{[TEST+ITERATOR*2]}, {[2+3]});}";
		//String testExpression = "{@IF(\"ITERATOR>'1&&ITERATOR<'10\",{[TEST+ITERATOR*2+\"_\"+ITERATOR@5]}, {[2+3]});}";
		//String testExpression = "{@LOOP({[_I_+1]}, 0, 5, 1);}";
		//String testExpression = "{[15-1*(12@4+1)]}";
		//String testExpression = "{[6@2*(1+2)]}";
		//String testExpression = "{[*[\"TE\"+\"ST\"]+ITERATOR*2]}";
		//String testExpression = "{@INT(TEST,1);@DOUBLE(TEST2,2.1);@STRING(TEST3,\"TEST\");@BOOL(TEST4,TRUE);@IF(\"TEST4'TRUE\", {[TEST+TEST2+\"2\"+TEST3]}, {});}";
		//String testExpression = "{@CONV(ITERATOR, \"BOOL\");}";
		//String testExpression = "{ITERATOR^SET(5);@STRING(STRING_TEST,[TEST+ITERATOR]);@RETURN(STRING_TEST);}";
		String testExpression = "{TMPHOLDER^SET([TEST_DODAWANIA+\"|\"+BOOL_Z_BOOLEM__+[TEST2+TEST2_COMPARE]+\"|\"+BOOL_ZE_STRINGIEM__+[TEST2+TEST_COMPARE]+\"|\"+BOOL_Z_INTEGEREM__+[TEST2+TEST4_COMPARE]+\"|\"+BOOL_Z_DOUBLEM__+[TEST2+TEST3_COMPARE]+\"|\"+STRING_ZE_STRINGIEM__+[TEST+TEST_COMPARE]+\"|\"+STRING_Z_INTEGEREM__+[TEST+TEST4_COMPARE]+\"|\"+STRING_Z_DOUBLEM__+[TEST+TEST3_COMPARE]+\"|\"+STRING_Z_BOOLEM__+[TEST+TEST2_COMPARE]+\"|\"+INTEGER_ZE_STRINGIEM__+[TEST4+TEST_COMPARE]+\"|\"+INTEGER_Z_INTEGEREM__+[TEST4+TEST4_COMPARE]+\"|\"+INTEGER_Z_DOUBLEM__+[TEST4+TEST3_COMPARE]+\"|\"+INTEGER_Z_BOOLEM__+[TEST4+TEST2_COMPARE]+\"|\"+DOUBLE_ZE_STRINGIEM__+[TEST3+TEST_COMPARE]+\"|\"+DOUBLE_Z_INTEGEREM__+[TEST3+TEST4_COMPARE]+\"|\"+DOUBLE_Z_DOUBLEM__+[TEST3+TEST3_COMPARE]+\"|\"+DOUBLE_Z_BOOLEM__+[TEST3+TEST2_COMPARE]]);}";

		Context context = new Context();
		context.setVariable("TEST", new StringVariable("TEST", "ANIMO_", context));
		context.setVariable("ITERATOR", new IntegerVariable("ITERATOR", 3, context));
		
		context.setVariable("TMPHOLDER", new StringVariable("TMPHOLDER", "", context));

		AidemMediaLexer lexer = new AidemMediaLexer(CharStreams.fromString(testExpression));
		AidemMediaParser parser = new AidemMediaParser(new CommonTokenStream(lexer));
		ParseTree tree = parser.script();

		ASTBuilderVisitor astBuilder = new ASTBuilderVisitor(context);
		Node astRoot = astBuilder.visit(tree);
		System.out.println(astRoot);

		Interpreter interpreter2 = new Interpreter(astRoot, context);
		interpreter2.interpret();
		//System.out.println("Returned value: " + interpreter2.getReturnValue());
		System.out.println(((String) context.getVariable("TMPHOLDER").getValue()).replace("|", "\n"));
	}
}
