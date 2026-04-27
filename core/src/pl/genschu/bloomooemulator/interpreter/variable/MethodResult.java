package pl.genschu.bloomooemulator.interpreter.variable;

import pl.genschu.bloomooemulator.interpreter.runtime.BreakResult;
import pl.genschu.bloomooemulator.interpreter.runtime.ExecutionResult;
import pl.genschu.bloomooemulator.interpreter.values.NullValue;
import pl.genschu.bloomooemulator.interpreter.values.Value;

/**
 * Result of a method call on a Variable.
 */
public record MethodResult(
    Value returnValue,
    ControlFlow controlFlow
) {

    public enum ControlFlow { NONE, BREAK, ONE_BREAK }

    public MethodResult(Value returnValue) {
        this(returnValue, ControlFlow.NONE);
    }

    public static MethodResult returns(Value v) {
        return new MethodResult(v, ControlFlow.NONE);
    }

    public static MethodResult noReturn() {
        return new MethodResult(NullValue.INSTANCE, ControlFlow.NONE);
    }

    public static MethodResult breakAll() {
        return new MethodResult(NullValue.INSTANCE, ControlFlow.BREAK);
    }

    public static MethodResult oneBreak() {
        return new MethodResult(NullValue.INSTANCE, ControlFlow.ONE_BREAK);
    }

    /**
     * Converts a behaviour ExecutionResult into a MethodResult for propagation
     * across procedure boundaries. {@link BreakResult} becomes {@link ControlFlow#BREAK}
     * so it keeps climbing the procedure tree; everything else becomes a normal return.
     */
    public static MethodResult fromExecution(ExecutionResult execResult) {
        if (execResult instanceof BreakResult) {
            return MethodResult.breakAll();
        }
        return MethodResult.returns(execResult.getValue());
    }

    public Value getReturnValue() {
        return returnValue != null ? returnValue : NullValue.INSTANCE;
    }
}
