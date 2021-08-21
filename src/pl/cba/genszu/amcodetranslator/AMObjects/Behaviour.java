package pl.cba.genszu.amcodetranslator.AMObjects;
import pl.cba.genszu.amcodetranslator.interpreter.*;

public class Behaviour {
	private InstructionsBlock CODE;
	private String CONDITION;

    /*public void RUN() {

    }*/

	public void setCODE(InstructionsBlock CODE)
	{
		this.CODE = CODE;
	}

	public InstructionsBlock getCODE()
	{
		return CODE;
	}
	
	public void setCONDITION(String CONDITION)
	{
		this.CONDITION = CONDITION;
	}

	public String getCONDITION()
	{
		return CONDITION;
	}
}
