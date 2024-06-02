package pl.cba.genszu.amcodetranslator.interpreter.types;

import pl.cba.genszu.amcodetranslator.interpreter.variable.Variable;

public class MusicVariable extends Variable {
	public MusicVariable(String name, Object value) {
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
	}
}
