package pl.cba.genszu.amcodetranslator.interpreter.variabletypes;

import org.antlr.v4.runtime.tree.ParseTree;
import pl.cba.genszu.amcodetranslator.interpreter.Variable;


public class ComplexConditionVariable extends Variable {
	public ComplexConditionVariable(String name, Object value) {
		super(name);
	}

	private String CONDITION1;
	private String OPERATOR;
	private String CONDITION2;
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

	public void setCONDITION1(String CONDITION1)
	{
		this.CONDITION1 = CONDITION1;
	}

	public String getCONDITION1()
	{
		return CONDITION1;
	}

	public void setOPERATOR(String OPERATOR)
	{
		this.OPERATOR = OPERATOR;
	}

	public String getOPERATOR()
	{
		return OPERATOR;
	}

	public void setCONDITION2(String CONDITION2)
	{
		this.CONDITION2 = CONDITION2;
	}

	public String getCONDITION2()
	{
		return CONDITION2;
	}}
