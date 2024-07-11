package pl.genschu.bloomooemulator.interpreter.exceptions;

import pl.genschu.bloomooemulator.interpreter.variable.Variable;

public class VariableUnsupportedOperationException extends InterpreterException
{
	public VariableUnsupportedOperationException(Variable var, String method) {
		super(String.format("Nieobsługiwana operacja %s na zmiennej typu %s", method, var.getType()));
	}

	public VariableUnsupportedOperationException(Variable var1, Variable var2, String operation) {
		super(String.format("Niekompatybilne typy zmiennych do wykonania operacji %s (otrzymano %s, %s)", operation, var1.getType(), var2.getType()));
	}

	public VariableUnsupportedOperationException(Variable var1, String var2type, String operation) {
		super(String.format("Niekompatybilne typy zmiennych do wykonania operacji %s (otrzymano %s, %s)", operation, var1.getType(), var2type));
	}
}
