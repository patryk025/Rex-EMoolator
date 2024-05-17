package pl.cba.genszu.amcodetranslator.interpreter.types;

import org.antlr.v4.runtime.tree.ParseTree;
import pl.cba.genszu.amcodetranslator.interpreter.Variable;
import java.util.*;


public class SequenceVariable extends Variable {
	public SequenceVariable(String name, Object value) {
		super(name);
	}

	String FILENAME;
	ParseTree ONINIT;
	ParseTree ONFINISHED;
	ParseTree ONSTARTED;
	String DESCRIPTION;
	boolean VISIBLE;
	HashMap<String, SpeakingVariable> speakingMap;
	HashMap<String, SequenceVariable> subsequencesMap;
	HashMap<String, SimpleVariable> simplesMap;
	HashMap<String, SceneVariable> scenesMap;
	HashMap<String, String> SEQEVENT; //No, it's String. NumberFormatException is here :/
	
	/*public SequenceAM() {
		this.FILENAME= "";
		this.ONINIT = new ParseTree();
		this.ONFINISHED = new ParseTree();
	}*/
	
	public void setFILENAME(String FILENAME)
	{
		this.FILENAME = FILENAME;
	}

	public String getFILENAME()
	{
		return FILENAME;
	}

	public void setONINIT(ParseTree ONINIT)
	{
		this.ONINIT = ONINIT;
	}

	public ParseTree getONINIT()
	{
		return ONINIT;
	}

	public void setONFINISHED(ParseTree ONFINISHED)
	{
		this.ONFINISHED = ONFINISHED;
	}

	public ParseTree getONFINISHED()
	{
		return ONFINISHED;
	}
	
	public void setONSTARTED(ParseTree ONSTARTED)
	{
		this.ONSTARTED = ONSTARTED;
	}

	public ParseTree getONSTARTED()
	{
		return ONSTARTED;
	}

	public void setDESCRIPTION(String DESCRIPTION)
	{
		this.DESCRIPTION = DESCRIPTION;
	}

	public String getDESCRIPTION()
	{
		return DESCRIPTION;
	}

	public void setVISIBLE(boolean VISIBLE)
	{
		this.VISIBLE = VISIBLE;
	}

	public boolean isVISIBLE()
	{
		return VISIBLE;
	}
	
	public void addSpeaking(String name, SpeakingVariable sp) {
		if(speakingMap == null) speakingMap = new HashMap<>();
		speakingMap.put(name, sp);
	}
	
	public void addSequence(String name, SequenceVariable seq) {
		if(subsequencesMap == null) subsequencesMap = new HashMap<>();
		subsequencesMap.put(name, seq);
	}
	
	public void addSimple(String name, SimpleVariable smpl) {
		if(simplesMap == null) simplesMap = new HashMap<>();
		simplesMap.put(name, smpl);
	}
	
	public void addScene(String name, SceneVariable scen) {
		if(scenesMap == null) scenesMap = new HashMap<>();
		scenesMap.put(name, scen);
	}
	
	public void addSEQEVENT(String name, String val) {
		if(SEQEVENT == null) SEQEVENT = new HashMap<>();
		SEQEVENT.put(name, val);
	}
}
