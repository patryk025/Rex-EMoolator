package pl.cba.genszu.amcodetranslator.interpreter.types;

import org.antlr.v4.runtime.tree.ParseTree;
import pl.cba.genszu.amcodetranslator.interpreter.Variable;


public class ArrayVariable extends Variable {
	public ArrayVariable(String name, Object value) {
		super(name);
	}

	private String DESCRIPTION;
	private ParseTree ONINIT;
	
	public void setDESCRIPTION(String DESCRIPTION)
	{
		this.DESCRIPTION = DESCRIPTION;
	}

	public String getDESCRIPTION()
	{
		return DESCRIPTION;
	}
	
	public void setONINIT(ParseTree ONINIT)
	{
		this.ONINIT = ONINIT;
	}

	public ParseTree getONINIT()
	{
		return ONINIT;
	}
}
