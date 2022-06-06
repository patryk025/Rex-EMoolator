package pl.cba.genszu.amcodetranslator.AMObjects;

import pl.cba.genszu.amcodetranslator.interpreter.*;

public class Sound
 {
    private String FILENAME;
    private boolean PRELOAD;
    private boolean RELEASE;
    private boolean FLUSHAFTERPLAYED;
    private InstructionsBlock ONSTARTED; //nazwa obiektu Behaviour
    private InstructionsBlock ONFINISHED; //nazwa obiektu Behaviour
	private InstructionsBlock ONINIT;
	private String DESCRIPTION;

    /*methods of Sound*/
    public void PLAY() {

    }
    public void SETVOLUME(int volume) {
        
    }

    /*internal methods*/
    public String getFILENAME() {
        return FILENAME;
    }

    public void setFILENAME(String FILENAME) {
        this.FILENAME = FILENAME;
    }

    public boolean isPRELOAD() {
        return PRELOAD;
    }

    public void setPRELOAD(boolean PRELOAD) {
        this.PRELOAD = PRELOAD;
    }

    public boolean isRELEASE() {
        return RELEASE;
    }

    public void setRELEASE(boolean RELEASE) {
        this.RELEASE = RELEASE;
    }

    public InstructionsBlock getONSTARTED() {
        return ONSTARTED;
    }

    public void setONSTARTED(InstructionsBlock ONSTARTED) {
        this.ONSTARTED = ONSTARTED;
    }

    public InstructionsBlock getONFINISHED() {
        return ONFINISHED;
    }

    public void setONFINISHED(InstructionsBlock ONFINISHED) {
        this.ONFINISHED = ONFINISHED;
    }

    public boolean isFLUSHAFTERPLAYED() {
        return FLUSHAFTERPLAYED;
    }

    public void setFLUSHAFTERPLAYED(boolean FLUSHAFTERPLAYED) {
        this.FLUSHAFTERPLAYED = FLUSHAFTERPLAYED;
    }
	
	public void setDESCRIPTION(String DESCRIPTION)
	{
		this.DESCRIPTION = DESCRIPTION;
	}

	public String getDESCRIPTION()
	{
		return DESCRIPTION;
	}
	
	public void setONINIT(InstructionsBlock ONINIT)
	{
		this.ONINIT = ONINIT;
	}

	public InstructionsBlock getONINIT()
	{
		return ONINIT;
	}
}
