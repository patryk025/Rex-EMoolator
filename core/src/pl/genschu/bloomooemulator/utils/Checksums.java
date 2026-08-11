package pl.genschu.bloomooemulator.utils;

public final class Checksums {

    private static final int CRC16_CCITT_POLYNOMIAL = 0x1021;
    private static final int[] CRC16_CCITT_TABLE = createCrc16CcittTable();

    private Checksums() {
        // Utility class
    }

    public static int crc16Ccitt(byte[] data) {
        return crc16Ccitt(data, 0, data.length);
    }

    public static int crc16Ccitt(byte[] data, int offset, int length) {
        int crc = 0;
        int end = offset + length;

        for (int i = offset; i < end; i++) {
            int index = ((crc >>> 8) ^ (data[i] & 0xFF)) & 0xFF;
            crc = (CRC16_CCITT_TABLE[index] ^ (crc << 8)) & 0xFFFF;
        }

        return crc;
    }

    private static int[] createCrc16CcittTable() {
        int[] table = new int[256];

        for (int i = 0; i < table.length; i++) {
            int crc = i << 8;

            for (int bit = 0; bit < 8; bit++) {
                crc = (crc & 0x8000) != 0
                        ? (crc << 1) ^ CRC16_CCITT_POLYNOMIAL
                        : crc << 1;
            }

            table[i] = crc & 0xFFFF;
        }

        return table;
    }
}
