package pl.cba.genszu.amcodetranslator.AMObjects;
import pl.cba.genszu.amcodetranslator.interpreter.*;

public class Animo {
    private String FILENAME;
    private boolean TOCANVAS;
    private boolean VISIBLE;
    private int FPS;
    private int PRIORITY;
	private InstructionsBlock ONFINISHED;
	private InstructionsBlock ONSTARTED;
	private boolean PRELOAD;
	private boolean RELEASE;
	private boolean MONITORCOLLISION;
	private boolean MONITORCOLLISIONALPHA;
	private InstructionsBlock ONINIT;
	private InstructionsBlock ONCOLLISION; //Behaviour
	private InstructionsBlock ONFRAMECHANGED;
	private String DESCRIPTION;
	private InstructionsBlock ONCLICK;
	private InstructionsBlock ONFOCUSOFF;
	private InstructionsBlock ONFOCUSON;
	private InstructionsBlock ONRELEASE;
	private InstructionsBlock ONSIGNAL;

    //private List<>

    /*methods of Animo*/
    public int GETPOSITIONX() {return 0;}
    public int GETPOSITIONY() {return 0;}

    public void SETPOSITION(int x, int y) {}

    public void PLAY(String name) {}
    public void MOVE(int xOffset, int yOffset) {
        SETPOSITION(GETPOSITIONX()+xOffset, GETPOSITIONY()+yOffset);
    }
    public void HIDE() {
        this.VISIBLE = false;
    }

    public void SETPRIORITY(int priority) {
        this.PRIORITY = priority;
    }

    public void SETFRAME(int number) {}

    public int GETCFRAMEINEVENT() {return 0;}

    /*internal methods*/
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
    public int getFPS() {
        return FPS;
    }
    public void setFPS(int FPS) {
        this.FPS = FPS;
    }
    public int getPRIORITY() {
        return PRIORITY;
    }
    public void setPRIORITY(int PRIORITY) {
        this.PRIORITY = PRIORITY;
    }

	public void setONFINISHED(InstructionsBlock ONFINISHED)
	{
		this.ONFINISHED = ONFINISHED;
	}

	public InstructionsBlock getONFINISHED()
	{
		if(ONFINISHED == null)
			ONFINISHED = new InstructionsBlock();
		return ONFINISHED;
	}

	public void setONSTARTED(InstructionsBlock ONSTARTED)
	{
		this.ONSTARTED = ONSTARTED;
	}

	public InstructionsBlock getONSTARTED()
	{
		if(ONSTARTED == null)
			ONSTARTED = new InstructionsBlock();
		return ONSTARTED;
	}
	
	public void setPRELOAD(boolean PRELOAD)
	{
		this.PRELOAD = PRELOAD;
	}

	public boolean getPRELOAD()
	{
		return PRELOAD;
	}

	public void setRELEASE(boolean RELEASE)
	{
		this.RELEASE = RELEASE;
	}

	public boolean isRELEASE()
	{
		return RELEASE;
	}

	public void setMONITORCOLLISION(boolean MONITORCOLLISION)
	{
		this.MONITORCOLLISION = MONITORCOLLISION;
	}

	public boolean getMONITORCOLLISION()
	{
		return MONITORCOLLISION;
	}

	public void setMONITORCOLLISIONALPHA(boolean MONITORCOLLISIONALPHA)
	{
		this.MONITORCOLLISIONALPHA = MONITORCOLLISIONALPHA;
	}

	public boolean getMONITORCOLLISIONALPHA()
	{
		return MONITORCOLLISIONALPHA;
	}

	public void setONINIT(InstructionsBlock ONINIT)
	{
		this.ONINIT = ONINIT;
	}

	public InstructionsBlock getONINIT()
	{
		if(ONINIT == null) 
			ONINIT = new InstructionsBlock();
		return ONINIT;
	}
	
	public void setONCOLLISION(InstructionsBlock ONCOLLISION)
	{
		this.ONCOLLISION = ONCOLLISION;
	}

	public InstructionsBlock getONCOLLISION()
	{
		if(ONCOLLISION == null) 
			ONCOLLISION = new InstructionsBlock();
		return ONCOLLISION;
	}

	public InstructionsBlock getONFRAMECHANGED() {
		if(ONFRAMECHANGED == null)
			ONFRAMECHANGED = new InstructionsBlock();
		return ONFRAMECHANGED;
	}

	public void setONFRAMECHANGED(InstructionsBlock ONFRAMECHANGED) {
		this.ONFRAMECHANGED = ONFRAMECHANGED;
	}
	
	public String getDESCRIPTION() {
        return DESCRIPTION;
    }

	public void setDESCRIPTION(String DESCRIPTION)
	{
		this.DESCRIPTION = DESCRIPTION;
	}
	
	public void setONCLICK(InstructionsBlock ONCLICK)
	{
		this.ONCLICK = ONCLICK;
	}

	public InstructionsBlock getONCLICK()
	{
		return ONCLICK;
	}

	public void setONFOCUSOFF(InstructionsBlock ONFOCUSOFF)
	{
		this.ONFOCUSOFF = ONFOCUSOFF;
	}

	public InstructionsBlock getONFOCUSOFF()
	{
		return ONFOCUSOFF;
	}

	public void setONFOCUSON(InstructionsBlock ONFOCUSON)
	{
		this.ONFOCUSON = ONFOCUSON;
	}

	public InstructionsBlock getONFOCUSON()
	{
		return ONFOCUSON;
	}

	public void setONRELEASE(InstructionsBlock ONRELEASE)
	{
		this.ONRELEASE = ONRELEASE;
	}

	public InstructionsBlock getONRELEASE()
	{
		return ONRELEASE;
	}

	public void setONSIGNAL(InstructionsBlock ONSIGNAL)
	{
		this.ONSIGNAL = ONSIGNAL;
	}

	public InstructionsBlock getONSIGNAL()
	{
		if(ONSIGNAL == null) 
			ONSIGNAL = new InstructionsBlock();
		return ONSIGNAL;
	}
}
