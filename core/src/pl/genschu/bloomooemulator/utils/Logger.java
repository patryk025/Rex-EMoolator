package pl.genschu.bloomooemulator.utils;

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
	private static PrintWriter logFile = null;
	
	private static boolean checkVerbose(int level) {
		return (verboseLvl & level) == level;
	}
	
	public static void setVerbosity(int verboseLvl) {
		Logger.verboseLvl = verboseLvl;
	}
	
	public static void setLogFile(String path) {
		try
		{
			Logger.logFile = new PrintWriter(path);
		}
		catch (FileNotFoundException e)
		{
			e("File not found, didn't changed path for logger");
		}
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
			
			log("EXCEPTION", sw.toString());
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
		String[] lines = message.split("\r?\n");
		
		for(String line : lines)
			log(line, true);
	}
	
	public static void log(String message, boolean newline) {
		System.out.printf("%s"+(newline?"\n":""), message);
		if(logFile != null) {
			logFile.printf("%s"+(newline?"\n":""), message);
			logFile.flush();
		}
	}
	
	public static void log(String level, String message) {
		String[] lines = message.split("\r?\n");

		for(String line : lines)
			log(level, line, true);
	}
	
	public static void log(String level, String message, boolean newline) {
		System.out.printf("%s: %s"+(newline?"\n":""), level, message);
		if(logFile != null) {
			logFile.printf("%s: %s"+(newline?"\n":""), level, message);
			logFile.flush();
		}
	}
}
