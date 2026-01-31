package pl.genschu.bloomooemulator.interpreter.runtime.effects;

import pl.genschu.bloomooemulator.interpreter.context.Context;
import pl.genschu.bloomooemulator.interpreter.runtime.ASTInterpreter;
import pl.genschu.bloomooemulator.interpreter.runtime.ExecutionResult;
import pl.genschu.bloomooemulator.interpreter.runtime.ReturnResult;
import pl.genschu.bloomooemulator.interpreter.values.NullValue;
import pl.genschu.bloomooemulator.interpreter.values.Value;
import pl.genschu.bloomooemulator.interpreter.variable.BehaviourVariable;
import pl.genschu.bloomooemulator.interpreter.variable.Variable;

import java.util.List;

/**
 * Effect: run a behaviour's AST code.
 *
 * Used by RUN, RUNC, and signal handlers to execute behaviour code.
 */
public final class RunBehaviourEffect implements Effect {
    private final boolean checkCondition;

    /**
     * @param checkCondition if true, checks CONDITION attribute before running (RUNC)
     */
    public RunBehaviourEffect(boolean checkCondition) {
        this.checkCondition = checkCondition;
    }

    @Override
    public Value apply(Context context, Variable self, List<Value> arguments) {
        if (!(self instanceof BehaviourVariable behaviour)) {
            throw new IllegalArgumentException("RunBehaviourEffect can only be applied to BehaviourVariable");
        }

        // TODO: Check CONDITION attribute if checkCondition is true

        // Set up argument locals ($1, $2, etc.)
        if (arguments != null && !arguments.isEmpty()) {
            for (int i = 0; i < arguments.size(); i++) {
                context.exec().setLocal("$" + (i + 1), arguments.get(i));
            }
        }

        // Execute the AST
        ASTInterpreter interpreter = new ASTInterpreter(context);
        ExecutionResult result = interpreter.execute(behaviour.ast());

        // Return the result value
        if (result instanceof ReturnResult returnResult) {
            return returnResult.getValue();
        }

        return result.getValue();
    }
}
