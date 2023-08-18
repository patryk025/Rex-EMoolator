package pl.cba.genszu.amcodetranslator.interpreter.exceptions;

public class VariableNotFoundException extends InterpreterException {
    public VariableNotFoundException(String variableName) {
        super(String.format("Zmienna %s jest niezdefiniowana", variableName));
    }
}
