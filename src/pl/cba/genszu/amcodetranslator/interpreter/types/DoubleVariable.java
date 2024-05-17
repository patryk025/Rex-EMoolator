package pl.cba.genszu.amcodetranslator.interpreter.types;

import pl.cba.genszu.amcodetranslator.interpreter.Variable;

public class DoubleVariable extends Variable {
	public DoubleVariable(String name, Object value) {
		super(name);
        if(value instanceof String) {
            try {
                this.SET(Double.parseDouble((String) value));
            }
            catch (NumberFormatException e) {
                this.SET(0.0d);
            }
        }
        else if(value instanceof Double || value instanceof Integer) {
            this.SET((double) value);
        }
	}

	private double VALUE;
    private boolean TOINI;

	public void ADD(double add)
	{
		this.VALUE += add;
	}
	
	public void SET(double value) {
        this.VALUE = value;
    }

    public double GET() {
        return this.VALUE;
    }

    public int toInt() {
        return (int) this.VALUE;
    }

    public boolean toBool() {
        return this.VALUE != 0;
    }

    public String toStringVariable() {
        return String.valueOf(this.VALUE);
    }

    public Variable convert(String type) {
        if(type.equals("INTEGER")) {
            return new IntegerVariable(this.getName(), this.toInt());
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
	
	public boolean isTOINI() {
        return TOINI;
    }

    public void setTOINI(boolean TOINI) {
        this.TOINI = TOINI;
    }
}
