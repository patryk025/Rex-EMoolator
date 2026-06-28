package pl.genschu.bloomooemulator.patch;

/**
 * CONFLICT         - two active patches declared each other (or one declared the other) as incompatible.
 * MISSING_REQUIRED - an active patch declares {@code requires: [X]} but X is not active.
 * SUPERSEDED       - active patch A declares {@code supersedes: [B]} and B is also active — redundant.
 */
public enum PatchIssueType {
    CONFLICT,
    MISSING_REQUIRED,
    SUPERSEDED
}
