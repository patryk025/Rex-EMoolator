package pl.genschu.bloomooemulator.interpreter.v2.values;

import pl.genschu.bloomooemulator.interpreter.v2.ast.ASTNode;

/**
 * Represents a behaviour (executable code block) as a value.
 * Contains the compiled AST that can be executed.
 */
public record BehaviourValue(String name, ASTNode ast) implements Value {

    public BehaviourValue {
        if (name == null) {
            name = "<anonymous>";
        }
        if (ast == null) {
            throw new IllegalArgumentException("AST cannot be null for behaviour");
        }
    }

    @Override
    public ValueType getType() {
        return ValueType.BEHAVIOUR;
    }

    @Override
    public Object unwrap() {
        return ast;
    }

    @Override
    public String toDisplayString() {
        return "BEHAVIOUR[" + name + "]";
    }
}
