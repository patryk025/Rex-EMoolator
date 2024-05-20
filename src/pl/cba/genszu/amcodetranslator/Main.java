package pl.cba.genszu.amcodetranslator;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import pl.cba.genszu.amcodetranslator.interpreter.*;
import pl.cba.genszu.amcodetranslator.interpreter.antlr.AidemMediaLexer;
import pl.cba.genszu.amcodetranslator.interpreter.antlr.AidemMediaParser;
import pl.cba.genszu.amcodetranslator.interpreter.ast.ASTBuilderVisitor;
import pl.cba.genszu.amcodetranslator.interpreter.ast.Node;
import pl.cba.genszu.amcodetranslator.interpreter.types.*;

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
		String testExpression = "{[15-1*(12@4+1)]}";
		//String testExpression = "{[6@2*(1+2)]}";
		//String testExpression = "{[*[\"TE\"+\"ST\"]+ITERATOR*2]}";
		//String testExpression = "{@INT(TEST,1);@DOUBLE(TEST2,2.1);@STRING(TEST3,\"TEST\");@BOOL(TEST4,TRUE);@IF(\"TEST4'TRUE\", {[TEST+TEST2+TEST3]}, {});}";
		//String testExpression = "{@CONV(ITERATOR, \"BOOL\");}";
		//String testExpression = "{ITERATOR^SET(5);@STRING(STRING_TEST,[TEST+ITERATOR]);@RETURN(STRING_TEST);}";

		// old tests
		System.out.println("Old interpreter: ");
		List<Variable> vars = new ArrayList<>();
		Variable test1 = new StringVariable("TEST", "ANIMO_");
		Variable test2 = new IntegerVariable("ITERATOR", 3);

		vars.add(test1);
		vars.add(test2);

		//System.out.println(test1.getType());
		
		InterpreterOld interpreter = new InterpreterOld(vars);
		interpreter.interpret(testExpression);

		// new tests
		System.out.println("New interpreter: ");
		Context context = new Context();
		context.setVariable("TEST", new StringVariable("TEST", "ANIMO_"));
		context.setVariable("ITERATOR", new IntegerVariable("ITERATOR", 3));

		AidemMediaLexer lexer = new AidemMediaLexer(CharStreams.fromString(testExpression));
		AidemMediaParser parser = new AidemMediaParser(new CommonTokenStream(lexer));
		ParseTree tree = parser.script();

		ASTBuilderVisitor astBuilder = new ASTBuilderVisitor(context);
		Node astRoot = astBuilder.visit(tree);

		Interpreter interpreter2 = new Interpreter(astRoot, context);
		interpreter2.interpret();
	}
}
