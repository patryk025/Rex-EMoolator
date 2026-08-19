package pl.genschu.bloomooemulator.engine.filesystem;

import pl.genschu.bloomooemulator.loader.helpers.BinaryReader;
import pl.genschu.bloomooemulator.loader.helpers.InputStreamBinaryReader;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * On-disk structures used by the UDF reader.
 *
 * <p>The filesystem traversal deliberately lives outside this class. Keeping
 * these small value objects and parsers together makes byte offsets auditable
 * without mixing them with path handling or I/O policy.</p>
 */
final class UdfStructures {
    private static final long EXTENT_LENGTH_MASK = 0x3FFF_FFFFL;

    private UdfStructures() {
    }

    enum TagIdentifier {
        UNKNOWN(0),
        PRIMARY_VOLUME_DESCRIPTOR(1),
        ANCHOR_VOLUME_DESCRIPTOR_POINTER(2),
        VOLUME_DESCRIPTOR_POINTER(3),
        IMPLEMENTATION_USE_VOLUME_DESCRIPTOR(4),
        PARTITION_DESCRIPTOR(5),
        LOGICAL_VOLUME_DESCRIPTOR(6),
        UNALLOCATED_SPACE_DESCRIPTOR(7),
        TERMINATING_DESCRIPTOR(8),
        LOGICAL_VOLUME_INTEGRITY_DESCRIPTOR(9),
        FILE_SET_DESCRIPTOR(256),
        FILE_IDENTIFIER_DESCRIPTOR(257),
        ALLOCATION_EXTENT_DESCRIPTOR(258),
        INDIRECT_ENTRY(259),
        TERMINAL_ENTRY(260),
        FILE_ENTRY(261),
        EXTENDED_ATTRIBUTE_HEADER_DESCRIPTOR(262),
        UNALLOCATED_SPACE_ENTRY(263),
        SPACE_BITMAP_DESCRIPTOR(264),
        PARTITION_INTEGRITY_ENTRY(265),
        EXTENDED_FILE_ENTRY(266);

        private final int value;

        TagIdentifier(int value) {
            this.value = value;
        }

        int value() {
            return value;
        }

        static TagIdentifier fromValue(int value) {
            for (TagIdentifier identifier : values()) {
                if (identifier.value == value) {
                    return identifier;
                }
            }
            return UNKNOWN;
        }
    }

    record DescriptorTag(
            int identifier,
            int descriptorVersion,
            int checksum,
            int reserved,
            int serialNumber,
            int descriptorCrc,
            int descriptorCrcLength,
            long location
    ) {
        TagIdentifier type() {
            return TagIdentifier.fromValue(identifier);
        }

        static DescriptorTag parse(byte[] bytes) throws IOException {
            if (bytes.length != UdfDescriptorReader.TAG_LENGTH) {
                throw new IOException("Descriptor tag must contain 16 bytes");
            }

            BinaryReader reader = reader(bytes);
            return new DescriptorTag(
                    reader.readU16LE(),
                    reader.readU16LE(),
                    reader.readU8(),
                    reader.readU8(),
                    reader.readU16LE(),
                    reader.readU16LE(),
                    reader.readU16LE(),
                    reader.readU32LE()
            );
        }
    }

    record VolumeStructureDescriptor(int type, String identifier, int version) {
        static VolumeStructureDescriptor readFrom(BinaryReader reader, int sectorSize)
                throws IOException {
            int type = reader.readU8();
            String identifier = new String(reader.readBytes(5), StandardCharsets.US_ASCII);
            int version = reader.readU8();
            reader.skipFully(sectorSize - 7L);
            return new VolumeStructureDescriptor(type, identifier, version);
        }
    }

    enum ExtentType {
        RECORDED_AND_ALLOCATED,
        NOT_RECORDED_BUT_ALLOCATED,
        NOT_RECORDED_AND_NOT_ALLOCATED,
        NEXT_EXTENT_OF_ALLOCATION_DESCRIPTORS;

