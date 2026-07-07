package pl.genschu.bloomooemulator.loader;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Loader for the PTR format used by KOLOROWANKA
 *
 * <pre>
 * 4× short-string metadata, u32 width, u32 height,
 * u8 bitmap[w*h]           — outline brightness: 255 = blank paper, 0 = black line,
 * palette: u32 count + u32 colors (0x00BBGGRR, Windows COLORREF),
 * fields: u32 count + PtrField each.
 * </pre>
 */
public final class PtrLoader {

    private PtrLoader() {}

    /** Pixel region encoded as per-row spans. Row {@code i} lies at {@code y = top + i}. */
    public static final class Region {
        public int left, top, right, bottom;
        /** rows[i] = flattened span pairs: {xStart0, len0, xStart1, len1, ...} */
        public int[][] rows;

        public boolean contains(int x, int y) {
            if (x < left || y < top || x >= right || y >= bottom) return false;
            int row = y - top;
            if (rows == null || row < 0 || row >= rows.length) return false;
            int[] spans = rows[row];
            for (int i = 0; i + 1 < spans.length; i += 2) {
                if (x >= spans[i] && x < spans[i] + spans[i + 1]) return true;
            }
            return false;
        }
    }

    /** Conditional reaction fired when the owning field gets colored. */
    public static final class Reaction {
        /** Fires only when the field was painted with this palette index (−1 paint matches any). */
        public int triggerColorId;
        /** Event name to fire on the SEQANN sequence ("" = none). */
        public String annEventName = "";
        /** "; "-separated WAV list — one is picked at random ("" = none). */
        public String wavNames = "";
        /** Flattened pairs: {expectedColour0, fieldIndex0, ...} — all must match (see RE notes §7). */
        public int[] conditions = new int[0];
    }

    public static final class Field {
        public String name = "";
        public boolean locked;
        public boolean disabled;
        /** Do not draw the outline region while the field is uncolored. */
        public boolean hideOutlineWhenBlank;
        /** Initial palette index (−1 = uncoloured). */
        public int initialColorId = -1;
        /** Palette index required for ONFINISHED (−1 = does not count). */
        public int requiredColorId = -1;
        /** Fields sharing a group id are colored together (−1 = no group). */
        public int groupId = -1;
        public Region fillRegion;
        public Region outlineRegion;
        public final List<Reaction> reactions = new ArrayList<>();
    }

    public static final class PtrFile {
        public String meta1 = "", meta2 = "", meta3 = "", meta4 = "";
        public int width;
        public int height;
        /** Outline brightness per pixel: 255 = paper, 0 = black line. */
        public byte[] bitmap;
        /** Palette as 0x00BBGGRR (Windows COLORREF). */
        public int[] palette;
        public final List<Field> fields = new ArrayList<>();
    }

    public static PtrFile load(InputStream is) throws IOException {
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;
        while ((length = is.read(buffer)) != -1) {
            result.write(buffer, 0, length);
        }
        ByteBuffer buf = ByteBuffer.wrap(result.toByteArray()).order(ByteOrder.LITTLE_ENDIAN);
        PtrFile ptr = new PtrFile();

        ptr.meta1 = readShortString(buf);
        ptr.meta2 = readShortString(buf);
        ptr.meta3 = readShortString(buf);
        ptr.meta4 = readShortString(buf);

        ptr.width = buf.getInt();
        ptr.height = buf.getInt();
        if (ptr.width <= 0 || ptr.height <= 0 || (long) ptr.width * ptr.height > buf.remaining()) {
            throw new IOException("PTR: invalid dimensions " + ptr.width + "x" + ptr.height);
        }
        ptr.bitmap = new byte[ptr.width * ptr.height];
        buf.get(ptr.bitmap);

        int colorCount = buf.getInt();
        ptr.palette = new int[colorCount];
        for (int i = 0; i < colorCount; i++) {
            ptr.palette[i] = buf.getInt();
        }

        int fieldCount = buf.getInt();
        for (int i = 0; i < fieldCount; i++) {
            ptr.fields.add(readField(buf));
        }
        return ptr;
    }

    private static Field readField(ByteBuffer buf) {
        Field field = new Field();
        field.name = readFixedString(buf);
        field.locked = buf.get() != 0;
        field.disabled = buf.get() != 0;
        field.hideOutlineWhenBlank = buf.get() != 0;
        field.initialColorId = buf.getInt();
        field.requiredColorId = buf.getInt();
        field.groupId = buf.getInt();
        field.fillRegion = readRegion(buf);
        field.outlineRegion = readRegion(buf);

        int reactionCount = buf.getInt();
        for (int i = 0; i < reactionCount; i++) {
            Reaction reaction = new Reaction();
            reaction.triggerColorId = buf.getInt();
            reaction.annEventName = readFixedString(buf);
            reaction.wavNames = readFixedString(buf);
            int pairCount = buf.getInt();
            if (pairCount > 0) {
                reaction.conditions = new int[pairCount * 2];
                for (int p = 0; p < pairCount * 2; p++) {
                    reaction.conditions[p] = buf.getInt();
                }
            }
            field.reactions.add(reaction);
        }
        return field;
    }

    private static Region readRegion(ByteBuffer buf) {
        Region region = new Region();
        region.left = buf.getInt();
        region.top = buf.getInt();
        region.right = buf.getInt();
        region.bottom = buf.getInt();
        int rowCount = buf.getInt();
        region.rows = new int[rowCount][];
        for (int r = 0; r < rowCount; r++) {
            int spanCount = buf.getInt();
            int[] spans = new int[spanCount * 2];
            for (int s = 0; s < spanCount * 2; s++) {
                spans[s] = buf.getInt();
            }
            region.rows[r] = spans;
        }
        return region;
    }

    private static String readShortString(ByteBuffer buf) {
        int length = buf.get() & 0xFF;
        byte[] data = new byte[length];
        buf.get(data);
        return new String(data, StandardCharsets.ISO_8859_1);
    }

    /** char[256] zero-padded. */
    private static String readFixedString(ByteBuffer buf) {
        byte[] data = new byte[256];
        buf.get(data);
        int end = 0;
        while (end < data.length && data[end] != 0) end++;
        return new String(data, 0, end, StandardCharsets.ISO_8859_1);
    }
}
