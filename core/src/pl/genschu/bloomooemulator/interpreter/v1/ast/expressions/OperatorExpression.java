package pl.genschu.bloomooemulator.interpreter.v1.ast.expressions;

import pl.genschu.bloomooemulator.interpreter.v1.Context;
import pl.genschu.bloomooemulator.interpreter.v1.ast.Expression;

public class OperatorExpression extends Expression {
    private final Object value;

    public OperatorExpression(Object value) {
        this.value = value;
    }

    @Override
    public Object evaluate(Context context) {
        return this.value;
    }
}
