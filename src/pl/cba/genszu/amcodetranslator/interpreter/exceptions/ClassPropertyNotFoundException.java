package pl.cba.genszu.amcodetranslator.interpreter.exceptions;

public class ClassPropertyNotFoundException extends Exception
{
	public ClassPropertyNotFoundException(String property, String classname) {
		super(String.format("Klasa %s nie posiada pola %s", classname, property));
	}
}
