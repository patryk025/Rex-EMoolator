package pl.genschu.bloomooemulator.interpreter.variable;

import com.badlogic.gdx.Gdx;
import pl.genschu.bloomooemulator.interpreter.ast.ASTNode;
import pl.genschu.bloomooemulator.interpreter.runtime.BreakResult;
import pl.genschu.bloomooemulator.interpreter.runtime.ExecutionResult;
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
            ExecutionResult result = ctx.runBehaviour("RUN:" + behaviour.name(), null, behaviour, args);
            return MethodResult.fromExecution(result);
        })),

        Map.entry("RUNC", MethodSpec.of((self, args, ctx) -> {
            BehaviourVariable behaviour = (BehaviourVariable) self;

            if (!checkCondition(behaviour, ctx)) {
                return MethodResult.noReturn();
            }

            ExecutionResult result = ctx.runBehaviour("RUNC:" + behaviour.name(), null, behaviour, args);
            return MethodResult.fromExecution(result);
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
            Variable condition = resolveCondition(behaviour, ctx);

            for (int i = startVal; i < startVal + endDiff; i += step) {
                // Re-check condition each iteration if present
                if (condition != null && !evaluateConditionSafe(condition, ctx)) {
                    break;
                }

                List<Value> loopArgs = new ArrayList<>(1 + extraArgs.size());
                loopArgs.add(new IntValue(i)); // $1 = loop counter
                loopArgs.addAll(extraArgs);    // $2, $3, ... = extra args

                ExecutionResult iterResult = ctx.runBehaviour(
                    "RUNLOOPED:" + behaviour.name(), null, behaviour, loopArgs);

                // RUNLOOPED owns its own loop frame, so @BREAK terminates this loop
                // only — the calling procedure keeps running. @ONEBREAK was already
                // swallowed at the procedure boundary and just ends the current
                // iteration, letting the next one run.
                if (iterResult instanceof BreakResult) {
                    break;
                }
            }

            return MethodResult.noReturn();
        }))
    );

    // ========================================
    // CONDITION CHECKING
    // ========================================

    public static boolean checkCondition(BehaviourVariable behaviour, MethodContext ctx) {
        Variable condition = resolveCondition(behaviour, ctx);
        if (condition == null) return true;
        return evaluateConditionSafe(condition, ctx);
    }

    private static Variable resolveCondition(BehaviourVariable behaviour, MethodContext ctx) {
        if (ctx == null) return null;

        String conditionName = ctx.context().getAttribute(behaviour.name(), "CONDITION");
        if (conditionName == null || conditionName.isEmpty()) return null;

        Variable condVar = ctx.getVariable(conditionName);
        if (condVar instanceof ConditionVariable) return condVar;
        if (condVar instanceof ComplexConditionVariable) return condVar;

        if (condVar == null) {
            Gdx.app.error("BehaviourVariable", "Condition variable " + conditionName + " not found");
        } else {
            Gdx.app.error("BehaviourVariable", "Variable " + conditionName + " is not a ConditionVariable or ComplexConditionVariable");
        }
        return null;
    }

    private static boolean evaluateConditionSafe(Variable cond, MethodContext ctx) {
        try {
            BoolValue condResult;
            if (cond instanceof ConditionVariable condition) {
                condResult = condition.evaluate(ctx, true);
            } else if (cond instanceof ComplexConditionVariable complex) {
                condResult = complex.evaluate(ctx, true);
            } else {
                return true;
            }
            return condResult.value();
        } catch (Exception e) {
            Gdx.app.error("BehaviourVariable", "Error evaluating condition: " + e.getMessage());
            return true; // Default to true on error (like v1)
        }
    }

    @Override
    public Variable withSignal(String signalName, SignalHandler handler) {
        Map<String, SignalHandler> newSignals = new java.util.HashMap<>(signals);
        if (handler == null) {
            newSignals.remove(signalName);
        } else {
            newSignals.put(signalName, handler);
        }
        return new BehaviourVariable(name, ast, newSignals);
    }
}
