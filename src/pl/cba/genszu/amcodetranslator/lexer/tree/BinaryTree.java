package pl.cba.genszu.amcodetranslator.lexer.tree;

import pl.cba.genszu.amcodetranslator.lexer.stack.Stack;
import pl.cba.genszu.amcodetranslator.lexer.stack.exceptions.StackOverflowException;

public class BinaryTree
{
	public Node root;
	
	public BinaryTree() {
		this.root = null;
	}

	public BinaryTree(Node root)
	{
		this.root = root;
	}
	
	public void traverseInOrder() {
		traverseInOrder(this.root);
	}
	
	public void traverseInOrder(Node node) {
		if (node != null) {
			traverseInOrder(node.left);
			System.out.println(node.value);
			traverseInOrder(node.right);
		}
	}
	
	public void traversePreOrder() {
		traversePreOrder(this.root);
	}
	
	public void traversePreOrder(Node node) {
		if (node != null) {
			System.out.println(node.value);
			traversePreOrder(node.left);
			traversePreOrder(node.right);
		}
	}
	
	public void traversePostOrder() {
		traversePostOrder(this.root);
	}
	
	public void traversePostOrder(Node node) {
		if (node != null) {
			traversePostOrder(node.left);
			traversePostOrder(node.right);
			System.out.println(node.value);
		}
	}

	public void instructionsToStack(Node node, Stack stack) {
		if (node != null) {
			traversePostOrder(node.left);
			traversePostOrder(node.right);
			try {
				stack.push(node.value);
			} catch (StackOverflowException e) {
				try {
					stack.push(node.value);
				} catch (StackOverflowException e1) {
					//i will not try to extend anymore because this code is unreachable
					e1.printStackTrace();
				}
				e.printStackTrace();
			}
		}
	}
}
