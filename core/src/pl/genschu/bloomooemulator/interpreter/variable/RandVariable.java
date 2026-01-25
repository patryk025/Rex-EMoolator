package pl.genschu.bloomooemulator.interpreter.variable;

import pl.genschu.bloomooemulator.interpreter.helpers.ArgumentHelper;
import pl.genschu.bloomooemulator.interpreter.values.*;

import java.util.*;

/**
 * RandVariable provides random number generation functionality.
 **/
public record RandVariable(
    String name,
    Map<String, SignalHandler> signals
) implements Variable {

    public RandVariable {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Variable name cannot be null or empty");
        }
        if (signals == null) {
            signals = Map.of();
        } else {
            signals = Map.copyOf(signals);
        }
    }

    public RandVariable(String name) {
        this(name, Map.of());
    }

    public RandVariable(String name, long seed) {
        this(name, Map.of());
    }

    // ========================================
    // INTERFACE IMPLEMENTATION
    // ========================================

    @Override
    public Value value() {
        // Return a random value
        Random random = new Random();
        return new IntValue(random.nextInt());
    }

    @Override
    public VariableType type() {
        return VariableType.RAND;
    }

    @Override
    public Variable withValue(Value newValue) {
        // RandVariable doesn't support direct value assignment
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
        return new RandVariable(name, newSignals);
    }

    // ========================================
    // HELPER METHODS
    // ========================================

    /**
     * Generates a random integer in range [offset, offset + range).
     */
    private static int getRandom(int offset, int range) {
        if (range <= 0) return offset;
        Random random = new Random();
        return random.nextInt(range) + offset;
    }

    /**
     * Generates a random double in range [0.0, 1.0).
     */
    private static double getRandomDouble() {
        Random random = new Random();
        return random.nextDouble();
    }

    // ========================================
    // METHODS DEFINITION
    // ========================================

    private static final Map<String, VariableMethod> METHODS = Map.ofEntries(
        Map.entry("GET", (self, args) -> {
            if (args.isEmpty()) {
                throw new IllegalArgumentException("GET requires at least 1 argument");
            }

            int offset = 0;
            int range;
            if (args.size() >= 2) {
                offset = ArgumentHelper.getInt(args.get(0));
                range = ArgumentHelper.getInt(args.get(1));
            } else {
                range = ArgumentHelper.getInt(args.get(0));
            }

            int result = getRandom(offset, range);
            return MethodResult.noChange(new IntValue(result));
        })

        // NOTE: GETPLENTY requires context to access target ArrayVariable
        // and should be handled at a higher level
    );

    // ========================================
    // CONVENIENT ACCESSORS
    // ========================================

    @Override
    public String toString() {
        return "RandVariable[" + name + "]";
    }
}
