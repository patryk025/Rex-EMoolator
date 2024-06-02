package pl.cba.genszu.amcodetranslator.interpreter.types;

import pl.cba.genszu.amcodetranslator.interpreter.variable.Variable;

import java.util.Arrays;
import java.util.List;

public class EpisodeVariable extends Variable {
	public EpisodeVariable(String name, Object value) {
		super(name);
	}

    private String DESCRIPTION;
    private String CREATIONTIME;
    private String LASTMODIFYTIME;
    private String AUTHOR;
    private String VERSION;
    private List<String> SCENES;
    private String STARTWITH;
	private String PATH;

    /*methods of Episode*/
    public void GOTO(String scene) {
        /*TODO: podłączenie do silnika*/
    }

    /*internal class methods*/
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

    public List<String> getSCENES() {
        return SCENES;
    }

    public void setSCENES(String SCENES) {
        this.SCENES = Arrays.asList(SCENES.split(","));
    }

    public String getSTARTWITH() {
        return STARTWITH;
    }

    public void setSTARTWITH(String STARTWITH) {
        this.STARTWITH = STARTWITH;
    }
	
	public void setPATH(String PATH)
	{
		this.PATH = PATH;
	}

	public String getPATH()
	{
		return PATH;
	}
}
