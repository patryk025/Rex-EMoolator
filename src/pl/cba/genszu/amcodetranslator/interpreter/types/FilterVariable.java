package pl.cba.genszu.amcodetranslator.interpreter.types;

import pl.cba.genszu.amcodetranslator.interpreter.variable.Variable;

public class FilterVariable extends Variable {
	public FilterVariable(String name, Object value) {
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
	}}
