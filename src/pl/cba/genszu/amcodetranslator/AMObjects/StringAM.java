package pl.cba.genszu.amcodetranslator.AMObjects;

import pl.cba.genszu.amcodetranslator.interpreter.*;

public class StringAM
 {
    private String VALUE;
    private boolean TOINI;
	private InstructionsBlock ONINIT;
	private InstructionsBlock ONBRUTALCHANGED;
	private InstructionsBlock ONCHANGED;
	private String DESCRIPTION;

    public void SET(String value) {
        this.VALUE = value;
    }

    public String GET() {
        return this.VALUE;
    }

    public int FIND(String needle) {
        return VALUE.indexOf(needle);
    }

    public void ONINIT() {

    }

    public boolean isTOINI() {
        return TOINI;
    }

    public void setTOINI(boolean TOINI) {
        this.TOINI = TOINI;
    }
	
	public void setONINIT(InstructionsBlock ONINIT) {
		this.ONINIT = ONINIT;
    }

	public InstructionsBlock getONINIT() {
		return this.ONINIT;
	}

	public void setONBRUTALCHANGED(InstructionsBlock ONBRUTALCHANGED)
	{
		this.ONBRUTALCHANGED = ONBRUTALCHANGED;
	}

	public InstructionsBlock getONBRUTALCHANGED()
	{
		if(ONBRUTALCHANGED == null) 
			ONBRUTALCHANGED = new InstructionsBlock();
		return this.ONBRUTALCHANGED;
	}

	public void setONCHANGED(InstructionsBlock ONCHANGED)
	{
		this.ONCHANGED = ONCHANGED;
	}

	public InstructionsBlock getONCHANGED()
	{
		if(ONCHANGED == null) 
			ONCHANGED = new InstructionsBlock();
		return this.ONCHANGED;
	}
	
	public void setDESCRIPTION(String DESCRIPRION)
	{
		this.DESCRIPTION = DESCRIPRION;
	}

	public String getDESCRIPTION()
	{
		return DESCRIPTION;
	}
}
