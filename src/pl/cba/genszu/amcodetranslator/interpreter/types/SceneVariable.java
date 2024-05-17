package pl.cba.genszu.amcodetranslator.interpreter.types;

import pl.cba.genszu.amcodetranslator.interpreter.Variable;

public class SceneVariable extends Variable {
	public SceneVariable(String name, Object value) {
		super(name);
	}

    private String DESCRIPTION;
    private String CREATIONTIME;
    private String LASTMODIFYTIME;
    private String AUTHOR;
    private String VERSION;
    private String PATH;
    private String BACKGROUND;
	private String DLLS;
	private String MUSIC;
	
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
	
	public void setDLLS(String DLLS)
	{
		this.DLLS = DLLS;
	}

	public String getDLLS()
	{
		return DLLS;
	}

	public void setMUSIC(String MUSIC)
	{
		this.MUSIC = MUSIC;
	}

	public String getMUSIC()
	{
		return MUSIC;
	}
}
