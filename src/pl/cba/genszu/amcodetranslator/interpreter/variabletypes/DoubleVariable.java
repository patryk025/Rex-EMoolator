package pl.cba.genszu.amcodetranslator.interpreter.variabletypes;

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
	
	public void SET(double value) {
        this.VALUE = value;
    }

    public double GET() {
        return this.VALUE;
    }
	
	public boolean isTOINI() {
        return TOINI;
    }

    public void setTOINI(boolean TOINI) {
        this.TOINI = TOINI;
    }
}
