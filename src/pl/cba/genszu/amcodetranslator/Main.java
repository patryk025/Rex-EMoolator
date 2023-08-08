package pl.cba.genszu.amcodetranslator;
import pl.cba.genszu.amcodetranslator.interpreter.*;
import pl.cba.genszu.amcodetranslator.interpreter.variabletypes.*;

public class Main
{
    public static void main(String[] args)
	{
		String testExpression = "{[1+2]}";
		
		Variable test1 = new IntegerVariable("test", 1);
		
		System.out.println(test1.getType());
		
		Interpreter interpreter = new Interpreter();
		interpreter.interpret(testExpression);
	}
}
