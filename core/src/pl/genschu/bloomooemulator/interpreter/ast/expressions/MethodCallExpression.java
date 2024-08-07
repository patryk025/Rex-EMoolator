package pl.genschu.bloomooemulator.interpreter.ast.expressions;

import com.badlogic.gdx.Gdx;
import pl.genschu.bloomooemulator.interpreter.Context;
import pl.genschu.bloomooemulator.interpreter.variable.Variable;
import pl.genschu.bloomooemulator.interpreter.ast.Expression;
import pl.genschu.bloomooemulator.interpreter.exceptions.ClassMethodNotFoundException;

import static pl.genschu.bloomooemulator.interpreter.util.VariableHelper.getVariableFromObject;

public class MethodCallExpression extends Expression {
    private final Expression targetVariable;
    private final String methodName;
    private final Expression[] arguments;

    public MethodCallExpression(Expression targetVariable, String methodName, Expression... arguments) {
        this.targetVariable = targetVariable;
        this.methodName = methodName;
        this.arguments = arguments;
    }

    @Override
    public Object evaluate(Context context) {
        Variable targetVariable = getVariableFromObject(this.targetVariable, context);
        if (targetVariable == null) {
            throw new RuntimeException("Method call target must be a variable");
        }
        return callMethod(targetVariable, methodName, context);
    }

    private Object callMethod(Variable variable, String methodName, Context context) {
        Gdx.app.log("MethodCallExpression", "Calling method " + methodName + " on " + variable.getName() + " (" + variable.getType() + ")");
        int argumentsLength = this.arguments.length;
        Variable[] arguments = new Variable[argumentsLength];
        Gdx.app.log("MethodCallExpression", "Arguments: ");
        for (int i = 0; i < argumentsLength; i++) {
            arguments[i] = getVariableFromObject(this.arguments[i], context);
            Gdx.app.log("MethodCallExpression", "Argument " + i + ": " + arguments[i].getValue().toString());
        }
        try {
            return variable.fireMethod(methodName, (Object[]) arguments);
        } catch (ClassMethodNotFoundException | NullPointerException e) {
            Gdx.app.error("MethodCallExpression", "Method call error in class " + variable.getType() + ": " + e.getMessage(), e);
            return null;
        }
    }
}
