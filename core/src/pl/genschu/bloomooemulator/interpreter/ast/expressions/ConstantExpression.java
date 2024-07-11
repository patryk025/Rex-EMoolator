package pl.genschu.bloomooemulator.interpreter.ast.expressions;

import pl.genschu.bloomooemulator.interpreter.Context;
import pl.genschu.bloomooemulator.interpreter.ast.Expression;

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
