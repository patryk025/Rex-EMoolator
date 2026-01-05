package pl.genschu.bloomooemulator.interpreter.v1.ast.statements;

import pl.genschu.bloomooemulator.interpreter.v1.Context;
import pl.genschu.bloomooemulator.interpreter.v1.ast.Statement;
import pl.genschu.bloomooemulator.interpreter.v1.ast.Expression;
import pl.genschu.bloomooemulator.interpreter.v1.ast.expressions.ConstantExpression;
import pl.genschu.bloomooemulator.interpreter.v1.variable.Variable;
import pl.genschu.bloomooemulator.interpreter.v1.variable.types.IntegerVariable;
import pl.genschu.bloomooemulator.utils.ArgumentsHelper;

public class LoopStatement extends Statement {
    private final Expression body;
    private final Expression start;
    private final Expression diff;
    private final Expression step;

    public LoopStatement(Expression start, Expression end, Expression step, Expression body) {
        this.start = start;
        this.diff = end;
        this.step = step;
        this.body = body;
    }

    @Override
    public void execute(Context context) {
		int startValue = getIntValue(start.evaluate(context), context);
		int diffValue = getIntValue(diff.evaluate(context), context);
		int stepValue = getIntValue(step.evaluate(context), context);

        IntegerVariable stepVariable = new IntegerVariable("", stepValue, context); // just for fireMethod :)

		context.setVariable("_I_", new IntegerVariable("_I_", startValue, context));
        for (int i = startValue; i < startValue+diffValue; i += stepValue) {
            Object result = body.evaluate(context);
			if(result instanceof OneBreakStatement) {
				break;
			}
			context.getVariable("_I_").fireMethod("ADD", stepVariable);
        }
    }

    private int getIntValue(Object object, Context context) {
        Object value = object;
        if (value instanceof Expression) {
            value = ((Expression) value).evaluate(context);
        }
        if (value instanceof ConstantExpression) {
            value = ((ConstantExpression) value).evaluate(context);
        }
        if (value instanceof Variable) {
            return ArgumentsHelper.getInteger((Variable) value);
        }
        if (value instanceof Integer) {
            return (Integer) value;
        }
        if (value instanceof Double) {
            return ((Double) value).intValue();
        }
        if (value instanceof String) {
            String raw = value.toString();
            try {
                return Integer.parseInt(raw);
            } catch (NumberFormatException e) {
                Variable var = context.getVariable(raw);
                if (var != null) {
                    return ArgumentsHelper.getInteger(var);
                }
            }
        }
        return 0;
    }
}
