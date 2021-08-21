package pl.cba.genszu.amcodetranslator.lexer.stack.exceptions;

public class StackEmptyException extends Exception {
    public StackEmptyException() {
        super("Stos jest pusty");
    }
}
