package pl.cba.genszu.amcodetranslator.interpreter;

public class InterpreterResult
{
	private String type; //później z niego zrobię enuma
	private String value; //na razie taki generyczny string
	
	public InterpreterResult(String type, String value) {
		this.type = type;
		this.value = value;
	}

	public void setType(String type)
	{
		this.type = type;
	}

	public String getType()
	{
		return type;
	}

	public void setValue(String value)
	{
		this.value = value;
	}

	public String getValue()
	{
		return value;
	}
}
