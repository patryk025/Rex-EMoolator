package pl.cba.genszu.amcodetranslator.interpreter.types;

import pl.cba.genszu.amcodetranslator.interpreter.variable.Variable;

public class FontVariable extends Variable {
	public FontVariable(String name, Object value) {
		super(name);
	}

	private String family;
	private String style;
	private int size;

	public void DEF(String[] split)
	{
		this.family = split[0];
		this.style = split[1];
		this.size = Integer.parseInt(split[2]);
	}
}
