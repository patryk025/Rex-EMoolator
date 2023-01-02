package pl.cba.genszu.amcodetranslator.utils;

import java.io.*;

public class Logger
{
	static class Level {
		public static int ALL = 15;
		public static int ERROR = 8;
		public static int WARNING = 4;
		public static int INFO = 2;
		public static int DEBUG = 1;
	}
	
	private static int verboseLvl = Level.ALL;
	
	private static boolean checkVerbose(int level) {
		return (verboseLvl & level) == level;
	}
	
	public static void setVerbosity(int verboseLvl) {
		Logger.verboseLvl = verboseLvl;
	}
	
	public static void w(String message) {
		w(message, true);
	}
	
	public static void w(String message, boolean newline) {
		if(checkVerbose(Level.WARNING))
			log("WARNING", message);
	}
	
	public static void e(String message) {
		e(message, true);
	}
	
	public static void e(String message, boolean newline) {
		if(checkVerbose(Level.ERROR))
			log("ERROR", message);
	}
	
	public static void e(Exception exception) {
		if(checkVerbose(Level.ERROR)) {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			exception.printStackTrace(pw);
			
			String[] lines = sw.toString().split("\r?\n");
			for(String line : lines)
				log("EXCEPTION", line);
		}
	}
	
	public static void i(String message) {
		i(message, true);
	}
	
	public static void i(String message, boolean newline) {
		if(checkVerbose(Level.INFO))
			log("INFO", message);
	}
	
	public static void d(String message) {
		d(message, true);
	}
	
	public static void d(String message, boolean newline) {
		if(checkVerbose(Level.DEBUG))
			log("DEBUG", message);
	}
	
	public static void log(String message) {
		log(message, true);
	}
	
	public static void log(String message, boolean newline) {
		System.out.printf("%s"+(newline?"\n":""), new String[] {message});
	}
	
	public static void log(String level, String message) {
		log(level, message, true);
	}
	
	public static void log(String level, String message, boolean newline) {
		System.out.printf("%s: %s"+(newline?"\n":""), new String[] {level, message});
	}
}
