package pl.cba.genszu.amcodetranslator.interpreter.variabletypes;

import org.antlr.v4.runtime.tree.ParseTree;
import pl.cba.genszu.amcodetranslator.interpreter.Variable;


public class AnimoVariable extends Variable {
	public AnimoVariable(String name, Object value) {
		super(name, value);
	}

    private String FILENAME;
    private boolean TOCANVAS;
    private boolean VISIBLE;
    private int FPS;
    private int PRIORITY;
	private ParseTree ONFINISHED;
	private ParseTree ONSTARTED;
	private boolean PRELOAD;
	private boolean RELEASE;
	private boolean MONITORCOLLISION;
	private boolean MONITORCOLLISIONALPHA;
	private ParseTree ONINIT;
	private ParseTree ONCOLLISION; //Behaviour
	private ParseTree ONFRAMECHANGED;
	private String DESCRIPTION;
	private ParseTree ONCLICK;
	private ParseTree ONFOCUSOFF;
	private ParseTree ONFOCUSON;
	private ParseTree ONRELEASE;
	private ParseTree ONSIGNAL;

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

	public void setONFINISHED(ParseTree ONFINISHED)
	{
		this.ONFINISHED = ONFINISHED;
	}

	public ParseTree getONFINISHED()
	{
		return ONFINISHED;
	}

	public void setONSTARTED(ParseTree ONSTARTED)
	{
		this.ONSTARTED = ONSTARTED;
	}

	public ParseTree getONSTARTED()
	{
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

	public void setONINIT(ParseTree ONINIT)
	{
		this.ONINIT = ONINIT;
	}

	public ParseTree getONINIT()
	{
		return ONINIT;
	}
	
	public void setONCOLLISION(ParseTree ONCOLLISION)
	{
		this.ONCOLLISION = ONCOLLISION;
	}

	public ParseTree getONCOLLISION()
	{
		return ONCOLLISION;
	}

	public ParseTree getONFRAMECHANGED() {
		return ONFRAMECHANGED;
	}

	public void setONFRAMECHANGED(ParseTree ONFRAMECHANGED) {
		this.ONFRAMECHANGED = ONFRAMECHANGED;
	}
	
	public String getDESCRIPTION() {
        return DESCRIPTION;
    }

	public void setDESCRIPTION(String DESCRIPTION)
	{
		this.DESCRIPTION = DESCRIPTION;
	}
	
	public void setONCLICK(ParseTree ONCLICK)
	{
		this.ONCLICK = ONCLICK;
	}

	public ParseTree getONCLICK()
	{
		return ONCLICK;
	}

	public void setONFOCUSOFF(ParseTree ONFOCUSOFF)
	{
		this.ONFOCUSOFF = ONFOCUSOFF;
	}

	public ParseTree getONFOCUSOFF()
	{
		return ONFOCUSOFF;
	}

	public void setONFOCUSON(ParseTree ONFOCUSON)
	{
		this.ONFOCUSON = ONFOCUSON;
	}

	public ParseTree getONFOCUSON()
	{
		return ONFOCUSON;
	}

	public void setONRELEASE(ParseTree ONRELEASE)
	{
		this.ONRELEASE = ONRELEASE;
	}

	public ParseTree getONRELEASE()
	{
		return ONRELEASE;
	}

	public void setONSIGNAL(ParseTree ONSIGNAL)
	{
		this.ONSIGNAL = ONSIGNAL;
	}

	public ParseTree getONSIGNAL()
	{
		return ONSIGNAL;
	}
}
