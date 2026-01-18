package pl.genschu.bloomooemulator.builders;

import pl.genschu.bloomooemulator.interpreter.v1.Context;
import pl.genschu.bloomooemulator.interpreter.factories.LegacyVariableFactory;
import pl.genschu.bloomooemulator.interpreter.v1.variable.Variable;

/**
 * Builder for creating test contexts with v1 (legacy) infrastructure.
 *
 * This is for tests that still use the old v1 Variable system.
 * For new tests, use {@link ContextBuilder} instead.
 *
 * @deprecated Use {@link ContextBuilder} for new tests
 */
@Deprecated(since = "2.0")
public final class LegacyContextBuilder {
    private final Context ctx = new Context();

    /**
     * Adds a variable using type string and value (old factory method).
     *
     * @param type Variable type (INTEGER, DOUBLE, STRING, etc.)
     * @param name Variable name
     * @param value Variable value
     * @return this builder
     */
    public LegacyContextBuilder withFactory(String type, String name, Object value) {
        Variable v = LegacyVariableFactory
                .createVariable(type, name, value, ctx);
        ctx.setVariable(name, v);
        return this;
    }

    /**
     * Adds a variable directly.
     *
     * @param v Variable to add
     * @return this builder
     */
    public LegacyContextBuilder withVariable(Variable v) {
        ctx.setVariable(v.getName(), v);
        return this;
    }

    /**
     * Builds the legacy context.
     *
     * @return the configured v1 context
     */
    public Context build() {
        return ctx;
    }
}
