package pl.cba.genszu.amcodetranslator.interpreter.variabletypes;

import org.antlr.v4.runtime.tree.ParseTree;
import pl.cba.genszu.amcodetranslator.interpreter.Variable;


public class DatabaseVariable extends Variable {
	public DatabaseVariable(String name, Object value) {
		super(name, value);
	}

    //private Struct MODEL;
	private String MODEL;
	private ParseTree ONINIT;

	public void setONINIT(ParseTree ONINIT)
	{
		this.ONINIT = ONINIT;
	}

	public ParseTree getONINIT()
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
