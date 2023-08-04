package pl.cba.genszu.amcodetranslator.interpreter.variabletypes;

import org.antlr.v4.runtime.tree.ParseTree;
import pl.cba.genszu.amcodetranslator.interpreter.Variable;



public class KeyboardVariable extends Variable {
	public KeyboardVariable(String name, Object value) {
		super(name, value);
	}

	private ParseTree ONCHAR;
	private ParseTree ONKEYUP;
	private ParseTree ONKEYDOWN;
	
	public void setONCHAR(ParseTree ONCHAR)
	{
		this.ONCHAR = ONCHAR;
	}

	public ParseTree getONCHAR()
	{
		return ONCHAR;
	}

	public void setONKEYUP(ParseTree ONKEYUP)
	{
		this.ONKEYUP = ONKEYUP;
	}

	public ParseTree getONKEYUP()
	{
		return ONKEYUP;
	}

	public void setONKEYDOWN(ParseTree ONKEYDOWN)
	{
		this.ONKEYDOWN = ONKEYDOWN;
	}

	public ParseTree getONKEYDOWN()
	{
		return ONKEYDOWN;
	}
}
