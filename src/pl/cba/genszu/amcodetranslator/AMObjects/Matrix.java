package pl.cba.genszu.amcodetranslator.AMObjects;
import pl.cba.genszu.amcodetranslator.interpreter.*;

public class Matrix
{
	private int[] SIZE;
	private int[] BASEPOS;
	private int CELLHEIGHT;
	private int CELLWIDTH;
	private InstructionsBlock ONLATEST;
	private InstructionsBlock ONNEXT;
	
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

	public void setONLATEST(InstructionsBlock ONLATEST)
	{
		this.ONLATEST = ONLATEST;
	}

	public InstructionsBlock getONLATEST()
	{
		return ONLATEST;
	}

	public void setONNEXT(InstructionsBlock ONNEXT)
	{
		this.ONNEXT = ONNEXT;
	}

	public InstructionsBlock getONNEXT()
	{
		return ONNEXT;
	}
}
