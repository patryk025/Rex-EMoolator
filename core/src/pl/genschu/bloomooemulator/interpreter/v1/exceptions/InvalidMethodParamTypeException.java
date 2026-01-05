package pl.genschu.bloomooemulator.interpreter.v1.exceptions;

public class InvalidMethodParamTypeException extends InterpreterException {
    public InvalidMethodParamTypeException(String expected, String got) {
        super(String.format("Oczekiwano typu %s, a otrzymano %s", expected, got));
    }
}
