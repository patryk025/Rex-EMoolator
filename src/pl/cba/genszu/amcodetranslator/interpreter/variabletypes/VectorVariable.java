package pl.cba.genszu.amcodetranslator.interpreter.variabletypes;

import org.antlr.v4.runtime.tree.ParseTree;
import pl.cba.genszu.amcodetranslator.interpreter.Variable;
import pl.cba.genszu.amcodetranslator.utils.*;

public class VectorVariable extends Variable {
	public VectorVariable(String name, Object value) {
		super(name);
	}

	private int SIZE;
	private float[] VALUE;
	
	public void setSIZE(int sIZE)
	{
		SIZE = sIZE;
		VALUE = new float[SIZE];
	}

	public int getSIZE()
	{
		return SIZE;
	}

	public void setVALUE(String values)
	{
		String[] params = values.split(",");
		if(params.length>SIZE) {
			Logger.w("Got "+params.length+", cutted to "+SIZE);
		}
		for(int i = 0; i < SIZE; i++) {
			VALUE[i] = Float.parseFloat(params[i]);
		}
	}

	public float[] getVALUESl()
	{
		return VALUE;
	}
}
