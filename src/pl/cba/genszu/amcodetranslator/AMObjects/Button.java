package pl.cba.genszu.amcodetranslator.AMObjects;
import pl.cba.genszu.amcodetranslator.interpreter.*;

public class Button {
    private String RECT;
    private boolean ENABLE;
    private boolean VISIBLE;
    private boolean DRAGGABLE;
    private String GFXSTANDARD;
    private String GFXONCLICK;
    private String GFXONMOVE;
	private String ONACTION; //Behaviour
	private InstructionsBlock ONFOCUSON;
	private InstructionsBlock ONFOCUSOFF;

	
    public void SETRECT(String rect) {
        this.RECT = rect;
        /*TODO: rozpatrzenie dwóch przypadków
        * 700,0,800,200 - prostokąt (do przeskalowania)
        * string - nazwa Image*/
    }

    public void ENABLE() {
        this.ENABLE = true;
        this.VISIBLE = true;
        //TODO: podpięcie do silnika
    }

    public void DISABLEBUTVISIBLE() {
        this.ENABLE = false;
        this.VISIBLE = true;
        //TODO: podpięcie do silnika
    }

    public void DISABLE() {
        this.ENABLE = false;
        //TODO: podpięcie do silnika
    }

    /*event methods*/
    public void ONACTION() {

    }

    /*internal methods*/
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
	
	public void setONACTION(String oNACTION)
	{
		ONACTION = oNACTION;
	}

	public String getONACTION()
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
	
}
