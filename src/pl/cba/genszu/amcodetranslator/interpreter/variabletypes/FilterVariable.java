package pl.cba.genszu.amcodetranslator.interpreter.variabletypes;

import org.antlr.v4.runtime.tree.ParseTree;
import pl.cba.genszu.amcodetranslator.interpreter.Variable;

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
