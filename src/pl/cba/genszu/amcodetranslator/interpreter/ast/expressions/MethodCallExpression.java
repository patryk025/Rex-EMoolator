package pl.cba.genszu.amcodetranslator.interpreter.ast.expressions;

import pl.cba.genszu.amcodetranslator.interpreter.Context;
import pl.cba.genszu.amcodetranslator.interpreter.Variable;
import pl.cba.genszu.amcodetranslator.interpreter.ast.Expression;

public class MethodCallExpression extends Expression {
    private final Expression pointerExpression;
    private final String methodName;
    private final Expression[] arguments;

    public MethodCallExpression(Expression pointerExpression, String methodName, Expression... arguments) {
        this.pointerExpression = pointerExpression;
        this.methodName = methodName;
        this.arguments = arguments;
    }

    @Override
    public Object evaluate() {
        return null;
    }

    @Override
    public Object evaluate(Context context) {
        Object result = pointerExpression.evaluate(context);
        if (!(result instanceof Variable)) {
            throw new RuntimeException("Method call target must be a variable: " + result);
        }
        Variable targetVariable = (Variable) result;
        return callMethod(targetVariable, methodName);
    }

    private Object callMethod(Variable variable, String methodName) {
        // TODO: implement method call with arguments
        variable.fireFunction(methodName);
        throw new RuntimeException("Unknown method: " + methodName);
    }
}
