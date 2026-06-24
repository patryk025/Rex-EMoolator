package pl.genschu.bloomooemulator.encoding;

/**
 * Decoder of CLZW2 format (8-byte header + raw LZO1X-1 stream).
 */
public final class CLZW2Compression {

    private static final int HEADER_LENGTH = 8;
    private static final int MINIMAL_ENCODED_LENGTH = HEADER_LENGTH + 3; // + EOS marker

    private CLZW2Compression() {}

    public static byte[] compress() {
        throw new UnsupportedOperationException("Not implemented");
    }

    public static byte[] decompress(byte[] data) {
        if (data.length < MINIMAL_ENCODED_LENGTH) {
            throw new IllegalArgumentException("Encoded data too short");
        }

        final int decodedLength = readIntLE(data, 0);
        final int encodedLength = readIntLE(data, 4);
        if (data.length - HEADER_LENGTH != encodedLength) {
            throw new IllegalArgumentException("Length of encoded data does not match header");
        }

        final byte[] out = new byte[decodedLength];
        final int end = data.length;
        int enc = HEADER_LENGTH;
        int dec = 0;
        boolean literalSeen = false;

        while (true) {
            // end of stream marker: 0x11 0x00 0x00
            if (enc + 3 <= end && data[enc] == 0x11 && data[enc + 1] == 0 && data[enc + 2] == 0) {
                enc += 3;
                break;
            }
            if (enc >= end) {
                throw new IllegalArgumentException("Encoded data ended unexpectedly at index " + enc);
            }

            final int op = data[enc] & 0xFF;
            final int nibble = op >>> 4;

            if (nibble == 0) {
                // --- literals run ---
                int litLen;
                if (op != 0) {
                    litLen = op + 3;
                    enc++;
                } else {
                    int zeros = 0;
                    while (data[enc] == 0) { zeros++; enc++; }
                    litLen = (data[enc++] & 0xFF) + 18 + (zeros - 1) * 255;
                }
                System.arraycopy(data, enc, out, dec, litLen);
                enc += litLen;
                dec += litLen;
                literalSeen = true;

            } else if (!literalSeen) {
                // --- first literals run (special stream start) ---
                if (op < 18) {
                    throw new IllegalArgumentException("Invalid first literal length " + op + " (min 18)");
                }
                int litLen = op - 17;
                enc++;
                System.arraycopy(data, enc, out, dec, litLen);
                enc += litLen;
                dec += litLen;
                literalSeen = true;

            } else if (nibble >= 0b0100) {
                // --- short match (M2) ---
                final int b0 = data[enc++] & 0xFF;
                final int length = ((b0 >> 5) & 0b111) + 1;
                int distance = (b0 >> 2) & 0b111;
                final int trailing = b0 & 0b0011;
                distance |= (data[enc++] & 0xFF) << 3;
                distance += 1;
                dec = copyMatch(out, dec, distance, length);
                dec = copyTrailing(data, out, enc, dec, trailing);
                enc += trailing;

            } else if (nibble >= 0b0010) {
                // --- medium-distance match (M3) ---
                int length = data[enc++] & 0b00011111;
                if (length == 0) {
                    int zeros = 0;
                    length = 31;
                    while (data[enc] == 0) { zeros++; enc++; }
                    length += (data[enc++] & 0xFF) + zeros * 255;
                }
                length += 2;
                final int b0 = data[enc++] & 0xFF;
                int distance = b0 >> 2;
                final int trailing = b0 & 0b0011;
                distance |= (data[enc++] & 0xFF) << 6;
                distance += 1;
                dec = copyMatch(out, dec, distance, length);
                dec = copyTrailing(data, out, enc, dec, trailing);
                enc += trailing;

            } else { // nibble == 0b0001
                // --- long-distance match (M4) ---
                final int b0 = data[enc++] & 0xFF;
                int distance = ((b0 & 0b1000) >> 3) << 14;
                int length = b0 & 0b0111;
                if (length == 0) {
                    int zeros = 0;
                    length = 7;
                    while (data[enc] == 0) { zeros++; enc++; }
                    length += (data[enc++] & 0xFF) + zeros * 255;
                }
                length += 2;
                final int b1 = data[enc++] & 0xFF;
                distance |= b1 >> 2;
                final int trailing = b1 & 0b0011;
                distance |= (data[enc++] & 0xFF) << 6;
                distance += 16384;
                dec = copyMatch(out, dec, distance, length);
                dec = copyTrailing(data, out, enc, dec, trailing);
                enc += trailing;
            }
        }

        if (dec != out.length) {
            throw new IllegalArgumentException(
                    "Decoded length " + dec + " does not match header length " + out.length);
        }
        return out;
    }

    /** Copies match backwards. Boundary check once (dec-distance only increases). */
    private static int copyMatch(byte[] out, int dec, int distance, int length) {
        if (dec - distance < 0) {
            throw new IllegalArgumentException("Out of bounds match source at index " + (dec - distance));
        }
        if (distance >= length) {
            // no overlapping -> single bulk copy
            System.arraycopy(out, dec - distance, out, dec, length);
            return dec + length;
        }
        // overlapping (RLE propagation) -> byte by byte
        final int stop = dec + length;
        while (dec < stop) {
            out[dec] = out[dec - distance];
            dec++;
        }
        return dec;
    }

    /** 0-3 literals appended to the match. */
    private static int copyTrailing(byte[] data, byte[] out, int enc, int dec, int count) {
        for (int i = 0; i < count; i++) {
            out[dec++] = data[enc++];
        }
        return dec;
    }

    private static int readIntLE(byte[] d, int off) {
        return (d[off] & 0xFF)
                | ((d[off + 1] & 0xFF) << 8)
                | ((d[off + 2] & 0xFF) << 16)
                | ((d[off + 3] & 0xFF) << 24);
    }
}
