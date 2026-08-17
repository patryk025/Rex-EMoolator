package pl.genschu.bloomooemulator.engine.filesystem;

import com.badlogic.gdx.Gdx;
import pl.genschu.bloomooemulator.loader.helpers.BinaryReader;
import pl.genschu.bloomooemulator.loader.helpers.RandomAccessFileBinaryReader;
import pl.genschu.bloomooemulator.loader.helpers.SeekableBinaryReader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
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
            PartitionDescriptor partitionDescriptor = null;
            LogicalVolumeDescriptor logicalVolumeDescriptor = null;

            while (reader.position() < vdsEnd && !foundTerminatingDescriptor) {
                long descriptorStart = reader.position();
                Tag tag = Tag.readFrom(reader);

                switch (TagType.fromValue(tag.tagIdentifier)) {
                    case Unknown -> {
                        // Unspecified/unknown descriptor type; skip the remainder of this logical block.
                        reader.seek(descriptorStart + SECTOR_SIZE);
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
                        partitionDescriptor = PartitionDescriptor.readFrom(reader);
                    }
                    case LogicalVolumeDescriptor -> {
                        logicalVolumeDescriptor = LogicalVolumeDescriptor.readFrom(reader);
                    }
                    case UnallocatedSpaceDescriptor -> {
                        // Handle Unallocated Space Descriptor
                        reader.skipFully(512 - 16);
                    }
                    case TerminatingDescriptor -> {
                        // Terminating Descriptor has only the tag, so we can skip the rest of the sector
                        reader.skipFully(512 - 16);
                        foundTerminatingDescriptor = true;
                    }
                    default -> throw new IOException("Unknown TagType: " + tag.tagIdentifier);
                }
            }

            if(partitionDescriptor == null) {
                throw new IOException("No valid Partition Descriptor found in the Volume Structure Descriptor.");
            }

            if(logicalVolumeDescriptor == null) {
                throw new IOException("No valid Logical Volume Descriptor found in the Volume Structure Descriptor.");
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

    public record ExtentAllocationDescriptor(
            long extentLength,
            long extentLocation
    ) {
        public static ExtentAllocationDescriptor readFrom(BinaryReader reader)
                throws IOException {

            return new ExtentAllocationDescriptor(
                    reader.readU32LE(),
                    reader.readU32LE()
            );
        }
    }

    public record AnchorVolumeDescriptorPointer(
            Tag tag,
            ExtentAllocationDescriptor mainVolumeDescriptorSequenceExtent,
            ExtentAllocationDescriptor reserveVolumeDescriptorSequenceExtent
    ) {
        public static AnchorVolumeDescriptorPointer readFrom(BinaryReader reader) throws IOException {
            Tag tag = Tag.readFrom(reader);
            ExtentAllocationDescriptor mainVolumeDescriptorSequenceExtent = ExtentAllocationDescriptor.readFrom(reader);
            ExtentAllocationDescriptor reserveVolumeDescriptorSequenceExtent = ExtentAllocationDescriptor.readFrom(reader);
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
            ExtentAllocationDescriptor integritySequenceExtent,
            byte[] partitionMaps
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
            ExtentAllocationDescriptor integritySequenceExtent = ExtentAllocationDescriptor.readFrom(reader);

            if (mapTableLength > Integer.MAX_VALUE) {
                throw new IOException("Partition map table too large: " + mapTableLength);
            }

            byte[] partitionMaps = reader.readBytes((int) mapTableLength);

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
}
