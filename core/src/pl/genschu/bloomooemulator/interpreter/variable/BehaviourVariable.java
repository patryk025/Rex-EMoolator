package pl.genschu.bloomooemulator.interpreter.variable;

import pl.genschu.bloomooemulator.interpreter.ast.ASTNode;
import pl.genschu.bloomooemulator.interpreter.runtime.effects.RunBehaviourEffect;
import pl.genschu.bloomooemulator.interpreter.values.NullValue;
import pl.genschu.bloomooemulator.interpreter.values.Value;
import pl.genschu.bloomooemulator.loader.BehaviourCodeParser;

import java.util.List;
import java.util.Map;

/**
 * BehaviourVariable stores executable code (AST).
 **/
public record BehaviourVariable(
    String name,
    ASTNode ast,
    Map<String, SignalHandler> signals
) implements Variable {

    public BehaviourVariable {
        if (name == null) {
            name = "";  // Allow empty name for anonymous behaviours
        }
        if (ast == null) {
            throw new IllegalArgumentException("AST cannot be null for behaviour");
        }
        if (signals == null) {
            signals = Map.of();
        } else {
            signals = Map.copyOf(signals);
        }
    }

    @Override
    public Value value() {
        return NullValue.INSTANCE;
    }

    @Override
    public VariableType type() {
        return VariableType.BEHAVIOUR;
    }

    @Override
    public Variable withValue(Value newValue) {
        throw new UnsupportedOperationException("Cannot set value on BEHAVIOUR");
    }

    public Variable withScript(String code) {
        // fire up a parser to create a new AST from the script
        ASTNode ast = BehaviourCodeParser.parseCode(code, name());
        return new BehaviourVariable(name, ast, signals);
    }

    @Override
    public Map<String, MethodSpec> methods() {
        return METHODS;
    }

    // ========================================
    // METHODS DEFINITION
    // ========================================

    private static final Map<String, MethodSpec> METHODS = Map.ofEntries(
        Map.entry("RUN", MethodSpec.of((self, args) -> {
            return MethodResult.effects(List.of(new RunBehaviourEffect(false)));
        })),

        Map.entry("RUNC", MethodSpec.of((self, args) -> {
            // RUNC checks CONDITION attribute before running
            return MethodResult.effects(List.of(new RunBehaviourEffect(true)));
        })),

        Map.entry("RUNLOOPED", MethodSpec.of((self, args) -> {
            // TODO: Implement looped execution
            // RUNLOOPED runs the behaviour in a loop until break/return
            return MethodResult.effects(List.of(new RunBehaviourEffect(false)));
        }))
    );

    @Override
    public Variable withSignal(String signalName, SignalHandler handler) {
        Map<String, SignalHandler> newSignals = new java.util.HashMap<>(signals);
        newSignals.put(signalName, handler);
        return new BehaviourVariable(name, ast, newSignals);
    }
}
