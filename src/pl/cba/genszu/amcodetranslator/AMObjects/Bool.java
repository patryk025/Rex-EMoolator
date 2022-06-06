package pl.cba.genszu.amcodetranslator.AMObjects;
import pl.cba.genszu.amcodetranslator.interpreter.*;

public class Bool {
    /*public Bool(boolean VALUE) {
        this.VALUE = VALUE;
    }*/

    private boolean VALUE;
	private String DESCRIPTION;
	private InstructionsBlock ONINIT;
	private InstructionsBlock ONBRUTALCHANGED;
	private InstructionsBlock ONCHANGED;;
	private boolean TOINI;

	public void setDESCRIPTION(String DESCRIPTION)
	{
		this.DESCRIPTION = DESCRIPTION;
	}

	public String getDESCRIPTION()
	{
		return DESCRIPTION;
	}

    public void SET(boolean value) {
        this.VALUE = value;
    }

    public boolean GET() {
        return this.VALUE;
    }
	
	public void setONINIT(InstructionsBlock ONINIT)
	{
		this.ONINIT = ONINIT;
	}

	public InstructionsBlock getONINIT()
	{
		return ONINIT;
	}

	public void setONBRUTALCHANGED(InstructionsBlock ONBRUTALCHANGED)
	{
		this.ONBRUTALCHANGED = ONBRUTALCHANGED;
	}

	public InstructionsBlock getONBRUTALCHANGED()
	{
		if(ONBRUTALCHANGED == null) 
			ONBRUTALCHANGED = new InstructionsBlock();
		return ONBRUTALCHANGED;
	}

	public void setONCHANGED(InstructionsBlock ONCHANGED)
	{
		this.ONCHANGED = ONCHANGED;
	}

	public InstructionsBlock getONCHANGED()
	{
		if(ONCHANGED == null) 
			ONCHANGED = new InstructionsBlock();
		return ONCHANGED;
	}

	public void setTOINI(boolean TOINI)
	{
		this.TOINI = TOINI;
	}

	public boolean isTOINI()
	{
		return TOINI;
	}
	
}
