package pl.genschu.bloomooemulator.interpreter.variable;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import pl.genschu.bloomooemulator.annotations.InternalMutable;
import pl.genschu.bloomooemulator.interpreter.helpers.ArgumentHelper;
import pl.genschu.bloomooemulator.interpreter.v1.util.KeyboardsKeysMapper;
import pl.genschu.bloomooemulator.interpreter.values.*;

import java.util.*;

/**
 * KeyboardVariable represents the keyboard input device in the game.
 * Uses mutable KeyboardState for enable and auto-repeat state.
 */
public record KeyboardVariable(
    String name,
    @InternalMutable KeyboardState state,
    Map<String, SignalHandler> signals
) implements Variable {

    /**
     * Mutable state for keyboard handling.
     */
    public static final class KeyboardState {
        public boolean enabled = true;
        public boolean autoRepeat = false;

        public KeyboardState() {}

        public KeyboardState copy() {
            KeyboardState copy = new KeyboardState();
            copy.enabled = this.enabled;
            copy.autoRepeat = this.autoRepeat;
            return copy;
        }

        public void dispose() {}
    }

    public KeyboardVariable {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Variable name cannot be null or empty");
        }
        if (state == null) {
            state = new KeyboardState();
        }
        if (signals == null) {
            signals = Map.of();
        } else {
            signals = Map.copyOf(signals);
        }
    }

    public KeyboardVariable(String name) {
        this(name, new KeyboardState(), Map.of());
    }

    // ========================================
    // INTERFACE IMPLEMENTATION
    // ========================================

    @Override
    public Value value() {
        return NullValue.INSTANCE;
    }

    @Override
    public VariableType type() {
        return VariableType.KEYBOARD;
    }

    @Override
    public Variable withValue(Value newValue) {
        return this;
    }

    @Override
    public Map<String, MethodSpec> methods() {
        return METHODS;
    }

    @Override
    public Variable withSignal(String signalName, SignalHandler handler) {
        Map<String, SignalHandler> newSignals = new HashMap<>(signals);
        if (handler != null) {
            newSignals.put(signalName, handler);
        } else {
            newSignals.remove(signalName);
        }
        return new KeyboardVariable(name, state, newSignals);
    }

    @Override
    public Variable copyAs(String newName) {
        return new KeyboardVariable(newName, state.copy(), new HashMap<>(signals));
    }

    // ========================================
    // INPUT MANAGER SUPPORT
    // ========================================

    public boolean isEnabled() { return state.enabled; }
    public boolean isAutoRepeat() { return state.autoRepeat; }

    // ========================================
    // METHODS DEFINITION
    // ========================================

    private static final Map<String, MethodSpec> METHODS = Map.ofEntries(
        Map.entry("DISABLE", MethodSpec.of((self, args, ctx) -> {
            ((KeyboardVariable) self).state.enabled = false;
            return MethodResult.noReturn();
        })),

        Map.entry("ENABLE", MethodSpec.of((self, args, ctx) -> {
            ((KeyboardVariable) self).state.enabled = true;
            return MethodResult.noReturn();
        })),

        Map.entry("GETLATESTKEY", MethodSpec.of((self, args, ctx) -> {
            // Not implemented in v1 either — return empty string
            Gdx.app.log("KeyboardVariable", "GETLATESTKEY not implemented");
            return MethodResult.returns(new StringValue(""));
        })),

        Map.entry("ISENABLED", MethodSpec.of((self, args, ctx) ->
            MethodResult.returns(BoolValue.of(((KeyboardVariable) self).state.enabled))
        )),

        Map.entry("ISKEYDOWN", MethodSpec.of((self, args, ctx) -> {
            String keyName = ArgumentHelper.getString(args.get(0)).toUpperCase();
            int keyCode = KeyboardsKeysMapper.getKeyCode(keyName);
            if (keyCode == -1) {
                Gdx.app.error("KeyboardVariable", "Unknown key: " + keyName);
                return MethodResult.returns(BoolValue.FALSE);
            }
            return MethodResult.returns(BoolValue.of(Gdx.input.isKeyPressed(keyCode)));
        })),

        Map.entry("SETAUTOREPEAT", MethodSpec.of((self, args, ctx) -> {
            ((KeyboardVariable) self).state.autoRepeat = ArgumentHelper.getBoolean(args.get(0));
            return MethodResult.noReturn();
        }))
    );

    @Override
    public String toString() {
        return "KeyboardVariable[" + name + ", enabled=" + state.enabled + ", autoRepeat=" + state.autoRepeat + "]";
    }
}
