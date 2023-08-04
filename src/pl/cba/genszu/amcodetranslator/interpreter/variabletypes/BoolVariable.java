package pl.cba.genszu.amcodetranslator.interpreter.variabletypes;

import org.antlr.v4.runtime.tree.ParseTree;
import pl.cba.genszu.amcodetranslator.interpreter.Variable;


public class BoolVariable extends Variable {
	public BoolVariable(String name, Object value) {
		super(name, value);
	}

    /*public Bool(boolean VALUE) {
        this.VALUE = VALUE;
    }*/

    private boolean VALUE;
	private String DESCRIPTION;
	private ParseTree ONINIT;
	private ParseTree ONBRUTALCHANGED;
	private ParseTree ONCHANGED;;
	private boolean TOINI;

	public void setDESCRIPTION(String DESCRIPTION)
	{
		this.DESCRIPTION = DESCRIPTION;
	}

	public String getDESCRIPTION()
	{
		return DESCRIPTION;
	}

    public void SET(boolean value) {
        this.VALUE = value;
    }

    public boolean GET() {
        return this.VALUE;
    }
	
	public void setONINIT(ParseTree ONINIT)
	{
		this.ONINIT = ONINIT;
	}

	public ParseTree getONINIT()
	{
		return ONINIT;
	}

	public void setONBRUTALCHANGED(ParseTree ONBRUTALCHANGED)
	{
		this.ONBRUTALCHANGED = ONBRUTALCHANGED;
	}

	public ParseTree getONBRUTALCHANGED()
	{
		return ONBRUTALCHANGED;
	}

	public void setONCHANGED(ParseTree ONCHANGED)
	{
		this.ONCHANGED = ONCHANGED;
	}

	public ParseTree getONCHANGED()
	{
		return ONCHANGED;
	}

	public void setTOINI(boolean TOINI)
	{
		this.TOINI = TOINI;
	}

	public boolean isTOINI()
	{
		return TOINI;
	}
	
}
