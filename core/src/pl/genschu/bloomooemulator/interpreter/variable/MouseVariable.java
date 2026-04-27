package pl.genschu.bloomooemulator.interpreter.variable;

import com.badlogic.gdx.Gdx;
import pl.genschu.bloomooemulator.annotations.InternalMutable;
import pl.genschu.bloomooemulator.interpreter.helpers.ArgumentHelper;
import pl.genschu.bloomooemulator.interpreter.values.*;

import java.util.*;

/**
 * MouseVariable represents the mouse input device in the game.
 * Uses mutable MouseState for position and enable/signal state.
 */
public record MouseVariable(
    String name,
    @InternalMutable MouseState state,
    Map<String, SignalHandler> signals
) implements Variable {

    /**
     * Mutable state for mouse tracking.
     */
    public static final class MouseState {
        public boolean enabled = true;
        public boolean emitSignals = true;
        public int posX = 0;
        public int posY = 0;

        public MouseState() {}

        public MouseState copy() {
            MouseState copy = new MouseState();
            copy.enabled = this.enabled;
            copy.emitSignals = this.emitSignals;
            copy.posX = this.posX;
            copy.posY = this.posY;
            return copy;
        }

        public void dispose() {}
    }

    public MouseVariable {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Variable name cannot be null or empty");
        }
        if (state == null) {
            state = new MouseState();
        }
        if (signals == null) {
            signals = Map.of();
        } else {
            signals = Map.copyOf(signals);
        }
    }

    public MouseVariable(String name) {
        this(name, new MouseState(), Map.of());
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
        return VariableType.MOUSE;
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
        return new MouseVariable(name, state, newSignals);
    }

    @Override
    public Variable copyAs(String newName) {
        return new MouseVariable(newName, state.copy(), new HashMap<>(signals));
    }

    // ========================================
    // INPUT MANAGER SUPPORT
    // ========================================

    /**
     * Called by InputManager to update mouse position.
     * Emits ONMOVE signal if coordinates changed.
     */
    public void update(int x, int y) {
        if (!state.enabled) return;
        if (x != state.posX || y != state.posY) {
            state.posX = x;
            state.posY = y;
            if (state.emitSignals) {
                emitSignal("ONMOVE");
            }
        }
    }

    public boolean isEnabled() { return state.enabled; }
    public boolean isEmitSignals() { return state.emitSignals; }
    public int getPosX() { return state.posX; }
    public int getPosY() { return state.posY; }

    // ========================================
    // METHODS DEFINITION
    // ========================================

    private static final Map<String, MethodSpec> METHODS = Map.ofEntries(
        Map.entry("DISABLE", MethodSpec.of((self, args, ctx) -> {
            ((MouseVariable) self).state.enabled = false;
            return MethodResult.noReturn();
        })),

        Map.entry("DISABLESIGNAL", MethodSpec.of((self, args, ctx) -> {
            ((MouseVariable) self).state.emitSignals = false;
            return MethodResult.noReturn();
        })),

        Map.entry("ENABLE", MethodSpec.of((self, args, ctx) -> {
            ((MouseVariable) self).state.enabled = true;
            return MethodResult.noReturn();
        })),

        Map.entry("ENABLESIGNAL", MethodSpec.of((self, args, ctx) -> {
            ((MouseVariable) self).state.emitSignals = true;
            return MethodResult.noReturn();
        })),

        Map.entry("GETPOSX", MethodSpec.of((self, args, ctx) ->
            MethodResult.returns(new IntValue(((MouseVariable) self).state.posX))
        )),

        Map.entry("GETPOSY", MethodSpec.of((self, args, ctx) ->
            MethodResult.returns(new IntValue(((MouseVariable) self).state.posY))
        )),

        Map.entry("HIDE", MethodSpec.of((self, args, ctx) -> {
            ctx.getGame().getInputManager().setMouseVisible(false);
            return MethodResult.noReturn();
        })),

        Map.entry("SETPOSITION", MethodSpec.of((self, args, ctx) -> {
            MouseVariable mouse = (MouseVariable) self;
            int x = Math.max(0, Math.min(800, ArgumentHelper.getInt(args.get(0))));
            int y = Math.max(0, Math.min(600, ArgumentHelper.getInt(args.get(1))));
            if (x != mouse.state.posX || y != mouse.state.posY) {
                mouse.state.posX = x;
                mouse.state.posY = y;
                if (mouse.state.emitSignals) {
                    mouse.emitSignal("ONMOVE");
                }
            }
            return MethodResult.noReturn();
        })),

        Map.entry("SHOW", MethodSpec.of((self, args, ctx) -> {
            ctx.getGame().getInputManager().setMouseVisible(true);
            return MethodResult.noReturn();
        }))
    );

    @Override
    public String toString() {
        return "MouseVariable[" + name + ", pos=" + state.posX + "," + state.posY + ", enabled=" + state.enabled + "]";
    }
}
