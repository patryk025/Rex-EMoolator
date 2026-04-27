package pl.genschu.bloomooemulator.interpreter.errors;

import pl.genschu.bloomooemulator.interpreter.runtime.ExecutionContext;

/**
 * Exception thrown during AST execution when an error occurs.
 * Contains full stack trace information for debugging.
 */
public class InterpreterException extends RuntimeException {
    private final ExecutionContext context;
    private final SourceLocation location;

    public InterpreterException(
        String message,
        ExecutionContext context,
        SourceLocation location
    ) {
        super(formatMessage(message, context, location));
        this.context = context;
        this.location = location;
    }

    public InterpreterException(
        String message,
        ExecutionContext context,
        SourceLocation location,
        Throwable cause
    ) {
        super(formatMessage(message, context, location), cause);
        this.context = context;
        this.location = location;
    }

    public ExecutionContext getContext() {
        return context;
    }

    public SourceLocation getLocation() {
        return location;
    }

    private static String formatMessage(
        String message,
        ExecutionContext context,
        SourceLocation location
    ) {
        StringBuilder sb = new StringBuilder();

        sb.append("Runtime error");

        if (location != null && location != SourceLocation.UNKNOWN) {
            sb.append(" at ").append(location);
        }

        sb.append(": ").append(message);

        if (context != null) {
            sb.append("\n\n").append(context.getStackTrace());
        }

        return sb.toString();
    }
}
