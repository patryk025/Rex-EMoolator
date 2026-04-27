package pl.genschu.bloomooemulator.interpreter.variable.capabilities;

import pl.genschu.bloomooemulator.interpreter.variable.Variable;

/**
 * Used to resolve DATABASE_CURSOR syntax.
 *
 * Example:
 * - Variable "MYDB" of type DATABASE
 * - Access "MYDB_CURSOR" resolves to MYDB.getCursor()
 *
 * This is a capability interface - only DATABASE variables should implement it.
 */
public interface HasCursor {
    /**
     * Returns the cursor variable for this database.
     *
     * @return Cursor variable
     */
    Variable getCursor();
}
