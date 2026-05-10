package pl.genschu.bloomooemulator.engine.context;

/**
 * Common interface for variables accessible from the engine layer.
 * Keeps engine code decoupled from concrete interpreter variable implementations.
 */
public interface EngineVariable {
    /**
     * Returns the name of this variable.
     */
    String getName();

    /**
     * Returns the type name of this variable (e.g. "ANIMO", "IMAGE", "TIMER").
     */
    String getTypeName();
}
