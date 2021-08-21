package pl.cba.genszu.amcodetranslator.AMObjects;
import pl.cba.genszu.amcodetranslator.interpreter.*;

public class IntegerAM {
    private int VALUE;
    private boolean TOINI;
	private InstructionsBlock ONINIT;
	private InstructionsBlock ONBRUTALCHANGED;

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
	
	public InstructionsBlock setONBRUTALCHANGED()
	{
		return this.ONBRUTALCHANGED;
	}

    public boolean isTOINI() {
        return TOINI;
    }

    public void setTOINI(boolean TOINI) {
        this.TOINI = TOINI;
    }
}
