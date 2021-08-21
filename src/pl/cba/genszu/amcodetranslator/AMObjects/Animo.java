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
	private String ONCOLLISION; //Behaviour
	private InstructionsBlock ONFRAMECHANGED;

    //private List<>

    /*methods of Animo*/
    public int GETPOSITIONX() {return 0;}
    public int GETPOSITIONY() {return 0;}

    public void SETPOSITION(int x, int y) {}

    public void PLAY(String name) {}
    public void MOVE(int xOffset, int yOffset) {
        //na razie coś takiego, podejrzewam, że jest to do odpalenia animacji poruszania do jakiegoś punktu
        SETPOSITION(GETPOSITIONX()+xOffset, GETPOSITIONY()+yOffset);
    }
    public void HIDE() {
        this.VISIBLE = false;
        /*TODO: podłączenie do silnika*/
    }

    public void SETPRIORITY(int priority) {
        this.PRIORITY = priority;
        /*TODO: podłączenie do silnika*/
    }

    public void SETFRAME(int number) {}

    public int GETCFRAMEINEVENT() {return 0;}

    /*events methods*/
    public void ONSTARTED() {}
    public void ONSTARTED(String anim) {}
    public void ONFINISHED(String anim) {}
    public void ONFRAMECHANGED() {}

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
		return ONINIT;
	}
	
	public void setONCOLLISION(String ONCOLLISION)
	{
		this.ONCOLLISION = ONCOLLISION;
	}

	public String getONCOLLISION()
	{
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
}
