package pl.cba.genszu.amcodetranslator.interpreter.types;

import org.antlr.v4.runtime.tree.ParseTree;
import pl.cba.genszu.amcodetranslator.interpreter.Variable;



public class ConditionVariable extends Variable {
	public ConditionVariable(String name, Object value) {
		super(name);
	}

	private ParseTree OPERAND1;
	private String OPERATOR;
	private ParseTree OPERAND2;
	private String DESCRIPTION;
	private ParseTree ONRUNTIMEFAILED;
	private ParseTree ONRUNTIMESUCCESS;

	public void setDESCRIPTION(String DESCRIPTION)
	{
		this.DESCRIPTION = DESCRIPTION;
	}

	public String getDESCRIPTION()
	{
		return DESCRIPTION;
	}

	public void setONRUNTIMEFAILED(ParseTree ONRUNTIMEFAILED)
	{
		this.ONRUNTIMEFAILED = ONRUNTIMEFAILED;
	}

	public ParseTree getONRUNTIMEFAILED()
	{
		return ONRUNTIMEFAILED;
	}

	public void setONRUNTIMESUCCESS(ParseTree ONRUNTIMESUCCESS)
	{
		this.ONRUNTIMESUCCESS = ONRUNTIMESUCCESS;
	}

	public ParseTree getONRUNTIMESUCCESS()
	{
		return ONRUNTIMESUCCESS;
	}
	

	public void setOPERAND1(ParseTree OPERAND1)
	{
		this.OPERAND1 = OPERAND1;
	}

	public ParseTree getOPERAND1()
	{
		return OPERAND1;
	}

	public void setOPERATOR(String OPERATOR)
	{
		this.OPERATOR = OPERATOR;
	}

	public String getOPERATOR()
	{
		return OPERATOR;
	}

	public void setOPERAND2(ParseTree OPERAND2)
	{
		this.OPERAND2 = OPERAND2;
	}

	public ParseTree getOPERAND2()
	{
		return OPERAND2;
	}
}
