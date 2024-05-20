package pl.cba.genszu.amcodetranslator.interpreter.ast.expressions;

import pl.cba.genszu.amcodetranslator.interpreter.Context;
import pl.cba.genszu.amcodetranslator.interpreter.ast.Expression;

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
