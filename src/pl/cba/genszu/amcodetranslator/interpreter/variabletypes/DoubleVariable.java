package pl.cba.genszu.amcodetranslator.interpreter.variabletypes;

import pl.cba.genszu.amcodetranslator.interpreter.Variable;

public class DoubleVariable extends Variable {
	public DoubleVariable(String name, Object value) {
		super(name, value);
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
