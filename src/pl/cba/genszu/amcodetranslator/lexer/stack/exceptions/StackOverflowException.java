package pl.cba.genszu.amcodetranslator.lexer.stack.exceptions;

public class StackOverflowException extends Exception {
    public StackOverflowException() {
        super("Stos jest pełny");
    }
}
