package pl.genschu.bloomooemulator.engine.context;

/**
 * Common interface for variables accessible from the engine layer.
 * Both v1 (abstract class) and v2 (sealed interface) Variable hierarchies implement this,
 * allowing the engine to work with either implementation.
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
