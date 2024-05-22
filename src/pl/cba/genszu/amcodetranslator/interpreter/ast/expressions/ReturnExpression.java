package pl.cba.genszu.amcodetranslator.interpreter.ast.expressions;

import pl.cba.genszu.amcodetranslator.interpreter.*;
import pl.cba.genszu.amcodetranslator.interpreter.ast.*;

public class ReturnExpression extends Expression
 {
    private final Expression returnValue;

    public ReturnExpression(Expression returnValue) {
        this.returnValue = returnValue;
    }

    @Override
    public Object evaluate(Context context) {
        return returnValue.evaluate(context);
    }
}
