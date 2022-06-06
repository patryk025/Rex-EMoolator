package pl.cba.genszu.amcodetranslator.AMObjects;

import pl.cba.genszu.amcodetranslator.interpreter.*;

public class Keyboard
{
	private InstructionsBlock ONCHAR;
	private InstructionsBlock ONKEYUP;
	private InstructionsBlock ONKEYDOWN;
	
	public void setONCHAR(InstructionsBlock ONCHAR)
	{
		this.ONCHAR = ONCHAR;
	}

	public InstructionsBlock getONCHAR()
	{
		if(ONCHAR == null) 
			ONCHAR = new InstructionsBlock();
		return ONCHAR;
	}

	public void setONKEYUP(InstructionsBlock ONKEYUP)
	{
		this.ONKEYUP = ONKEYUP;
	}

	public InstructionsBlock getONKEYUP()
	{
		if(ONKEYUP == null) 
			ONKEYUP = new InstructionsBlock();
		return ONKEYUP;
	}

	public void setONKEYDOWN(InstructionsBlock ONKEYDOWN)
	{
		this.ONKEYDOWN = ONKEYDOWN;
	}

	public InstructionsBlock getONKEYDOWN()
	{
		if(ONKEYDOWN == null) 
			ONKEYDOWN = new InstructionsBlock();
		return ONKEYDOWN;
	}
}
