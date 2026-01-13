package pl.genschu.bloomooemulator.interpreter.variable.capabilities;

import pl.genschu.bloomooemulator.interpreter.variable.Variable;

import java.util.List;

/**
 * Used by OriginalEngineQuirksHandler to resolve clone references.
 *
 * This is a capability interface - not all Variables need to implement it.
 * Only variables that can have clones (like graphics objects) should implement this.
 */
public interface CloneableVar {
    /**
     * Returns list of clones for this variable.
     * Empty list if no clones exist.
     *
     * @return Unmodifiable list of clones
     */
    List<Variable> getClones();

    /**
     * Adds a clone to this variable.
     *
     * @param clone The clone to add
     * @return New variable
     */
    Variable withAddedClone(Variable clone);
}
