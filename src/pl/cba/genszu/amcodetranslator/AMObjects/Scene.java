package pl.cba.genszu.amcodetranslator.AMObjects;

public class Scene {
    private String DESCRIPTION;
    private String CREATIONTIME;
    private String LASTMODIFYTIME;
    private String AUTHOR;
    private String VERSION;
    private String PATH;
    private String BACKGROUND;

    public String getDESCRIPTION() {
        return DESCRIPTION;
    }

    public void setDESCRIPTION(String DESCRIPTION) {
        this.DESCRIPTION = DESCRIPTION;
    }

    public String getCREATIONTIME() {
        return CREATIONTIME;
    }

    public void setCREATIONTIME(String CREATIONTIME) {
        this.CREATIONTIME = CREATIONTIME;
    }

    public String getLASTMODIFYTIME() {
        return LASTMODIFYTIME;
    }

    public void setLASTMODIFYTIME(String LASTMODIFYTIME) {
        this.LASTMODIFYTIME = LASTMODIFYTIME;
    }

    public String getAUTHOR() {
        return AUTHOR;
    }

    public void setAUTHOR(String AUTHOR) {
        this.AUTHOR = AUTHOR;
    }

    public String getVERSION() {
        return VERSION;
    }

    public void setVERSION(String VERSION) {
        this.VERSION = VERSION;
    }

    public String getPATH() {
        return PATH;
    }

    public void setPATH(String PATH) {
        this.PATH = PATH;
    }

    public String getBACKGROUND() {
        return BACKGROUND;
    }

    public void setBACKGROUND(String BACKGROUND) {
        this.BACKGROUND = BACKGROUND;
    }
}
