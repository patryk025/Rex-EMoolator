package pl.genschu.bloomooemulator.interpreter.variable;

/**
 * Method spec wrapping a VariableMethod.
 */
public record MethodSpec(
    VariableMethod method
) {
    public static MethodSpec of(VariableMethod method) {
        return new MethodSpec(method);
    }
}
