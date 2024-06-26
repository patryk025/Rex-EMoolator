package pl.cba.genszu.amcodetranslator.interpreter.ast.expressions;

import pl.cba.genszu.amcodetranslator.interpreter.*;
import pl.cba.genszu.amcodetranslator.interpreter.ast.*;

import static pl.cba.genszu.amcodetranslator.interpreter.util.VariableHelper.getVariableFromObject;

public class ReturnExpression extends Expression
 {
    private final Expression returnValue;

    public ReturnExpression(Expression returnValue) {
        this.returnValue = returnValue;
    }

    @Override
    public Object evaluate(Context context) {
        return getVariableFromObject(returnValue, context);
    }
}
