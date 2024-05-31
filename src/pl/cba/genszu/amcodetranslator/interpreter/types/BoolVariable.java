package pl.cba.genszu.amcodetranslator.interpreter.types;

import org.antlr.v4.runtime.tree.ParseTree;
import pl.cba.genszu.amcodetranslator.interpreter.Variable;


public class BoolVariable extends Variable {
	public BoolVariable() {
		super(null);
	}
	
	public BoolVariable(String name, Object value) {
		super(name);
		if(value instanceof Boolean) {
			this.SET((Boolean) value);
		}
		else {
			String valueString = value.toString();
			if(valueString.startsWith("\"") && valueString.endsWith("\"")) {
				valueString = valueString.substring(1, valueString.length() - 1);
			}
			this.SET(valueString.equals("TRUE"));
		}
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
		boolean oldValue = this.VALUE;
        this.VALUE = value;
		/*
		if(oldValue != this.VALUE)
			//this.ONCHANGED;
		else
			//this.ONBRUTALCHANGED

		 */
    }

    public boolean GET() {
        return this.VALUE;
    }

	public int toInt() {
		return this.VALUE ? 1 : 0;
	}

	public double toDouble() {
		return this.VALUE ? 1.0 : 0.0;
	}

	public String toStringVariable() {
		return this.VALUE ? "TRUE" : "FALSE";
	}

	public Variable convert(String type) {
		if(type.equals("INTEGER")) {
			return new IntegerVariable(this.getName(), this.toInt());
		}
		else if(type.equals("DOUBLE")) {
			return new DoubleVariable(this.getName(), this.toDouble());
		}
		else if(type.equals("STRING")) {
			return new StringVariable(this.getName(), this.toStringVariable());
		}
		else {
			return this;
		}
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
