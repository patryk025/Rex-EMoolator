package pl.genschu.bloomooemulator.engine.filesystem;

import org.junit.jupiter.api.Test;
import pl.genschu.bloomooemulator.loader.helpers.InputStreamBinaryReader;
import pl.genschu.bloomooemulator.utils.Checksums;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UdfDescriptorReaderTest {

    @Test
    void acceptsValidTagChecksumAndDescriptorCrc() throws IOException {
        byte[] payload = {0x70, 0x6A, 0x77};
        byte[] descriptor = descriptor(261, 7, payload);

        UdfDescriptorReader.TaggedDescriptor actual = UdfDescriptorReader.read(
                new InputStreamBinaryReader(new ByteArrayInputStream(descriptor))
        );

        assertEquals(261, actual.tag().identifier());
        assertEquals(7, actual.tag().location());
        assertArrayEquals(payload, actual.payload());
    }

    @Test
    void rejectsInvalidTagChecksumBeforeReadingPayload() {
        byte[] descriptor = descriptor(261, 7, new byte[] {1, 2, 3});
        descriptor[0] ^= 1;

        IOException error = assertThrows(IOException.class, () ->
                UdfDescriptorReader.read(new InputStreamBinaryReader(
                        new ByteArrayInputStream(descriptor)
                ))
        );

        assertTrue(error.getMessage().contains("tag checksum"));
    }

    @Test
    void rejectsInvalidDescriptorCrc() {
        byte[] descriptor = descriptor(261, 7, new byte[] {1, 2, 3});
        descriptor[descriptor.length - 1] ^= 1;

        IOException error = assertThrows(IOException.class, () ->
                UdfDescriptorReader.read(new InputStreamBinaryReader(
                        new ByteArrayInputStream(descriptor)
                ))
        );

        assertTrue(error.getMessage().contains("descriptor CRC"));
    }

    private static byte[] descriptor(int identifier, long location, byte[] payload) {
        byte[] result = Arrays.copyOf(new byte[16], 16 + payload.length);
        putU16LE(result, 0, identifier);
        putU16LE(result, 2, 3);
        putU16LE(result, 6, 1);
        putU16LE(result, 8, Checksums.crc16Ccitt(payload));
        putU16LE(result, 10, payload.length);
        putU32LE(result, 12, location);
        System.arraycopy(payload, 0, result, 16, payload.length);

        int checksum = 0;
        for (int i = 0; i < 16; i++) {
            if (i != 4) {
                checksum = (checksum + (result[i] & 0xFF)) & 0xFF;
            }
        }
        result[4] = (byte) checksum;
        return result;
    }

    private static void putU16LE(byte[] data, int offset, int value) {
        data[offset] = (byte) value;
        data[offset + 1] = (byte) (value >>> 8);
    }

    private static void putU32LE(byte[] data, int offset, long value) {
        data[offset] = (byte) value;
        data[offset + 1] = (byte) (value >>> 8);
        data[offset + 2] = (byte) (value >>> 16);
        data[offset + 3] = (byte) (value >>> 24);
    }
}
