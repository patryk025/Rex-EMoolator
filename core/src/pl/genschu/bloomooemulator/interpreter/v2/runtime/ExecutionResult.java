package pl.genschu.bloomooemulator.interpreter.v2.runtime;

import pl.genschu.bloomooemulator.interpreter.v2.values.Value;

/**
 * Represents the result of executing an AST node.
 *
 * This sealed interface replaces the old system of throwing exceptions
 * for control flow (BreakException, OneBreakException, ReturnException).
 *
 * Benefits:
 * - No exception overhead
 * - Explicit control flow
 * - Type-safe pattern matching
 * - Easy to test and reason about
 */
public sealed interface ExecutionResult permits
    NormalResult,
    BreakResult,
    OneBreakResult,
    ReturnResult {

    /**
     * Returns the value produced by execution.
     * For control flow results (break, return), this may be NullValue or the return value.
     */
    Value getValue();

    /**
     * Returns true if execution should continue normally.
     * False for break, onebreak, and return.
     */
    boolean shouldContinue();

    /**
     * Returns true if this is a BREAK (break all loops).
     * Used to propagate breaks up the call stack.
     */
    boolean isBreakAll();

    /**
     * Returns true if this is a RETURN.
     * Used to propagate returns up the call stack.
     */
    boolean isReturn();
}
