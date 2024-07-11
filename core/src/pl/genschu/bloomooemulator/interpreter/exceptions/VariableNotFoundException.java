package pl.genschu.bloomooemulator.interpreter.exceptions;

public class VariableNotFoundException extends InterpreterException {
    public VariableNotFoundException(String variableName) {
        super(String.format("Zmienna %s jest niezdefiniowana", variableName));
    }
}
