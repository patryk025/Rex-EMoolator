package pl.cba.genszu.amcodetranslator.AMObjects;

public class Image {
    private String FILENAME;
    private boolean TOCANVAS;
    private boolean VISIBLE;
    private int PRIORITY;
    private boolean PRELOAD;
    private boolean RELEASE;
    private boolean MONITORCOLLISION;
    private boolean MONITORCOLLISIONALPHA;

    /*methods of Image*/
    public void SHOW() {
        this.VISIBLE = true;
        /*TODO: podłączenie do silnika*/
    }

    public void HIDE() {
        this.VISIBLE = false;
        /*TODO: podłączenie do silnika*/
    }

    public void SETPRIORITY(int priority) {
        this.PRIORITY = priority;
        /*TODO: podłączenie do silnika*/
    }

    /*internal method*/
    public String getFILENAME() {
        return FILENAME;
    }

    public void setFILENAME(String FILENAME) {
        this.FILENAME = FILENAME;
    }

    public boolean isTOCANVAS() {
        return TOCANVAS;
    }

    public void setTOCANVAS(boolean TOCANVAS) {
        this.TOCANVAS = TOCANVAS;
    }

    public boolean isVISIBLE() {
        return VISIBLE;
    }

    public void setVISIBLE(boolean VISIBLE) {
        this.VISIBLE = VISIBLE;
    }

    public boolean isPRELOAD() {
        return PRELOAD;
    }

    public void setPRELOAD(boolean PRELOAD) {
        this.PRELOAD = PRELOAD;
    }

    public boolean isRELEASE() {
        return RELEASE;
    }

    public void setRELEASE(boolean RELEASE) {
        this.RELEASE = RELEASE;
    }

    public boolean isMONITORCOLLISION() {
        return MONITORCOLLISION;
    }

    public void setMONITORCOLLISION(boolean MONITORCOLLISION) {
        this.MONITORCOLLISION = MONITORCOLLISION;
    }

    public boolean isMONITORCOLLISIONALPHA() {
        return MONITORCOLLISIONALPHA;
    }

    public void setMONITORCOLLISIONALPHA(boolean MONITORCOLLISIONALPHA) {
        this.MONITORCOLLISIONALPHA = MONITORCOLLISIONALPHA;
    }

    public int getPRIORITY() {
        return PRIORITY;
    }
}
