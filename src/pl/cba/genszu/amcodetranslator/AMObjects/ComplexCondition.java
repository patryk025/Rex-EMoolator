package pl.cba.genszu.amcodetranslator.AMObjects;
import pl.cba.genszu.amcodetranslator.interpreter.*;

public class ComplexCondition
{
	private String CONDITION1;
	private String OPERATOR;
	private String CONDITION2;
	private String DESCRIPTION;
	private InstructionsBlock ONRUNTIMEFAILED;
	private InstructionsBlock ONRUNTIMESUCCESS;

	public void setDESCRIPTION(String DESCRIPTION)
	{
		this.DESCRIPTION = DESCRIPTION;
	}

	public String getDESCRIPTION()
	{
		return DESCRIPTION;
	}

	public void setONRUNTIMEFAILED(InstructionsBlock ONRUNTIMEFAILED)
	{
		this.ONRUNTIMEFAILED = ONRUNTIMEFAILED;
	}

	public InstructionsBlock getONRUNTIMEFAILED()
	{
		return ONRUNTIMEFAILED;
	}

	public void setONRUNTIMESUCCESS(InstructionsBlock ONRUNTIMESUCCESS)
	{
		this.ONRUNTIMESUCCESS = ONRUNTIMESUCCESS;
	}

	public InstructionsBlock getONRUNTIMESUCCESS()
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
