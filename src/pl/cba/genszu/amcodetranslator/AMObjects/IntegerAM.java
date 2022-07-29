package pl.cba.genszu.amcodetranslator.AMObjects;
import pl.cba.genszu.amcodetranslator.interpreter.*;

public class IntegerAM {
    private int VALUE;
    private boolean TOINI;
	private InstructionsBlock ONINIT;
	private InstructionsBlock ONBRUTALCHANGED;
	private InstructionsBlock ONCHANGED;
	private String DESCRIPTION;
	private InstructionsBlock ONSIGNAL;
	private String VARTYPE;

	public void setVARTYPE(String vARTYPE)
	{
		VARTYPE = vARTYPE;
	}

	public String getVARTYPE()
	{
		return VARTYPE;
	}
	
    /*public IntegerAM(int VALUE, boolean TOINI) {
        this.VALUE = VALUE;
        this.TOINI = TOINI;
    }

    public IntegerAM(int VALUE) {
        this.VALUE = VALUE;
        this.TOINI = false;
    }*/

    public void SET(int value) {
        this.VALUE = value;
    }

    public int GET() {
        return this.VALUE;
    }

    public void SWITCH(int i1, int i2) {
        if(this.VALUE == i1) this.VALUE = i2;
        else this.VALUE = i1;
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
	
	public void setONSIGNAL(InstructionsBlock ONSIGNAL)
	{
		this.ONSIGNAL = ONSIGNAL;
	}

	public InstructionsBlock getONSIGNAL()
	{
		if(ONSIGNAL == null) 
			ONSIGNAL = new InstructionsBlock();
		return this.ONSIGNAL;
	}

    public boolean isTOINI() {
        return TOINI;
    }

    public void setTOINI(boolean TOINI) {
        this.TOINI = TOINI;
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
