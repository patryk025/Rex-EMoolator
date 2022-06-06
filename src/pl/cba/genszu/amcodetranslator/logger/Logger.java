package pl.cba.genszu.amcodetranslator.logger;
import java.io.*;

public class Logger
{
	private String uri;
	private PrintWriter pw;
	private static Logger instance;
	
	public Logger() {
		this.uri = "/sdcard/skrypty/logi.txt";
		try
		{
			pw = new PrintWriter(this.uri);
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
	}
	
	public void log(String message) {
		if(pw != null) {
			pw.write(message);
			pw.flush();
		}
		System.out.println(message);
	}
	
	public Logger getInstance() {
		if(instance == null) instance = new Logger();
		return instance;
	}
}
