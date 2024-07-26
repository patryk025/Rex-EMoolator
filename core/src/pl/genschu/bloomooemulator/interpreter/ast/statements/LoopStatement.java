package pl.genschu.bloomooemulator.interpreter.ast.statements;

import pl.genschu.bloomooemulator.interpreter.Context;
import pl.genschu.bloomooemulator.interpreter.ast.Statement;
import pl.genschu.bloomooemulator.interpreter.ast.Expression;
import pl.genschu.bloomooemulator.interpreter.variable.types.IntegerVariable;

public class LoopStatement extends Statement {
    private final Expression body;
    private final Expression start;
    private final Expression end;
    private final Expression step;

    public LoopStatement(Expression start, Expression end, Expression step, Expression body) {
        this.start = start;
        this.end = end;
        this.step = step;
        this.body = body;
    }

    @Override
    public void execute(Context context) {
		int startValue = (int) start.evaluate(context);
		int endValue = (int) end.evaluate(context);
		int stepValue = (int) step.evaluate(context);

        IntegerVariable stepVariable = new IntegerVariable("", stepValue, context); // just for fireMethod :)

		context.setVariable("_I_", new IntegerVariable("_I_", startValue, context));
        for (int i = startValue; i < endValue; i += stepValue) {
            Object result = body.evaluate(context);
			if(result instanceof OneBreakStatement) {
				break;
			}
			context.getVariable("_I_", null).fireMethod("ADD", stepVariable);
        }
    }
}
