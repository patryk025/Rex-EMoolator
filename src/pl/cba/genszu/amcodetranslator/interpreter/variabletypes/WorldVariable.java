package pl.cba.genszu.amcodetranslator.interpreter.variabletypes;

import org.antlr.v4.runtime.tree.ParseTree;
import pl.cba.genszu.amcodetranslator.interpreter.Variable;

public class WorldVariable extends Variable {
	public WorldVariable(String name, Object value) {
		super(name);
	}

	private String FILENAME;
	
	public void setFILENAME(String FILENAME)
	{
		this.FILENAME = FILENAME;
	}

	public String getFILENAME()
	{
		return FILENAME;
	}}
