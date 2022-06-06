package pl.cba.genszu.amcodetranslator.AMObjects;

public class DoubleAM
{
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
