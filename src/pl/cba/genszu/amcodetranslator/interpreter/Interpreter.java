package pl.cba.genszu.amcodetranslator.interpreter;

import java.util.*;
import org.antlr.v4.runtime.*;
import pl.cba.genszu.amcodetranslator.antlr.*;
import pl.cba.genszu.amcodetranslator.interpreter.factories.VariableFactory;
import pl.cba.genszu.amcodetranslator.visitors.*;
import pl.cba.genszu.amcodetranslator.interpreter.variabletypes.*;

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
	
	private Variable performOperation(Variable operand1, Variable operand2, String token) {
		if(token.equals("+")) {
			return add(operand1, operand2);
		}
		return null; //TODO: obsłużyć resztę
	}
	
	private Variable add(Variable var1, Variable var2) {
		if (var1 instanceof StringVariable && var2 instanceof StringVariable) {
			return add((StringVariable) var1, (StringVariable) var2);
		} else if (var1 instanceof IntegerVariable && var2 instanceof StringVariable) {
			return add((IntegerVariable) var1, (StringVariable) var2);
		} else if (var1 instanceof StringVariable && var2 instanceof IntegerVariable) {
			return add((StringVariable) var1, (IntegerVariable) var2);
		} else if (var1 instanceof IntegerVariable && var2 instanceof IntegerVariable) {
			return add((IntegerVariable) var1, (IntegerVariable) var2);
		}
		else {
			throw new IllegalArgumentException("Podane zmienne mają niekompatybilne typy");
		}
	}
	
	private Variable add(StringVariable var1, StringVariable var2) {
		return VariableFactory.createVariable("STRING", null, var1.GET() + var2.GET());
	}
	
	private Variable add(IntegerVariable var1, StringVariable var2) {
		return VariableFactory.createVariable("STRING", null, var1.GET() + var2.GET());
	}
	
	private Variable add(StringVariable var1, IntegerVariable var2) {
		return VariableFactory.createVariable("STRING", null, var1.GET() + var2.GET());
	}
	
	private Variable add(IntegerVariable var1, IntegerVariable var2) {
		return VariableFactory.createVariable("INTEGER", null, var1.GET() + var2.GET());
	}
	
	public Variable interpret(String code) {
		AidemMediaLexer lexer = new AidemMediaLexer(CharStreams.fromString(code));

		CommonTokenStream tokens = new CommonTokenStream(lexer);
		AidemMediaParser parser = new AidemMediaParser(tokens);
		
		AidemMediaParser.ScriptContext scriptContext = parser.script();
		AidemMediaCodeVisitor visitor = new AidemMediaCodeVisitor(this);
		visitor.visit(scriptContext);
		
		return null;
	}
}
