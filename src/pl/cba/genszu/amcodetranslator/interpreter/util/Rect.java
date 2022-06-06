package pl.cba.genszu.amcodetranslator.interpreter.util;

public class Rect
{
	public int x;
	public int y;
	public int x2;
	public int y2;
	
	public Rect(String values) {
		String[] vals = values.split(",");
		this.x = Integer.parseInt(vals[0]);
		this.y = Integer.parseInt(vals[1]);
		this.x2 = Integer.parseInt(vals[2]);
		this.y2 = Integer.parseInt(vals[3]);
	}
}
