package pl.cba.genszu.amcodetranslator.interpreter.types;

import pl.cba.genszu.amcodetranslator.interpreter.Variable;

public class PatternVariable extends Variable {
	public PatternVariable(String name, Object value) {
		super(name);
	}

	private int LAYERS;
	private boolean TOCANVAS;
	private boolean VISIBLE;
	private int PRIORITY;
	private int WIDTH;
	private int HEIGHT;
	private int GRIDX;
	private int GRIDY;


	public void setLAYERS(int lAYERS)
	{
		LAYERS = lAYERS;
	}

	public int getLAYERS()
	{
		return LAYERS;
	}

	public void setTOCANVAS(boolean tOCANVAS)
	{
		TOCANVAS = tOCANVAS;
	}

	public boolean isTOCANVAS()
	{
		return TOCANVAS;
	}

	public void setVISIBLE(boolean vISIBLE)
	{
		VISIBLE = vISIBLE;
	}

	public boolean isVISIBLE()
	{
		return VISIBLE;
	}

	public void setPRIORITY(int pRIORITY)
	{
		PRIORITY = pRIORITY;
	}

	public int getPRIORITY()
	{
		return PRIORITY;
	}

	public void setWIDTH(int wIDTH)
	{
		WIDTH = wIDTH;
	}

	public int getWIDTH()
	{
		return WIDTH;
	}

	public void setHEIGHT(int hEIGHT)
	{
		HEIGHT = hEIGHT;
	}

	public int getHEIGHT()
	{
		return HEIGHT;
	}

	public void setGRIDX(int gRIDX)
	{
		GRIDX = gRIDX;
	}

	public int getGRIDX()
	{
		return GRIDX;
	}

	public void setGRIDY(int gRIDY)
	{
		GRIDY = gRIDY;
	}

	public int getGRIDY()
	{
		return GRIDY;
	}
}
