package pl.cba.genszu.amcodetranslator.interpreter.types;

import org.antlr.v4.runtime.tree.ParseTree;
import pl.cba.genszu.amcodetranslator.interpreter.Variable;


public class ButtonVariable extends Variable {
	public ButtonVariable(String name, Object value) {
		super(name);
	}

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
	private ParseTree ONACTION; //Behaviour
	private ParseTree ONFOCUSON;
	private ParseTree ONFOCUSOFF;
	private ParseTree ONCLICKED;
	private ParseTree ONENDDRAGGING;
	private ParseTree ONINIT;
	private ParseTree ONRELEASED;
	private ParseTree ONSTARTDRAGGING;

	public void setONCLICKED(ParseTree ONCLICKED)
	{
		this.ONCLICKED = ONCLICKED;
	}

	public ParseTree getONCLICKED()
	{
		return ONCLICKED;
	}

	public void setONENDDRAGGING(ParseTree ONENDDRAGGING)
	{
		this.ONENDDRAGGING = ONENDDRAGGING;
	}

	public ParseTree getONENDDRAGGING()
	{
		return ONENDDRAGGING;
	}

	public void setONINIT(ParseTree ONINIT)
	{
		this.ONINIT = ONINIT;
	}

	public ParseTree getONINIT()
	{
		return ONINIT;
	}

	public void setONRELEASED(ParseTree ONRELEASED)
	{
		this.ONRELEASED = ONRELEASED;
	}

	public ParseTree getONRELEASED()
	{
		return ONRELEASED;
	}

	public void setONSTARTDRAGGING(ParseTree ONSTARTDRAGGING)
	{
		this.ONSTARTDRAGGING = ONSTARTDRAGGING;
	}

	public ParseTree getONSTARTDRAGGING()
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
	
	public void setONACTION(ParseTree ONACTION)
	{
		this.ONACTION = ONACTION;
	}

	public ParseTree getONACTION()
	{
		return ONACTION;
	}

	public void setONFOCUSON(ParseTree oNFOCUSON)
	{
		ONFOCUSON = oNFOCUSON;
	}

	public ParseTree getONFOCUSON()
	{
		return ONFOCUSON;
	}

	public void setONFOCUSOFF(ParseTree oNFOCUSOFF)
	{
		ONFOCUSOFF = oNFOCUSOFF;
	}

	public ParseTree getONFOCUSOFF()
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
