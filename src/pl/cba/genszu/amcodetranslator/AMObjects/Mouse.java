package pl.cba.genszu.amcodetranslator.AMObjects;
import pl.cba.genszu.amcodetranslator.interpreter.*;

public class Mouse
{
	//interfejs myszy
	
	private InstructionsBlock ONMOVE;
	private InstructionsBlock ONCLICK;
	private InstructionsBlock ONDBLCLICK;
	private InstructionsBlock ONRELEASE;
	private InstructionsBlock ONINIT;
	private int RAW;

	public void setONMOVE(InstructionsBlock oNMOVE)
	{
		ONMOVE = oNMOVE;
	}

	public InstructionsBlock getONMOVE()
	{
		return ONMOVE;
	}

	public void setONCLICK(InstructionsBlock oNCLICK)
	{
		ONCLICK = oNCLICK;
	}

	public InstructionsBlock getONCLICK()
	{
		if(ONCLICK == null) ONCLICK = new InstructionsBlock();
		return ONCLICK;
	}

	public void setONDBLCLICK(InstructionsBlock oNDBLCLICK)
	{
		ONDBLCLICK = oNDBLCLICK;
	}

	public InstructionsBlock getONDBLCLICK()
	{
		if(ONDBLCLICK == null) ONDBLCLICK = new InstructionsBlock();
		return ONDBLCLICK;
	}

	public void setONRELEASE(InstructionsBlock oNRELEASE)
	{
		ONRELEASE = oNRELEASE;
	}

	public InstructionsBlock getONRELEASE()
	{
		if(ONRELEASE == null) ONRELEASE = new InstructionsBlock();
		return ONRELEASE;
	}
	
	public void setONINIT(InstructionsBlock oNINIT)
	{
		ONINIT = oNINIT;
	}

	public InstructionsBlock getONINIT()
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
