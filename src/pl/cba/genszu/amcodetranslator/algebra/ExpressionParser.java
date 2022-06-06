package pl.cba.genszu.amcodetranslator.algebra;

import java.util.*;
import pl.cba.genszu.amcodetranslator.interpreter.util.*;
import pl.cba.genszu.amcodetranslator.lexer.*;
import pl.cba.genszu.amcodetranslator.lexer.tree.*;
import pl.cba.genszu.amcodetranslator.lexer.tree.exception.*;

public class ExpressionParser
{
	private static List<String> infixToPostfix(List<String> infix, HashMap<String, Integer> prec) {
		//implementation of Djikstra's Shunting-Yard algorythm
		
		Stack<String> operators = new Stack<>();
		List<String> postfix = new ArrayList<>();
		
		for(String operand : infix) {
			if(!(operand.matches("[\\+\\-\\*\\@\\%\\(\\)]") || operand.equals("OR") || operand.equals("AND")))
				postfix.add(operand);
			else if(operand.equals("(")) 
				operators.push(operand); // push it on stack
			else if(operand.equals(")")) {
				while(!operators.isEmpty() && !operators.peek().equals("(")) {
					postfix.add(operators.pop());
				}
				try {
					operators.pop(); //remove left parenthesis
				}
				catch(EmptyStackException e) {
					System.out.println("DEBUG: pusty stack, wyrażenie => "+infix);
					throw e;
				}
			}
			else {
				while (!operators.isEmpty() && (prec.get(operators.peek()) >= prec.get(operand))) {
					postfix.add(operators.pop());
				}
				operators.push(operand); //push operand on stack
			}
		}
		
		while (!operators.isEmpty()) {
			if(operators.peek().equals("(")) 
				throw new RuntimeException("Mismatched parenthesis detected. Correct your expression.");
			postfix.add(operators.pop());
		}
		
		return postfix;
	}
	
	public static List<String> parseExpression(String expression) {
		List<String> parts = new ArrayList<>();

		String[] letters = expression.split("");

		String buffer = "";

		int counter = 0;
		int len = letters.length;
		boolean isFunctionCall = false;
		boolean inString = false;
		int intLvl = 0;

		for(String letter : letters) {
			counter++;
			if(letter.equals("\"")) {
				if(inString) inString = false;
				else inString = true;
			}
			if(letter.matches("[\\+\\-\\*\\@\\(\\)\\%]") && !isFunctionCall) {
				if(!buffer.equals("") && !buffer.matches("[\\+\\-]")) {
					/*if(!buffer.matches("[\\+|-|\\*|\\/|\\(|\\)]")) {
					 parts.add(buffer);
					 buffer = letter;
					 }
					 else {
					 buffer += letter;
					 }*/
					parts.add(buffer);
					parts.add(letter);
					buffer = "";
				}
				else {
					if(letter.equals("(")) {
						parts.add(letter);
						intLvl++;
					}
					else if(letter.equals("-")) {
						if(!buffer.equals("-")) {
							buffer += letter;
						}
						else {
							System.out.println("WARNING: too much minuses, omitting...");
						}
					}
					else if(!letter.equals("+")) {
						if(!letter.equals("*") && counter != 1) { //suppress false warnings
							System.out.println("WARNING: unexpected logic operator, no value before");
							System.out.println("DEBUG: expression => "+expression);
						}
					}
				}
			}
			else {
				buffer += letter;
				if(letter.equals("^") && !inString) isFunctionCall = true;
				if(letter.equals(")")) intLvl--;
				if(isFunctionCall && letter.equals(")") && intLvl == 0) isFunctionCall = false;
				if(counter == len) {
					parts.add(buffer);
				}
			}
		}
		return parts;
	}
	
	public static Node expressionToTree(String expression) throws BinaryTreeInsertException {
		//expression = extractExpression(expression);

		//System.out.println("DEBUG: expression => " + expression);
		
		List<String> parts = parseExpression(expression);

		HashMap<String, Integer> prec = new HashMap<>();
		//prec.put("^", 3);
		prec.put("*", 2);
		prec.put("@", 2); //tak małpa to dzielenie, taaa
		prec.put("%", 2); //modulo chyba tak samo jak wyżej
		prec.put("+", 1);
		prec.put("-", 1);
		prec.put("(", -1);
		
		List<String> postfix;
		try {
			postfix = infixToPostfix(parts, prec);
		}
		catch(EmptyStackException e) {
			System.out.println("ERROR: błąd w wyrażeniu, wyrażenie => "+expression);
			throw e;
		}
		
		//konwersja postaci postfixowej do drzewa binarnego
		Stack<Node> operands = new Stack<>();
		for(int i = 0; i < postfix.size(); i++) {
			if(!postfix.get(i).matches("[\\+\\-\\*\\@\\%]")) {
				operands.push(new Node(new Token(Constants.VARVAL, postfix.get(i))));
			}
			else {
				Node tmp;
				if(postfix.get(i).equals("+")) {
					tmp = new Node(new Token(Constants.PLUS));
				}	
				else if(postfix.get(i).equals("-")) {
					tmp = new Node(new Token(Constants.MINUS));
				}
				else if(postfix.get(i).equals("*")) {
					tmp = new Node(new Token(Constants.MUL));
				}
				else if(postfix.get(i).equals("@")) {
					tmp = new Node(new Token(Constants.DIV));
				}
				else if(postfix.get(i).equals("%")) {
					tmp = new Node(new Token(Constants.MOD));
				}
				else {
					tmp = new Node(new Token(Constants.DEBUG));
				}
				try
				{
					String testVal = operands.peek().value.value;
					/*System.out.println("DEBUG: testVal = " + testVal);
					System.out.println("DEBUG: LexerUtils.isMethodCall(testVal) = " + LexerUtils.isMethodCall(testVal));
					System.out.println("DEBUG: LexerUtils.isStructFieldExpr(testVal) = " + LexerUtils.isStructFieldExpr(testVal));
					System.out.println("DEBUG: LexerUtils.isMethodCall(testVal) || LexerUtils.isStructFieldExpr(testVal) = " + (LexerUtils.isMethodCall(testVal) || LexerUtils.isStructFieldExpr(testVal)));
					*/
					tmp.right = (LexerUtils.isMethodCall(testVal) || LexerUtils.isStructFieldExpr(testVal) ?Lexer.parseCode(operands.pop().value.value).instr.get(0).root: operands.pop());
					testVal = operands.peek().value.value;
					tmp.left = (LexerUtils.isMethodCall(testVal) || LexerUtils.isStructFieldExpr(testVal) ?Lexer.parseCode(operands.pop().value.value).instr.get(0).root: operands.pop());
					operands.push(tmp);
				}
				catch (Exception e)
				{
					//todo
					//e.printStackTrace();
				}
			}
		}

		Node tree = null;
		if(!operands.isEmpty())
			tree = operands.pop();

		//BSTPrinter.print(new BinaryTree(tree));
		return tree;
	}
}
