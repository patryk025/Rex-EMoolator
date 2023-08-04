package pl.cba.genszu.amcodetranslator.interpreter.variabletypes;

import pl.cba.genszu.amcodetranslator.interpreter.Variable;

public class ClassVariable extends Variable {
	public ClassVariable(String name, Object value) {
		super(name, value);
	}

	private String BASE;
	private String DEF;
	
	public void setBASE(String BASE)
	{
		this.BASE = BASE;
	}

	public String getBASE()
	{
		return BASE;
	}

	public void setDEF(String DEF)
	{
		this.DEF = DEF;
	}

	public String getDEF()
	{
		return DEF;
	}
}
