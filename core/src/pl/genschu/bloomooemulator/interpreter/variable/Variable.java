package pl.genschu.bloomooemulator.interpreter.variable;

import com.badlogic.gdx.Gdx;
import pl.genschu.bloomooemulator.engine.context.EngineVariable;
import pl.genschu.bloomooemulator.interpreter.context.Context;
import pl.genschu.bloomooemulator.interpreter.helpers.ArgumentHelper;
import pl.genschu.bloomooemulator.interpreter.values.*;

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
        KolorowankaVariable,
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
     * Returns the current value.
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
                String behaviourName = ArgumentHelper.getString(args.get(1));
                Variable behaviourVar = ctx.getVariable(behaviourName);
                if (!(behaviourVar instanceof BehaviourVariable behaviour)) {
                    return MethodResult.noReturn();
                }
                List<String> declaredArguments = args.subList(2, args.size()).stream()
                    .map(Value::toDisplayString)
                    .toList();

                SignalHandler handler = new BehaviourSignalHandler(self.name(), signalName,
                    behaviour, declaredArguments, ctx);
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
     * Preference order matches RESETINI behavior:
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
        dispatchSignal(signalName, null, List.of(), false);
    }

    /**
     * Emits a qualified signal. If no qualified binding exists, the base binding
     * is used and {@code fallbackPayload} becomes its explicit payload.
     */
    default void emitSignal(String signalName, Value qualifier, Value... fallbackPayload) {
        List<Value> payload = fallbackPayload == null
            ? List.of()
            : List.of(fallbackPayload);
        dispatchSignal(signalName, qualifier, payload, !payload.isEmpty());
    }

    /** Emits an unqualified signal whose payload replaces declared arguments. */
    default void emitSignalWithPayload(String signalName, Value... arguments) {
        List<Value> payload = arguments == null ? List.of() : List.of(arguments);
        dispatchSignal(signalName, null, payload, true);
    }

    private void dispatchSignal(String signalName, Value qualifier,
                                List<Value> fallbackPayload, boolean hasFallbackPayload) {
        Map<String, SignalHandler> registeredSignals = signals();
        String emittedName = signalName + (qualifier != null ? "^" + qualifier.toDisplayString() : "");
        Gdx.app.debug(getTypeName(), "Emitting signal: " + emittedName + " for " + name()
            + " with fallback payload: " + fallbackPayload);
        if (registeredSignals == null) {
            return;
        }

        boolean qualifiedEmission = qualifier != null;
        String bindingName = emittedName;
        SignalHandler handler = qualifiedEmission ? registeredSignals.get(bindingName) : null;
        boolean exactBinding = qualifiedEmission && handler != null;
        if (handler == null) {
            bindingName = signalName;
            handler = registeredSignals.get(bindingName);
        }
        if (handler != null) {
            List<Value> effectivePayload = exactBinding ? List.of() : fallbackPayload;
            boolean hasExplicitPayload = !exactBinding && hasFallbackPayload;
            SignalEmission emission = new SignalEmission(emittedName, bindingName,
                effectivePayload, hasExplicitPayload);
            Gdx.app.debug(getTypeName(), "Executing signal: " + emission + " for " + name());
            handler.handle(this, emission);
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
     * After any value mutation, check TOINI and write to game INI if needed.
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

}
