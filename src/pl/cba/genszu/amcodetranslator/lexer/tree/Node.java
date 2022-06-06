package pl.cba.genszu.amcodetranslator.lexer.tree;
import pl.cba.genszu.amcodetranslator.interpreter.util.Token;
import pl.cba.genszu.amcodetranslator.lexer.tree.exception.BinaryTreeInsertException;
import pl.cba.genszu.amcodetranslator.interpreter.*;

public class Node
{
	public Token value;
    public Node left;
    public Node right;
	public InstructionsList instr; //dla ifów

	public Node() {
		this.value = null;
		right = null;
		left = null;
		instr = null;
	}

    public Node(Token value) {
        this.value = value;
        right = null;
        left = null;
		instr = null;
    }
	
	public Node(String tokenName) {
        this.value = new Token(tokenName);
        right = null;
        left = null;
		instr = null;
    }
	
	public boolean isLeaf() {
		return this.left == null && this.right == null;
	}
	
	public Node add(Token token) throws BinaryTreeInsertException {
		if(left == null) {
			left = new Node(token);
			return left;
		}
		else if(right == null) {
			right = new Node(token);
			return right;
		}
		else {
			throw new BinaryTreeInsertException("This node is full");
		}
	}

	public Node add(Node node) throws BinaryTreeInsertException {
		if(left == null) {
			left = node;
			return left;
		}
		else if(right == null) {
			right = node;
			return right;
		}
		else {
			throw new BinaryTreeInsertException("This node is full");
		}
	}
	
	public Node add(InstructionsList instr) throws BinaryTreeInsertException {
		if(isLeaf()) {
			this.instr = instr;
			return this;
		}
		else {
			throw new BinaryTreeInsertException("Adding instructions list to node is illegal");
		}
	}
	
	@Override
	public String toString() {
		return "Node["+this.value+"]";
	}
}
