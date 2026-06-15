package pl.genschu.bloomooemulator.encoding;

import java.io.ByteArrayOutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * Decrypts the variable-shift transposition cipher used to obfuscate the
 * engine's text scripts (CNV/DEF/CLASS/SEQ).
 *
 * <p>The cipher operates on <b>raw bytes</b> and wraps every result modulo 256
 * (byte space), exactly like the original engine. Earlier implementations ran
 * the arithmetic on charset-decoded {@code char}s, which neither wrapped at 256
 * nor preserved byte values — and patched the resulting artefacts with
 * platform-specific magic-number remaps. Working on bytes makes those hacks
 * unnecessary.
 */
public class ScriptDecypher {

    /** Scripts are authored in the Windows Central-European code page. */
    private static final Charset SCRIPT_CHARSET = Charset.forName("windows-1250");

    /**
     * Returns the decoded text. If {@code bytes} start with the {@code {<X:N>}}
     * cipher header the payload is decrypted; otherwise the bytes are decoded
     * with the platform charset (matching how plain scripts have always loaded).
     */
    public static String decypherIfNeeded(byte[] bytes) {
        if (!isEncrypted(bytes)) {
            return new String(bytes, Charset.defaultCharset());
        }
        int headerEnd = indexOf(bytes, (byte) '}');
        if (headerEnd < 0) {
            return new String(bytes, Charset.defaultCharset());
        }
        String header = new String(bytes, 0, headerEnd + 1, StandardCharsets.US_ASCII);
        String[] param = header.replace("{<", "").replace(">}", "").split(":");
        int offset = Integer.parseInt(param[1].trim());
        if (param[0].equalsIgnoreCase("D")) {
            offset = -offset;
        }
        return decode(bytes, headerEnd + 1, offset);
    }

    /** True if the buffer carries the {@code {<X:N>}} cipher header. */
    public static boolean isEncrypted(byte[] bytes) {
        return bytes.length >= 2 && bytes[0] == '{' && bytes[1] == '<';
    }

    /**
     * Decrypts the ciphertext in {@code data}, starting at {@code start}.
     *
     * <p>{@code <E>} markers become newlines; raw CR/LF bytes are ignored (they
     * are file formatting, not ciphertext); every output byte is taken modulo
     * 256; the result is decoded as windows-1250.
     *
     * @param data   buffer holding the ciphertext
     * @param start  index of the first ciphertext byte (after the header)
     * @param offset cipher offset; negative when the header direction is D
     */
    public static String decode(byte[] data, int start, int offset) {
        int movement = Math.abs(offset);
        if (movement == 0) {
            return new String(data, start, data.length - start, SCRIPT_CHARSET);
        }
        int direction = offset < 0 ? -1 : 1;

        ByteArrayOutputStream out = new ByteArrayOutputStream(Math.max(16, data.length - start));
        int step = 0;
        int i = start;
        while (i < data.length) {
            int b = data[i] & 0xFF;

            if (b == '<' && i + 2 < data.length && data[i + 1] == 'E' && data[i + 2] == '>') {
                out.write('\n');
                i += 3;
                continue;
            }
            if (b == '\r' || b == '\n') {
                i++;
                continue;
            }

            step = (step % movement) + 1;
            int magnitude = (step + 1) / 2;                 // ceil(step / 2)
            int shift = (step % 2 != 0) ? -magnitude : magnitude;
            out.write((b + shift * direction) & 0xFF);
            i++;
        }
        return out.toString(SCRIPT_CHARSET);
    }

    private static int indexOf(byte[] bytes, byte target) {
        for (int i = 0; i < bytes.length; i++) {
            if (bytes[i] == target) {
                return i;
            }
        }
        return -1;
    }
}
