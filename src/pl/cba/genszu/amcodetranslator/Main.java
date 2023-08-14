package pl.cba.genszu.amcodetranslator;
import pl.cba.genszu.amcodetranslator.interpreter.*;
import pl.cba.genszu.amcodetranslator.interpreter.variabletypes.*;

import java.util.ArrayList;
import java.util.List;

public class Main
{
    public static void main(String[] args)
	{
		//String testExpression = "{[2+2*2]}";
		//String testExpression = "{[TEST+ITERATOR*2]}";
		//String testExpression = "{@IF(\"ITERATOR'3\",{[TEST+ITERATOR*2]}, {[2+3]});}";
		//String testExpression = "{@IF(\"ITERATOR>'1&&ITERATOR<'10\",{[TEST+ITERATOR*2+\"_\"+ITERATOR@5]}, {[2+3]});}";
		String testExpression = "{@LOOP({[_I_+1]}, 0, 5, 1);}";

		List<Variable> vars = new ArrayList<>();
		Variable test1 = new StringVariable("TEST", "ANIMO_");
		Variable test2 = new IntegerVariable("ITERATOR", 3);

		vars.add(test1);
		vars.add(test2);

		//System.out.println(test1.getType());
		
		Interpreter interpreter = new Interpreter(vars);
		interpreter.interpret(testExpression);
	}
}
