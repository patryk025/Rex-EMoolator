package pl.cba.genszu.amcodetranslator.interpreter;

import java.util.*;
import org.antlr.v4.runtime.*;
import pl.cba.genszu.amcodetranslator.antlr.*;
import pl.cba.genszu.amcodetranslator.interpreter.factories.VariableFactory;
import pl.cba.genszu.amcodetranslator.visitors.*;

public class Interpreter
{
	private List<Variable> variables;
	
	public Interpreter() {
		this.variables = new ArrayList<>();
	}
	
	public Interpreter(List<Variable> variables) {
		this.variables = variables;
	}

	private Variable getVariable(String name) {
		for(Variable var : variables) {
			if(var.getName().equals(name))
				return var;
		}
		return null;
	}

	private Variable createVariable(String name, String type, Object value) {
		Variable tmp = getVariable(name);
		if(tmp != null) {
			variables.remove(tmp); //usuń starą zmienną
		}
		Variable result = VariableFactory.createVariable(type, name, value);
		variables.add(result);
		return result;
	}
	
	private Variable calcArithmetic(List<String> operations) {
		Variable result = createVariable("test", "INTEGER", 0);
		Stack<Variable> stack = new Stack<>();
		for(String token : operations) {
			if (token.matches("[+\\-*@%]")) {
				// Pop the top two operands from the stack.
				Variable operand1 = stack.pop();
				Variable operand2 = stack.pop();

				// Perform the operation.
				//String result = performOperation(operand1, operand2, token);

				// Push the result back onto the stack.
				stack.push(result);
			} else {
				// Push the operand onto the stack.
				stack.push(VariableFactory.createVariable("tmp_operand", "STRING", token));
			}
		}
		return result;
	}
	
	public Variable interpret(String code) {
		AidemMediaLexer lexer = new AidemMediaLexer(CharStreams.fromString(code));

		CommonTokenStream tokens = new CommonTokenStream(lexer);
		AidemMediaParser parser = new AidemMediaParser(tokens);
		
		AidemMediaParser.ScriptContext scriptContext = parser.script();
		AidemMediaCodeVisitor visitor = new AidemMediaCodeVisitor();
		visitor.visit(scriptContext);
		
		return null;
	}
}
