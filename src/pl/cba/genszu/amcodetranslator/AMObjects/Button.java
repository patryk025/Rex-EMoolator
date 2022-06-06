package pl.cba.genszu.amcodetranslator.AMObjects;
import pl.cba.genszu.amcodetranslator.interpreter.*;

public class Button {
    private String RECT;
    private boolean ENABLE;
    private boolean VISIBLE;
    private boolean DRAGGABLE;
	private Integer PRIORITY;
	private String SNDONMOVE;
    private String GFXSTANDARD;
    private String GFXONCLICK;
    private String GFXONMOVE;
	private String DESCRIPTION;
	private InstructionsBlock ONACTION; //Behaviour
	private InstructionsBlock ONFOCUSON;
	private InstructionsBlock ONFOCUSOFF;
	private InstructionsBlock ONCLICKED;
	private InstructionsBlock ONENDDRAGGING;
	private InstructionsBlock ONINIT;
	private InstructionsBlock ONRELEASED;
	private InstructionsBlock ONSTARTDRAGGING;

	public void setONCLICKED(InstructionsBlock ONCLICKED)
	{
		this.ONCLICKED = ONCLICKED;
	}

	public InstructionsBlock getONCLICKED()
	{
		return ONCLICKED;
	}

	public void setONENDDRAGGING(InstructionsBlock ONENDDRAGGING)
	{
		this.ONENDDRAGGING = ONENDDRAGGING;
	}

	public InstructionsBlock getONENDDRAGGING()
	{
		return ONENDDRAGGING;
	}

	public void setONINIT(InstructionsBlock ONINIT)
	{
		this.ONINIT = ONINIT;
	}

	public InstructionsBlock getONINIT()
	{
		return ONINIT;
	}

	public void setONRELEASED(InstructionsBlock ONRELEASED)
	{
		this.ONRELEASED = ONRELEASED;
	}

	public InstructionsBlock getONRELEASED()
	{
		return ONRELEASED;
	}

	public void setONSTARTDRAGGING(InstructionsBlock ONSTARTDRAGGING)
	{
		this.ONSTARTDRAGGING = ONSTARTDRAGGING;
	}

	public InstructionsBlock getONSTARTDRAGGING()
	{
		return ONSTARTDRAGGING;
	}

	public void setDESCRIPTION(String DESCRIPTION)
	{
		this.DESCRIPTION = DESCRIPTION;
	}

	public String getDESCRIPTION()
	{
		return DESCRIPTION;
	}

	public void setPRIORITY(Integer PRIORITY)
	{
		this.PRIORITY = PRIORITY;
	}

	public Integer getPRIORITY()
	{
		return PRIORITY;
	}
	
	
    public void SETRECT(String rect) {
        this.RECT = rect;
        /*TODO: rozpatrzenie dwóch przypadków
        * 700,0,800,200 - prostokąt (do przeskalowania)
        * string - nazwa Image*/
    }

    public void ENABLE() {
        this.ENABLE = true;
        this.VISIBLE = true;
    }

    public void DISABLEBUTVISIBLE() {
        this.ENABLE = false;
        this.VISIBLE = true;
    }

    public void DISABLE() {
        this.ENABLE = false;
    }

    public String getRECT() {
        return RECT;
    }

    public void setRECT(String RECT) {
        this.RECT = RECT;
    }

    public boolean isENABLE() {
        return ENABLE;
    }

    public void setENABLE(boolean ENABLE) {
        this.ENABLE = ENABLE;
    }

    public boolean isVISIBLE() {
        return VISIBLE;
    }

    public void setVISIBLE(boolean VISIBLE) {
        this.VISIBLE = VISIBLE;
    }

    public boolean isDRAGGABLE() {
        return DRAGGABLE;
    }

    public void setDRAGGABLE(boolean DRAGGABLE) {
        this.DRAGGABLE = DRAGGABLE;
    }

    public String getGFXSTANDARD() {
        return GFXSTANDARD;
    }

    public void setGFXSTANDARD(String GFXSTANDARD) {
        this.GFXSTANDARD = GFXSTANDARD;
    }

    public String getGFXONCLICK() {
        return GFXONCLICK;
    }

    public void setGFXONCLICK(String GFXONCLICK) {
        this.GFXONCLICK = GFXONCLICK;
    }

    public String getGFXONMOVE() {
        return GFXONMOVE;
    }

    public void setGFXONMOVE(String GFXONMOVE) {
        this.GFXONMOVE = GFXONMOVE;
    }
	
	public void setONACTION(InstructionsBlock ONACTION)
	{
		this.ONACTION = ONACTION;
	}

	public InstructionsBlock getONACTION()
	{
		return ONACTION;
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
	
	public void setSNDONMOVE(String SNDONMOVE)
	{
		this.SNDONMOVE = SNDONMOVE;
	}

	public String getSNDONMOVE()
	{
		return SNDONMOVE;
	}
}