        static ExtentType fromRawLength(long rawLength) {
            return values()[(int) (rawLength >>> 30)];
        }
    }

    record ShortAllocationDescriptor(long rawExtentLength, long extentLocation) {
        long byteLength() {
            return rawExtentLength & EXTENT_LENGTH_MASK;
        }

        ExtentType extentType() {
            return ExtentType.fromRawLength(rawExtentLength);
        }

        static ShortAllocationDescriptor readFrom(BinaryReader reader) throws IOException {
            return new ShortAllocationDescriptor(reader.readU32LE(), reader.readU32LE());
        }
    }

    record LogicalBlockAddress(long logicalBlockNumber, int partitionReferenceNumber) {
        static LogicalBlockAddress readFrom(BinaryReader reader) throws IOException {
            return new LogicalBlockAddress(reader.readU32LE(), reader.readU16LE());
        }
    }

    record LongAllocationDescriptor(
            long rawExtentLength,
            LogicalBlockAddress extentLocation,
            byte[] implementationUse
    ) {
        long byteLength() {
            return rawExtentLength & EXTENT_LENGTH_MASK;
        }

        ExtentType extentType() {
            return ExtentType.fromRawLength(rawExtentLength);
        }

        static LongAllocationDescriptor readFrom(BinaryReader reader) throws IOException {
            return new LongAllocationDescriptor(
                    reader.readU32LE(),
                    LogicalBlockAddress.readFrom(reader),
                    reader.readBytes(6)
            );
        }
    }

    record ExtendedAllocationDescriptor(
            long rawExtentLength,
            long recordedLength,
            long informationLength,
            LogicalBlockAddress extentLocation
    ) {
        long byteLength() {
            return rawExtentLength & EXTENT_LENGTH_MASK;
        }

        ExtentType extentType() {
            return ExtentType.fromRawLength(rawExtentLength);
        }

        static ExtendedAllocationDescriptor readFrom(BinaryReader reader) throws IOException {
            ExtendedAllocationDescriptor descriptor = new ExtendedAllocationDescriptor(
                    reader.readU32LE(),
                    reader.readU32LE(),
                    reader.readU32LE(),
                    LogicalBlockAddress.readFrom(reader)
            );
            reader.skipFully(2);
            return descriptor;
        }
    }

    record AnchorVolumeDescriptorPointer(
            ShortAllocationDescriptor mainVolumeDescriptorSequence,
            ShortAllocationDescriptor reserveVolumeDescriptorSequence
    ) {
        static AnchorVolumeDescriptorPointer parse(byte[] payload) throws IOException {
            requireLength(payload, 16, "Anchor Volume Descriptor Pointer");
            BinaryReader reader = reader(payload);
            return new AnchorVolumeDescriptorPointer(
                    ShortAllocationDescriptor.readFrom(reader),
                    ShortAllocationDescriptor.readFrom(reader)
            );
        }
    }

    sealed interface PartitionMap permits Type1PartitionMap, Type2PartitionMap,
            UnknownPartitionMap {
        int type();
    }

    record Type1PartitionMap(int volumeSequenceNumber, int partitionNumber)
            implements PartitionMap {
        @Override
        public int type() {
            return 1;
        }
    }

    record Type2PartitionMap(byte[] partitionIdentifier) implements PartitionMap {
        @Override
        public int type() {
            return 2;
        }
    }

    record UnknownPartitionMap(int type, byte[] data) implements PartitionMap {
    }

    record PartitionDescriptor(int partitionNumber, long startingLocation, long length) {
        static PartitionDescriptor parse(byte[] payload) throws IOException {
            requireLength(payload, 496, "Partition Descriptor");
            BinaryReader reader = reader(payload);
            reader.skipFully(4); // Volume Descriptor Sequence Number
            reader.skipFully(2); // Partition Flags
            int partitionNumber = reader.readU16LE();
            reader.skipFully(32); // Partition Contents
            reader.skipFully(128); // Partition Contents Use
            reader.skipFully(4); // Access Type
            long startingLocation = reader.readU32LE();
            long length = reader.readU32LE();
            return new PartitionDescriptor(partitionNumber, startingLocation, length);
        }
    }

