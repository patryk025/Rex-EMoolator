package pl.genschu.bloomooemulator.patch;

/**
 * One validation finding produced by {@link PatchManager#validate}.
 *
 * {@code patchId} is the patch that triggered the rule; {@code relatedPatchId}
 * is the other side (the conflicting / required / superseded patch). For
 * {@link PatchIssueType#MISSING_REQUIRED}, {@code relatedPatchId} may name a
 * patch that isn't installed at all — UI should treat it as a plain string.
 */
public final class PatchIssue {
    private final PatchIssueType type;
    private final PatchIssueSeverity severity;
    private final String patchId;
    private final String relatedPatchId;
    private final String message;

    public PatchIssue(PatchIssueType type, PatchIssueSeverity severity,
                      String patchId, String relatedPatchId, String message) {
        this.type = type;
        this.severity = severity;
        this.patchId = patchId;
        this.relatedPatchId = relatedPatchId;
        this.message = message;
    }

    public PatchIssueType getType() { return type; }
    public PatchIssueSeverity getSeverity() { return severity; }
    public String getPatchId() { return patchId; }
    public String getRelatedPatchId() { return relatedPatchId; }
    public String getMessage() { return message; }
}
