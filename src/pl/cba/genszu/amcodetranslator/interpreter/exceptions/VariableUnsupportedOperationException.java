package pl.cba.genszu.amcodetranslator.interpreter.exceptions;

import pl.cba.genszu.amcodetranslator.interpreter.*;

public class VariableUnsupportedOperationException extends InterpreterException
{
	public VariableUnsupportedOperationException(Variable... vars) {
		super("...");
	}
}
