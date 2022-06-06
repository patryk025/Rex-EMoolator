package pl.cba.genszu.amcodetranslator.AMObjects;

import pl.cba.genszu.amcodetranslator.interpreter.*;

public class Expression
{
	private String OPERAND1;
	private String OPERATOR;
	private String OPERAND2;
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
