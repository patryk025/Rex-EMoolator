package pl.genschu.bloomooemulator.interpreter.context;

import pl.genschu.bloomooemulator.interpreter.variable.Variable;
import pl.genschu.bloomooemulator.interpreter.variable.VariableType;

import java.util.*;

/**
 * Maintains cached views of variables by type (part of optimization efforts).
 */
public class VariableCacheIndex {
    private final Map<String, Variable> graphics = new LinkedHashMap<>();
    private final Map<String, Variable> buttons = new LinkedHashMap<>();
    private final Map<String, Variable> timers = new LinkedHashMap<>();
    private final Map<String, Variable> sounds = new LinkedHashMap<>();
    private final Map<String, Variable> texts = new LinkedHashMap<>();

    private Variable mouse = null;
    private Variable keyboard = null;
    private Variable world = null;

    /**
     * Indexes a variable by type.
     * Called automatically by VariableStore.set().
     */
    public void index(String name, Variable var) {
        VariableType varType = var.type();

        switch (varType) {
            case ANIMO, IMAGE, SEQUENCE -> graphics.put(name, var);
            case BUTTON -> buttons.put(name, var);
            case TIMER -> timers.put(name, var);
            case SOUND -> sounds.put(name, var);
            case TEXT -> texts.put(name, var);
            case MOUSE -> mouse = var;
            case KEYBOARD -> keyboard = var;
            case WORLD -> world = var;
            default -> {} // Not cached
        }
    }

    /**
     * Removes a variable from all caches.
     *
     * @param name Variable name
     * @param oldVar The variable being removed (for singleton check)
     */
    public void remove(String name, Variable oldVar) {
        graphics.remove(name);
        buttons.remove(name);
        timers.remove(name);
        sounds.remove(name);
        texts.remove(name);

        if (oldVar != null) {
            if (mouse == oldVar) mouse = null;
            if (keyboard == oldVar) keyboard = null;
            if (world == oldVar) world = null;
        }
    }

    /**
     * Clears all caches.
     */
    public void clear() {
        graphics.clear();
        buttons.clear();
        timers.clear();
        sounds.clear();
        texts.clear();
        mouse = null;
        keyboard = null;
        world = null;
    }

    /**
     * Explicitly registers a variable as a button (e.g., AnimoVariable with ASBUTTON).
     */
    public void addButton(String name, Variable var) {
        buttons.put(name, var);
    }

    /**
     * Explicitly unregisters a variable from buttons.
     */
    public void removeButton(String name) {
        buttons.remove(name);
    }

    // ===== Getters (unmodifiable views) =====

    public Map<String, Variable> getGraphics() {
        return Collections.unmodifiableMap(graphics);
    }

    public Map<String, Variable> getButtons() {
        return Collections.unmodifiableMap(buttons);
    }

    public Map<String, Variable> getTimers() {
        return Collections.unmodifiableMap(timers);
    }

    public Map<String, Variable> getSounds() {
        return Collections.unmodifiableMap(sounds);
    }

    public Map<String, Variable> getTexts() {
        return Collections.unmodifiableMap(texts);
    }

    public Variable getMouse() {
        return mouse;
    }

    public Variable getKeyboard() {
        return keyboard;
    }

    public Variable getWorld() {
        return world;
    }
}
