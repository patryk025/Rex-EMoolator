package pl.genschu.bloomooemulator.engine.filesystem;

import com.badlogic.gdx.Gdx;
import pl.genschu.bloomooemulator.loader.helpers.BinaryReader;
import pl.genschu.bloomooemulator.loader.helpers.InputStreamBinaryReader;
import pl.genschu.bloomooemulator.loader.helpers.RandomAccessFileBinaryReader;
import pl.genschu.bloomooemulator.loader.helpers.SeekableBinaryReader;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UdfFileSystem implements IFileSystem {
    private static final int SECTOR_SIZE = 2048;
    private static final int VRS_START_SECTOR = 16;
    private static final int AVDP_START_SECTOR = 256;

    private final File isoFile;

    public UdfFileSystem(File isoFile) {
        if (isoFile == null) {
            throw new IllegalArgumentException("isoFile cannot be null");
        }
        this.isoFile = isoFile;
        validateAndLoadUdfFile();
    }

    @Override
    public InputStream open(String path) throws IOException {

        return null;
    }

    @Override
    public boolean exists(String path) {
        return false;
    }

    @Override
    public boolean isDirectory(String path) {
        return false;
    }

    @Override
    public String[] list(String path) {
        return new String[0];
    }

    @Override
    public long length(String path) {
        return 0;
    }

    private void validateAndLoadUdfFile() {
        try (RandomAccessFile raf = new RandomAccessFile(isoFile, "r")) {
            SeekableBinaryReader reader = new RandomAccessFileBinaryReader(raf);

            // Pre-flight check for UDF signatures
            reader.seek(VRS_START_SECTOR * SECTOR_SIZE);

            boolean hasBea = false;
            boolean hasTea = false;
            boolean isUdf = false;

            while (reader.position() < (VRS_START_SECTOR + 32) * SECTOR_SIZE && !hasTea) {
                VolumeStructureDescriptor vsd = VolumeStructureDescriptor.readFrom(reader);

                if (vsd.type != 0x00) {
                    throw new IOException("Invalid descriptor type in UDF Volume Structure Descriptor: " + vsd.type);
                }
                String identifier = new String(vsd.identifier, StandardCharsets.US_ASCII);

                switch (identifier) {
                    case "BEA01" -> hasBea = true;
                    case "NSR02", "NSR03" -> {
                        if(!hasBea)
                            Gdx.app.log("UdfFileSystem", "Warning: NSR02/NSR03 descriptor found without preceding BEA01 descriptor.");
                        isUdf = true;
                    }
                    case "TEA01" -> {
                        if (!hasBea) {
                            Gdx.app.log("UdfFileSystem", "Warning: TEA01 descriptor found without preceding BEA01 descriptor.");
                        } // Valid UDF structure found
                        hasTea = true;
                    }
                }
            }

            if (!isUdf) {
                throw new IOException("No valid UDF descriptors found in the Volume Structure Descriptor.");
            }

            // Parse the Anchor Volume Descriptor Pointer (AVDP)
            // Find it at 3 locations: sector 256, N - 256 and N - 1, where N is the total number of sectors in the volume.
            // ECMA TR/112-2, Page 23
            long totalSectors = reader.length() / SECTOR_SIZE;
            long[] anchorSectors = {AVDP_START_SECTOR, totalSectors - AVDP_START_SECTOR, totalSectors - 1};

            List<AnchorVolumeDescriptorPointer> pointers = new ArrayList<>();

            for (long sector : anchorSectors) {
                reader.seek(sector * SECTOR_SIZE);

                AnchorVolumeDescriptorPointer candidate =
                        AnchorVolumeDescriptorPointer.readFrom(reader);

                if (candidate.tag.tagIdentifier == TagType.AnchorVolumeDescriptorPointer.value) { // Check for valid AVDP tag identifier
                    pointers.add(candidate);
                }
            }

            if (pointers.isEmpty()) {
                throw new IOException(
                        "No valid Anchor Volume Descriptor Pointer (AVDP) found in the UDF volume."
                );
            }

            // ECMA TR/112-2 says:
            // "As specified in section 6.11.2, unclosed sequential Write-Once media may have
            // a single AVDP present at either sector 256 or 512. If on an unclosed disc a single
            // AVDP is recorded on sector 256, any AVDP recorded on sector 512 must be ignored."
            // At this moment, I don't check for unclosed discs and does not check for sector 512,
            // so I will just log a warning if only one AVDP is found.
            if (pointers.size() < 2) {
                Gdx.app.log(
                        "UdfFileSystem",
                        "Warning: Only one valid AVDP found. UDF volume may be incomplete or corrupted."
                );
            }

            AnchorVolumeDescriptorPointer avdp = pointers.get(0);

            long minVdsLength = 16L * SECTOR_SIZE;

            if (avdp.mainVolumeDescriptorSequenceExtent.extentLength < minVdsLength) {
                Gdx.app.log(
                        "UdfFileSystem",
                        "Warning: Main Volume Descriptor Sequence extent length is less than 16 sectors."
                );
            }

            if (avdp.reserveVolumeDescriptorSequenceExtent.extentLength < minVdsLength) {
                Gdx.app.log(
                        "UdfFileSystem",
                        "Warning: Reserve Volume Descriptor Sequence extent length is less than 16 sectors."
                );
            }

            // Let's read a Volume Descriptor Sequence (VDS) from the main volume descriptor sequence extent
            long vdsStart = avdp.mainVolumeDescriptorSequenceExtent.extentLocation * SECTOR_SIZE;
            long vdsEnd = vdsStart + avdp.mainVolumeDescriptorSequenceExtent.extentLength;

            reader.seek(vdsStart);

            boolean foundTerminatingDescriptor = false;
            List<PartitionDescriptor> partitions = new ArrayList<>();
            LogicalVolumeDescriptor logicalVolumeDescriptor = null;

            while (reader.position() < vdsEnd && !foundTerminatingDescriptor) {
                long descriptorStart = reader.position();
                Tag tag = Tag.readFrom(reader);

                switch (TagType.fromValue(tag.tagIdentifier)) {
                    case Unknown -> {
                        // Unspecified/unknown descriptor type; skip the remainder of this logical block and jump to the next sector
                        long nextSector = ((descriptorStart+SECTOR_SIZE-1)/SECTOR_SIZE) * SECTOR_SIZE;
                        reader.seek(nextSector);
                    }
                    case PrimaryVolumeDescriptor -> {
                        // Information about the primary volume descriptor can be read here if needed
                        reader.skipFully(512 - 16);
                    }
                    case VolumeDescriptorSequence -> {
                        // Handle Volume Descriptor Sequence
                        reader.skipFully(512 - 16);
                    }
                    case ImplementationUseVolumeDescriptor -> {
                        // Handle Implementation Use Volume Descriptor
                        reader.skipFully(512 - 16);
                    }
                    case PartitionDescriptor -> {
                        partitions.add(PartitionDescriptor.readFrom(reader));
                    }
                    case LogicalVolumeDescriptor -> {
                        logicalVolumeDescriptor = LogicalVolumeDescriptor.readFrom(reader);
                    }
                    case UnallocatedSpaceDescriptor -> {
                        // Handle Unallocated Space Descriptor
                        reader.skipFully(512 - 16);
                    }
                    case TerminatingDescriptor -> {
                        // Terminating Descriptor has only the tag, so we can skip the rest of the descriptor bytes
                        reader.skipFully(512 - 16);
                        foundTerminatingDescriptor = true;
                    }
                    default -> throw new IOException("Unknown TagType: " + tag.tagIdentifier);
                }
            }

            if(partitions.isEmpty()) {
                throw new IOException("No valid Partition Descriptor found in the Volume Structure Descriptor.");
            }

            if(logicalVolumeDescriptor == null) {
                throw new IOException("No valid Logical Volume Descriptor found in the Volume Structure Descriptor.");
            }

            Gdx.app.log("UdfFileSystem", "Logical Volume Identifier: " + logicalVolumeDescriptor.logicalVolumeIdentifier.decode(logicalVolumeDescriptor.descriptorCharacterSet));

            // LVD fields
            List<PartitionMap> partitionMaps = logicalVolumeDescriptor.partitionMaps;
            long numberOfPartitionMaps = logicalVolumeDescriptor.numberOfPartitionMaps;
            long mapTableLength = logicalVolumeDescriptor.mapTableLength;

            // let's find partition, where FSD (File Set Descriptor) is located
            LongAllocationDescriptor fsdExtent =
                    logicalVolumeDescriptor.logicalVolumeContentsUse;

            int partitionReferenceNumber = fsdExtent.extentLocation().partitionReferenceNumber();
            PartitionMap fsdMap = partitionMaps.get(partitionReferenceNumber);
            PartitionDescriptor fsdPartition = resolvePartitionDescriptor(fsdMap, partitions);

            // Calculate the offset of the FSD in the ISO file
            long logicalBlockNumber = fsdExtent.extentLocation().logicalBlockNumber();
            long physicalBlock = fsdPartition.partitionStartingLocation() + logicalBlockNumber;
            long fsdOffset = physicalBlock * logicalVolumeDescriptor.logicalBlockSize();

            reader.seek(fsdOffset);

            Tag fsdTag = Tag.readFrom(reader);

            if (fsdTag.tagIdentifier() != 256) {
                throw new IOException(
                        "Expected File Set Descriptor (tag 256), got: "
                                + fsdTag.tagIdentifier()
                );
            }

            // Read the File Set Descriptor (FSD) from the calculated offset
            FileSetDescriptor fsd = FileSetDescriptor.readFrom(reader);

            LongAllocationDescriptor rootDirExtent = fsd.rootDirectoryICB();
            int rootDirPartitionRef = rootDirExtent.extentLocation().partitionReferenceNumber();
            PartitionMap rootDirMap = partitionMaps.get(rootDirPartitionRef);
            PartitionDescriptor rootDirPartition = resolvePartitionDescriptor(rootDirMap, partitions);

            long rootDirLogicalBlock = rootDirExtent.extentLocation().logicalBlockNumber();
            long rootDirPhysicalBlock = rootDirPartition.partitionStartingLocation() + rootDirLogicalBlock;
            long rootDirOffset = rootDirPhysicalBlock * logicalVolumeDescriptor.logicalBlockSize();

            reader.seek(rootDirOffset);

            // Finally, after this long journey we are reading our first File Entry
            Tag rootTag = Tag.readFrom(reader);

            if (rootTag.tagIdentifier() != 261) {
                throw new IOException(
                        "Expected File Entry for root directory, got tag: "
                                + rootTag.tagIdentifier()
                );
            }

            FileEntry rootEntry = FileEntry.readFrom(reader);

            // Not so fast. Is it directory?
            if (rootEntry.icbTag().fileType() != FileType.Directory) {
                throw new IOException(
                        "Root Directory ICB does not describe a directory. FileType="
                                + rootEntry.icbTag().fileType()
                );
            }

            // Geez, let's read the directory name
            // of course we have FOUR different structs for allocating allocationDescriptors...
            AllocationDescriptorType icbAllocationType = rootEntry.icbTag().allocationDescriptorType();
            byte[] allocationDescriptors = rootEntry.allocationDescriptors;
            BinaryReader allocationReader =
                    new InputStreamBinaryReader(
                            new ByteArrayInputStream(allocationDescriptors)
                    );

            long fidOffset = 0;
            long icbPhysicalBlock = 0;

            switch (icbAllocationType) {
                case Short -> {
                    ShortAllocationDescriptor shortAd = ShortAllocationDescriptor.readFrom(allocationReader);
                    icbPhysicalBlock = rootDirPartition.partitionStartingLocation() + shortAd.extentLocation();

                    fidOffset = icbPhysicalBlock * logicalVolumeDescriptor.logicalBlockSize();
                }
                case Long -> {
                    LongAllocationDescriptor ad = LongAllocationDescriptor.readFrom(allocationReader);
                    int partitionRef = ad.extentLocation().partitionReferenceNumber();
                    PartitionMap map = partitionMaps.get(partitionRef);
                    PartitionDescriptor partition = resolvePartitionDescriptor(map, partitions);

                    icbPhysicalBlock = partition.partitionStartingLocation() + ad.extentLocation().logicalBlockNumber();
                    fidOffset = icbPhysicalBlock * logicalVolumeDescriptor.logicalBlockSize();
                }
                case Extended -> {
                    ExtendedAllocationDescriptor ad = ExtendedAllocationDescriptor.readFrom(allocationReader);
                    int partitionRef = ad.extentLocation().partitionReferenceNumber();
                    PartitionMap map = partitionMaps.get(partitionRef);
                    PartitionDescriptor partition = resolvePartitionDescriptor(map, partitions);

                    icbPhysicalBlock = partition.partitionStartingLocation() + ad.extentLocation().logicalBlockNumber();
                    fidOffset = icbPhysicalBlock * logicalVolumeDescriptor.logicalBlockSize();
                }
                case Inline -> {
                    // data are in allocationDescriptors, so we can read them directly
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private record Entry(boolean directory, long offset, long length) {}

    private enum TagType {
        Unknown(0),
        PrimaryVolumeDescriptor(1),
        AnchorVolumeDescriptorPointer(2),
        VolumeDescriptorSequence(3),
        ImplementationUseVolumeDescriptor(4),
        PartitionDescriptor(5),
        LogicalVolumeDescriptor(6),
        UnallocatedSpaceDescriptor(7),
        TerminatingDescriptor(8);

        private final int value;

        TagType(int value) {
            this.value = value;
        }

        public static TagType fromValue(int value) {
            for (TagType type : values()) {
                if (type.value == value) {
                    return type;
                }
            }
            throw new IllegalArgumentException("Unknown TagType value: " + value);
        }
    }

    // ECMA 167, section 7.2.1.1
    public record CharSpec(
            CharsetSetType type,
            byte[] information
    ) {
        public static CharSpec readFrom(BinaryReader reader) throws IOException {
            CharsetSetType type =
                    CharsetSetType.fromValue(reader.readU8());

            byte[] information = reader.readBytes(63);

            return new CharSpec(type, information);
        }
    }

    private enum CharsetSetType {
        RESERVED(-1),
        CS0(0),
        CS1(1),
        CS2(2),
        CS3(3),
        CS4(4),
        CS5(5),
        CS6(6),
        CS7(7),
        CS8(8);

        private final int value;
        CharsetSetType(int value) {
            this.value = value;
        }

        public static CharsetSetType fromValue(int value) {
            for (CharsetSetType type : values()) {
                if (type.value == value) {
                    return type;
                }
            }
            if(value > 8 && value <= 255) {
                return RESERVED;
            }
            throw new IllegalArgumentException("Unknown CharsetSetTypes value: " + value);
        }
    }

    public record Tag(int tagIdentifier, int descriptorVersion, int tagChecksum, int reserved, int tagSerialNumber, int descriptorCRC, int descriptorCRCLength, long tagLocation) {
        public static Tag readFrom(BinaryReader reader) throws IOException {
            int tagIdentifier = reader.readU16LE();
            int descriptorVersion = reader.readU16LE();
            int tagChecksum = reader.readU8();
            int reserved = reader.readU8();
            int tagSerialNumber = reader.readU16LE();
            int descriptorCRC = reader.readU16LE();
            int descriptorCRCLength = reader.readU16LE();
            long tagLocation = reader.readU32LE();
            return new Tag(tagIdentifier, descriptorVersion, tagChecksum, reserved, tagSerialNumber, descriptorCRC, descriptorCRCLength, tagLocation);
        }
    }

    public record DString(byte[] data, int usedLength) {

        public static DString readFrom(BinaryReader reader, int fieldLength)
                throws IOException {

            if (fieldLength < 1) {
                throw new IllegalArgumentException(
                        "dstring field length must be at least 1"
                );
            }

            byte[] raw = reader.readBytes(fieldLength);
            int usedLength = raw[fieldLength - 1] & 0xFF;

            if (usedLength > fieldLength - 1) {
                throw new IOException(
                        "Invalid dstring length: " + usedLength +
                                " for field of " + fieldLength + " bytes"
                );
            }

            return new DString(
                    Arrays.copyOf(raw, usedLength),
                    usedLength
            );
        }

        public String decode(CharSpec charSpec) throws IOException {
            if (data.length == 0) {
                return "";
            }

            if (charSpec.type() != CharsetSetType.CS0) {
                throw new IOException(
                        "Unsupported character set type: " + charSpec.type()
                );
            }

            String charsetInfo = new String(
                    charSpec.information(),
                    StandardCharsets.US_ASCII
            ).replace("\0", "");

            if (!"OSTA Compressed Unicode".equals(charsetInfo)) {
                throw new IOException(
                        "Unsupported CS0 character set: " + charsetInfo
                );
            }

            int compressionId = data[0] & 0xFF;
            StringBuilder result = new StringBuilder();

            switch (compressionId) {
                case 8 -> {
                    for (int i = 1; i < data.length; i++) {
                        result.append((char) (data[i] & 0xFF));
                    }
                }

                case 16 -> {
                    if (((data.length - 1) & 1) != 0) {
                        throw new IOException(
                                "Invalid OSTA Compressed Unicode 16-bit length: "
                                        + data.length
                        );
                    }

                    for (int i = 1; i < data.length; i += 2) {
                        int ch = ((data[i] & 0xFF) << 8)
                                | (data[i + 1] & 0xFF);

                        result.append((char) ch);
                    }
                }

                default -> throw new IOException(
                        "Unsupported OSTA Compressed Unicode compression ID: "
                                + compressionId
                );
            }

            return result.toString();
        }
    }

    public record Timestamp(
            int typeAndTimezone,
            int year,
            int month,
            int day,
            int hour,
            int minute,
            int second,
            int centiseconds,
            int hundredsOfMicroseconds,
            int microseconds
    ) {
        public static Timestamp readFrom(BinaryReader reader) throws IOException {
            int typeAndTimezone = reader.readU16LE();
            int year = reader.readU16LE();
            int month = reader.readU8();
            int day = reader.readU8();
            int hour = reader.readU8();
            int minute = reader.readU8();
            int second = reader.readU8();
            int centiseconds = reader.readU8();
            int hundredsOfMicroseconds = reader.readU8();
            int microseconds = reader.readU8();
            return new Timestamp(typeAndTimezone, year, month, day, hour, minute, second, centiseconds, hundredsOfMicroseconds, microseconds);
        }
    }

    public sealed interface PartitionMap
            permits Type1PartitionMap, Type2PartitionMap, UnknownPartitionMap {

        int type();
        int length();
    }

    public record Type1PartitionMap(
            int volumeSequenceNumber,
            int partitionNumber
    ) implements PartitionMap {

        @Override
        public int type() {
            return 1;
        }

        @Override
        public int length() {
            return 6;
        }
    }

    public record Type2PartitionMap(
            byte[] partitionIdentifier
    ) implements PartitionMap {

        @Override
        public int type() {
            return 2;
        }

        @Override
        public int length() {
            return 64;
        }
    }

    public record UnknownPartitionMap(
            int type,
            int length,
            byte[] data
    ) implements PartitionMap {
    }

    private static List<PartitionMap> parsePartitionMaps(
            byte[] data,
            long expectedCount
    ) throws IOException {

        List<PartitionMap> maps = new ArrayList<>();

        int offset = 0;

        while (offset < data.length && maps.size() < expectedCount) {
            if (offset + 2 > data.length) {
                throw new IOException("Truncated partition map header");
            }

            int type = data[offset] & 0xFF;
            int length = data[offset + 1] & 0xFF;

            if (length < 2) {
                throw new IOException(
                        "Invalid partition map length: " + length
                );
            }

            if (offset + length > data.length) {
                throw new IOException(
                        "Partition map exceeds map table length"
                );
            }

            switch (type) {
                case 1 -> {
                    if (length != 6) {
                        throw new IOException(
                                "Invalid Type 1 partition map length: " + length
                        );
                    }

                    int volumeSequenceNumber =
                            (data[offset + 2] & 0xFF)
                                    | ((data[offset + 3] & 0xFF) << 8);

                    int partitionNumber =
                            (data[offset + 4] & 0xFF)
                                    | ((data[offset + 5] & 0xFF) << 8);

                    maps.add(new Type1PartitionMap(
                            volumeSequenceNumber,
                            partitionNumber
                    ));
                }

                case 2 -> {
                    if (length != 64) {
                        throw new IOException(
                                "Invalid Type 2 partition map length: " + length
                        );
                    }

                    maps.add(new Type2PartitionMap(
                            Arrays.copyOfRange(
                                    data,
                                    offset + 2,
                                    offset + 64
                            )
                    ));
                }

                default -> {
                    maps.add(new UnknownPartitionMap(
                            type,
                            length,
                            Arrays.copyOfRange(
                                    data,
                                    offset + 2,
                                    offset + length
                            )
                    ));
                }
            }

            offset += length;
        }

        if (maps.size() != expectedCount) {
            throw new IOException(
                    "Partition map count mismatch: expected "
                            + expectedCount
                            + ", got "
                            + maps.size()
            );
        }

        if (offset != data.length) {
            throw new IOException(
                    "Partition map table length mismatch: consumed "
                            + offset
                            + " of "
                            + data.length
            );
        }

        return maps;
    }

    private PartitionDescriptor resolvePartitionDescriptor(
            PartitionMap map,
            List<PartitionDescriptor> descriptors
    ) throws IOException {

        return switch (map) {
            case Type1PartitionMap type1 ->
                    descriptors.stream()
                            .filter(p -> p.partitionNumber() == type1.partitionNumber())
                            .findFirst()
                            .orElseThrow(() -> new IOException(
                                    "Partition Descriptor not found for partition number: "
                                            + type1.partitionNumber()
                            ));

            case Type2PartitionMap type2 ->
                    throw new IOException(
                            "Type 2 partition maps are not supported yet"
                    );

            case UnknownPartitionMap unknown ->
                    throw new IOException(
                            "Unsupported partition map type: " + unknown.type()
                    );
        };
    }

    public record LogicalBlockAddress(
            long logicalBlockNumber,
            int partitionReferenceNumber
    ) {
        public static LogicalBlockAddress readFrom(BinaryReader reader)
                throws IOException {
            return new LogicalBlockAddress(
                    reader.readU32LE(),
                    reader.readU16LE()
            );
        }
    }

    public record LongAllocationDescriptor(
            long extentLength,
            LogicalBlockAddress extentLocation,
            byte[] implementationUse
    ) {
        public static LongAllocationDescriptor readFrom(BinaryReader reader)
                throws IOException {
            return new LongAllocationDescriptor(
                    reader.readU32LE(),
                    LogicalBlockAddress.readFrom(reader),
                    reader.readBytes(6)
            );
        }
    }

    public record VolumeStructureDescriptor(byte type, byte[] identifier, byte version, byte[] data) {
        public static VolumeStructureDescriptor readFrom(BinaryReader reader) throws IOException {
            byte type = reader.readI8();
            byte[] identifier = reader.readBytes(5);
            byte version = reader.readI8();
            byte[] data = reader.readBytes(SECTOR_SIZE - 7);
            return new VolumeStructureDescriptor(type, identifier, version, data);
        }
    }

    public record ShortAllocationDescriptor(
            long extentLength,
            long extentLocation
    ) {
        public static ShortAllocationDescriptor readFrom(BinaryReader reader)
                throws IOException {

            return new ShortAllocationDescriptor(
                    reader.readU32LE(),
                    reader.readU32LE()
            );
        }
    }

    public record ExtendedAllocationDescriptor(
            long extentLength,
            long recordedLength,
            long informationLength,
            LogicalBlockAddress extentLocation,
            byte[] implementationUse
    ) {
        public static ExtendedAllocationDescriptor readFrom(BinaryReader reader)
                throws IOException {

            return new ExtendedAllocationDescriptor(
                    reader.readU32LE(),
                    reader.readU32LE(),
                    reader.readU32LE(),
                    LogicalBlockAddress.readFrom(reader),
                    reader.readBytes(2)
            );
        }
    }

    public enum ExtentType {
        RecordedAndAllocated(0),
        NotRecordedButAllocated(1),
        NotRecordedAndNotAllocated(2),
        NextExtentOfAllocationDescriptors(3);

        private final int value;

        ExtentType(int value) {
            this.value = value;
        }

        public static ExtentType fromValue(int value) {
            return switch (value) {
                case 0 -> RecordedAndAllocated;
                case 1 -> NotRecordedButAllocated;
                case 2 -> NotRecordedAndNotAllocated;
                case 3 -> NextExtentOfAllocationDescriptors;
                default -> throw new IllegalArgumentException();
            };
        }
    }

    public record AnchorVolumeDescriptorPointer(
            Tag tag,
            ShortAllocationDescriptor mainVolumeDescriptorSequenceExtent,
            ShortAllocationDescriptor reserveVolumeDescriptorSequenceExtent
    ) {
        public static AnchorVolumeDescriptorPointer readFrom(BinaryReader reader) throws IOException {
            Tag tag = Tag.readFrom(reader);
            ShortAllocationDescriptor mainVolumeDescriptorSequenceExtent = ShortAllocationDescriptor.readFrom(reader);
            ShortAllocationDescriptor reserveVolumeDescriptorSequenceExtent = ShortAllocationDescriptor.readFrom(reader);
            return new AnchorVolumeDescriptorPointer(tag, mainVolumeDescriptorSequenceExtent, reserveVolumeDescriptorSequenceExtent);
        }
    }

    public record EntityIdentifier(byte[] flags, byte[] identifier, byte[] identifierSuffix) {
        public static EntityIdentifier readFrom(BinaryReader reader) throws IOException {
            byte[] flags = reader.readBytes(1);
            byte[] identifier = reader.readBytes(23);
            byte[] identifierSuffix = reader.readBytes(8);
            return new EntityIdentifier(flags, identifier, identifierSuffix);
        }
    }

    public record PartitionDescriptor(
            long volumeDescriptorSequenceNumber,
            int partitionFlags,
            int partitionNumber,
            EntityIdentifier partitionContents,
            byte[] partitionContentsUse,
            long accessType,
            long partitionStartingLocation,
            long partitionLength,
            EntityIdentifier implementationIdentifier,
            byte[] implementationUse,
            byte[] reserved
    ) {
        public static PartitionDescriptor readFrom(BinaryReader reader) throws IOException {
            long volumeDescriptorSequenceNumber = reader.readU32LE();
            int partitionFlags = reader.readU16LE();
            int partitionNumber = reader.readU16LE();
            EntityIdentifier partitionContents = EntityIdentifier.readFrom(reader);
            byte[] partitionContentsUse = reader.readBytes(128);
            long accessType = reader.readU32LE();
            long partitionStartingLocation = reader.readU32LE();
            long partitionLength = reader.readU32LE();
            EntityIdentifier implementationIdentifier = EntityIdentifier.readFrom(reader);
            byte[] implementationUse = reader.readBytes(128);
            byte[] reserved = reader.readBytes(156);
            return new PartitionDescriptor(
                    volumeDescriptorSequenceNumber,
                    partitionFlags,
                    partitionNumber,
                    partitionContents,
                    partitionContentsUse,
                    accessType,
                    partitionStartingLocation,
                    partitionLength,
                    implementationIdentifier,
                    implementationUse,
                    reserved
            );
        }
    }

    public record LogicalVolumeDescriptor(
            long volumeDescriptorSequenceNumber,
            CharSpec descriptorCharacterSet,
            DString logicalVolumeIdentifier,
            long logicalBlockSize,
            EntityIdentifier domainIdentifier,
            LongAllocationDescriptor logicalVolumeContentsUse,
            long mapTableLength,
            long numberOfPartitionMaps,
            EntityIdentifier implementationIdentifier,
            byte[] implementationUse,
            ShortAllocationDescriptor integritySequenceExtent,
            List<PartitionMap> partitionMaps
    ) {
        public static LogicalVolumeDescriptor readFrom(BinaryReader reader) throws IOException {
            long volumeDescriptorSequenceNumber = reader.readU32LE();
            CharSpec descriptorCharacterSet = CharSpec.readFrom(reader);
            DString logicalVolumeIdentifier = DString.readFrom(reader, 128);
            long logicalBlockSize = reader.readU32LE();
            EntityIdentifier domainIdentifier = EntityIdentifier.readFrom(reader);
            LongAllocationDescriptor logicalVolumeContentsUse = LongAllocationDescriptor.readFrom(reader);
            long mapTableLength = reader.readU32LE();
            long numberOfPartitionMaps = reader.readU32LE();
            EntityIdentifier implementationIdentifier = EntityIdentifier.readFrom(reader);
            byte[] implementationUse = reader.readBytes(128);
            ShortAllocationDescriptor integritySequenceExtent = ShortAllocationDescriptor.readFrom(reader);

            if (mapTableLength > Integer.MAX_VALUE) {
                throw new IOException("Partition map table too large: " + mapTableLength);
            }

            List<PartitionMap> partitionMaps = parsePartitionMaps(
                    reader.readBytes((int) mapTableLength),
                    numberOfPartitionMaps
            );

            return new LogicalVolumeDescriptor(
                    volumeDescriptorSequenceNumber,
                    descriptorCharacterSet,
                    logicalVolumeIdentifier,
                    logicalBlockSize,
                    domainIdentifier,
                    logicalVolumeContentsUse,
                    mapTableLength,
                    numberOfPartitionMaps,
                    implementationIdentifier,
                    implementationUse,
                    integritySequenceExtent,
                    partitionMaps
            );
        }
    }

    public record FileSetDescriptor (
            Timestamp recordingDateAndTime,
            int interchangeLevel,
            int maximumInterchangeLevel,
            long characterSetList,
            long maximumCharacterSetList,
            long fileSetNumber,
            long fileSetDescriptorNumber,
            CharSpec logicalVolumeIdentifierCharacterSet,
            DString logicalVolumeIdentifier,
            CharSpec fileSetCharacterSet,
            DString fileSetIdentifier,
            DString copyrightFileIdentifier,
            DString abstractFileIdentifier,
            LongAllocationDescriptor rootDirectoryICB,
            EntityIdentifier domainIdentifier,
            LongAllocationDescriptor nextExtent,
            LongAllocationDescriptor systemStreamDirectoryICB,
            byte[] reserved
    ) {
        public static FileSetDescriptor readFrom(BinaryReader reader) throws IOException {
            Timestamp recordingDateAndTime = Timestamp.readFrom(reader);
            int interchangeLevel = reader.readU16LE();
            int maximumInterchangeLevel = reader.readU16LE();
            long characterSetList = reader.readU32LE();
            long maximumCharacterSetList = reader.readU32LE();
            long fileSetNumber = reader.readU32LE();
            long fileSetDescriptorNumber = reader.readU32LE();
            CharSpec logicalVolumeIdentifierCharacterSet = CharSpec.readFrom(reader);
            DString logicalVolumeIdentifier = DString.readFrom(reader, 128);
            CharSpec fileSetCharacterSet = CharSpec.readFrom(reader);
            DString fileSetIdentifier = DString.readFrom(reader, 32);
            DString copyrightFileIdentifier = DString.readFrom(reader, 32);
            DString abstractFileIdentifier = DString.readFrom(reader, 32);
            LongAllocationDescriptor rootDirectoryICB = LongAllocationDescriptor.readFrom(reader);
            EntityIdentifier domainIdentifier = EntityIdentifier.readFrom(reader);
            LongAllocationDescriptor nextExtent = LongAllocationDescriptor.readFrom(reader);
            LongAllocationDescriptor systemStreamDirectoryICB = LongAllocationDescriptor.readFrom(reader);
            byte[] reserved = reader.readBytes(32);

            return new FileSetDescriptor(
                    recordingDateAndTime,
                    interchangeLevel,
                    maximumInterchangeLevel,
                    characterSetList,
                    maximumCharacterSetList,
                    fileSetNumber,
                    fileSetDescriptorNumber,
                    logicalVolumeIdentifierCharacterSet,
                    logicalVolumeIdentifier,
                    fileSetCharacterSet,
                    fileSetIdentifier,
                    copyrightFileIdentifier,
                    abstractFileIdentifier,
                    rootDirectoryICB,
                    domainIdentifier,
                    nextExtent,
                    systemStreamDirectoryICB,
                    reserved
            );
        }
    }

    public enum FileType {
        Unspecified(0),
        UnallocatedSpaceEntry(1),
        PartitionIntegrityEntry(2),
        IndirectEntry(3),
        Directory(4),
        RegularFile(5),   // byte-addressable file
        BlockDevice(6),
        CharacterDevice(7),
        ExtendedAttribute(8),
        Fifo(9),
        Socket(10),
        TerminalEntry(11),
        SymbolicLink(12),
        StreamDirectory(13),

        // UDF specific
        Vat(248),
        RealTimeFile(249),
        MetadataFile(250),
        MetadataMirrorFile(251),
        MetadataBitmapFile(252),

        Reserved(-1);

        private final int value;

        FileType(int value) {
            this.value = value;
        }

        public int value() {
            return value;
        }

        public static FileType fromValue(int value) {
            for (FileType type : values()) {
                if (type.value == value) {
                    return type;
                }
            }

            if (value >= 14 && value <= 247) {
                return Reserved;
            }

            throw new IllegalArgumentException(
                    "Invalid ICB file type: " + value
            );
        }
    }

    public enum AllocationDescriptorType {
        Short(0),  // short_ad
        Long(1),  // long_ad
        Extended(2),  // ext_ad
        Inline(3);  // inline in File Entry

        private final int value;

        AllocationDescriptorType(int value) {
            this.value = value;
        }

        public int value() {
            return value;
        }
        public static AllocationDescriptorType fromValue(int value) {
            for (AllocationDescriptorType type : values()) {
                if (type.value == value) {
                    return type;
                }
            }

            throw new IllegalArgumentException(
                    "Invalid allocation descriptor type: " + value
            );
        }
    }

    public record IcbTag(
            long priorRecordedNumberOfDirectEntries,
            int strategyType,
            byte[] strategyParameter,
            int maximumNumberOfEntries,
            int reserved,
            FileType fileType,
            LogicalBlockAddress parentIcbLocation,
            int flags
    ) {
        public static IcbTag readFrom(BinaryReader reader) throws IOException {
            return new IcbTag(
                    reader.readU32LE(),
                    reader.readU16LE(),
                    reader.readBytes(2),
                    reader.readU16LE(),
                    reader.readU8(),
                    FileType.fromValue(reader.readU8()),
                    LogicalBlockAddress.readFrom(reader),
                    reader.readU16LE()
            );
        }

        public AllocationDescriptorType allocationDescriptorType() {
            return AllocationDescriptorType.fromValue(flags & 0x07);
        }
    }

    public record FileEntry(
            IcbTag icbTag,
            long uid,
            long gid,
            long permissions,
            int fileLinkCount,
            int recordFormat,
            int recordDisplayAttributes,
            long recordLength,
            long informationLength,
            long logicalBlocksRecorded,
            Timestamp accessDateAndTime,
            Timestamp modificationDateAndTime,
            Timestamp attributeDateAndTime,
            long checkpoint,
            LongAllocationDescriptor extendedAttributeICB,
            EntityIdentifier implementationIdentifier,
            long uniqueId,
            long lengthOfExtendedAttributes,
            long lengthOfAllocationDescriptors,
            byte[] extendedAttributes,
            byte[] allocationDescriptors
    ) {
        public static FileEntry readFrom(BinaryReader reader)
                throws IOException {

            IcbTag icbTag = IcbTag.readFrom(reader);

            long uid = reader.readU32LE();
            long gid = reader.readU32LE();
            long permissions = reader.readU32LE();

            int fileLinkCount = reader.readU16LE();
            int recordFormat = reader.readU8();
            int recordDisplayAttributes = reader.readU8();

            long recordLength = reader.readU32LE();

            long informationLength = reader.readU64LE();
            long logicalBlocksRecorded = reader.readU64LE();

            Timestamp accessDateAndTime = Timestamp.readFrom(reader);
            Timestamp modificationDateAndTime = Timestamp.readFrom(reader);
            Timestamp attributeDateAndTime = Timestamp.readFrom(reader);

            long checkpoint = reader.readU32LE();

            LongAllocationDescriptor extendedAttributeICB = LongAllocationDescriptor.readFrom(reader);
            EntityIdentifier implementationIdentifier = EntityIdentifier.readFrom(reader);

            long uniqueId = reader.readU64LE();
            long lengthOfExtendedAttributes = reader.readU32LE();
            long lengthOfAllocationDescriptors = reader.readU32LE();

            if (lengthOfExtendedAttributes > Integer.MAX_VALUE) {
                throw new IOException(
                        "Extended attributes too large: "
                                + lengthOfExtendedAttributes
                );
            }

            if (lengthOfAllocationDescriptors > Integer.MAX_VALUE) {
                throw new IOException(
                        "Allocation descriptors too large: "
                                + lengthOfAllocationDescriptors
                );
            }

            byte[] extendedAttributes = reader.readBytes((int) lengthOfExtendedAttributes);
            byte[] allocationDescriptors = reader.readBytes((int) lengthOfAllocationDescriptors);

            return new FileEntry(
                    icbTag,
                    uid,
                    gid,
                    permissions,
                    fileLinkCount,
                    recordFormat,
                    recordDisplayAttributes,
                    recordLength,
                    informationLength,
                    logicalBlocksRecorded,
                    accessDateAndTime,
                    modificationDateAndTime,
                    attributeDateAndTime,
                    checkpoint,
                    extendedAttributeICB,
                    implementationIdentifier,
                    uniqueId,
                    lengthOfExtendedAttributes,
                    lengthOfAllocationDescriptors,
                    extendedAttributes,
                    allocationDescriptors
            );
        }
    }

    public record FileIdentifierDescriptor(
            int fileVersionNumber,
            int fileCharacteristics,
            int lengthOfFileIdentifier,
            LongAllocationDescriptor icb,
            int lengthOfImplementationUse,
            byte[] implementationUse,
            byte[] fileIdentifier,
            byte[] padding
    ) {
        public static FileIdentifierDescriptor readFrom(BinaryReader reader)
                throws IOException {

            int fileVersionNumber = reader.readU16LE();
            int fileCharacteristics = reader.readU8();
            int lengthOfFileIdentifier = reader.readU8();

            LongAllocationDescriptor icb = LongAllocationDescriptor.readFrom(reader);
            int lengthOfImplementationUse = reader.readU16LE();
            byte[] implementationUse = reader.readBytes(lengthOfImplementationUse);
            byte[] fileIdentifier = reader.readBytes(lengthOfFileIdentifier);
            int descriptorLength = 38 + lengthOfImplementationUse + lengthOfFileIdentifier;

            int paddingLength = (4 - (descriptorLength % 4)) % 4;
            byte[] padding = reader.readBytes(paddingLength);

            return new FileIdentifierDescriptor(
                    fileVersionNumber,
                    fileCharacteristics,
                    lengthOfFileIdentifier,
                    icb,
                    lengthOfImplementationUse,
                    implementationUse,
                    fileIdentifier,
                    padding
            );
        }
    }
}
