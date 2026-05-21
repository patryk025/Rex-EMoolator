package pl.genschu.bloomooemulator.patch;

/**
 * ERROR  - blocks runtime mounting; UI should refuse to enable the offending set.
 * WARNING - mount succeeds but something is fishy; UI surfaces a banner.
 */
public enum PatchIssueSeverity {
    ERROR,
    WARNING
}
