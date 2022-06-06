package pl.cba.genszu.amcodetranslator.AMObjects;
import pl.cba.genszu.amcodetranslator.interpreter.*;

public class Timer {
    private int ELAPSE; //prawdopodobnie w ms
	private boolean ENABLED;
	private int TICKS;
	private InstructionsBlock ONINIT;
	private InstructionsBlock ONTICK;

    public int getELAPSE() {
        return ELAPSE;
    }

    public void setELAPSE(int ELAPSE) {
        this.ELAPSE = ELAPSE;
    }
	
	public void setENABLED(boolean ENABLED)
	{
		this.ENABLED = ENABLED;
	}

	public boolean isENABLED()
	{
		return ENABLED;
	}

	public void setTICKS(int TICKS)
	{
		this.TICKS = TICKS;
	}

	public int getTICKS()
	{
		return TICKS;
	}

	public void setONINIT(InstructionsBlock ONINIT)
	{
		this.ONINIT = ONINIT;
	}

	public InstructionsBlock getONINIT()
	{
		return ONINIT;
	}

	public void setONTICK(InstructionsBlock ONTICK)
	{
		this.ONTICK = ONTICK;
	}

	public InstructionsBlock getONTICK()
	{
		if(ONTICK == null) 
			ONTICK = new InstructionsBlock();
		return ONTICK;
	}
}
