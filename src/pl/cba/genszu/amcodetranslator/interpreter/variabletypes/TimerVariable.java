package pl.cba.genszu.amcodetranslator.interpreter.variabletypes;

import org.antlr.v4.runtime.tree.ParseTree;
import pl.cba.genszu.amcodetranslator.interpreter.Variable;


public class TimerVariable extends Variable {
	public TimerVariable(String name, Object value) {
		super(name, value);
	}

    private int ELAPSE; //prawdopodobnie w ms
	private boolean ENABLED;
	private int TICKS;
	private ParseTree ONINIT;
	private ParseTree ONTICK;

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

	public void setONINIT(ParseTree ONINIT)
	{
		this.ONINIT = ONINIT;
	}

	public ParseTree getONINIT()
	{
		return ONINIT;
	}

	public void setONTICK(ParseTree ONTICK)
	{
		this.ONTICK = ONTICK;
	}

	public ParseTree getONTICK()
	{
		return ONTICK;
	}
}