    record CharacterSet(int type, byte[] information) {
        static CharacterSet readFrom(BinaryReader reader) throws IOException {
            return new CharacterSet(reader.readU8(), reader.readBytes(63));
        }

        void requireOstaCompressedUnicode() throws IOException {
            if (type != 0) {
                throw new IOException("Unsupported UDF character set type: " + type);
            }

            String name = new String(information, StandardCharsets.US_ASCII).replace("\0", "");
            if (!"OSTA Compressed Unicode".equals(name)) {
                throw new IOException("Unsupported UDF CS0 character set: " + name);
            }
        }
    }

    record LogicalVolumeDescriptor(
            CharacterSet descriptorCharacterSet,
            byte[] logicalVolumeIdentifier,
            long logicalBlockSize,
            LongAllocationDescriptor fileSetDescriptorSequence,
            List<PartitionMap> partitionMaps
    ) {
        static LogicalVolumeDescriptor parse(byte[] payload) throws IOException {
            requireLength(payload, 424, "Logical Volume Descriptor");
            BinaryReader reader = reader(payload);

            reader.skipFully(4); // Volume Descriptor Sequence Number
            CharacterSet descriptorCharacterSet = CharacterSet.readFrom(reader);
            byte[] logicalVolumeIdentifier = readDString(reader, 128);
            long logicalBlockSize = reader.readU32LE();
            reader.skipFully(32); // Domain Identifier
            LongAllocationDescriptor fileSetDescriptorSequence =
                    LongAllocationDescriptor.readFrom(reader);
            long mapTableLength = reader.readU32LE();
            long numberOfPartitionMaps = reader.readU32LE();
            reader.skipFully(32); // Implementation Identifier
            reader.skipFully(128); // Implementation Use
            reader.skipFully(8); // Integrity Sequence Extent

            if (mapTableLength > Integer.MAX_VALUE
                    || mapTableLength > payload.length - 424L) {
                throw new IOException("Invalid partition map table length: " + mapTableLength);
            }

            List<PartitionMap> partitionMaps = parsePartitionMaps(
                    reader.readBytes((int) mapTableLength),
                    numberOfPartitionMaps
            );
            return new LogicalVolumeDescriptor(
                    descriptorCharacterSet,
                    logicalVolumeIdentifier,
                    logicalBlockSize,
                    fileSetDescriptorSequence,
                    List.copyOf(partitionMaps)
            );
        }

        String decodedIdentifier() throws IOException {
            descriptorCharacterSet.requireOstaCompressedUnicode();
            return decodeOstaCompressedUnicode(logicalVolumeIdentifier);
        }
    }

    record FileSetDescriptor(LongAllocationDescriptor rootDirectoryIcb) {
        static FileSetDescriptor parse(byte[] payload) throws IOException {
            requireLength(payload, 400, "File Set Descriptor");
            BinaryReader reader = reader(payload);
            reader.skipFully(384);
            return new FileSetDescriptor(LongAllocationDescriptor.readFrom(reader));
        }
    }

    enum FileType {
        UNSPECIFIED(0),
        UNALLOCATED_SPACE_ENTRY(1),
        PARTITION_INTEGRITY_ENTRY(2),
        INDIRECT_ENTRY(3),
        DIRECTORY(4),
        REGULAR_FILE(5),
        BLOCK_DEVICE(6),
        CHARACTER_DEVICE(7),
        EXTENDED_ATTRIBUTE(8),
        FIFO(9),
        SOCKET(10),
        TERMINAL_ENTRY(11),
        SYMBOLIC_LINK(12),
        STREAM_DIRECTORY(13),
        VAT(248),
        REAL_TIME_FILE(249),
        METADATA_FILE(250),
        METADATA_MIRROR_FILE(251),
        METADATA_BITMAP_FILE(252),
        RESERVED(-1);

        private final int value;

