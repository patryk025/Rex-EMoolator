package pl.genschu.bloomooemulator.interpreter.ast.expressions;

import pl.genschu.bloomooemulator.interpreter.Context;
import pl.genschu.bloomooemulator.interpreter.ast.Expression;

import static pl.genschu.bloomooemulator.interpreter.util.VariableHelper.getVariableFromObject;

public class ReturnExpression extends Expression
 {
    private final Expression returnValue;

    public ReturnExpression(Expression returnValue) {
        this.returnValue = returnValue;
    }

    @Override
    public Object evaluate(Context context) {
        return getVariableFromObject(returnValue, context, true);
    }
}
