package pl.cba.genszu.amcodetranslator.lexer.tree;

import pl.cba.genszu.amcodetranslator.interpreter.util.Token;

public class Branch extends Node {
    public Node condition;
	public Node loopIncrement;

    public Branch() {
        super();
    }
	
	public Branch(String tokenName) {
		super(tokenName);
	}

    public Branch(Token value) {
        super(value);
    }

    public Node addCondition(Token token) {
        return addCondition(new Node(token));
    }
    public Node addCondition(Node token) {
        condition = token;
		return condition;
    }
	
	public Node addIncrement(Token token) {
        return addIncrement(new Node(token));
    }
    public Node addIncrement(Node token) {
        loopIncrement = token;
		return loopIncrement;
    }
}
