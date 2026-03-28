package pl.genschu.bloomooemulator.interpreter.variable;

import com.badlogic.gdx.Gdx;
import pl.genschu.bloomooemulator.annotations.InternalMutable;
import pl.genschu.bloomooemulator.engine.decision.events.ButtonEvent;
import pl.genschu.bloomooemulator.engine.decision.states.ButtonState;
import pl.genschu.bloomooemulator.interpreter.context.Context;
import pl.genschu.bloomooemulator.interpreter.helpers.ArgumentHelper;
import pl.genschu.bloomooemulator.interpreter.values.*;
import pl.genschu.bloomooemulator.interpreter.variable.capabilities.Initializable;
import pl.genschu.bloomooemulator.geometry.shapes.Box2D;

import java.util.*;

/**
 * ButtonVariable represents an interactive button in the game.
 * Uses mutable ButtonVarState for button state machine, rectangle, and graphics/sound references.
 */
public record ButtonVariable(
    String name,
    @InternalMutable ButtonVarState state,
    Map<String, SignalHandler> signals
) implements Variable, Initializable {

    /**
     * Mutable state for button interaction.
     */
    public static final class ButtonVarState {
        public ButtonState buttonState = ButtonState.INIT;
        public Box2D rect = null;
        public String rectVarName = null;
        public String gfxStandardName = null;
        public String gfxOnMoveName = null;
        public String gfxOnClickName = null;
        public String sndStandardName = null;
        public String sndOnMoveName = null;
        public String sndOnClickName = null;

        public ButtonVarState() {}

        public ButtonVarState copy() {
            ButtonVarState copy = new ButtonVarState();
            copy.buttonState = this.buttonState;
            copy.rect = this.rect != null
                    ? new Box2D(rect.getXLeft(), rect.getYBottom(), rect.getXRight(), rect.getYTop())
                    : null;
            copy.rectVarName = this.rectVarName;
            copy.gfxStandardName = this.gfxStandardName;
            copy.gfxOnMoveName = this.gfxOnMoveName;
            copy.gfxOnClickName = this.gfxOnClickName;
            copy.sndStandardName = this.sndStandardName;
            copy.sndOnMoveName = this.sndOnMoveName;
            copy.sndOnClickName = this.sndOnClickName;
            return copy;
        }

        public void dispose() {}
    }

    public ButtonVariable {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Variable name cannot be null or empty");
        }
        if (state == null) {
            state = new ButtonVarState();
        }
        if (signals == null) {
            signals = Map.of();
        } else {
            signals = Map.copyOf(signals);
        }
    }

    public ButtonVariable(String name) {
        this(name, new ButtonVarState(), Map.of());
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
        return VariableType.BUTTON;
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
        return new ButtonVariable(name, state, newSignals);
    }

    @Override
    public Variable copyAs(String newName) {
        return new ButtonVariable(newName, state.copy(), new HashMap<>(signals));
    }

    // ========================================
    // INITIALIZABLE
    // ========================================

    @Override
    public void init(Context context) {
        // Read graphics and sound variable references from attributes
        String gfxStd = context.attributes().get(name, "GFXSTANDARD");
        if (gfxStd != null) state.gfxStandardName = gfxStd;

        String gfxMove = context.attributes().get(name, "GFXONMOVE");
        if (gfxMove != null) state.gfxOnMoveName = gfxMove;

        String gfxClick = context.attributes().get(name, "GFXONCLICK");
        if (gfxClick != null) state.gfxOnClickName = gfxClick;

        String sndStd = context.attributes().get(name, "SNDSTANDARD");
        if (sndStd != null) state.sndStandardName = sndStd;

        String sndMove = context.attributes().get(name, "SNDONMOVE");
        if (sndMove != null) state.sndOnMoveName = sndMove;

        String sndClick = context.attributes().get(name, "SNDONCLICK");
        if (sndClick != null) state.sndOnClickName = sndClick;

        // Parse RECT attribute
        String rectAttr = context.attributes().get(name, "RECT");
        if (rectAttr != null) {
            parseRect(rectAttr, context);
        }

        // Load rect from GFXSTANDARD if no RECT set
        if (state.rect == null && state.gfxStandardName != null) {
            loadRectFromGfx(state.gfxStandardName, context);
        }

        // Set default state
        String enableAttr = context.attributes().get(name, "ENABLE");
        if (enableAttr != null && enableAttr.equalsIgnoreCase("FALSE")) {
            state.buttonState = ButtonState.DISABLED;
        } else {
            changeState(ButtonEvent.ENABLE, context);
        }
    }

    private void parseRect(String rectAttr, Context context) {
        // Try as variable reference first
        Variable rectVar = context.getVariable(rectAttr);
        if (rectVar instanceof AnimoVariable animo) {
            state.rectVarName = rectAttr;
            state.rect = animo.getRect() != null ? animo.getRect() : new Box2D(0, 0, 0, 0);
            return;
        }
        if (rectVar instanceof ImageVariable img) {
            state.rectVarName = rectAttr;
            state.rect = img.getRect() != null ? img.getRect() : new Box2D(0, 0, 0, 0);
            return;
        }
        // Try as comma-separated coordinates
        try {
            String[] parts = rectAttr.split(",");
            if (parts.length >= 4) {
                int xL = Integer.parseInt(parts[0].trim());
                int yB = Integer.parseInt(parts[1].trim());
                int xR = Integer.parseInt(parts[2].trim());
                int yT = Integer.parseInt(parts[3].trim());
                state.rect = new Box2D(xL, yB, xR, yT);
            }
        } catch (NumberFormatException e) {
            Gdx.app.error("ButtonVariable", "Invalid RECT format: " + rectAttr);
        }
    }

    private void loadRectFromGfx(String gfxName, Context context) {
        Variable gfx = context.getVariable(gfxName);
        if (gfx instanceof AnimoVariable animo && animo.getRect() != null) {
            state.rect = animo.getRect();
        } else if (gfx instanceof ImageVariable img && img.getRect() != null) {
            state.rect = img.getRect();
        }
    }

    // ========================================
    // STATE MACHINE
    // ========================================

    /**
     * Evaluates a state transition and applies side effects.
     */
    public void changeState(ButtonEvent event, Context context) {
        ButtonState newState = evaluateTransition(state.buttonState, event);
        if (newState == state.buttonState) return;

        ButtonState oldState = state.buttonState;
        state.buttonState = newState;

        switch (newState) {
            case STANDARD -> {
                showGfx(state.gfxStandardName, true, context);
                showGfx(state.gfxOnMoveName, false, context);
                showGfx(state.gfxOnClickName, false, context);
                playSnd(state.sndStandardName, context);
                if (oldState == ButtonState.HOVERED) {
                    emitSignal("ONFOCUSOFF");
                }
            }
            case HOVERED -> {
                showGfx(state.gfxStandardName, false, context);
                showGfx(state.gfxOnMoveName, true, context);
                showGfx(state.gfxOnClickName, false, context);
                playSnd(state.sndOnMoveName, context);
                if (oldState == ButtonState.PRESSED) {
                    emitSignal("ONRELEASED");
                    emitSignal("ONACTION");
                }
                if (oldState == ButtonState.STANDARD) {
                    emitSignal("ONFOCUSON");
                }
            }
            case PRESSED -> {
                showGfx(state.gfxStandardName, false, context);
                showGfx(state.gfxOnMoveName, false, context);
                showGfx(state.gfxOnClickName, true, context);
                playSnd(state.sndOnClickName, context);
                emitSignal("ONCLICKED");
            }
            case DISABLED -> {
                showGfx(state.gfxStandardName, false, context);
                showGfx(state.gfxOnMoveName, false, context);
                showGfx(state.gfxOnClickName, false, context);
                stopAllSounds(context);
            }
            case DISABLED_BUT_VISIBLE -> {
                showGfx(state.gfxStandardName, true, context);
                showGfx(state.gfxOnMoveName, false, context);
                showGfx(state.gfxOnClickName, false, context);
                stopAllSounds(context);
            }
            default -> {}
        }
    }

    /**
     * Pure state transition logic (mirrors ButtonStateTransitionTree).
     */
    private static ButtonState evaluateTransition(ButtonState current, ButtonEvent event) {
        return switch (event) {
            case ENABLE -> (current == ButtonState.INIT || current == ButtonState.DISABLED || current == ButtonState.DISABLED_BUT_VISIBLE)
                    ? ButtonState.STANDARD : current;
            case DISABLE -> ButtonState.DISABLED;
            case DISABLE_BUT_VISIBLE -> ButtonState.DISABLED_BUT_VISIBLE;
            case PRESSED -> current == ButtonState.HOVERED ? ButtonState.PRESSED : current;
            case RELEASED -> current == ButtonState.PRESSED ? ButtonState.HOVERED : current;
            case FOCUS_ON -> current == ButtonState.STANDARD ? ButtonState.HOVERED : current;
            case FOCUS_OFF -> (current == ButtonState.HOVERED || current == ButtonState.PRESSED)
                    ? ButtonState.STANDARD : current;
        };
    }

    private void showGfx(String varName, boolean visible, Context context) {
        if (varName == null) return;
        Variable gfx = context.getVariable(varName);
        if (gfx instanceof AnimoVariable animo) {
            if (visible) animo.callMethod("SHOW", List.of());
            else animo.callMethod("HIDE", List.of());
        } else if (gfx instanceof ImageVariable img) {
            if (visible) img.state().visible = true;
            else img.state().visible = false;
        }
    }

    private void playSnd(String varName, Context context) {
        if (varName == null) return;
        Variable snd = context.getVariable(varName);
        if (snd instanceof SoundVariable sound) {
            sound.play();
        }
    }

    private void stopAllSounds(Context context) {
        for (String sndName : new String[]{state.sndStandardName, state.sndOnMoveName, state.sndOnClickName}) {
            if (sndName == null) continue;
            Variable snd = context.getVariable(sndName);
            if (snd instanceof SoundVariable sound) {
                sound.stop(false);
            }
        }
    }

    // ========================================
    // CONVENIENT ACCESSORS
    // ========================================

    public ButtonState getButtonState() { return state.buttonState; }
    public Box2D getRect() { return state.rect; }

    public boolean isEnabled() {
        return state.buttonState != ButtonState.DISABLED && state.buttonState != ButtonState.DISABLED_BUT_VISIBLE;
    }

    /**
     * Returns the currently active graphics variable name based on state.
     */
    public String getCurrentGfxName() {
        return switch (state.buttonState) {
            case HOVERED -> state.gfxOnMoveName != null ? state.gfxOnMoveName : state.gfxStandardName;
            case PRESSED -> state.gfxOnClickName != null ? state.gfxOnClickName : state.gfxStandardName;
            default -> state.gfxStandardName;
        };
    }

    // ========================================
    // METHODS DEFINITION
    // ========================================

    private static final Map<String, MethodSpec> METHODS = Map.ofEntries(
        Map.entry("DISABLE", MethodSpec.of((self, args, ctx) -> {
            ButtonVariable btn = (ButtonVariable) self;
            btn.state.buttonState = ButtonState.DISABLED;
            // Clear from InputManager if this is the active button
            try {
                if (ctx.getGame().getInputManager().getActiveButton() != null
                        && ctx.getGame().getInputManager().getActiveButton().getName().equals(btn.name())) {
                    ctx.getGame().getInputManager().clearActiveButton(null);
                }
            } catch (Exception ignored) {}
            return MethodResult.noReturn();
        })),

        Map.entry("DISABLEBUTVISIBLE", MethodSpec.of((self, args, ctx) -> {
            ButtonVariable btn = (ButtonVariable) self;
            btn.state.buttonState = ButtonState.DISABLED_BUT_VISIBLE;
            try {
                if (ctx.getGame().getInputManager().getActiveButton() != null
                        && ctx.getGame().getInputManager().getActiveButton().getName().equals(btn.name())) {
                    ctx.getGame().getInputManager().clearActiveButton(null);
                }
            } catch (Exception ignored) {}
            return MethodResult.noReturn();
        })),

        Map.entry("ENABLE", MethodSpec.of((self, args, ctx) -> {
            ButtonVariable btn = (ButtonVariable) self;
            btn.state.buttonState = evaluateTransition(btn.state.buttonState, ButtonEvent.ENABLE);
            return MethodResult.noReturn();
        })),

        Map.entry("SETPRIORITY", MethodSpec.of((self, args, ctx) -> {
            ButtonVariable btn = (ButtonVariable) self;
            int priority = ArgumentHelper.getInt(args.get(0));
            // Forward priority to all three graphics variables
            for (String gfxName : new String[]{btn.state.gfxStandardName, btn.state.gfxOnMoveName, btn.state.gfxOnClickName}) {
                if (gfxName == null) continue;
                Variable gfx = ctx.getVariable(gfxName);
                if (gfx != null) {
                    gfx.callMethod("SETPRIORITY", List.of(new IntValue(priority)), ctx);
                }
            }
            return MethodResult.noReturn();
        })),

        Map.entry("SETRECT", MethodSpec.of((self, args, ctx) -> {
            ButtonVariable btn = (ButtonVariable) self;
            if (args.size() == 1) {
                // SETRECT(varName)
                String varName = ArgumentHelper.getString(args.get(0));
                Variable rectVar = ctx.getVariable(varName);
                if (rectVar instanceof AnimoVariable animo) {
                    btn.state.rectVarName = varName;
                    btn.state.rect = animo.getRect();
                } else if (rectVar instanceof ImageVariable img) {
                    btn.state.rectVarName = varName;
                    btn.state.rect = img.getRect();
                }
            } else if (args.size() >= 4) {
                // SETRECT(xLeft, yBottom, xRight, yTop)
                int xL = ArgumentHelper.getInt(args.get(0));
                int yB = ArgumentHelper.getInt(args.get(1));
                int xR = ArgumentHelper.getInt(args.get(2));
                int yT = ArgumentHelper.getInt(args.get(3));
                btn.state.rect = new Box2D(xL, yB, xR, yT);
                btn.state.rectVarName = null;
            }
            return MethodResult.noReturn();
        })),

        Map.entry("SETSTD", MethodSpec.of((self, args, ctx) -> {
            ButtonVariable btn = (ButtonVariable) self;
            String varName = ArgumentHelper.getString(args.get(0));
            btn.state.gfxStandardName = varName;
            // Set priority to 0 on the new standard graphics
            Variable gfx = ctx.getVariable(varName);
            if (gfx != null) {
                gfx.callMethod("SETPRIORITY", List.of(new IntValue(0)), ctx);
            }
            return MethodResult.noReturn();
        }))
    );

    @Override
    public String toString() {
        return "ButtonVariable[" + name + ", state=" + state.buttonState + ", gfx=" + state.gfxStandardName + "]";
    }
}
