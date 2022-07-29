package pl.cba.genszu.amcodetranslator.utils;

public class Logger
{
	public static void w(String message) {
		log("WARNING", message);
	}
	
	public static void e(String message) {
		log("ERROR", message);
	}
	
	public static void i(String message) {
		log("INFO", message);
	}
	
	public static void d(String message) {
		log("DEBUG", message);
	}
	
	private static void log(String level, String message) {
		System.out.printf("%s: %s", new String[] {level, message});
	}
}
