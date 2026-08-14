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
            raf.seek(VRS_START_SECTOR * SECTOR_SIZE);

            boolean hasBea = false;
            boolean hasTea = false;
            boolean isUdf = false;

            while (raf.getFilePointer() < (VRS_START_SECTOR + 32) * SECTOR_SIZE && !hasTea) {
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
            long totalSectors = raf.length() / SECTOR_SIZE;
            long[] anchorSectors = {256, totalSectors - 256, totalSectors - 1};

            int validAvdpCount = 0;
            AnchorVolumeDescriptorPointer avdp = null;
            for (long sector : anchorSectors) {
                raf.seek(sector * SECTOR_SIZE);
                avdp = AnchorVolumeDescriptorPointer.readFrom(reader);
                if (avdp.tag.tagIdentifier == 2) { // Check for valid AVDP tag identifier
                    validAvdpCount++;
                }
                else {
                    avdp = null; // Reset if not valid
                }
            }

            if (avdp == null) {
                throw new IOException("No valid Anchor Volume Descriptor Pointer (AVDP) found in the UDF volume.");
            }

            if (validAvdpCount < 2) {
                Gdx.app.log("UdfFileSystem", "Warning: Only one valid AVDP found. UDF volume may be incomplete or corrupted.");
            }

            // Let's read a Volume Descriptor Sequence (VDS) from the main volume descriptor sequence extent
            raf.seek(avdp.mainVolumeDescriptorSequenceExtent.extentLocation * SECTOR_SIZE);
            long vdsEnd = avdp.mainVolumeDescriptorSequenceExtent.extentLocation * SECTOR_SIZE + avdp.mainVolumeDescriptorSequenceExtent.extentLength;

            int offset = 0;
            while (raf.getFilePointer() < vdsEnd) {
                Tag tag = Tag.readFrom(reader);
                offset += 16; // Size of the tag

                switch (TagType.fromValue(tag.tagIdentifier)) {
                    case Unknown -> {
                        // blank sector or unknown descriptor, skip to next sector
                    }
                    case PrimaryVolumeDescriptor -> {
                        // Handle Primary Volume Descriptor
                    }
                    case AnchorVolumeDescriptorPointer -> {
                        // Handle Anchor Volume Descriptor Pointer
                    }
                    case VolumeDescriptorSequence -> {
                        // Handle Volume Descriptor Sequence
                    }
                    case ImplementationUseVolumeDescriptor -> {
                        // Handle Implementation Use Volume Descriptor
                    }
                    case PartitionDescriptor -> {
                        // Handle Partition Descriptor
                    }
                    case LogicalVolumeDescriptor -> {
                        // Handle Logical Volume Descriptor
                    }
                    case UnallocatedSpaceDescriptor -> {
                        // Handle Unallocated Space Descriptor
                    }
                    case TerminatingDescriptor -> {
                        // Handle Terminating Descriptor
                    }
                    default -> throw new IOException("Unknown TagType: " + tag.tagIdentifier);
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
}
