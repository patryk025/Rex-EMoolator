package pl.genschu.bloomooemulator.interpreter.variable;

/**
 * Describes how an argument should be treated before passing to a variable method.
 */
public enum ArgKind {
    /**
     * Use the evaluated Value as-is.
     */
    VALUE,
    /**
     * Treat value as a variable reference if possible (String/VariableRef/VariableValue).
     */
    VAR_REF,
    /**
     * Require a variable reference (VariableValue/VariableRef/VariableNode).
     */
    VAR
}
