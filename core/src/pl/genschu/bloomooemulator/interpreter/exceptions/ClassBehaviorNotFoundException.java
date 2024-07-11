package pl.genschu.bloomooemulator.interpreter.exceptions;

public class ClassBehaviorNotFoundException extends InterpreterException {
    public ClassBehaviorNotFoundException(String behaviour, String classname) {
        super(String.format("Klasa %s nie posiada zarejestrowanej obsługi sygnału dla wiadomości '%s'", classname, behaviour));
    }
}
