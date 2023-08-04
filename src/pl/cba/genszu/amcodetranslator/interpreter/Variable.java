package pl.cba.genszu.amcodetranslator.interpreter;

public class Variable<T> {
    private String name;
    private String type;
    private T value;

    public Variable(String name, String type, T value) {
        this.name = name;
        this.type = type;
        this.value = value;
    }

	public void setName(String name)
	{
		this.name = name;
	}

	public String getName()
	{
		return name;
	}

	public void setType(String type)
	{
		this.type = type;
	}

	public String getType()
	{
		return type;
	}

	public void setValue(T value)
	{
		this.value = value;
	}

	public T getValue()
	{
		return value;
	}
}
