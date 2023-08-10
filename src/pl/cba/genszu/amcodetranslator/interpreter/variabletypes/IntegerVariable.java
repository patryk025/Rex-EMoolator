package pl.cba.genszu.amcodetranslator.interpreter.variabletypes;

import org.antlr.v4.runtime.tree.ParseTree;
import pl.cba.genszu.amcodetranslator.interpreter.Variable;


public class IntegerVariable extends Variable {
	public IntegerVariable() {
		super(null);
	}
	
	public IntegerVariable(String name, Object value) {
		super(name);
		if(value instanceof String) {
			try {
				this.SET(Integer.parseInt((String) value));
			}
			catch (NumberFormatException e) {
				this.SET(0);
			}
		}
		else if(value instanceof Integer || value instanceof Double) {
			this.SET((Integer) value);
		}
	}

    private int VALUE;
    private boolean TOINI;
	private ParseTree ONINIT;
	private ParseTree ONBRUTALCHANGED;
	private ParseTree ONCHANGED;
	private String DESCRIPTION;
	private ParseTree ONSIGNAL;
	private String VARTYPE;

	public void setVARTYPE(String vARTYPE)
	{
		VARTYPE = vARTYPE;
	}

	public String getVARTYPE()
	{
		return VARTYPE;
	}

    public void SET(int value) {
        this.VALUE = value;
    }

    public int GET() {
        return this.VALUE;
    }

    public void SWITCH(int i1, int i2) {
        if(this.VALUE == i1) this.VALUE = i2;
        else this.VALUE = i1;
    }

    public void setONINIT(ParseTree ONINIT) {
		this.ONINIT = ONINIT;
    }
	
	public ParseTree getONINIT() {
		return this.ONINIT;
	}
	
	public void setONBRUTALCHANGED(ParseTree ONBRUTALCHANGED)
	{
		this.ONBRUTALCHANGED = ONBRUTALCHANGED;
	}
	
	public ParseTree getONBRUTALCHANGED()
	{
		return this.ONBRUTALCHANGED;
	}
	
	public void setONCHANGED(ParseTree ONCHANGED)
	{
		this.ONCHANGED = ONCHANGED;
	}

	public ParseTree getONCHANGED()
	{
		return this.ONCHANGED;
	}
	
	public void setONSIGNAL(ParseTree ONSIGNAL)
	{
		this.ONSIGNAL = ONSIGNAL;
	}

	public ParseTree getONSIGNAL()
	{
		return this.ONSIGNAL;
	}

    public boolean isTOINI() {
        return TOINI;
    }

    public void setTOINI(boolean TOINI) {
        this.TOINI = TOINI;
    }
	
	public void setDESCRIPTION(String DESCRIPRION)
	{
		this.DESCRIPTION = DESCRIPRION;
	}

	public String getDESCRIPTION()
	{
		return DESCRIPTION;
	}
}
