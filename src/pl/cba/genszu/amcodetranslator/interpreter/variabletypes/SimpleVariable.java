package pl.cba.genszu.amcodetranslator.interpreter.variabletypes;

import org.antlr.v4.runtime.tree.ParseTree;
import pl.cba.genszu.amcodetranslator.interpreter.Variable;

public class SimpleVariable extends Variable {
	public SimpleVariable(String name, Object value) {
		super(name, value);
	}

	private String FILENAME;
	private String EVENT;
	
	public void setFILENAME(String fILENAME)
	{
		FILENAME = fILENAME;
	}

	public String getFILENAME()
	{
		return FILENAME;
	}

	public void setEVENT(String eVENT)
	{
		EVENT = eVENT;
	}

	public String getEVENT()
	{
		return EVENT;
	}
}
