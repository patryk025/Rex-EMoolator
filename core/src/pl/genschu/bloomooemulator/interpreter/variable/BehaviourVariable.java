package pl.genschu.bloomooemulator.interpreter.variable;

import pl.genschu.bloomooemulator.interpreter.ast.ASTNode;
import pl.genschu.bloomooemulator.interpreter.values.NullValue;
import pl.genschu.bloomooemulator.interpreter.values.Value;
import pl.genschu.bloomooemulator.loader.BehaviourCodeParser;

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
        Map.entry("RUN", MethodSpec.of((self, args, ctx) -> {
            BehaviourVariable behaviour = (BehaviourVariable) self;
            Value result = ctx.runBehaviour("RUN:" + behaviour.name(), null, behaviour, args);
            return MethodResult.returns(result);
        })),

        Map.entry("RUNC", MethodSpec.of((self, args, ctx) -> {
            // TODO: Check CONDITION attribute before running
            BehaviourVariable behaviour = (BehaviourVariable) self;
            Value result = ctx.runBehaviour("RUNC:" + behaviour.name(), null, behaviour, args);
            return MethodResult.returns(result);
        })),

        Map.entry("RUNLOOPED", MethodSpec.of((self, args, ctx) -> {
            // TODO: Implement looped execution (run in a loop until break/return)
            BehaviourVariable behaviour = (BehaviourVariable) self;
            Value result = ctx.runBehaviour("RUNLOOPED:" + behaviour.name(), null, behaviour, args);
            return MethodResult.returns(result);
        }))
    );

    @Override
    public Variable withSignal(String signalName, SignalHandler handler) {
        Map<String, SignalHandler> newSignals = new java.util.HashMap<>(signals);
        newSignals.put(signalName, handler);
        return new BehaviourVariable(name, ast, newSignals);
    }
}
