package pl.genschu.bloomooemulator.engine.context;

import pl.genschu.bloomooemulator.engine.Game;

import java.util.Map;

/**
 * Abstraction over the interpreter context, used by engine managers (Render, Update, Input)
 * and Game.java for lifecycle management.
 *
 * Both v1.Context and v2.Context implement this interface, allowing the engine
 * to be decoupled from a specific interpreter version.
 *
 * Variable return types use EngineVariable - callers cast to specific concrete types
 * (v1 or v2) as needed via instanceof checks.
 */
public interface GameContext {

    // ============================================================
    // Variable access (read)
    // ============================================================

    /**
     * Gets a variable by name (full lookup chain including parent contexts).
     */
    EngineVariable getVariable(String name);

    /**
     * Gets all variables from this context (not including parent by default).
     */
    Map<String, ? extends EngineVariable> getVariables();

    /**
     * Checks if a variable exists anywhere in the context hierarchy
     * (local store, additional contexts, parent contexts).
     */
    boolean hasVariable(String name);

    // ============================================================
    // Typed variable collections (cached views, includes hierarchy)
    // ============================================================

    /**
     * Gets all graphics variables (ANIMO, IMAGE, SEQUENCE) from context hierarchy.
     */
    Map<String, ? extends EngineVariable> getGraphicsVariables();

    /**
     * Gets all button variables from context hierarchy.
     */
    Map<String, ? extends EngineVariable> getButtonsVariables();

    /**
     * Gets all timer variables from context hierarchy.
     */
    Map<String, ? extends EngineVariable> getTimerVariables();

    /**
     * Gets all text variables from context hierarchy.
     */
    Map<String, ? extends EngineVariable> getTextVariables();

    /**
     * Gets all sound variables from context hierarchy.
     */
    Map<String, ? extends EngineVariable> getSoundVariables();

    // ============================================================
    // Singleton variables
    // ============================================================

    /**
     * Gets the MOUSE variable (searches up parent chain).
     */
    EngineVariable getMouseVariable();

    /**
     * Gets the KEYBOARD variable (searches up parent chain).
     */
    EngineVariable getKeyboardVariable();

    /**
     * Gets the WORLD variable (searches up parent chain).
     */
    EngineVariable getWorldVariable();

    // ============================================================
    // Game reference
    // ============================================================

    /**
     * Gets the game instance (searches up parent chain).
     */
    Game getGame();

    /**
     * Sets the game instance.
     */
    void setGame(Game game);
}
