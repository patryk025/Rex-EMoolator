package pl.genschu.bloomooemulator.interpreter.variable;

import pl.genschu.bloomooemulator.engine.Game;
import pl.genschu.bloomooemulator.interpreter.context.CloneRegistry;
import pl.genschu.bloomooemulator.interpreter.values.Value;

import java.util.List;

/**
 * Contextual access for variable methods that need runtime information.
 * Passed as a third parameter to VariableMethod.execute().
 *
 * May be null when called from outside interpreter (tests calling simple methods,
 * SequenceVariable internal calls). Methods that need context must document this requirement.
 */
public interface MethodContext {

    /**
     * Gets a variable by name from the current context hierarchy.
     */
    Variable getVariable(String name);

    /**
     * Sets a variable in the current (local) context.
     */
    void setVariable(String name, Variable variable);

    /**
     * Updates a variable in the context hierarchy (searches up the parent chain).
     *
     * @return true if the update succeeded
     */
    boolean updateVariable(String name, Variable variable);

    /**
     * Gets the Game instance for scene navigation and application control.
     * May return null in test contexts.
     */
    Game getGame();

    /**
     * Executes a BehaviourVariable's AST in a new frame.
     * Handles $1/$2 locals setup, THIS assignment, and frame push/pop.
     *
     * @param frameName  Label for the stack frame (e.g. "RUN:myBehaviour")
     * @param thisVar    Variable to set as THIS (may be null)
     * @param behaviour  The BehaviourVariable whose AST to execute
     * @param args       Arguments to bind as $1, $2, ...
     * @return The return value from the behaviour, or NullValue.INSTANCE
     */
    Value runBehaviour(String frameName, Variable thisVar, BehaviourVariable behaviour, List<Value> args);

    /**
     * Returns the CloneRegistry for the current context.
     */
    CloneRegistry clones();
}
