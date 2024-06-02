package pl.cba.genszu.amcodetranslator.interpreter.types;

import org.antlr.v4.runtime.tree.ParseTree;
import pl.cba.genszu.amcodetranslator.interpreter.variable.Variable;


public class IntegerVariable extends Variable {
	public IntegerVariable() {
		super(null);
	}
	
	public IntegerVariable(String name, Object value) {
		super(name);
		if(value instanceof String) {
			String valueString = value.toString();
			if(valueString.startsWith("\"") && valueString.endsWith("\"")) {
				valueString = valueString.substring(1, valueString.length() - 1);
			}
			try {
				this.SET(Integer.parseInt(valueString));
			}
			catch (NumberFormatException e) {
				this.SET(0);
			}
		}
		else if(value instanceof Integer) {
			this.SET((Integer) value);
		}
		else if(value instanceof Double) {
			this.SET(((Double) value).intValue());
		}
		else if(value instanceof Boolean) {
			this.SET((Boolean) value ? 1 : 0);
		}
		else if(value instanceof Long) {
			this.SET(((Long) value).intValue());
		}
		else {
			this.SET(0);
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
	
	public void ADD(int add) {
		this.VALUE += add;
	}

	public String toStringVariable() {
		return String.valueOf(this.VALUE);
	}

	public boolean toBool() {
		return this.VALUE != 0;
	}

	public double toDouble() {
		return (double) this.VALUE;
	}

	public Variable convert(String type) {
		if(type.equals("DOUBLE")) {
			return new DoubleVariable(this.getName(), this.toDouble());
		}
		else if(type.equals("BOOL")) {
			return new BoolVariable(this.getName(), this.toBool());
		}
		else if(type.equals("STRING")) {
			return new StringVariable(this.getName(), this.toStringVariable());
		}
		else {
			return this;
		}
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
