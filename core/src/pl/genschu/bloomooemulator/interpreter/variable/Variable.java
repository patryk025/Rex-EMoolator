package pl.genschu.bloomooemulator.interpreter.variable;

import com.badlogic.gdx.Gdx;
import pl.genschu.bloomooemulator.engine.context.EngineVariable;
import pl.genschu.bloomooemulator.interpreter.context.Context;
import pl.genschu.bloomooemulator.interpreter.helpers.ArgumentHelper;
import pl.genschu.bloomooemulator.interpreter.values.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

public sealed interface Variable extends EngineVariable permits
        IntegerVariable,
        DoubleVariable,
        StringVariable,
        BoolVariable,
        BehaviourVariable,
        ArrayVariable,
        DatabaseVariable,
        StructVariable,
        DatabaseCursorVariable,
        ClassVariable,
        InstanceVariable,
        CNVLoaderVariable,
        TimerVariable,
        VectorVariable,
        RandVariable,
        GroupVariable,
        ExpressionVariable,
        ConditionVariable,
        ComplexConditionVariable,
        ApplicationVariable,
        EpisodeVariable,
        SceneVariable,
        AnimoVariable,
        SequenceVariable,
        MultiArrayVariable,
        ImageVariable,
        SoundVariable,
        MouseVariable,
        KeyboardVariable,
        ButtonVariable,
        TextVariable,
        CanvasObserverVariable,
        FontVariable,
        InertiaVariable,
        MatrixVariable,
        PatternVariable,
        StaticFilterVariable,
        SystemVariable,
        VirtualGraphicsObjectVariable,
        WorldVariable {

    /**
     * Returns the name of this variable.
     */
    String name();

    /**
     * EngineVariable bridge: delegates to name().
     */
    @Override
    default String getName() {
        return name();
    }

    /**
     * EngineVariable bridge: delegates to type().name().
     */
    @Override
    default String getTypeName() {
        return type().name();
    }

    /**
     * Returns the current value as a v2 Value.
     */
    Value value();

    /**
     * Returns the type of this variable.
     */
    VariableType type();

    /**
     * Sets the value of this variable in-place (mutates via MutableValue holder).
     * Returns this variable for chaining.
     */
    Variable withValue(Value newValue);

    /**
     * Calls a method on this variable with context access.
     * This is the primary overload used by the interpreter.
     */
    default MethodResult callMethod(String methodName, List<Value> arguments, MethodContext ctx) {
        Gdx.app.debug(getTypeName(), "Calling method: " + methodName + " for " + name() + " with arguments: " + (arguments == null ? List.of() : arguments));
        Map<String, MethodSpec> availableMethods = methods();
        MethodSpec spec = availableMethods != null
                ? availableMethods.get(methodName.toUpperCase())
                : null;

        MethodResult result;
        if (spec == null || spec.method() == null) {
            MethodSpec global = globalMethods().get(methodName.toUpperCase());
            if (global == null || global.method() == null) {
                throw new IllegalArgumentException("Method not found: " + methodName + " on " + type() + " variable");
            }
            result = global.method().execute(this, arguments == null ? List.of() : arguments, ctx);
        } else {
            result = spec.method().execute(this, arguments == null ? List.of() : arguments, ctx);
        }

        // Persist value to INI file if TOINI attribute is set
        persistToIniIfNeeded(ctx);

        return result;
    }

    /**
     * Calls a method without context (convenience for tests and internal variable calls).
     */
    default MethodResult callMethod(String methodName, List<Value> arguments) {
        return callMethod(methodName, arguments, null);
    }

    /**
     * Calls a method without context (varargs convenience).
     */
    default MethodResult callMethod(String methodName, Value... arguments) {
        return callMethod(methodName, arguments != null ? List.of(arguments) : List.of(), null);
    }

    /**
     * Global methods available on all variable types.
     */
    Map<String, MethodSpec> GLOBAL_METHODS = Map.ofEntries(
            Map.entry("ADDBEHAVIOUR", MethodSpec.of((self, args, ctx) -> {
                if (args == null || args.size() < 2) {
                    throw new IllegalArgumentException("ADDBEHAVIOUR requires 2 arguments: signalName, behaviourName");
                }
                String rawSignalName = ArgumentHelper.getString(args.get(0));
                String signalName = rawSignalName.replace("$", "^");
                String behaviourSpec = ArgumentHelper.getString(args.get(1));

                // Parse behaviour spec - may have parameters like "MYBEHAVIOUR(\"param1\", VAR)"
                String behaviourName = behaviourSpec;
                String[] params = null;
                if (behaviourSpec.contains("(") && behaviourSpec.endsWith(")")) {
                    int parenStart = behaviourSpec.indexOf('(');
                    behaviourName = behaviourSpec.substring(0, parenStart).trim();
                    String paramStr = behaviourSpec.substring(parenStart + 1, behaviourSpec.length() - 1);
                    if (!paramStr.isEmpty()) {
                        params = paramStr.split(",");
                        for (int i = 0; i < params.length; i++) {
                            params[i] = params[i].trim();
                        }
                    }
                }

                Variable behaviourVar = ctx.getVariable(behaviourName.trim());
                if (!(behaviourVar instanceof BehaviourVariable behaviour)) {
                    return MethodResult.noReturn();
                }

                final String[] finalParams = params;
                SignalHandler handler = (variable, signal, signalArgs) -> {
                    List<Value> resolvedParams = resolveSignalParams(finalParams, ctx);
                    ctx.runBehaviour("Signal:" + signal, variable, behaviour, resolvedParams);
                };

                Variable updated = self.withSignal(signalName, handler);
                ctx.updateVariable(self.name(), updated);
                return MethodResult.noReturn();
            })),

            Map.entry("CLONE", MethodSpec.of((self, args, ctx) -> {
                int amount = 1;
                if (args != null && !args.isEmpty()) {
                    amount = ArgumentHelper.getInt(args.get(0));
                }
                int count = Math.max(0, amount);
                if (count > 0) {
                    String baseName = self.name();
                    int existing = ctx.clones().getCloneNames(baseName).size();
                    for (int i = 0; i < count; i++) {
                        String cloneName = baseName + "_" + (existing + i + 1);
                        Variable clone = self.copyAs(cloneName);
                        ctx.setVariable(cloneName, clone);
                        ctx.clones().registerClone(baseName, cloneName);
                    }
                }
                return MethodResult.noReturn();
            })),

            Map.entry("GETCLONEINDEX", MethodSpec.of((self, args, ctx) -> {
                String name = self.name();
                int idx = 0;
                int pos = name.lastIndexOf('_');
                if (pos > -1 && pos < name.length() - 1) {
                    try {
                        idx = Integer.parseInt(name.substring(pos + 1));
                    } catch (NumberFormatException ignored) {
                        idx = 0;
                    }
                }
                return MethodResult.returns(new IntValue(idx));
            })),

            Map.entry("REMOVEBEHAVIOUR", MethodSpec.of((self, args, ctx) -> {
                if (args == null || args.isEmpty()) {
                    throw new IllegalArgumentException("REMOVEBEHAVIOUR requires 1 argument: signalName");
                }
                String rawSignalName = ArgumentHelper.getString(args.get(0));
                String signalName = rawSignalName.replace("$", "^");
                Variable updated = self.withoutSignal(signalName);
                ctx.updateVariable(self.name(), updated);
                return MethodResult.noReturn();
            })),

            Map.entry("SEND", MethodSpec.of((self, args, ctx) -> {
                if (args == null || args.isEmpty()) {
                    throw new IllegalArgumentException("SEND requires 1 argument: signal");
                }
                String signal = ArgumentHelper.getString(args.get(0));
                self.emitSignal("ONSIGNAL", new StringValue(signal));
                return MethodResult.noReturn();
            }))
    );

    /**
     * Returns available methods for this variable type.
     */
    Map<String, MethodSpec> methods();

    /**
     * Returns available global methods for all variable types.
     */
    default Map<String, MethodSpec> globalMethods() {
        return GLOBAL_METHODS;
    }

    /**
     * Returns signal handlers attached to this variable.
     */
    Map<String, SignalHandler> signals();

    /**
     * Gets a signal handler by name.
     * Convenience method for signals().get(signalName).
     */
    default SignalHandler getSignal(String signalName) {
        Map<String, SignalHandler> registeredSignals = signals();
        return registeredSignals != null ? registeredSignals.get(signalName) : null;
    }

    /**
     * Creates a new Variable with an additional signal handler.
     * If handler is null, removes the signal.
     */
    Variable withSignal(String signalName, SignalHandler handler);

    /**
     * Creates a new Variable without the specified signal.
     */
    default Variable withoutSignal(String signalName) {
        return withSignal(signalName, null);
    }

    /**
     * Resolves the best available reset value from context attributes.
     *
     * Preference order matches legacy RESETINI behavior:
     * DEFAULT -> INIT_VALUE -> VALUE
     */
    default String getResetAttributeValue(MethodContext ctx) {
        if (ctx == null) {
            return null;
        }

        Context context = ctx.context();
        if (context == null) {
            return null;
        }

        String resetValue = context.getAttributeInHierarchy(name(), "DEFAULT");
        if (resetValue != null) {
            return resetValue;
        }

        resetValue = context.getAttributeInHierarchy(name(), "INIT_VALUE");
        if (resetValue != null) {
            return resetValue;
        }

        return context.getAttributeInHierarchy(name(), "VALUE");
    }

    /**
     * Emits a signal on this variable.
     * This executes the signal handler if one is registered.
     */
    default void emitSignal(String signalName) {
        emitSignal(signalName, null);
    }

    default void emitSignal(String signalName, Value newValue, Value... arguments) {
        Map<String, SignalHandler> registeredSignals = signals();
        String fullSignalName = signalName + (newValue != null ? "^" + newValue.toDisplayString() : "");
        Gdx.app.debug(getTypeName(), "Emitting signal: " + fullSignalName + " for " + name() + " with arguments: " + (arguments == null ? List.of() : List.of(arguments)));
        if(registeredSignals != null) {
            SignalHandler handler = registeredSignals.get(fullSignalName);
            if (handler == null) {
                handler = registeredSignals.get(signalName);
            }
            if (handler != null) {
                Gdx.app.debug(getTypeName(), "Executing signal: " + fullSignalName + " for " + name());
                handler.handle(this, fullSignalName, arguments);
            }
        }
    }

    /**
     * Creates a copy of this variable with a new name.
     */
    default Variable copyAs(String newName) {
        throw new UnsupportedOperationException("copyAs not implemented for " + type() + " variable");
    }

    /**
     * Map of converters from Value to Variable by target type name.
     */
    Map<String, BiFunction<String, Value, Variable>> CONVERTERS = Map.of(
            "INTEGER", (name, val) -> new IntegerVariable(name, val.toInt().value()),
            "DOUBLE",  (name, val) -> new DoubleVariable(name, val.toDouble().value()),
            "STRING",  (name, val) -> new StringVariable(name, val.toStringValue().value()),
            "BOOLEAN", (name, val) -> new BoolVariable(name, val.toBool().value())
    );

    /**
     * Converts this variable to another type.
     */
    default Variable convertTo(String targetType) {
        String target = targetType.toUpperCase();
        if (type().name().equals(target)) {
            return this;
        }

        if(!type().isPrimitive()) {
            throw new UnsupportedOperationException("Conversion from non-primitive type " + type() + " is not supported");
        }

        var factory = CONVERTERS.get(target);
        if (factory == null) {
            throw new UnsupportedOperationException("Unsupported target type: " + target);
        }

        try {
            return factory.apply(name(), value());
        } catch (Exception e) {
            throw new RuntimeException("Conversion failed to " + target, e);
        }
    }

    // ========================================
    // PRIVATE HELPERS
    // ========================================

    /**
     * Persists value to INI file if TOINI attribute is set.
     * Mirrors v1 Variable.set() behavior: after any value mutation,
     * check TOINI and write to game INI if needed.
     */
    private void persistToIniIfNeeded(MethodContext ctx) {
        if (ctx == null) return;
        Context context = ctx.context();
        if (context == null) return;

        String toini = context.getAttributeInHierarchy(name(), "TOINI");
        if (!"TRUE".equalsIgnoreCase(toini)) return;

        pl.genschu.bloomooemulator.engine.Game game = ctx.getGame();
        if (game == null || game.getGameINI() == null) return;

        String section = game.findINISectionForVariable(name().toUpperCase());
        if (section != null) {
            game.getGameINI().put(section, name().toUpperCase(), value().toDisplayString());
        }
    }

    private static List<Value> resolveSignalParams(String[] params, MethodContext ctx) {
        if (params == null) return List.of();
        List<Value> resolved = new ArrayList<>(params.length);
        for (String param : params) {
            param = param.trim();
            if (param.startsWith("\"") && param.endsWith("\"")) {
                param = param.substring(1, param.length() - 1);
                resolved.add(new StringValue(param));
                continue;
            }
            Variable paramVar = ctx.getVariable(param);
            if (paramVar != null) {
                resolved.add(paramVar.value());
                continue;
            }
            resolved.add(new StringValue(param));
        }
        return resolved;
    }

}