        FileType(int value) {
            this.value = value;
        }

        static FileType fromValue(int value) throws IOException {
            for (FileType type : values()) {
                if (type.value == value) {
                    return type;
                }
            }
            if (value >= 14 && value <= 247) {
                return RESERVED;
            }
            throw new IOException("Invalid ICB file type: " + value);
        }
    }

    enum AllocationDescriptorType {
        SHORT,
        LONG,
        EXTENDED,
        INLINE;

        static AllocationDescriptorType fromFlags(int flags) {
            return values()[flags & 0x07];
        }
    }

    record IcbTag(FileType fileType, AllocationDescriptorType allocationDescriptorType) {
        static IcbTag readFrom(BinaryReader reader) throws IOException {
            reader.skipFully(4); // Prior Recorded Number of Direct Entries
            reader.skipFully(2); // Strategy Type
            reader.skipFully(2); // Strategy Parameter
            reader.skipFully(2); // Maximum Number of Entries
            reader.skipFully(1); // Reserved
            FileType fileType = FileType.fromValue(reader.readU8());
            reader.skipFully(6); // Parent ICB Location
            int flags = reader.readU16LE();
            try {
                return new IcbTag(fileType, AllocationDescriptorType.fromFlags(flags));
            } catch (ArrayIndexOutOfBoundsException e) {
                throw new IOException("Invalid allocation descriptor type in ICB flags: "
                        + (flags & 0x07), e);
            }
        }
    }

    record FileEntry(
            IcbTag icbTag,
            long informationLength,
            byte[] allocationDescriptors
    ) {
        static FileEntry parse(byte[] payload) throws IOException {
            requireLength(payload, 160, "File Entry");
            BinaryReader reader = reader(payload);
            IcbTag icbTag = IcbTag.readFrom(reader);
            reader.skipFully(20); // UID through Record Length
            long informationLength = reader.readU64LE();
            reader.skipFully(8); // Logical Blocks Recorded
            reader.skipFully(36); // Access, Modification and Attribute timestamps
            reader.skipFully(4); // Checkpoint
            reader.skipFully(16); // Extended Attribute ICB
            reader.skipFully(32); // Implementation Identifier
            reader.skipFully(8); // Unique ID
            long extendedAttributesLength = reader.readU32LE();
            long allocationDescriptorsLength = reader.readU32LE();

            long variableLength = extendedAttributesLength + allocationDescriptorsLength;
            if (extendedAttributesLength > Integer.MAX_VALUE
                    || allocationDescriptorsLength > Integer.MAX_VALUE
                    || variableLength > payload.length - 160L) {
                throw new IOException("File Entry variable data exceeds descriptor length");
            }

            reader.skipFully(extendedAttributesLength);
            byte[] allocationDescriptors =
                    reader.readBytes((int) allocationDescriptorsLength);
            return new FileEntry(icbTag, informationLength, allocationDescriptors);
        }
    }

    record FileIdentifierDescriptor(
            int characteristics,
            LongAllocationDescriptor icb,
            byte[] fileIdentifier
    ) {
        private static final int DIRECTORY = 0x02;
        private static final int DELETED = 0x04;
        private static final int PARENT = 0x08;

        static FileIdentifierDescriptor parse(byte[] payload) throws IOException {
            requireLength(payload, 22, "File Identifier Descriptor");
            BinaryReader reader = reader(payload);
            reader.skipFully(2); // File Version Number
            int characteristics = reader.readU8();
            int fileIdentifierLength = reader.readU8();
            LongAllocationDescriptor icb = LongAllocationDescriptor.readFrom(reader);
            int implementationUseLength = reader.readU16LE();

            long variableLength = (long) implementationUseLength + fileIdentifierLength;
            if (variableLength > payload.length - 22L) {
                throw new IOException("File Identifier Descriptor variable data exceeds descriptor length");
            }

            reader.skipFully(implementationUseLength);
            return new FileIdentifierDescriptor(
                    characteristics,
                    icb,
                    reader.readBytes(fileIdentifierLength)
            );
        }

        boolean isDirectory() {
            return (characteristics & DIRECTORY) != 0;
        }

        boolean isDeleted() {
            return (characteristics & DELETED) != 0;
        }

        boolean isParent() {
            return (characteristics & PARENT) != 0;
        }

        String decodedName() throws IOException {
            return decodeOstaCompressedUnicode(fileIdentifier);
        }
    }

