package pl.genschu.bloomooemulator.interpreter.exceptions;

public class InvalidMethodParamTypeException extends InterpreterException {
    public InvalidMethodParamTypeException(String expected, String got) {
        super(String.format("Oczekiwano typu %s, a otrzymano %s", expected, got));
    }
}
