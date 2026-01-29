package pl.genschu.bloomooemulator.interpreter.variable;

import pl.genschu.bloomooemulator.interpreter.context.Context;
import pl.genschu.bloomooemulator.interpreter.values.StringValue;
import pl.genschu.bloomooemulator.interpreter.values.Value;
import pl.genschu.bloomooemulator.interpreter.variable.capabilities.HasInstanceContext;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * InstanceVariable represents an instance of a CLASS.
 */
public record InstanceVariable(
    String name,
    Context instanceContext,
    Map<String, SignalHandler> signals
) implements Variable, HasInstanceContext {

    public InstanceVariable {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Variable name cannot be null or empty");
        }
        if (instanceContext == null) {
            throw new IllegalArgumentException("Instance context cannot be null");
        }
        if (signals == null) {
            signals = Map.of();
        } else {
            signals = Map.copyOf(signals);
        }
    }

    // Convenience constructor without signals
    public InstanceVariable(String name, Context instanceContext) {
        this(name, instanceContext, Map.of());
    }

    // ========================================
    // INTERFACE IMPLEMENTATION
    // ========================================

    @Override
    public Value value() {
        // I will return instance value as its name
        return new StringValue("INSTANCE[" + name + "]");
    }

    @Override
    public VariableType type() {
        return VariableType.INSTANCE;
    }

    @Override
    public Variable withValue(Value newValue) {
        // Instance doesn't support direct value assignment
        // Modify instance variables through the instance context
        return this;
    }

    @Override
    public Map<String, MethodSpec> methods() {
        // Instance methods are dynamically resolved from the instance context
        // Return empty map - method calls are handled by callMethod() override
        return Map.of();
    }

    @Override
    public Variable withSignal(String signalName, SignalHandler handler) {
        Map<String, SignalHandler> newSignals = new HashMap<>(signals);
        newSignals.put(signalName, handler);
        return new InstanceVariable(name, instanceContext, newSignals);
    }

    /**
     * Override callMethod to delegate to instance context.
     *
     * When calling obj.method(), we look for BEHAVIOUR "method" in instance context
     * and call its RUN method.
     */
    @Override
    public MethodResult callMethod(String methodName, List<Value> arguments) {
        // Look for BEHAVIOUR variable with the method name in instance context
        Variable behaviour = instanceContext.getVariable(methodName);

        if (behaviour == null) {
            throw new IllegalArgumentException("Method not found in instance: " + methodName);
        }

        if (behaviour.type() != VariableType.BEHAVIOUR) {
            throw new IllegalArgumentException("Variable " + methodName + " is not a BEHAVIOUR");
        }

        // Call RUN method on the behaviour
        return behaviour.callMethod("RUN", arguments);
    }

    // ========================================
    // HasInstanceContext CAPABILITY
    // ========================================

    @Override
    public Context getInstanceContext() {
        return instanceContext;
    }

    @Override
    public String toString() {
        return "Instance[" + name + " with context]";
    }
}
