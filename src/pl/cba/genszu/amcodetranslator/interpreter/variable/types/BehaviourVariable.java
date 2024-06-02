package pl.cba.genszu.amcodetranslator.interpreter.variable.types;

import org.antlr.v4.runtime.tree.ParseTree;
import pl.cba.genszu.amcodetranslator.interpreter.variable.Variable;


public class BehaviourVariable extends Variable {
	public BehaviourVariable(String name, Object value) {
		super(name);
	}

	private ParseTree CODE;
	private String CONDITION;
	private String DESCRIPTION;

	public void setDESCRIPTION(String DESCRIPTION)
	{
		this.DESCRIPTION = DESCRIPTION;
	}

	public String getDESCRIPTION()
	{
		return DESCRIPTION;
	}
	
    /*public void RUN() {

    }*/

	public void setCODE(ParseTree CODE)
	{
		this.CODE = CODE;
	}

	public ParseTree getCODE()
	{
		return CODE;
	}
	
	public void setCONDITION(String CONDITION)
	{
		this.CONDITION = CONDITION;
	}

	public String getCONDITION()
	{
		return CONDITION;
	}
}
