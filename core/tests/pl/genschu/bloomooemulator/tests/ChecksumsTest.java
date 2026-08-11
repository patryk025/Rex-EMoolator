package pl.genschu.bloomooemulator.tests;

import org.junit.jupiter.api.Test;
import pl.genschu.bloomooemulator.utils.Checksums;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ChecksumsTest {
    @Test
    void testCRC16CCITT() {
        // vector from listing in ECMA TR/112-2 1-st Edition / December 2023,
        // Universal Disk Format (UDF) specification – Part 2 (Revision 2.60), page 113
        // https://ecma-international.org/wp-content/uploads/ECMA_TR-112-2_1st_edition_december_2023.pdf
        byte[] data = { 0x70, 0x6A, 0x77 };
        int expected = 0x3299; // expected CRC value
        int actual = Checksums.crc16Ccitt(data);
        assertEquals(expected, actual);
    }
}
