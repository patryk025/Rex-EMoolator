package pl.cba.genszu.amcodetranslator.interpreter.exceptions;

public class ClassMethodNotFoundException extends InterpreterException {

    public ClassMethodNotFoundException(String method, String classname) {
        super(String.format("Klasa %s nie posiada metody %s", classname, method));
    }
}
