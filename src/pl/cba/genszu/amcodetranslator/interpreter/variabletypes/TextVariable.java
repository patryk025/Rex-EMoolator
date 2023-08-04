package pl.cba.genszu.amcodetranslator.interpreter.variabletypes;

import org.antlr.v4.runtime.tree.ParseTree;
import pl.cba.genszu.amcodetranslator.interpreter.Variable;


import pl.cba.genszu.amcodetranslator.interpreter.util.*;

public class TextVariable extends Variable {
	public TextVariable(String name, Object value) {
		super(name, value);
	}

	private boolean VISIBLE;
	private String FONT;
	private boolean TOCANVAS;
	private Rect RECT;
	private int PRIORITY;
	private boolean MONITORCOLLISION;
	private boolean MONITORCOLLISIONALPHA;
	private ParseTree ONINIT;
	private String TEXT;
	private String HJUSTIFY;
	private boolean VJUSTIFY;

	public String getHJUSTIFY()
	{
		return HJUSTIFY;
	}

	public boolean isVJUSTIFY()
	{
		return VJUSTIFY;
	}

	public void setVJUSTIFY(boolean value)
	{
		this.VJUSTIFY = value;
	}
	
	public void setHJUSTIFY(String value)
	{
		this.HJUSTIFY = value;
	}

	public void setTEXT(String tEXT)
	{
		TEXT = tEXT;
	}

	public String getTEXT()
	{
		return TEXT;
	}
	
	public void setVISIBLE(boolean VISIBLE)
	{
		this.VISIBLE = VISIBLE;
	}

	public boolean isVISIBLE()
	{
		return VISIBLE;
	}

	public void setFONT(String fONT)
	{
		FONT = fONT;
	}

	public String getFONT()
	{
		return FONT;
	}

	public void setTOCANVAS(boolean tOCANVAS)
	{
		TOCANVAS = tOCANVAS;
	}

	public boolean isTOCANVAS()
	{
		return TOCANVAS;
	}

	public void setRECT(String RECT)
	{
		this.RECT = new Rect(RECT);
	}

	public Rect getRECT()
	{
		return RECT;
	}

	public void setPRIORITY(int pRIORITY)
	{
		PRIORITY = pRIORITY;
	}

	public int getPRIORITY()
	{
		return PRIORITY;
	}

	public void setMONITORCOLLISION(boolean mONITORCOLLISION)
	{
		MONITORCOLLISION = mONITORCOLLISION;
	}

	public boolean isMONITORCOLLISION()
	{
		return MONITORCOLLISION;
	}

	public void setMONITORCOLLISIONALPHA(boolean mONITORCOLLISIONALPHA)
	{
		MONITORCOLLISIONALPHA = mONITORCOLLISIONALPHA;
	}

	public boolean isMONITORCOLLISIONALPHA()
	{
		return MONITORCOLLISIONALPHA;
	}

	public void setONINIT(ParseTree oNINIT)
	{
		ONINIT = oNINIT;
	}

	public ParseTree getONINIT()
	{
		return ONINIT;
	}
}
