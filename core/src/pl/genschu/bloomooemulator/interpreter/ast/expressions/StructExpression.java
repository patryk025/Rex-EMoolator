package pl.genschu.bloomooemulator.interpreter.ast.expressions;

import pl.genschu.bloomooemulator.interpreter.Context;
import pl.genschu.bloomooemulator.interpreter.ast.Expression;
import pl.genschu.bloomooemulator.interpreter.variable.Variable;
import pl.genschu.bloomooemulator.interpreter.variable.types.StructVariable;

import static pl.genschu.bloomooemulator.interpreter.util.VariableHelper.getVariableFromObject;

public class StructExpression extends Expression {
    private String structName;
    private String structColumn;

    public StructExpression(String structName, String structColumn) {
        this.structName = structName;
        this.structColumn = structColumn;
    }

    @Override
    public Object evaluate(Context context) {
        Variable targetVariable = context.getVariable(structName);
        if (targetVariable == null) {
            throw new RuntimeException("Target struct not defined: " + structName);
        }
        return ((StructVariable) targetVariable).getField(structColumn);
    }
}
