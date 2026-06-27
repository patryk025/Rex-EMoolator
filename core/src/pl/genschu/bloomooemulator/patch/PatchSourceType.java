package pl.genschu.bloomooemulator.patch;

/**
 * Where a patch was obtained from. Drives how the patch manager refreshes it.
 *
 * LOCAL  - user dropped the folder in {@code <userDir>/patches/}. No remote refresh.
 * GITHUB - fetched from a GitHub repo (release tag or pinned commit SHA). Refreshable.
 * URL    - generic HTTP(S) download of a ZIP. Refresh re-downloads from the same URL.
 * GDRIVE - Google Drive share link (or bare file id). The direct-download URL and the
 *          large-file virus-scan confirmation are resolved by {@link GoogleDriveDownloader}.
 */
public enum PatchSourceType {
    LOCAL,
    GITHUB,
    URL,
    GDRIVE
}
