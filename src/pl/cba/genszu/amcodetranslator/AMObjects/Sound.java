package pl.cba.genszu.amcodetranslator.AMObjects;

public class Sound {
    private String FILENAME;
    private boolean PRELOAD;
    private boolean RELEASE;
    private boolean FLUSHAFTERPLAYED;
    private String ONSTARTED; //nazwa obiektu Behaviour
    private String ONFINISHED; //nazwa obiektu Behaviour

    /*methods of Sound*/
    public void PLAY() {

    }
    public void SETVOLUME(int volume) {
        /*TODO: podpięcie do silnika*/
    }

    /*internal methods*/
    public String getFILENAME() {
        return FILENAME;
    }

    public void setFILENAME(String FILENAME) {
        this.FILENAME = FILENAME;
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

    public String getONSTARTED() {
        return ONSTARTED;
    }

    public void setONSTARTED(String ONSTARTED) {
        this.ONSTARTED = ONSTARTED;
    }

    public String getONFINISHED() {
        return ONFINISHED;
    }

    public void setONFINISHED(String ONFINISHED) {
        this.ONFINISHED = ONFINISHED;
    }

    public boolean isFLUSHAFTERPLAYED() {
        return FLUSHAFTERPLAYED;
    }

    public void setFLUSHAFTERPLAYED(boolean FLUSHAFTERPLAYED) {
        this.FLUSHAFTERPLAYED = FLUSHAFTERPLAYED;
    }
}
