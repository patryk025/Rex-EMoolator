package pl.cba.genszu.amcodetranslator.interpreter.variabletypes;

import org.antlr.v4.runtime.tree.ParseTree;
import pl.cba.genszu.amcodetranslator.interpreter.Variable;


public class MouseVariable extends Variable {
	public MouseVariable(String name, Object value) {
		super(name, value);
	}

	//interfejs myszy
	
	private ParseTree ONMOVE;
	private ParseTree ONCLICK;
	private ParseTree ONDBLCLICK;
	private ParseTree ONRELEASE;
	private ParseTree ONINIT;
	private int RAW;

	public void setONMOVE(ParseTree oNMOVE)
	{
		ONMOVE = oNMOVE;
	}

	public ParseTree getONMOVE()
	{
		return ONMOVE;
	}

	public void setONCLICK(ParseTree oNCLICK)
	{
		ONCLICK = oNCLICK;
	}

	public ParseTree getONCLICK()
	{
		return ONCLICK;
	}

	public void setONDBLCLICK(ParseTree oNDBLCLICK)
	{
		ONDBLCLICK = oNDBLCLICK;
	}

	public ParseTree getONDBLCLICK()
	{
		return ONDBLCLICK;
	}

	public void setONRELEASE(ParseTree oNRELEASE)
	{
		ONRELEASE = oNRELEASE;
	}

	public ParseTree getONRELEASE()
	{
		return ONRELEASE;
	}
	
	public void setONINIT(ParseTree oNINIT)
	{
		ONINIT = oNINIT;
	}

	public ParseTree getONINIT()
	{
		return ONINIT;
	}

	public void setRAW(int rAW)
	{
		RAW = rAW;
	}

	public int getRAW()
	{
		return RAW;
	}
}
