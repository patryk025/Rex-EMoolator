package pl.genschu.bloomooemulator.interpreter.variable;

import com.badlogic.gdx.Gdx;
import pl.genschu.bloomooemulator.annotations.InternalMutable;
import pl.genschu.bloomooemulator.engine.context.CanvasBoundsProvider;
import pl.genschu.bloomooemulator.engine.decision.events.ButtonEvent;
import pl.genschu.bloomooemulator.engine.decision.states.ButtonState;
import pl.genschu.bloomooemulator.geometry.coordinates.CanvasRect;
import pl.genschu.bloomooemulator.interpreter.context.Context;
import pl.genschu.bloomooemulator.interpreter.helpers.ArgumentHelper;
import pl.genschu.bloomooemulator.interpreter.values.*;
import pl.genschu.bloomooemulator.interpreter.variable.capabilities.Initializable;

import java.util.*;

/**
 * ButtonVariable represents an interactive button in the game.
 * Uses mutable ButtonVarState for button state machine, rectangle, and graphics/sound references.
 */
public record ButtonVariable(
    String name,
    @InternalMutable ButtonVarState state,
    Map<String, SignalHandler> signals
) implements Variable, Initializable, CanvasBoundsProvider {

    /**
     * Mutable state for button interaction.
     */
    public static final class ButtonVarState {
        public ButtonState buttonState = ButtonState.INIT;
        /** CHotSpot priority when no graphic supplies it (PIKLIB8 10030010). */
        public int hotspotPriority = 0;
        public CanvasRect rect = null;
        /** Live bounds source used by GFX-backed buttons. */
        public CanvasBoundsProvider rectProvider = null;
        public String rectVarName = null;
        /** True when the hotspot is derived from GFXSTANDARD rather than an explicit RECT/SETRECT. */
        public boolean rectFollowsStandard = true;
        public String gfxStandardName = null;
        public String gfxOnMoveName = null;
        public String gfxOnClickName = null;
        public String sndStandardName = null;
        public String sndOnMoveName = null;
        public String sndOnClickName = null;
        public String dragName = null;
        public boolean draggable = false;

        public ButtonVarState() {}

        public ButtonVarState copy() {
            ButtonVarState copy = new ButtonVarState();
            copy.buttonState = this.buttonState;
            copy.hotspotPriority = this.hotspotPriority;
            copy.rect = this.rect;
            copy.rectProvider = this.rectProvider;
            copy.rectVarName = this.rectVarName;
            copy.rectFollowsStandard = this.rectFollowsStandard;
            copy.gfxStandardName = this.gfxStandardName;
            copy.gfxOnMoveName = this.gfxOnMoveName;
            copy.gfxOnClickName = this.gfxOnClickName;
            copy.sndStandardName = this.sndStandardName;
            copy.sndOnMoveName = this.sndOnMoveName;
            copy.sndOnClickName = this.sndOnClickName;
            copy.dragName = this.dragName;
            copy.draggable = this.draggable;
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

        String drag = context.attributes().get(name, "DRAG");
        if (drag != null && !drag.isBlank()) state.dragName = drag;

        String draggable = context.attributes().get(name, "DRAGGABLE");
        if (draggable != null) state.draggable = draggable.equalsIgnoreCase("TRUE");

        // Parse RECT attribute
        String rectAttr = context.attributes().get(name, "RECT");
        if (rectAttr != null) {
            state.rectFollowsStandard = false;
            parseRect(rectAttr, context);
        }

        // Load rect from GFXSTANDARD if no RECT set
        if (state.rect == null && state.gfxStandardName != null) {
            loadRectFromGfx(state.gfxStandardName, context);
        }

        // Set default state
        String enableAttr = context.attributes().get(name, "ENABLE");
        if (enableAttr != null && enableAttr.equalsIgnoreCase("FALSE")) {
            changeState(ButtonEvent.DISABLE, context);
        } else {
            changeState(ButtonEvent.ENABLE, context);
        }
    }

    private void parseRect(String rectAttr, Context context) {
        // Try as variable reference first
        Variable rectVar = context.getVariable(rectAttr);
        if (rectVar instanceof CanvasBoundsProvider boundsProvider) {
            state.rectVarName = rectAttr;
            // RECT=<variable> historically snapshots the bounds during initialization.
            state.rect = boundsProvider.getCanvasBounds() != null
                    ? boundsProvider.getCanvasBounds()
                    : new CanvasRect(0, 0, 0, 0);
            state.rectProvider = null;
            return;
        }
        // Try as comma-separated coordinates
        try {
            String[] parts = rectAttr.split(",");
            if (parts.length >= 4) {
                int xL = Integer.parseInt(parts[0].trim());
                int yTop = Integer.parseInt(parts[1].trim());
                int xR = Integer.parseInt(parts[2].trim());
                int yBottom = Integer.parseInt(parts[3].trim());
                state.rect = new CanvasRect(xL, yTop, xR, yBottom);
                state.rectProvider = null;
            }
        } catch (NumberFormatException e) {
            Gdx.app.error("ButtonVariable", "Invalid RECT format: " + rectAttr);
        }
    }

    private void loadRectFromGfx(String gfxName, Context context) {
        Variable gfx = context.getVariable(gfxName);
        if (gfx instanceof CanvasBoundsProvider boundsProvider
                && boundsProvider.getCanvasBounds() != null) {
            state.rectVarName = gfxName;
            state.rectProvider = boundsProvider;
            state.rect = boundsProvider.getCanvasBounds();
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
                showGfx(state.gfxOnMoveName, false, context);
                showGfx(state.gfxOnClickName, false, context);
                showGfx(state.gfxStandardName, true, context);
                playSnd(state.sndStandardName, context);
                if (oldState == ButtonState.HOVERED) {
                    emitSignal("ONFOCUSOFF");
                }
            }
            case HOVERED -> {
                boolean hasOnMove = state.gfxOnMoveName != null;
                showGfx(state.gfxStandardName, !hasOnMove, context);
                showGfx(state.gfxOnClickName, false, context);
                showGfx(state.gfxOnMoveName, true, context);
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
                boolean hasOnClick = state.gfxOnClickName != null;
                showGfx(state.gfxStandardName, !hasOnClick, context);
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
                showGfx(state.gfxOnMoveName, false, context);
                showGfx(state.gfxOnClickName, false, context);
                showGfx(state.gfxStandardName, true, context);
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
            animo.state().toCanvas = true;
            if (visible) animo.callMethod("SHOW", List.of());
            else animo.callMethod("HIDE", List.of());
        } else if (gfx instanceof ImageVariable img) {
            img.state().visible = visible;
        }
    }

    /** Removes a graphic from the canvas entirely (mirrors CRefreshScreen::operator>>). */
    private void removeGfxFromCanvas(String varName, Context context) {
        if (varName == null) return;
        Variable gfx = context.getVariable(varName);
        if (gfx instanceof AnimoVariable animo) {
            animo.callMethod("HIDE", List.of());
            animo.state().toCanvas = false;
        } else if (gfx instanceof ImageVariable img) {
            img.state().visible = false;
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
    public CanvasRect getRect() { return getCanvasBounds(); }
    @Override
    public CanvasRect getCanvasBounds() {
        return state.rectProvider != null ? state.rectProvider.getCanvasBounds() : state.rect;
    }
    public String getDragName() { return state.dragName; }
    public boolean isDraggable() { return state.draggable; }

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
            btn.changeState(ButtonEvent.DISABLE, ctx.context());
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
            btn.changeState(ButtonEvent.DISABLE_BUT_VISIBLE, ctx.context());
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
            btn.changeState(ButtonEvent.ENABLE, ctx.context());
            return MethodResult.noReturn();
        })),

        Map.entry("ENABLEDRAGGING", MethodSpec.of((self, args, ctx) -> {
            ((ButtonVariable) self).state.draggable = true;
            return MethodResult.noReturn();
        })),

        Map.entry("DISABLEDRAGGING", MethodSpec.of((self, args, ctx) -> {
            ((ButtonVariable) self).state.draggable = false;
            return MethodResult.noReturn();
        })),

        Map.entry("GETSTD", MethodSpec.of((self, args, ctx) -> {
            ButtonVariable btn = (ButtonVariable) self;
            String stdName = btn.state.gfxStandardName;
            if(stdName == null || stdName.isEmpty()) {
                return MethodResult.returns(new StringValue("UNKNOWN"));
            }
            return MethodResult.returns(new StringValue(stdName));
        })),

        Map.entry("SETPRIORITY", MethodSpec.of((self, args, ctx) -> {
            ButtonVariable btn = (ButtonVariable) self;
            int priority = ArgumentHelper.getInt(args.get(0));
            btn.state.hotspotPriority = priority;
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
            btn.state.rectFollowsStandard = false;
            if (args.size() == 1) {
                // SETRECT(varName)
                String varName = ArgumentHelper.getString(args.get(0));
                Variable rectVar = ctx.getVariable(varName);
                if (rectVar instanceof CanvasBoundsProvider boundsProvider) {
                    btn.state.rectVarName = varName;
                    btn.state.rectProvider = boundsProvider;
                    btn.state.rect = boundsProvider.getCanvasBounds();
                }
            } else if (args.size() >= 4) {
                // SETRECT(left, top, right, bottom)
                int xL = ArgumentHelper.getInt(args.get(0));
                int yTop = ArgumentHelper.getInt(args.get(1));
                int xR = ArgumentHelper.getInt(args.get(2));
                int yBottom = ArgumentHelper.getInt(args.get(3));
                btn.state.rect = new CanvasRect(xL, yTop, xR, yBottom);
                btn.state.rectProvider = null;
                btn.state.rectVarName = null;
            }
            return MethodResult.noReturn();
        })),

        // SETSTD(graphicObjectName, removePreviousFromCanvas=true)
        Map.entry("SETSTD", MethodSpec.of((self, args, ctx) -> {
            ButtonVariable btn = (ButtonVariable) self;
            String varName = ArgumentHelper.getString(args.get(0));
            boolean removePreviousFromCanvas = ArgumentHelper.getBoolean(args, 1, true);

            // Detach the previous standard graphic from the canvas before swapping.
            String previous = btn.state.gfxStandardName;
            if (removePreviousFromCanvas && previous != null && !previous.equals(varName)) {
                btn.removeGfxFromCanvas(previous, ctx.context());
            }

            btn.state.gfxStandardName = varName;

            // Set priority to 0 on the new standard graphics
            Variable gfx = ctx.getVariable(varName);
            if (gfx != null) {
                gfx.callMethod("SETPRIORITY", List.of(new IntValue(0)), ctx);
            }

            // CButton::Set rebuilds its CHotSpot from the new standard graphics
            // when no explicit active RECT was configured. Keep the same live
            // relationship so moving/reloading the graphics also moves the hit area.
            if (btn.state.rectFollowsStandard) {
                if (gfx instanceof CanvasBoundsProvider boundsProvider) {
                    btn.state.rectVarName = varName;
                    btn.state.rectProvider = boundsProvider;
                    btn.state.rect = boundsProvider.getCanvasBounds();
                } else {
                    btn.state.rectVarName = null;
                    btn.state.rectProvider = null;
                    btn.state.rect = null;
                }
            }

            // Reflect the swap immediately according to the current button state
            boolean stdVisible = switch (btn.state.buttonState) {
                case STANDARD, DISABLED_BUT_VISIBLE -> true;
                case HOVERED -> btn.state.gfxOnMoveName == null;
                case PRESSED -> btn.state.gfxOnClickName == null;
                default -> false;
            };
            btn.showGfx(varName, stdVisible, ctx.context());
            return MethodResult.noReturn();
        }))
    );

    @Override
    public String toString() {
        return "ButtonVariable[" + name + ", state=" + state.buttonState + ", gfx=" + state.gfxStandardName + "]";
    }
}
