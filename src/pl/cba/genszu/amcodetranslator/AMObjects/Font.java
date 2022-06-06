package pl.cba.genszu.amcodetranslator.AMObjects;

public class Font
{
	private String family;
	private String style;
	private int size;

	public void DEF(String[] split)
	{
		this.family = split[0];
		this.style = split[1];
		this.size = Integer.parseInt(split[2]);
	}
}
