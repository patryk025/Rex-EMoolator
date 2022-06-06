package pl.cba.genszu.amcodetranslator.algebra;

import pl.cba.genszu.amcodetranslator.lexer.tree.exception.*;
import pl.cba.genszu.amcodetranslator.lexer.tree.*;

public class ExpressionSolver
{
	/*public static String optimaliseExpression(String expression) {
		List<String> parts = ExpressionParser.parseExpression(expression);

		Map<String, Integer> map = new HashMap<>();

		int tmp = 0;

		for(String part : parts) {
			if(part.matches("[\\+|\\-|\\*|\\/]")) {

			}
		}

		return null;
	}*/

	public void traversePostOrder(Node node) {
		if (node != null) {
			traversePostOrder(node.left);
			traversePostOrder(node.right);
			System.out.println(node.value);
		}
	}
	
	private static Node evaluate(Node op) {
		
		
		return null;
	}
	
	public static String optimaliseExpression(String expression) {
		try
		{
			Node tree = ExpressionParser.expressionToTree(expression);
			
			
		}
		catch (BinaryTreeInsertException e)
		{
			e.printStackTrace();
		}

		return null;
	}
	
	public void traverseInOrder(Node node) {
		if (node != null) {
			traverseInOrder(node.left);
			System.out.println(node.value);
			traverseInOrder(node.right);
		}
	}
}
