package pl.genschu.bloomooemulator.interpreter.ast.expressions;

import pl.genschu.bloomooemulator.interpreter.Context;
import pl.genschu.bloomooemulator.interpreter.ast.Expression;

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
