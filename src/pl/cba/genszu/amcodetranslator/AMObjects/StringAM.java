package pl.cba.genszu.amcodetranslator.AMObjects;

public class StringAM {
    private String VALUE;
    private boolean TOINI;

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
}
