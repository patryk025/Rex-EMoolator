package pl.cba.genszu.amcodetranslator.interpreter;

import java.util.*;
import org.antlr.v4.runtime.*;
import pl.cba.genszu.amcodetranslator.antlr.*;
import pl.cba.genszu.amcodetranslator.interpreter.exceptions.VariableUnsupportedOperationException;
import pl.cba.genszu.amcodetranslator.interpreter.factories.VariableFactory;
import pl.cba.genszu.amcodetranslator.interpreter.util.ArithmeticSolver;
import pl.cba.genszu.amcodetranslator.utils.InfixToPostfix;
import pl.cba.genszu.amcodetranslator.utils.ReverseListIterator;
import pl.cba.genszu.amcodetranslator.utils.TypeGuesser;
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

	public Variable getVariable(String name) {
		for(Variable var : variables) {
			if(var.getName().equals(name))
				return var;
		}
		return null;
	}

	public Variable createVariable(String name, String type, Object value) {
		Variable tmp = getVariable(name);
		if(tmp != null) {
			variables.remove(tmp); //usuń starą zmienną
		}
		Variable result;
		if(type == null)
			result = VariableFactory.createVariable(name, value);
		else
			result = VariableFactory.createVariable(type, name, value);
		variables.add(result);
		return result;
	}
	
	public Variable calcArithmetic(List<String> operations) {
		Variable result = VariableFactory.createVariable("INTEGER", null, 0);
		List<String> postfix = InfixToPostfix.infixToPostfix(operations);
		Stack<Variable> stack = new Stack<>();
		for(String token : postfix) {
			if (token.matches("[+\\-*@%]")) {
				// Pop the top two operands from the stack.
				Variable operand2 = stack.pop();
				Variable operand1 = stack.pop();

				// Perform the operation.
				Variable tmp_result = performOperation(operand1, operand2, token);

				// Push the result back onto the stack.
				stack.push(tmp_result);
			} else {
				// Push the operand onto the stack.
				Variable tmp = getVariable(token);
				if(tmp != null) {
					stack.push(tmp);
				}
				else {
					stack.push(VariableFactory.createVariable(TypeGuesser.guessNumber(token), null, token));
				}
			}
		}

		return stack.pop();
	}
	
	private Variable performOperation(Variable operand1, Variable operand2, String token) {
		if(token.equals("+")) {
			return ArithmeticSolver.add(operand1, operand2);
		}
		else if(token.equals("-")) {
			return ArithmeticSolver.subtract(operand1, operand2);
		}
		else if(token.equals("*")) {
			return ArithmeticSolver.multiply(operand1, operand2);
		}
		else if(token.equals("@")) {
			return ArithmeticSolver.divide(operand1, operand2);
		}
		else if(token.equals("%")) {
			return ArithmeticSolver.modulo(operand1, operand2);
		}
		return null;
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
