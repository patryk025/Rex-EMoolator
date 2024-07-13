package pl.genschu.bloomooemulator.encoding;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

// adapted and translated from https://gist.github.com/Dove6/0d21e763919daa8b5049e20b6bdacfaa#file-clzw2_decoder-py
public class CLZW2Compression
{
    private static final int HEADER_LENGTH = 8;
    private static final byte[] ETX_MARKER = new byte[]{(byte) 0x11, 0x00, 0x00};
    private static final int MINIMAL_ENCODED_LENGTH = HEADER_LENGTH + ETX_MARKER.length;

    private static int readInt(byte[] data) {
        return ByteBuffer.wrap(Arrays.copyOfRange(data, 0, 4)).order(ByteOrder.LITTLE_ENDIAN).getInt();
    }

    private static int readShort(byte[] data) {
        return ByteBuffer.wrap(Arrays.copyOfRange(data, 0, 2)).order(ByteOrder.LITTLE_ENDIAN).getShort();
    }

    private static int readByte(byte[] data) {
        return data[0] & 0xFF;
    }

    private static int readMostSignificantNibble(byte[] data) {
        return (readByte(data) >> 4) & 0x0F;
    }

    public static byte[] compress() throws Exception {
        throw new Exception("Not implemented");
    }

    // translated from https://gist.github.com/Dove6/0d21e763919daa8b5049e20b6bdacfaa#file-clzw2_decoder-py
    public static byte[] decompress(byte[] data) throws IllegalArgumentException {
        if (data.length < MINIMAL_ENCODED_LENGTH) {
            throw new IllegalArgumentException("Encoded data too short");
        }

        byte[] header = Arrays.copyOfRange(data, 0, HEADER_LENGTH);
        data = Arrays.copyOfRange(data, HEADER_LENGTH, data.length);

        int decodedLength = readInt(header);
        int encodedLength = readInt(Arrays.copyOfRange(header, 4, 8));

        if (data.length != encodedLength) {
            throw new IllegalArgumentException("Length of encoded data does not match length from the header");
        }

        byte[] decodedData = new byte[decodedLength];

        int encPtr = 0;
        int decPtr = 0;
        boolean literalEncountered = false;

        int zeroBytesCount = 0;
        int literalLength = 0;

        while(true) {
            if(encPtr >= data.length) {
                throw new IllegalArgumentException("Encoded data ended unexpectedly. Out of bounds at index " + encPtr);
            }
            if (Arrays.equals(Arrays.copyOfRange(data, encPtr, encPtr + 3), ETX_MARKER)) {
                encPtr += 3;
                break;
            }
            if(decPtr >= decodedData.length) {
                throw new IllegalArgumentException("Out of bounds access to decoded data at index " + decPtr);
            }
            if (readMostSignificantNibble(Arrays.copyOfRange(data, encPtr, encPtr + 1)) == 0b0000) {
                zeroBytesCount = 0;
                while (readByte(Arrays.copyOfRange(data, encPtr, encPtr + 1)) == 0) {
                    zeroBytesCount += 1;
                    encPtr += 1;
                }
                literalLength = readByte(Arrays.copyOfRange(data, encPtr, encPtr + 1));
                encPtr += 1;
                if(zeroBytesCount == 0) {
                    literalLength += 3;
                }
                else {
                    literalLength += 18 + (zeroBytesCount - 1) * 255;
                }
                System.arraycopy(data, encPtr, decodedData, decPtr, literalLength);
                encPtr += literalLength;
                decPtr += literalLength;
                literalEncountered = true;
            }
            else if(readMostSignificantNibble(Arrays.copyOfRange(data, encPtr, encPtr + 1)) > 0b0000 && !literalEncountered) {
                literalLength = readByte(Arrays.copyOfRange(data, encPtr, encPtr + 1));
                encPtr += 1;
                if(literalLength < 18) {
                    throw new IllegalArgumentException("Invalid literal length " + literalLength + " (should be at least 18)");
                }
                literalLength -= 17;
                System.arraycopy(data, encPtr, decodedData, decPtr, literalLength);
                encPtr += literalLength;
                decPtr += literalLength;
                literalEncountered = true;
            }
            else if(readMostSignificantNibble(Arrays.copyOfRange(data, encPtr, encPtr + 1)) >= 0b0100) {
                if(!literalEncountered) {
                    throw new IllegalArgumentException("No literal encountered before reference symbol 0b0100");
                }
                int buffer = readByte(Arrays.copyOfRange(data, encPtr, encPtr + 1));
                encPtr += 1;
                int length = (buffer >> 5) & 0b111;
                length += 1;
                int distance = (buffer >> 2) & 0b111;
                int followingLiteralLength = buffer & 0b0011;
                distance |= (readByte(Arrays.copyOfRange(data, encPtr, encPtr + 1)) << 3);
                distance += 1;
                encPtr += 1;
                for(int i = 0; i < length; i++) {
                    if(decPtr - distance <= 0) {
                        throw new IllegalArgumentException("Out of bounds access to decoded data at index " + decPtr);
                    }
                    System.arraycopy(decodedData, decPtr - distance, decodedData, decPtr, 1);
                    decPtr += 1;
                }
                System.arraycopy(data, encPtr, decodedData, decPtr, followingLiteralLength);
                encPtr += followingLiteralLength;
                decPtr += followingLiteralLength;
            }
            else if(readMostSignificantNibble(Arrays.copyOfRange(data, encPtr, encPtr + 1)) >= 0b0010) {
                if (!literalEncountered) {
                    throw new IllegalArgumentException("No literal encountered before reference symbol 0b0010");
                }
                int length = readByte(Arrays.copyOfRange(data, encPtr, encPtr + 1)) & 0b00011111;
                encPtr += 1;
                if(length == 0) {
                    zeroBytesCount = 0;
                    length += 31;
                    while (readByte(Arrays.copyOfRange(data, encPtr, encPtr + 1)) == 0) {
                        zeroBytesCount += 1;
                        encPtr += 1;
                    }
                    length += readByte(Arrays.copyOfRange(data, encPtr, encPtr + 1));
                    encPtr += 1;
                    length += zeroBytesCount * 255;
                }
                length += 2;
                int buffer = readByte(Arrays.copyOfRange(data, encPtr, encPtr + 1));
                encPtr += 1;
                int distance = buffer >> 2;
                int followingLiteralLength = buffer & 0b0011;
                distance |= (readByte(Arrays.copyOfRange(data, encPtr, encPtr + 1)) << 6);
                distance += 1;
                encPtr += 1;
                for(int i = 0; i < length; i++) {
                    if(decPtr - distance <= 0) {
                        throw new IllegalArgumentException("Out of bounds access to decoded data at index " + decPtr);
                    }
                    System.arraycopy(decodedData, decPtr - distance, decodedData, decPtr, 1);
                    decPtr += 1;
                }
                System.arraycopy(data, encPtr, decodedData, decPtr, followingLiteralLength);
                encPtr += followingLiteralLength;
                decPtr += followingLiteralLength;
            }
            else if(readMostSignificantNibble(Arrays.copyOfRange(data, encPtr, encPtr + 1)) == 0b0001) {
                if (!literalEncountered) {
                    throw new IllegalArgumentException("No literal encountered before reference symbol 0b0001");
                }
                int buffer = readByte(Arrays.copyOfRange(data, encPtr, encPtr + 1));
                encPtr += 1;
                int distance = ((buffer & 0b1000) >> 3) << 14;
                int length = buffer & 0b0111;
                if (length == 0) {
                    zeroBytesCount = 0;
                    length += 7;
                    while (readByte(Arrays.copyOfRange(data, encPtr, encPtr + 1)) == 0) {
                        zeroBytesCount += 1;
                        encPtr += 1;
                    }
                    length += readByte(Arrays.copyOfRange(data, encPtr, encPtr + 1));
                    encPtr += 1;
                    length += zeroBytesCount * 255;
                }
                length += 2;
                buffer = readByte(Arrays.copyOfRange(data, encPtr, encPtr + 1));
                encPtr += 1;
                distance |= buffer >> 2;
                int followingLiteralLength = buffer & 0b0011;
                distance |= (readByte(Arrays.copyOfRange(data, encPtr, encPtr + 1)) << 6);
                distance += 16384;
                encPtr += 1;
                for (int i = 0; i < length; i++) {
                    if (decPtr - distance <= 0) {
                        throw new IllegalArgumentException("Out of bounds access to decoded data at index " + decPtr);
                    }
                    System.arraycopy(decodedData, decPtr - distance, decodedData, decPtr, 1);
                    decPtr += 1;
                }
                System.arraycopy(data, encPtr, decodedData, decPtr, followingLiteralLength);
                encPtr += followingLiteralLength;
                decPtr += followingLiteralLength;
            }
        }

        if(decPtr != decodedData.length) {
            throw new IllegalArgumentException("Length of decoded data does not match length from the header");
        }

        return decodedData;
    }
}
