package pl.genschu.bloomooemulator.interpreter.v1.ast.expressions;

import pl.genschu.bloomooemulator.interpreter.v1.Context;
import pl.genschu.bloomooemulator.interpreter.v1.ast.Expression;
import pl.genschu.bloomooemulator.interpreter.v1.variable.Variable;
import pl.genschu.bloomooemulator.interpreter.v1.variable.types.StructVariable;

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
