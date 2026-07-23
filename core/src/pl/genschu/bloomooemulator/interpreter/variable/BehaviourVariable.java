package pl.genschu.bloomooemulator.interpreter.variable;

import com.badlogic.gdx.Gdx;
import pl.genschu.bloomooemulator.interpreter.ast.ASTNode;
import pl.genschu.bloomooemulator.interpreter.context.Context;
import pl.genschu.bloomooemulator.interpreter.runtime.BreakResult;
import pl.genschu.bloomooemulator.interpreter.runtime.ExecutionResult;
import pl.genschu.bloomooemulator.interpreter.values.*;
import pl.genschu.bloomooemulator.loader.BehaviourCodeParser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * BehaviourVariable stores CODE source and its compiled AST.
 **/
public final class BehaviourVariable implements Variable {
    private final String name;
    private final ASTNode ast;
    private final Map<String, SignalHandler> signals;
    private final String sourceCode;
    private final ConcurrentMap<List<String>, ASTNode> parameterizedAsts;

    /** Compatibility constructor for callers which only have a compiled AST. */
    public BehaviourVariable(String name, ASTNode ast, Map<String, SignalHandler> signals) {
        this(name, ast, signals, null, new ConcurrentHashMap<>());
    }

    private BehaviourVariable(String name, ASTNode ast, Map<String, SignalHandler> signals,
                              String sourceCode, ConcurrentMap<List<String>, ASTNode> parameterizedAsts) {
        this.name = name != null ? name : "";
        if (ast == null) {
            throw new IllegalArgumentException("AST cannot be null for behaviour");
        }
        this.ast = ast;
        this.signals = signals == null ? Map.of() : Map.copyOf(signals);
        this.sourceCode = sourceCode;
        this.parameterizedAsts = parameterizedAsts;
    }

    public static BehaviourVariable fromScript(String name, String code, Map<String, SignalHandler> signals) {
        ASTNode ast = BehaviourCodeParser.parseCode(code, name);
        return new BehaviourVariable(name, ast, signals, code, new ConcurrentHashMap<>());
    }

    @Override
    public String name() {
        return name;
    }

    public ASTNode ast() {
        return ast;
    }

    @Override
    public Map<String, SignalHandler> signals() {
        return signals;
    }

    public String sourceCode() {
        return sourceCode;
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
        return fromScript(name, code, signals);
    }

    /** Compiles CODE after original-style whole-source $1..$9 substitution. */
    public ASTNode astForArguments(List<Value> args, Context context) {
        if (sourceCode == null || args == null || args.isEmpty()) {
            return ast;
        }

        List<String> parameterTexts = args.stream()
            .map(value -> parameterText(value, context))
            .toList();
        List<String> cacheKey = List.copyOf(parameterTexts);
        return parameterizedAsts.computeIfAbsent(cacheKey, key ->
            BehaviourCodeParser.parseCode(spliceParameters(sourceCode, key), name + key));
    }

    /**
     * Safe reconstruction of fillParams. One character after '$' selects a
     * 1-based parameter; invalid and missing references remain literal.
     */
    public static String spliceParameters(String source, List<String> parameterTexts) {
        if (source == null || source.isEmpty() || parameterTexts == null || parameterTexts.isEmpty()) {
            return source == null ? "" : source;
        }

        StringBuilder result = new StringBuilder(source.length());
        for (int i = 0; i < source.length(); i++) {
            char current = source.charAt(i);
            if (current == '$' && i + 1 < source.length()) {
                char selector = source.charAt(i + 1);
                int index = selector - '1';
                if (selector >= '1' && selector <= '9' && index < parameterTexts.size()) {
                    result.append(parameterTexts.get(index));
                    i++;
                    continue;
                }
            }
            result.append(current);
        }
        return result.toString();
    }

    private static String parameterText(Value value, Context context) {
        String text = switch (value) {
            case VariableRef ref -> ref.name();
            case VariableValue wrapped -> wrapped.variable().name();
            default -> value.toDisplayString();
        };
        Variable namedObject = context != null ? context.getVariable(text) : null;
        return namedObject != null && !namedObject.type().isPrimitive()
            ? "\"" + text + "\""
            : text;
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

            // The loop counter is $1; everything from arg index 2 onward (the step
            // value included) is passed through as $2, $3, ...
            List<Value> extraArgs = args.size() > 2
                    ? args.subList(2, args.size())
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
            return true; // Preserve permissive condition handling on evaluation errors.
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
        return new BehaviourVariable(name, ast, newSignals, sourceCode, parameterizedAsts);
    }
}
