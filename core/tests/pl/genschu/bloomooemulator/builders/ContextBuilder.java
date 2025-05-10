package pl.genschu.bloomooemulator.builders;

import pl.genschu.bloomooemulator.interpreter.Context;
import pl.genschu.bloomooemulator.interpreter.factories.VariableFactory;
import pl.genschu.bloomooemulator.interpreter.variable.Variable;

public final class ContextBuilder {
    private final Context ctx = new Context();

    public ContextBuilder withPrimitive(String type, String name, Object value) {
        Variable v = VariableFactory
                .createVariable(type, name, value, ctx);
        ctx.setVariable(name, v);
        return this;
    }

    public ContextBuilder withVariable(Variable v) {
        ctx.setVariable(v.getName(), v);
        return this;
    }

    public Context build() { return ctx; }
}

