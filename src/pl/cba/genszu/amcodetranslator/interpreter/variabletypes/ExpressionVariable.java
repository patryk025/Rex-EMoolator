package pl.cba.genszu.amcodetranslator.interpreter.variabletypes;

import org.antlr.v4.runtime.tree.ParseTree;
import pl.cba.genszu.amcodetranslator.interpreter.Variable;

public class ExpressionVariable extends Variable {
	public ExpressionVariable(String name, Object value) {
		super(name, value);
	}

	private String OPERAND1;
	private String OPERATOR;
	private String OPERAND2;
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


	public void setOPERAND1(String OPERAND1)
	{
		this.OPERAND1 = OPERAND1;
	}

	public String getOPERAND1()
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

	public void setOPERAND2(String OPERAND2)
	{
		this.OPERAND2 = OPERAND2;
	}

	public String getOPERAND2()
	{
		return OPERAND2;
	}
}
