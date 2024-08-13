package pl.genschu.bloomooemulator.interpreter.ast.expressions;

import pl.genschu.bloomooemulator.interpreter.Context;
import pl.genschu.bloomooemulator.interpreter.ast.Expression;

public class StringExpression extends Expression {
    private final String value;

    public StringExpression(String value) {
        this.value = value;
    }

    @Override
    public String evaluate(Context context) {
        return this.value;
    }
}
