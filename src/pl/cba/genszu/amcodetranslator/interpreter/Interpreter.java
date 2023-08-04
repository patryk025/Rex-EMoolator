package pl.cba.genszu.amcodetranslator.interpreter;

import java.util.*;
import org.antlr.v4.runtime.*;
import pl.cba.genszu.amcodetranslator.antlr.*;
import pl.cba.genszu.amcodetranslator.visitors.*;

public class Interpreter
{
	private List<Variable> variables;
	
	public Interpreter(List<Variable> variables) {
		this.variables = variables;
	}
	
	private String calcArithmetic(Stack<String> operations) {
		InterpreterResult result = new InterpreterResult("INTEGER", "0"); // TODO: i tu mam właśnie problem. Java nie ma typu auto więc trzeba improwizować. Konwersje z inta czy floata na Stringi i z powrotem zjedzą masę czasu.
		while(!operations.isEmpty()) {
			
		}
		return result.getValue();
	}
	
	public InterpreterResult interpret(String code) {
		AidemMediaLexer lexer = new AidemMediaLexer(CharStreams.fromString(code));

		CommonTokenStream tokens = new CommonTokenStream(lexer);
		AidemMediaParser parser = new AidemMediaParser(tokens);
		
		AidemMediaParser.ScriptContext scriptContext = parser.script();
		AidemMediaCodeVisitor visitor = new AidemMediaCodeVisitor();
		visitor.visit(scriptContext);
		
		return null;
	}
}
