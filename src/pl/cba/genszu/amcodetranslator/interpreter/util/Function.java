package pl.cba.genszu.amcodetranslator.interpreter.util;

import pl.cba.genszu.amcodetranslator.lexer.Constants;

public class Function extends Token
{
	public String[] args;
	
	public Function(String name, String args) {
		super(Constants.FUNCTION, name);
		this.args = args.split(",");
	}
}
