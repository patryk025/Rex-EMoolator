package pl.cba.genszu.amcodetranslator.interpreter.types;

import pl.cba.genszu.amcodetranslator.interpreter.Variable;

public class StaticFilterVariable extends Variable {
	public StaticFilterVariable(String name, Object value) {
		super(name);
	}

	private String ACTION;

	public void setACTION(String ACTION)
	{
		this.ACTION = ACTION;
	}

	public String getACTION()
	{
		return ACTION;
	}
}
