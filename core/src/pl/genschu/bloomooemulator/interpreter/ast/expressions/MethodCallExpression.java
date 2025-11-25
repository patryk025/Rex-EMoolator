package pl.genschu.bloomooemulator.interpreter.ast.expressions;

import com.badlogic.gdx.Gdx;
import pl.genschu.bloomooemulator.interpreter.Context;
import pl.genschu.bloomooemulator.interpreter.variable.Variable;
import pl.genschu.bloomooemulator.interpreter.ast.Expression;
import pl.genschu.bloomooemulator.interpreter.exceptions.ClassMethodNotFoundException;
import pl.genschu.bloomooemulator.interpreter.variable.types.*;

import java.util.Arrays;

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
        Gdx.app.log("MethodCallExpression", methodName + " - arguments: ");
        for (int i = 0; i < argumentsLength; i++) {
            arguments[i] = getVariableFromObject(this.arguments[i], context);
            if(arguments[i] == null) {
                Gdx.app.error("MethodCallExpression", "Method call error in variable " + variable.getName() + " of class " + variable.getType() + ": Cannot find argument " + i + " in method call " + methodName);
            }
            else if(arguments[i] instanceof ExpressionVariable) {
                Gdx.app.log("MethodCallExpression", methodName + " - argument " + i + ": expression to calculate " + arguments[i].getName());
            }
            else if(arguments[i] instanceof VectorVariable) {
                Gdx.app.log("MethodCallExpression", methodName + " - argument " + i + ": vector " + arguments[i].getName() + " (" + Arrays.toString(((VectorVariable) arguments[i]).getComponents()) + ")");
            }
            else if(!(arguments[i] instanceof StringVariable
                    || arguments[i] instanceof IntegerVariable
                    || arguments[i] instanceof DoubleVariable
                    || arguments[i] instanceof BoolVariable)) {
                Gdx.app.log("MethodCallExpression", methodName + " - argument " + i + ": string " + arguments[i].getName());
                arguments[i] = new StringVariable("", arguments[i].getName(), context);
            }
            else {
                Gdx.app.log("MethodCallExpression", methodName + " - argument " + i + ": " + arguments[i].getValue().toString());
            }
        }
        try {
            return variable.fireMethod(methodName, (Object[]) arguments);
        } catch (Exception e) {
            Gdx.app.error("MethodCallExpression", "Method call error in variable " + variable.getName() + " of class " + variable.getType() + ": " + e.getMessage(), e);
            return null;
        }
    }
}
