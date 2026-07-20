package pl.genschu.bloomooemulator.loader.helpers;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

class InputStreamBinaryReaderTest {
    @Test
    void readsLittleEndianPrimitives() throws IOException {
        BinaryReader reader = reader(0xFE, 0x34, 0x12, 0x78, 0x56, 0x34, 0x12, 0x00, 0x00, 0xC0, 0x3F);

        assertEquals(254, reader.readU8());
        assertEquals(0x1234, reader.readU16LE());
        assertEquals(0x12345678, reader.readI32LE());
        assertEquals(1.5f, reader.readF32LE());
    }

    @Test
    void readsStringsWithExplicitEncodingAndShape() throws IOException {
        byte[] bytes = {3, 0, 0, 0, 'a', 0, 'x', 'o', 'k', 0, 'z'};
        BinaryReader reader = new InputStreamBinaryReader(new ByteArrayInputStream(bytes));

        assertEquals("a", reader.readLengthPrefixedString32LE(StandardCharsets.US_ASCII, true));
        assertEquals("ok", reader.readNullTerminatedString(StandardCharsets.US_ASCII));
        assertEquals("z", reader.readFixedString(1, StandardCharsets.US_ASCII, false));
    }

    @Test
    void readBytesHandlesPartialReads() throws IOException {
        InputStream partial = new ByteArrayInputStream(new byte[]{1, 2, 3, 4}) {
            @Override
            public synchronized int read(byte[] buffer, int offset, int length) {
                return super.read(buffer, offset, Math.min(length, 1));
            }
        };

        assertArrayEquals(new byte[]{1, 2, 3, 4}, new InputStreamBinaryReader(partial).readBytes(4));
    }

    @Test
    void fixedSizeReadsFailOnTruncatedData() {
        BinaryReader reader = reader(1, 2, 3);
        assertThrows(EOFException.class, reader::readI32LE);
    }

    @Test
    void skipFullyFallsBackWhenStreamDoesNotSkip() throws IOException {
        InputStream noSkip = new ByteArrayInputStream(new byte[]{1, 2, 3}) {
            @Override
            public synchronized long skip(long count) {
                return 0;
            }
        };
        BinaryReader reader = new InputStreamBinaryReader(noSkip);

        reader.skipFully(2);
        assertEquals(3, reader.readU8());
    }

    private static BinaryReader reader(int... values) {
        byte[] bytes = new byte[values.length];
        for (int i = 0; i < values.length; i++) {
            bytes[i] = (byte) values[i];
        }
        return new InputStreamBinaryReader(new ByteArrayInputStream(bytes));
    }
}
