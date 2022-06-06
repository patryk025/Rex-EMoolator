package pl.cba.genszu.amcodetranslator.interpreter;

import java.util.List;

import pl.cba.genszu.amcodetranslator.interpreter.util.Token;
import pl.cba.genszu.amcodetranslator.lexer.stack.Stack;
import pl.cba.genszu.amcodetranslator.lexer.tree.BinaryTree;
import pl.cba.genszu.amcodetranslator.lexer.tree.Node;
import pl.cba.genszu.amcodetranslator.lexer.Constants;

public class ScriptInterpreter {
	
	

    public ScriptInterpreter() {

    }

	public Token proceedOp(Token op, Token first, Token second) {
		//do this switch checks when all two tokens are numberic or any other cases which is needed
		if (first.type == Constants.NUMBER && second.type == Constants.NUMBER) {
			double x = Double.parseDouble(first.value);
			double y = Double.parseDouble(second.value);
			switch (op.type) {
				case "PLUS":
					return new Token(x + y);
				case "MINUS":
					return new Token(x - y);
				case "MUL":
					return new Token(x * y);
				case "DIV":
					return new Token(x / y);
			}
		}

		return new Token(0);
	} 

    public void evaluateCode(List<Object[]> instrukcje) throws InterpreterException {

    }

	//at now return double
	public double evaluateCode(BinaryTree instructions) {
		if(instructions.root == null) {
			return 0;
		}
		else return evaluateCode(instructions.root);
	}
	
	public double evaluateCode(Node node) {
		if(node == null) { //null pointer protection
			return 0;
		}
		else if(node.isLeaf()) {
			return Double.parseDouble(node.value.value);
		}
		else {
			double x = evaluateCode(node.left);
			double y = evaluateCode(node.right);

			return Double.parseDouble(proceedOp(node.value, new Token(x), new Token(y)).value);
		}
	}

	public void evaluateCode(Stack instrukcje) throws Exception {
        Token first = null;
        Token second = null;

        float firstVal = 0;
        float secondVal = 0;
        Token current;

		while (!instrukcje.isEmpty()) {
		    current = instrukcje.pop();

		    switch (current.type) {
                case "NUMBER":
                    if (first == null) {
                        first = current;
                        firstVal = Float.parseFloat(current.value);
                    }
                    else if (second == null) {
                        second = current;
                        secondVal = Float.parseFloat(current.value);
                    }
                    break;
                case "PLUS":
                    first = new Token(firstVal + secondVal);
                    second = null;
                    break;
                case "MINUS":
                    first = new Token(firstVal - secondVal);
                    second = null;
                    break;
                case "MUL":
                    first = new Token(firstVal * secondVal);
                    second = null;
                    break;
                case "DIV":
                    first = new Token(firstVal / secondVal);
                    second = null;
                    break;

            }
        }
	}


}
