package pl.cba.genszu.amcodetranslator.interpreter.util;

import pl.cba.genszu.amcodetranslator.interpreter.InstructionsList;
import pl.cba.genszu.amcodetranslator.lexer.Constants;
import pl.cba.genszu.amcodetranslator.lexer.tree.*;

public class Token
{
	public String type;
	public String value;
	public InstructionsList valueAsFunc;
	
	public Token(String type) {
		this.type = type;
		this.value = null;
		valueAsFunc = null;
	}
	
	public Token(String type, String value) {
		this.type = type;
		this.value = value;
		this.valueAsFunc = null;
	}

	public Token(double value) {
		this.type = Constants.NUMBER;
		this.value = ""+value;
		this.valueAsFunc = null;
	}
	
	public Token(String type, InstructionsList valueAsFunc) {
		this.type = type;
		this.value = null;
		this.valueAsFunc = valueAsFunc;
	}
	
	@Override
	public String toString() {
		return "Token(" + type + ", " + (value!=null?value:(valueAsFunc!=null?"BinaryTree":null)) + ")";
	}
}
