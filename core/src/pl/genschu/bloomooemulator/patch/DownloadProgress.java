package pl.genschu.bloomooemulator.patch;

/**
 * Reports byte-level progress of a patch download so a UI can drive a progress bar.
 * Invoked on the calling (background) thread — listeners must marshal to their UI
 * thread themselves.
 *
 * <p>{@code totalBytes} is {@code -1} when the server omits {@code Content-Length}
 * (e.g. chunked transfer); UIs should fall back to an indeterminate indicator then.
 */
@FunctionalInterface
public interface DownloadProgress {
    void onProgress(long bytesRead, long totalBytes);
}
