package pl.cba.genszu.amcodetranslator.interpreter.types;

import org.antlr.v4.runtime.tree.ParseTree;
import pl.cba.genszu.amcodetranslator.interpreter.variable.Variable;

public class SoundVariable extends Variable
{
    public SoundVariable(String name, Object value) {
        super(name);
    }

    private String FILENAME;
    private boolean PRELOAD;
    private boolean RELEASE;
    private boolean FLUSHAFTERPLAYED;
    private ParseTree ONSTARTED; //nazwa obiektu Behaviour
    private ParseTree ONFINISHED; //nazwa obiektu Behaviour
	private ParseTree ONINIT;
	private String DESCRIPTION;

    /*methods of Sound*/
    public void PLAY() {

    }
    public void SETVOLUME(int volume) {
        
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

    public ParseTree getONSTARTED() {
        return ONSTARTED;
    }

    public void setONSTARTED(ParseTree ONSTARTED) {
        this.ONSTARTED = ONSTARTED;
    }

    public ParseTree getONFINISHED() {
        return ONFINISHED;
    }

    public void setONFINISHED(ParseTree ONFINISHED) {
        this.ONFINISHED = ONFINISHED;
    }

    public boolean isFLUSHAFTERPLAYED() {
        return FLUSHAFTERPLAYED;
    }

    public void setFLUSHAFTERPLAYED(boolean FLUSHAFTERPLAYED) {
        this.FLUSHAFTERPLAYED = FLUSHAFTERPLAYED;
    }
	
	public void setDESCRIPTION(String DESCRIPTION)
	{
		this.DESCRIPTION = DESCRIPTION;
	}

	public String getDESCRIPTION()
	{
		return DESCRIPTION;
	}
	
	public void setONINIT(ParseTree ONINIT)
	{
		this.ONINIT = ONINIT;
	}

	public ParseTree getONINIT()
	{
		return ONINIT;
	}
}
