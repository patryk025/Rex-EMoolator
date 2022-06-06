package pl.cba.genszu.amcodetranslator.AMObjects;

import pl.cba.genszu.amcodetranslator.interpreter.*;

public class Image
 {
    private String FILENAME;
	private String DESCRIPTION;
    private boolean TOCANVAS;
    private boolean VISIBLE;
    private int PRIORITY;
    private boolean PRELOAD;
    private boolean RELEASE;
    private boolean MONITORCOLLISION;
    private boolean MONITORCOLLISIONALPHA;
	private InstructionsBlock ONCLICK;
	private InstructionsBlock ONFOCUSON;
	private InstructionsBlock ONFOCUSOFF;
	private InstructionsBlock ONINIT;

    public void SHOW() {
        this.VISIBLE = true;
    }

    public void HIDE() {
        this.VISIBLE = false;
    }

    public void SETPRIORITY(int priority) {
        this.PRIORITY = priority;
    }
	
    public String getFILENAME() {
        return FILENAME;
    }

    public void setFILENAME(String FILENAME) {
        this.FILENAME = FILENAME;
    }

    public boolean isTOCANVAS() {
        return TOCANVAS;
    }

    public void setTOCANVAS(boolean TOCANVAS) {
        this.TOCANVAS = TOCANVAS;
    }

    public boolean isVISIBLE() {
        return VISIBLE;
    }

    public void setVISIBLE(boolean VISIBLE) {
        this.VISIBLE = VISIBLE;
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

    public boolean isMONITORCOLLISION() {
        return MONITORCOLLISION;
    }

    public void setMONITORCOLLISION(boolean MONITORCOLLISION) {
        this.MONITORCOLLISION = MONITORCOLLISION;
    }

    public boolean isMONITORCOLLISIONALPHA() {
        return MONITORCOLLISIONALPHA;
    }

    public void setMONITORCOLLISIONALPHA(boolean MONITORCOLLISIONALPHA) {
        this.MONITORCOLLISIONALPHA = MONITORCOLLISIONALPHA;
    }

    public int getPRIORITY() {
        return PRIORITY;
    }
	
	public void setDESCRIPTION(String DESCRIPTION)
	{
		this.DESCRIPTION = DESCRIPTION;
	}

	public String getDESCRIPTION()
	{
		return DESCRIPTION;
	}
	
	public void setONCLICK(InstructionsBlock ONCLICK)
	{
		this.ONCLICK = ONCLICK;
	}

	public InstructionsBlock getONCLICK()
	{
		return ONCLICK;
	}
	
	public void setONINIT(InstructionsBlock ONINIT)
	{
		this.ONINIT = ONINIT;
	}

	public InstructionsBlock getONINIT()
	{
		return ONINIT;
	}
	
	public void setONFOCUSON(InstructionsBlock oNFOCUSON)
	{
		ONFOCUSON = oNFOCUSON;
	}

	public InstructionsBlock getONFOCUSON()
	{
		return ONFOCUSON;
	}

	public void setONFOCUSOFF(InstructionsBlock oNFOCUSOFF)
	{
		ONFOCUSOFF = oNFOCUSOFF;
	}

	public InstructionsBlock getONFOCUSOFF()
	{
		return ONFOCUSOFF;
	}
	
}
