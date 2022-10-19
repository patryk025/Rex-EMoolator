package pl.cba.genszu.amcodetranslator.AMObjects;
import java.util.*;
import pl.cba.genszu.amcodetranslator.interpreter.*;

public class SequenceAM {
	String FILENAME;
	InstructionsBlock ONINIT;
	InstructionsBlock ONFINISHED;
	InstructionsBlock ONSTARTED;
	String DESCRIPTION;
	boolean VISIBLE;
	HashMap<String, Speaking> speakingMap;
	HashMap<String, SequenceAM> subsequencesMap;
	
	/*public SequenceAM() {
		this.FILENAME= "";
		this.ONINIT = new InstructionsBlock();
		this.ONFINISHED = new InstructionsBlock();
	}*/
	
	public void setFILENAME(String FILENAME)
	{
		this.FILENAME = FILENAME;
	}

	public String getFILENAME()
	{
		return FILENAME;
	}

	public void setONINIT(InstructionsBlock ONINIT)
	{
		this.ONINIT = ONINIT;
	}

	public InstructionsBlock getONINIT()
	{
		return ONINIT;
	}

	public void setONFINISHED(InstructionsBlock ONFINISHED)
	{
		this.ONFINISHED = ONFINISHED;
	}

	public InstructionsBlock getONFINISHED()
	{
		if(ONFINISHED == null) 
			ONFINISHED = new InstructionsBlock();
		return ONFINISHED;
	}
	
	public void setONSTARTED(InstructionsBlock ONSTARTED)
	{
		this.ONSTARTED = ONSTARTED;
	}

	public InstructionsBlock getONSTARTED()
	{
		if(ONSTARTED == null) 
			ONSTARTED = new InstructionsBlock();
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
	
	public void addSpeaking(String name, Speaking sp) {
		if(speakingMap == null) speakingMap = new HashMap<>();
		speakingMap.put(name, sp);
	}
	
	public void addSequence(String name, SequenceAM seq) {
		if(subsequencesMap == null) subsequencesMap = new HashMap<>();
		subsequencesMap.put(name, seq);
	}
}
