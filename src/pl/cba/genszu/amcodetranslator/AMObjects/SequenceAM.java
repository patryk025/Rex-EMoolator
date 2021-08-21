package pl.cba.genszu.amcodetranslator.AMObjects;
import pl.cba.genszu.amcodetranslator.interpreter.*;

public class SequenceAM {
	String FILENAME;
	InstructionsBlock ONINIT;
	InstructionsBlock ONFINISHED;
	
	public SequenceAM() {
		this.FILENAME= "";
		this.ONINIT = new InstructionsBlock();
		this.ONFINISHED = new InstructionsBlock();
	}
	
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
		return ONFINISHED;
	}
}
