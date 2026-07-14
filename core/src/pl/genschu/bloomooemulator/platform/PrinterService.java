package pl.genschu.bloomooemulator.platform;

import com.badlogic.gdx.graphics.Pixmap;

/**
 * Platform hook for printing an image on the system printer (CPrinter in the
 * original engine, used by the KOLOROWANKA PRINT method).
 * <p>
 * Contract: the caller disposes the pixmap right after this call returns, so
 * implementations must copy the pixel data synchronously. The system print
 * dialog must not block the calling (GL) thread — the game keeps running and
 * guards itself via DISABLE/ENABLE while the dialog is open.
 */
public interface PrinterService {
    /** Whether printing is available on this platform/runtime. */
    boolean isSupported();

    /**
     * Shows the system print dialog and prints the image scaled to the page.
     *
     * @param image   picture to print; valid only for the duration of the call
     * @param jobName print job name shown in the system print queue
     */
    void printImage(Pixmap image, String jobName);
}
