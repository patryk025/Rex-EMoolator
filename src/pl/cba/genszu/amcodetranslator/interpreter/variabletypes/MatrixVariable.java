package pl.cba.genszu.amcodetranslator.interpreter.variabletypes;

import org.antlr.v4.runtime.tree.ParseTree;
import pl.cba.genszu.amcodetranslator.interpreter.Variable;


public class MatrixVariable extends Variable {
	public MatrixVariable(String name, Object value) {
		super(name, value);
	}

	private int[] SIZE;
	private int[] BASEPOS;
	private int CELLHEIGHT;
	private int CELLWIDTH;
	private ParseTree ONLATEST;
	private ParseTree ONNEXT;
	
	public void setSIZE(String SIZE)
	{
		String[] splitted = SIZE.split(",");
		this.SIZE = new int[] { 
			Integer.parseInt(splitted[0]),
			Integer.parseInt(splitted[1])
		};
	}

	public int[] getSIZE()
	{
		return SIZE;
	}

	public void setBASEPOS(String BASEPOS)
	{
		String[] splitted = BASEPOS.split(",");
		this.BASEPOS = new int[] { 
			Integer.parseInt(splitted[0]),
			Integer.parseInt(splitted[1])
		};
	}

	public int[] getBASEPOS()
	{
		return BASEPOS;
	}

	public void setCELLHEIGHT(int CELLHEIGHT)
	{
		this.CELLHEIGHT = CELLHEIGHT;
	}

	public int getCELLHEIGHT()
	{
		return CELLHEIGHT;
	}

	public void setCELLWIDTH(int CELLWIDTH)
	{
		this.CELLWIDTH = CELLWIDTH;
	}

	public int getCELLWIDTH()
	{
		return CELLWIDTH;
	}

	public void setONLATEST(ParseTree ONLATEST)
	{
		this.ONLATEST = ONLATEST;
	}

	public ParseTree getONLATEST()
	{
		return ONLATEST;
	}

	public void setONNEXT(ParseTree ONNEXT)
	{
		this.ONNEXT = ONNEXT;
	}

	public ParseTree getONNEXT()
	{
		return ONNEXT;
	}
}