    static String decodeOstaCompressedUnicode(byte[] data) throws IOException {
        if (data.length == 0) {
            return "";
        }

        int compressionId = data[0] & 0xFF;
        StringBuilder result = new StringBuilder();
        if (compressionId == 8) {
            for (int i = 1; i < data.length; i++) {
                result.append((char) (data[i] & 0xFF));
            }
            return result.toString();
        }

        if (compressionId == 16) {
            if (((data.length - 1) & 1) != 0) {
                throw new IOException("Invalid 16-bit OSTA Compressed Unicode length: "
                        + data.length);
            }
            for (int i = 1; i < data.length; i += 2) {
                result.append((char) (((data[i] & 0xFF) << 8) | (data[i + 1] & 0xFF)));
            }
            return result.toString();
        }

        throw new IOException("Unsupported OSTA Compressed Unicode compression ID: "
                + compressionId);
    }

    private static List<PartitionMap> parsePartitionMaps(byte[] data, long expectedCount)
            throws IOException {
        if (expectedCount > Integer.MAX_VALUE) {
            throw new IOException("Too many partition maps: " + expectedCount);
        }

        List<PartitionMap> maps = new ArrayList<>((int) expectedCount);
        int offset = 0;
        while (offset < data.length && maps.size() < expectedCount) {
            if (offset + 2 > data.length) {
                throw new IOException("Truncated partition map header");
            }

            int type = data[offset] & 0xFF;
            int length = data[offset + 1] & 0xFF;
            if (length < 2 || offset + length > data.length) {
                throw new IOException("Invalid partition map length: " + length);
            }

            if (type == 1) {
                if (length != 6) {
                    throw new IOException("Invalid Type 1 partition map length: " + length);
                }
                maps.add(new Type1PartitionMap(
                        readU16LE(data, offset + 2),
                        readU16LE(data, offset + 4)
                ));
            } else if (type == 2) {
                if (length != 64) {
                    throw new IOException("Invalid Type 2 partition map length: " + length);
                }
                maps.add(new Type2PartitionMap(
                        Arrays.copyOfRange(data, offset + 2, offset + length)
                ));
            } else {
                maps.add(new UnknownPartitionMap(
                        type,
                        Arrays.copyOfRange(data, offset + 2, offset + length)
                ));
            }
            offset += length;
        }

        if (maps.size() != expectedCount || offset != data.length) {
            throw new IOException("Partition map table mismatch: expected " + expectedCount
                    + " maps in " + data.length + " bytes, parsed " + maps.size()
                    + " maps in " + offset + " bytes");
        }
        return maps;
    }

    private static byte[] readDString(BinaryReader reader, int fieldLength) throws IOException {
        byte[] raw = reader.readBytes(fieldLength);
        int usedLength = raw[fieldLength - 1] & 0xFF;
        if (usedLength > fieldLength - 1) {
            throw new IOException("Invalid dstring length " + usedLength
                    + " for a " + fieldLength + " byte field");
        }
        return Arrays.copyOf(raw, usedLength);
    }

    private static BinaryReader reader(byte[] bytes) {
        return new InputStreamBinaryReader(new ByteArrayInputStream(bytes));
    }

    private static void requireLength(byte[] payload, int minimum, String descriptor)
            throws IOException {
        if (payload.length < minimum) {
            throw new IOException(descriptor + " payload is truncated: " + payload.length
                    + " bytes, expected at least " + minimum);
        }
    }

    private static int readU16LE(byte[] data, int offset) {
        return (data[offset] & 0xFF) | ((data[offset + 1] & 0xFF) << 8);
    }
}
