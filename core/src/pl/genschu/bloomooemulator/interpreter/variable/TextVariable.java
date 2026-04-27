package pl.genschu.bloomooemulator.interpreter.variable;

import pl.genschu.bloomooemulator.annotations.InternalMutable;
import pl.genschu.bloomooemulator.interpreter.context.Context;
import pl.genschu.bloomooemulator.interpreter.helpers.ArgumentHelper;
import pl.genschu.bloomooemulator.interpreter.values.*;
import pl.genschu.bloomooemulator.interpreter.variable.capabilities.Initializable;
import pl.genschu.bloomooemulator.geometry.shapes.Box2D;

import java.util.*;

/**
 * TextVariable represents a text element rendered on the game canvas.
 * Uses mutable TextState for text content, position, font, and justification.
 */
public record TextVariable(
    String name,
    @InternalMutable TextState state,
    Map<String, SignalHandler> signals
) implements Variable, Initializable {

    /**
     * Mutable state for text rendering.
     */
    public static final class TextState {
        public String text = "";
        public boolean visible = false;
        public int priority = 0;
        public Box2D rect = null;
        public String hJustify = "LEFT";
        public String vJustify = "TOP";
        public String fontName = null;
        public boolean toCanvas = false;

        public TextState() {}

        public TextState copy() {
            TextState copy = new TextState();
            copy.text = this.text;
            copy.visible = this.visible;
            copy.priority = this.priority;
            copy.rect = this.rect != null
                    ? new Box2D(rect.getXLeft(), rect.getYBottom(), rect.getXRight(), rect.getYTop())
                    : null;
            copy.hJustify = this.hJustify;
            copy.vJustify = this.vJustify;
            copy.fontName = this.fontName;
            copy.toCanvas = this.toCanvas;
            return copy;
        }

        public void dispose() {}
    }

    public TextVariable {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Variable name cannot be null or empty");
        }
        if (state == null) {
            state = new TextState();
        }
        if (signals == null) {
            signals = Map.of();
        } else {
            signals = Map.copyOf(signals);
        }
    }

    public TextVariable(String name) {
        this(name, new TextState(), Map.of());
    }

    // ========================================
    // INTERFACE IMPLEMENTATION
    // ========================================

    @Override
    public Value value() {
        return new StringValue(state.text);
    }

    @Override
    public VariableType type() {
        return VariableType.TEXT;
    }

    @Override
    public Variable withValue(Value newValue) {
        state.text = newValue.toDisplayString();
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
        return new TextVariable(name, state, newSignals);
    }

    @Override
    public Variable copyAs(String newName) {
        return new TextVariable(newName, state.copy(), new HashMap<>(signals));
    }

    // ========================================
    // INITIALIZABLE
    // ========================================

    @Override
    public void init(Context context) {
        String textAttr = context.attributes().get(name, "TEXT");
        if (textAttr != null) state.text = textAttr;

        String fontAttr = context.attributes().get(name, "FONT");
        if (fontAttr != null) state.fontName = fontAttr;

        String visibleAttr = context.attributes().get(name, "VISIBLE");
        if (visibleAttr != null) state.visible = visibleAttr.equalsIgnoreCase("TRUE");

        String priorityAttr = context.attributes().get(name, "PRIORITY");
        if (priorityAttr != null) {
            try { state.priority = Integer.parseInt(priorityAttr); } catch (NumberFormatException ignored) {}
        }

        String hJustifyAttr = context.attributes().get(name, "HJUSTIFY");
        if (hJustifyAttr != null) state.hJustify = hJustifyAttr.toUpperCase();

        String vJustifyAttr = context.attributes().get(name, "VJUSTIFY");
        if (vJustifyAttr != null) state.vJustify = vJustifyAttr.toUpperCase();

        String toCanvasAttr = context.attributes().get(name, "TOCANVAS");
        if (toCanvasAttr != null) state.toCanvas = toCanvasAttr.equalsIgnoreCase("TRUE");

        String rectAttr = context.attributes().get(name, "RECT");
        if (rectAttr != null) {
            parseRect(rectAttr, context);
        }
    }

    private void parseRect(String rectAttr, Context context) {
        // Try as variable reference
        Variable rectVar = context.getVariable(rectAttr);
        if (rectVar instanceof AnimoVariable animo && animo.getRect() != null) {
            state.rect = animo.getRect();
            return;
        }
        if (rectVar instanceof ImageVariable img && img.getRect() != null) {
            state.rect = img.getRect();
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
        } catch (NumberFormatException ignored) {}
    }

    // ========================================
    // CONVENIENT ACCESSORS (for rendering)
    // ========================================

    public String getText() { return state.text; }
    public boolean isVisible() { return state.visible && state.toCanvas; }
    public int getPriority() { return state.priority; }
    public Box2D getRect() { return state.rect; }
    public String getHJustify() { return state.hJustify; }
    public String getVJustify() { return state.vJustify; }
    public String getFontName() { return state.fontName; }

    // ========================================
    // METHODS DEFINITION
    // ========================================

    private static final Map<String, MethodSpec> METHODS = Map.ofEntries(
        Map.entry("HIDE", MethodSpec.of((self, args, ctx) -> {
            ((TextVariable) self).state.visible = false;
            return MethodResult.noReturn();
        })),

        Map.entry("SETJUSTIFY", MethodSpec.of((self, args, ctx) -> {
            TextVariable txt = (TextVariable) self;
            int xL = ArgumentHelper.getInt(args.get(0));
            int yB = ArgumentHelper.getInt(args.get(1));
            int xR = ArgumentHelper.getInt(args.get(2));
            int yT = ArgumentHelper.getInt(args.get(3));
            txt.state.rect = new Box2D(xL, yB, xR, yT);
            txt.state.hJustify = ArgumentHelper.getString(args.get(4)).toUpperCase();
            txt.state.vJustify = ArgumentHelper.getString(args.get(5)).toUpperCase();
            return MethodResult.noReturn();
        })),

        Map.entry("SETPRIORITY", MethodSpec.of((self, args, ctx) -> {
            ((TextVariable) self).state.priority = ArgumentHelper.getInt(args.get(0));
            return MethodResult.noReturn();
        })),

        Map.entry("SETTEXT", MethodSpec.of((self, args, ctx) -> {
            ((TextVariable) self).state.text = ArgumentHelper.getString(args.get(0));
            return MethodResult.noReturn();
        })),

        Map.entry("SHOW", MethodSpec.of((self, args, ctx) -> {
            ((TextVariable) self).state.visible = true;
            return MethodResult.noReturn();
        }))
    );

    @Override
    public String toString() {
        return "TextVariable[" + name + ", text=" + state.text + ", visible=" + state.visible + "]";
    }
}
