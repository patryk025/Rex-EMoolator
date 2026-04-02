package pl.genschu.bloomooemulator.interpreter.variable;

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

    public Value getReturnValue() {
        return returnValue != null ? returnValue : NullValue.INSTANCE;
    }
}
