package pl.cba.genszu.amcodetranslator.AMObjects;

import pl.cba.genszu.amcodetranslator.interpreter.*;

public class Group {
    private InstructionsBlock ONINIT;
	
	public void setONINIT(InstructionsBlock ONINIT)
	{
		this.ONINIT = ONINIT;
	}

	public InstructionsBlock getONINIT()
	{
		return ONINIT;
	}
}
