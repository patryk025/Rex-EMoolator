package pl.genschu.bloomooemulator.interpreter.variable;

import com.badlogic.gdx.Gdx;
import pl.genschu.bloomooemulator.interpreter.ast.ASTNode;
import pl.genschu.bloomooemulator.interpreter.values.*;
import pl.genschu.bloomooemulator.loader.BehaviourCodeParser;

import java.util.ArrayList;
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
        Map.entry("RUN", MethodSpec.of((self, args, ctx) -> {
            BehaviourVariable behaviour = (BehaviourVariable) self;
            Value result = ctx.runBehaviour("RUN:" + behaviour.name(), null, behaviour, args);
            return MethodResult.returns(result);
        })),

        Map.entry("RUNC", MethodSpec.of((self, args, ctx) -> {
            BehaviourVariable behaviour = (BehaviourVariable) self;

            if (!checkCondition(behaviour, ctx)) {
                return MethodResult.noReturn();
            }

            Value result = ctx.runBehaviour("RUNC:" + behaviour.name(), null, behaviour, args);
            return MethodResult.returns(result);
        })),

        Map.entry("RUNLOOPED", MethodSpec.of((self, args, ctx) -> {
            BehaviourVariable behaviour = (BehaviourVariable) self;

            if (args.size() < 2) {
                throw new IllegalArgumentException("RUNLOOPED requires at least 2 arguments: start, len [, step, ...]");
            }

            int startVal = args.get(0).toInt().value();
            int endDiff = args.get(1).toInt().value();
            int step = args.size() > 2 ? args.get(2).toInt().value() : 1;
            if (step == 0) step = 1;

            // Extra args beyond start/len/step are passed as $2, $3, ...
            List<Value> extraArgs = args.size() > 3
                    ? args.subList(3, args.size())
                    : List.of();

            // Check condition (if defined)
            ConditionVariable condition = resolveCondition(behaviour, ctx);

            for (int i = startVal; i < startVal + endDiff; i += step) {
                // Re-check condition each iteration if present
                if (condition != null && !evaluateConditionSafe(condition, ctx)) {
                    break;
                }

                List<Value> loopArgs = new ArrayList<>(1 + extraArgs.size());
                loopArgs.add(new IntValue(i)); // $1 = loop counter
                loopArgs.addAll(extraArgs);    // $2, $3, ... = extra args

                ctx.runBehaviour("RUNLOOPED:" + behaviour.name(), null, behaviour, loopArgs);
            }

            return MethodResult.noReturn();
        }))
    );

    // ========================================
    // CONDITION CHECKING
    // ========================================

    private static boolean checkCondition(BehaviourVariable behaviour, MethodContext ctx) {
        ConditionVariable condition = resolveCondition(behaviour, ctx);
        if (condition == null) return true;
        return evaluateConditionSafe(condition, ctx);
    }

    private static ConditionVariable resolveCondition(BehaviourVariable behaviour, MethodContext ctx) {
        if (ctx == null) return null;

        String conditionName = ctx.context().getAttribute(behaviour.name(), "CONDITION");
        if (conditionName == null || conditionName.isEmpty()) return null;

        Variable condVar = ctx.getVariable(conditionName);
        if (condVar instanceof ConditionVariable cond) return cond;

        if (condVar == null) {
            Gdx.app.error("BehaviourVariable", "Condition variable " + conditionName + " not found");
        } else {
            Gdx.app.error("BehaviourVariable", "Variable " + conditionName + " is not a ConditionVariable");
        }
        return null;
    }

    private static boolean evaluateConditionSafe(ConditionVariable cond, MethodContext ctx) {
        try {
            return cond.evaluate(ctx).value();
        } catch (Exception e) {
            Gdx.app.error("BehaviourVariable", "Error evaluating condition: " + e.getMessage());
            return true; // Default to true on error (like v1)
        }
    }

    @Override
    public Variable withSignal(String signalName, SignalHandler handler) {
        Map<String, SignalHandler> newSignals = new java.util.HashMap<>(signals);
        newSignals.put(signalName, handler);
        return new BehaviourVariable(name, ast, newSignals);
    }
}
