package pl.cba.genszu.amcodetranslator.AMObjects;

public class Speaking
{
	private String ANIMOFN;
	private String PREFIX;
	private String WAVFN;
	private boolean STARTING;
	private boolean ENDING;
	
	public void setANIMOFN(String aNIMOFN)
	{
		ANIMOFN = aNIMOFN;
	}

	public String getANIMOFN()
	{
		return ANIMOFN;
	}

	public void setPREFIX(String pREFIX)
	{
		PREFIX = pREFIX;
	}

	public String getPREFIX()
	{
		return PREFIX;
	}

	public void setWAVFN(String wAVFN)
	{
		WAVFN = wAVFN;
	}

	public String getWAVFN()
	{
		return WAVFN;
	}

	public void setSTARTING(boolean sTARTING)
	{
		STARTING = sTARTING;
	}

	public boolean isSTARTING()
	{
		return STARTING;
	}

	public void setENDING(boolean eNDING)
	{
		ENDING = eNDING;
	}

	public boolean isENDING()
	{
		return ENDING;
	}
}
