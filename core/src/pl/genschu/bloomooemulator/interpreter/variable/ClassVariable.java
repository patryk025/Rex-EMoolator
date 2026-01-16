package pl.genschu.bloomooemulator.interpreter.variable;

import pl.genschu.bloomooemulator.interpreter.values.StringValue;
import pl.genschu.bloomooemulator.interpreter.values.Value;

import java.util.HashMap;
import java.util.Map;

/**
 * ClassVariable represents a CLASS definition.
 */
public record ClassVariable(
    String name,
    String defPath,                      // Path to class definition file (DEF attribute)
    String basePath,                     // Base class (BASE attribute) - not yet used
    Map<String, SignalHandler> signals
) implements Variable {

    public ClassVariable {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Variable name cannot be null or empty");
        }
        if (signals == null) {
            signals = Map.of();
        } else {
            signals = Map.copyOf(signals);
        }
    }

    // Convenience constructor
    public ClassVariable(String name, String defPath) {
        this(name, defPath, null, Map.of());
    }

    // ========================================
    // INTERFACE IMPLEMENTATION
    // ========================================

    @Override
    public Value value() {
        // Class value is its definition path
        return new StringValue("CLASS[" + (defPath != null ? defPath : "undefined") + "]");
    }

    @Override
    public VariableType type() {
        return VariableType.CLASS;
    }

    @Override
    public Variable withValue(Value newValue) {
        // Class doesn't support direct value assignment
        // DEF and BASE are set via attributes during parsing
        return this;
    }

    @Override
    public Map<String, VariableMethod> methods() {
        return METHODS;
    }

    @Override
    public Variable withSignal(String signalName, SignalHandler handler) {
        Map<String, SignalHandler> newSignals = new HashMap<>(signals);
        newSignals.put(signalName, handler);
        return new ClassVariable(name, defPath, basePath, newSignals);
    }

    // ========================================
    // CLASS-SPECIFIC OPERATIONS
    // ========================================

    /**
     * Sets the DEF attribute (class definition file path).
     *
     * @param path Path to .cnv file
     * @return New ClassVariable with updated path
     */
    public ClassVariable withDefPath(String path) {
        return new ClassVariable(name, path, basePath, signals);
    }

    /**
     * Sets the BASE attribute (base class for inheritance).
     *
     * @param base Base class name
     * @return New ClassVariable with updated base
     */
    public ClassVariable withBasePath(String base) {
        return new ClassVariable(name, defPath, base, signals);
    }

    // ========================================
    // METHODS DEFINITION
    // ========================================

    private static final Map<String, VariableMethod> METHODS = Map.ofEntries(
        Map.entry("NEW", (self, args) -> {
            ClassVariable thisVar = (ClassVariable) self;
            if (args.isEmpty()) {
                throw new IllegalArgumentException("NEW requires at least 1 argument (varName)");
            }

            String varName = args.get(0).toDisplayString();

            // TODO: Implementation requires:
            // 1. Get parent context from somewhere (need context reference)
            // 2. Create new Context with parent
            // 3. Load .cnv file into new context using CNVParser
            // 4. Create InstanceVariable
            // 5. Store instance in parent context
            // 6. Find and run CONSTRUCTOR behaviour with remaining args

            // For now, throw exception indicating this needs integration
            throw new UnsupportedOperationException(
                "CLASS.NEW not yet integrated with CNVParser and Context. " +
                "Implementation requires: " +
                "1. Context reference to store instance, " +
                "2. CNVParser to load class definition, " +
                "3. ExecutionContext to run CONSTRUCTOR"
            );
        }),

        Map.entry("DELETE", (self, args) -> {
            ClassVariable thisVar = (ClassVariable) self;
            if (args.isEmpty()) {
                throw new IllegalArgumentException("DELETE requires at least 1 argument (varName)");
            }

            String varName = args.get(0).toDisplayString();

            // TODO: Implementation requires:
            // 1. Get parent context
            // 2. Get instance by name
            // 3. Find and run DESTRUCTOR behaviour
            // 4. Remove instance from context

            // For now, throw exception indicating this needs integration
            throw new UnsupportedOperationException(
                "CLASS.DELETE not yet integrated with Context. " +
                "Implementation requires: " +
                "1. Context reference to remove instance, " +
                "2. ExecutionContext to run DESTRUCTOR"
            );
        })
    );

    @Override
    public String toString() {
        return "Class[" + name + " def=" + (defPath != null ? defPath : "undefined") + "]";
    }
}
