package pl.cba.genszu.amcodetranslator.AMObjects;
import pl.cba.genszu.amcodetranslator.interpreter.*;

public class Database {
    //private Struct MODEL;
	private String MODEL;
	private InstructionsBlock ONINIT;

	public void setONINIT(InstructionsBlock ONINIT)
	{
		this.ONINIT = ONINIT;
	}

	public InstructionsBlock getONINIT()
	{
		return ONINIT;
	}
	
	public void setMODEL(String MODEL)
	{
		//this.MODEL = new Struct();
		//this.MODEL.addFIELDS(MODEL);
		this.MODEL = MODEL;
	}

	public String getMODEL()
	{
		return MODEL;
	}
}
