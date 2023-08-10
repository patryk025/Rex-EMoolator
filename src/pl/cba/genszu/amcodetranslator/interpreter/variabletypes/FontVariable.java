package pl.cba.genszu.amcodetranslator.interpreter.variabletypes;

import org.antlr.v4.runtime.tree.ParseTree;
import pl.cba.genszu.amcodetranslator.interpreter.Variable;

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
