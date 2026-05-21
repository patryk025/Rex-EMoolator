package pl.genschu.bloomooemulator.patch;

import java.io.Serializable;

/**
 * Origin metadata for an installed patch — used only by the patch manager
 * when deciding whether/how to refresh from upstream. The patch's overlay
 * files live on disk regardless of source type.
 *
 * For GITHUB: {@code url} is the repo (e.g. {@code https://github.com/owner/repo}),
 * {@code ref} is the pinned commit SHA or release tag.
 * For URL: {@code url} is the ZIP download URL, {@code ref} is unused.
 * For LOCAL: both may be null.
 */
public class PatchSource implements Serializable {
    private PatchSourceType type = PatchSourceType.LOCAL;
    private String url;
    private String ref;

    public PatchSource() {}

    public PatchSourceType getType() { return type; }
    public void setType(PatchSourceType type) { this.type = type; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public String getRef() { return ref; }
    public void setRef(String ref) { this.ref = ref; }
}
