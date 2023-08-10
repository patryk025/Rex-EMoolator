package pl.cba.genszu.amcodetranslator.interpreter.variabletypes;

import org.antlr.v4.runtime.tree.ParseTree;
import pl.cba.genszu.amcodetranslator.interpreter.Variable;

public class StringVariable extends Variable
{
	public StringVariable() {
		super(null);
	}
	
	public StringVariable(String name, Object value) {
		super(name);
	}

    private String VALUE;
    private boolean TOINI;
	private ParseTree ONINIT;
	private ParseTree ONBRUTALCHANGED;
	private ParseTree ONCHANGED;
	private String DESCRIPTION;

	public void SET(String value) {
        this.VALUE = value;
    }

    public String GET() {
        return this.VALUE;
    }

    public int FIND(String needle) {
        return VALUE.indexOf(needle);
    }

    public void ONINIT() {

    }

    public boolean isTOINI() {
        return TOINI;
    }

    public void setTOINI(boolean TOINI) {
        this.TOINI = TOINI;
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
	
	public void setDESCRIPTION(String DESCRIPRION)
	{
		this.DESCRIPTION = DESCRIPRION;
	}

	public String getDESCRIPTION()
	{
		return DESCRIPTION;
	}
}
