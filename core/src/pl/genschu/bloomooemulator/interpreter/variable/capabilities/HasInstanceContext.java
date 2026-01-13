package pl.genschu.bloomooemulator.interpreter.variable.capabilities;

import pl.genschu.bloomooemulator.interpreter.context.ContextV2;

/**
 * Used to collect graphics/buttons/etc. from class instances.
 *
 * When a CLASS is instantiated, it creates an INSTANCE variable with its own context.
 * Variables defined in the class are stored in that context.
 *
 * This is a capability interface - only INSTANCE variables should implement it.
 */
public interface HasInstanceContext {
    /**
     * Returns the context for this class instance.
     * Variables defined in the class are stored here.
     *
     * @return Instance context (never null)
     */
    ContextV2 getInstanceContext();
}
