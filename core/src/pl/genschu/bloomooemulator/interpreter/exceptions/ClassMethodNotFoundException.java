package pl.genschu.bloomooemulator.interpreter.exceptions;

import java.util.List;

public class ClassMethodNotFoundException extends InterpreterException {

    public ClassMethodNotFoundException(String method, List<String> paramTypes) {
        super(String.format("No method found with name: %s and parameter types: %s", method, paramTypes.toString()));
    }
}
