package pl.genschu.bloomooemulator.interpreter.v1.ast.expressions;

import pl.genschu.bloomooemulator.interpreter.v1.Context;
import pl.genschu.bloomooemulator.interpreter.v1.ast.Expression;

public class ConstantExpression extends Expression {
    private final Object value;

    public ConstantExpression(Object value) {
        this.value = value;
    }

    @Override
    public Object evaluate(Context context) {
        return this.value;
    }
}
