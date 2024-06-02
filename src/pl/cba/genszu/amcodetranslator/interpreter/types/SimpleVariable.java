package pl.cba.genszu.amcodetranslator.interpreter.types;

import pl.cba.genszu.amcodetranslator.interpreter.variable.Variable;

public class SimpleVariable extends Variable {
	public SimpleVariable(String name, Object value) {
		super(name);
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
