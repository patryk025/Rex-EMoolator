package pl.genschu.bloomooemulator.engine.filesystem;

import com.badlogic.gdx.Gdx;
import pl.genschu.bloomooemulator.loader.helpers.BinaryReader;
import pl.genschu.bloomooemulator.loader.helpers.InputStreamBinaryReader;
import pl.genschu.bloomooemulator.loader.helpers.RandomAccessFileBinaryReader;
import pl.genschu.bloomooemulator.loader.helpers.SeekableBinaryReader;

import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static pl.genschu.bloomooemulator.engine.filesystem.UdfDescriptorReader.TaggedDescriptor;
import static pl.genschu.bloomooemulator.engine.filesystem.UdfStructures.*;

/** Parses the UDF volume and turns its ICB tree into filesystem entries. */
final class UdfReader {
    private static final String LOG_TAG = "UdfFileSystem";
    private static final int SECTOR_SIZE = 2048;
    private static final int VRS_START_SECTOR = 16;
    private static final int VRS_MAX_SECTORS = 32;
    private static final int AVDP_START_SECTOR = 256;
    private static final int MINIMUM_VDS_SECTORS = 16;
    private static final int MAX_DIRECTORY_DEPTH = 1024;

    private final File image;
    private final Map<String, Entry> entries = new LinkedHashMap<>();

    UdfReader(File image) {
        this.image = image;
    }

    Map<String, Entry> readIndex() throws IOException {
        try (RandomAccessFile file = new RandomAccessFile(image, "r")) {
            SeekableBinaryReader reader = new RandomAccessFileBinaryReader(file);
            validateVolumeRecognitionSequence(reader);
            List<AnchorVolumeDescriptorPointer> anchors = findAnchors(reader);
            Volume volume = readVolume(reader, anchors);

            log("Logical Volume Identifier: " + volume.logicalVolume().decodedIdentifier());
            FileSetDescriptor fileSet = readFileSetDescriptor(reader, volume);
            walkEntry(
                    file,
                    reader,
                    volume,
                    fileSet.rootDirectoryIcb(),
                    "",
                    "",
                    true,
                    new HashSet<>(),
                    0
            );
        }
        return Map.copyOf(entries);
    }

    private void validateVolumeRecognitionSequence(SeekableBinaryReader reader)
            throws IOException {
        reader.seek((long) VRS_START_SECTOR * SECTOR_SIZE);
        boolean foundBeginning = false;
        boolean foundNamespace = false;
        boolean foundTerminator = false;

        for (int i = 0; i < VRS_MAX_SECTORS && !foundTerminator; i++) {
            VolumeStructureDescriptor descriptor =
                    VolumeStructureDescriptor.readFrom(reader, SECTOR_SIZE);
            if (descriptor.type() != 0) {
                throw new IOException("Invalid Volume Structure Descriptor type: "
                        + descriptor.type());
            }

            switch (descriptor.identifier()) {
                case "BEA01" -> foundBeginning = true;
                case "NSR02", "NSR03" -> {
                    if (!foundBeginning) {
                        log("Warning: " + descriptor.identifier()
                                + " appears before BEA01");
                    }
                    foundNamespace = true;
                }
                case "TEA01" -> foundTerminator = true;
                default -> {
                    // Other ECMA-167 namespaces may coexist in this area.
                }
            }
        }

        if (!foundNamespace) {
            throw new IOException("No NSR02/NSR03 descriptor in the Volume "
                    + "Recognition Sequence");
        }
    }

    private List<AnchorVolumeDescriptorPointer> findAnchors(SeekableBinaryReader reader)
            throws IOException {
        long totalSectors = reader.length() / SECTOR_SIZE;
        Set<Long> candidateSectors = new LinkedHashSet<>(List.of(
                (long) AVDP_START_SECTOR,
                totalSectors - AVDP_START_SECTOR,
                totalSectors - 1
        ));

        List<AnchorVolumeDescriptorPointer> anchors = new ArrayList<>();
        List<IOException> invalidCandidates = new ArrayList<>();
        for (long sector : candidateSectors) {
            if (sector < 0 || sector * SECTOR_SIZE + UdfDescriptorReader.TAG_LENGTH
                    > reader.length()) {
                continue;
            }

            reader.seek(sector * SECTOR_SIZE);
            try {
                TaggedDescriptor descriptor = UdfDescriptorReader.read(reader);
                if (descriptor.tag().type() == TagIdentifier.ANCHOR_VOLUME_DESCRIPTOR_POINTER) {
                    anchors.add(AnchorVolumeDescriptorPointer.parse(descriptor.payload()));
                }
            } catch (IOException e) {
                invalidCandidates.add(e);
            }
        }

        if (anchors.isEmpty()) {
            IOException failure = new IOException(
                    "No valid Anchor Volume Descriptor Pointer found in " + image);
            invalidCandidates.forEach(failure::addSuppressed);
            throw failure;
        }
        if (anchors.size() < 2) {
            log("Warning: only one valid Anchor Volume Descriptor Pointer was found");
        }
        return anchors;
    }

    private Volume readVolume(
            SeekableBinaryReader reader,
            List<AnchorVolumeDescriptorPointer> anchors
    ) throws IOException {
        List<IOException> failures = new ArrayList<>();
        for (AnchorVolumeDescriptorPointer anchor : anchors) {
            for (ShortAllocationDescriptor sequence : List.of(
                    anchor.mainVolumeDescriptorSequence(),
                    anchor.reserveVolumeDescriptorSequence()
            )) {
                try {
                    return readVolumeDescriptorSequence(reader, sequence);
                } catch (IOException e) {
                    failures.add(e);
                }
            }
        }

        IOException failure = new IOException(
                "Neither the main nor reserve Volume Descriptor Sequence is readable");
        failures.forEach(failure::addSuppressed);
        throw failure;
    }

    private Volume readVolumeDescriptorSequence(
            SeekableBinaryReader reader,
            ShortAllocationDescriptor sequence
    ) throws IOException {
        requireRecordedExtent(sequence.extentType(), "Volume Descriptor Sequence");
        if (sequence.byteLength() < (long) MINIMUM_VDS_SECTORS * SECTOR_SIZE) {
            log("Warning: Volume Descriptor Sequence is shorter than 16 sectors");
        }

        long start = Math.multiplyExact(sequence.extentLocation(), (long) SECTOR_SIZE);
        long end = Math.addExact(start, sequence.byteLength());
        requireImageRange(reader, start, sequence.byteLength(), "Volume Descriptor Sequence");

        List<PartitionDescriptor> partitions = new ArrayList<>();
        LogicalVolumeDescriptor logicalVolume = null;
        boolean terminated = false;
        for (long offset = start; offset + UdfDescriptorReader.TAG_LENGTH <= end;
             offset = Math.addExact(offset, SECTOR_SIZE)) {
            reader.seek(offset);
            TaggedDescriptor descriptor = UdfDescriptorReader.read(reader);
            switch (descriptor.tag().type()) {
                case PARTITION_DESCRIPTOR ->
                        partitions.add(PartitionDescriptor.parse(descriptor.payload()));
                case LOGICAL_VOLUME_DESCRIPTOR ->
                        logicalVolume = LogicalVolumeDescriptor.parse(descriptor.payload());
                case TERMINATING_DESCRIPTOR -> terminated = true;
                default -> {
                    // The remaining VDS descriptor types are not needed for traversal.
                }
            }
            if (terminated) {
                break;
            }
        }

        if (!terminated) {
            throw new IOException("Volume Descriptor Sequence has no terminating descriptor");
        }
        if (partitions.isEmpty()) {
            throw new IOException("Volume Descriptor Sequence has no Partition Descriptor");
        }
        if (logicalVolume == null) {
            throw new IOException("Volume Descriptor Sequence has no Logical Volume Descriptor");
        }
        if (logicalVolume.logicalBlockSize() <= 0) {
            throw new IOException("Invalid logical block size: "
                    + logicalVolume.logicalBlockSize());
        }
        return new Volume(logicalVolume, List.copyOf(partitions));
    }

    private FileSetDescriptor readFileSetDescriptor(
            SeekableBinaryReader reader,
            Volume volume
    ) throws IOException {
        LongAllocationDescriptor location =
                volume.logicalVolume().fileSetDescriptorSequence();
        requireRecordedExtent(location.extentType(), "File Set Descriptor Sequence");
        long offset = physicalOffset(volume, location.extentLocation());
        requireImageRange(reader, offset, UdfDescriptorReader.TAG_LENGTH,
                "File Set Descriptor");
        reader.seek(offset);
        TaggedDescriptor descriptor = UdfDescriptorReader.readExpected(
                reader,
                TagIdentifier.FILE_SET_DESCRIPTOR
        );
        return FileSetDescriptor.parse(descriptor.payload());
    }

    private void walkEntry(
            RandomAccessFile file,
            SeekableBinaryReader reader,
            Volume volume,
            LongAllocationDescriptor icb,
            String path,
            String name,
            boolean expectedDirectory,
            Set<IcbKey> activeDirectories,
            int depth
    ) throws IOException {
        if (depth > MAX_DIRECTORY_DEPTH) {
            throw new IOException("UDF directory nesting exceeds " + MAX_DIRECTORY_DEPTH);
        }
        requireRecordedExtent(icb.extentType(), "ICB");

        LogicalBlockAddress address = icb.extentLocation();
        long offset = physicalOffset(volume, address);
        requireImageRange(reader, offset, UdfDescriptorReader.TAG_LENGTH, "File Entry");
        reader.seek(offset);
        TaggedDescriptor descriptor = UdfDescriptorReader.read(reader);
        if (descriptor.tag().type() == TagIdentifier.EXTENDED_FILE_ENTRY) {
            throw new IOException("Extended File Entries are not supported yet");
        }
        if (descriptor.tag().type() != TagIdentifier.FILE_ENTRY) {
            throw new IOException("Expected File Entry at " + address + ", got tag "
                    + descriptor.tag().identifier());
        }

        FileEntry fileEntry = FileEntry.parse(descriptor.payload());
        boolean directory = fileEntry.icbTag().fileType() == FileType.DIRECTORY;
        if (directory != expectedDirectory) {
            throw new IOException("FID/ICB directory flag mismatch for " + printablePath(path));
        }
        if (!directory && fileEntry.icbTag().fileType() != FileType.REGULAR_FILE) {
            throw new IOException("Unsupported UDF file type " + fileEntry.icbTag().fileType()
                    + " at " + printablePath(path));
        }

        Content content = parseContent(volume, address, fileEntry);
        putEntry(path, new Entry(name, directory, fileEntry.informationLength(),
                content.segments(), content.inlineData()));
        if (!directory) {
            return;
        }

        IcbKey key = new IcbKey(address.logicalBlockNumber(),
                address.partitionReferenceNumber());
        if (!activeDirectories.add(key)) {
            throw new IOException("Directory ICB cycle at " + printablePath(path));
        }
        try {
            byte[] directoryBytes = readAll(file, content);
            readDirectory(
                    file,
                    reader,
                    volume,
                    directoryBytes,
                    path,
                    activeDirectories,
                    depth
            );
        } finally {
            activeDirectories.remove(key);
        }
    }

    private void readDirectory(
            RandomAccessFile file,
            SeekableBinaryReader reader,
            Volume volume,
            byte[] bytes,
            String parentPath,
            Set<IcbKey> activeDirectories,
            int depth
    ) throws IOException {
        ByteArrayInputStream input = new ByteArrayInputStream(bytes);
        BinaryReader directoryReader = new InputStreamBinaryReader(input);
        while (input.available() > 0) {
            if (input.available() < UdfDescriptorReader.TAG_LENGTH) {
                byte[] trailing = directoryReader.readBytes(input.available());
                if (isAllZero(trailing)) {
                    break;
                }
                throw new EOFException("Truncated File Identifier Descriptor in "
                        + printablePath(parentPath));
            }

            TaggedDescriptor descriptor = UdfDescriptorReader.readExpected(
                    directoryReader,
                    TagIdentifier.FILE_IDENTIFIER_DESCRIPTOR
            );
            FileIdentifierDescriptor fid =
                    FileIdentifierDescriptor.parse(descriptor.payload());

            int paddingLength = (4 - ((UdfDescriptorReader.TAG_LENGTH
                    + descriptor.payload().length) & 3)) & 3;
            directoryReader.skipFully(paddingLength);

            if (fid.isDeleted() || fid.isParent()) {
                continue;
            }

            String childName = fid.decodedName();
            validateFileName(childName);
            String childPath = parentPath.isEmpty()
                    ? childName
                    : parentPath + "/" + childName;
            walkEntry(
                    file,
                    reader,
                    volume,
                    fid.icb(),
                    childPath,
                    childName,
                    fid.isDirectory(),
                    activeDirectories,
                    depth + 1
            );
        }
    }

    private Content parseContent(
            Volume volume,
            LogicalBlockAddress fileEntryAddress,
            FileEntry entry
    ) throws IOException {
        if (entry.informationLength() < 0) {
            throw new IOException("UDF information length exceeds Java's signed long range");
        }

        byte[] descriptors = entry.allocationDescriptors();
        if (entry.icbTag().allocationDescriptorType() == AllocationDescriptorType.INLINE) {
            if (entry.informationLength() > descriptors.length) {
                throw new IOException("Inline file data is truncated");
            }
            return new Content(
                    entry.informationLength(),
                    List.of(),
                    Arrays.copyOf(descriptors, Math.toIntExact(entry.informationLength()))
            );
        }

        List<DataSegment> segments = new ArrayList<>();
        BinaryReader descriptorReader = new InputStreamBinaryReader(
                new ByteArrayInputStream(descriptors)
        );
        switch (entry.icbTag().allocationDescriptorType()) {
            case SHORT -> {
                requireDescriptorAlignment(descriptors, 8, "short_ad");
                for (int remaining = descriptors.length; remaining > 0; remaining -= 8) {
                    ShortAllocationDescriptor descriptor =
                            ShortAllocationDescriptor.readFrom(descriptorReader);
                    addSegment(
                            volume,
                            descriptor.extentType(),
                            descriptor.byteLength(),
                            new LogicalBlockAddress(
                                    descriptor.extentLocation(),
                                    fileEntryAddress.partitionReferenceNumber()
                            ),
                            segments
                    );
                }
            }
            case LONG -> {
                requireDescriptorAlignment(descriptors, 16, "long_ad");
                for (int remaining = descriptors.length; remaining > 0; remaining -= 16) {
                    LongAllocationDescriptor descriptor =
                            LongAllocationDescriptor.readFrom(descriptorReader);
                    addSegment(
                            volume,
                            descriptor.extentType(),
                            descriptor.byteLength(),
                            descriptor.extentLocation(),
                            segments
                    );
                }
            }
            case EXTENDED -> {
                requireDescriptorAlignment(descriptors, 20, "ext_ad");
                for (int remaining = descriptors.length; remaining > 0; remaining -= 20) {
                    ExtendedAllocationDescriptor descriptor =
                            ExtendedAllocationDescriptor.readFrom(descriptorReader);
                    addExtendedSegments(volume, descriptor, segments);
                }
            }
            case INLINE -> throw new AssertionError("handled above");
        }

        long describedLength = 0;
        for (DataSegment segment : segments) {
            describedLength = Math.addExact(describedLength, segment.length());
        }
        if (describedLength < entry.informationLength()) {
            throw new IOException("Allocation descriptors cover " + describedLength
                    + " bytes, but File Entry declares " + entry.informationLength());
        }
        return new Content(entry.informationLength(), List.copyOf(segments), null);
    }

    private void addSegment(
            Volume volume,
            ExtentType type,
            long length,
            LogicalBlockAddress location,
            List<DataSegment> segments
    ) throws IOException {
        if (length == 0) {
            return;
        }
        if (type == ExtentType.NEXT_EXTENT_OF_ALLOCATION_DESCRIPTORS) {
            throw new IOException("Chained Allocation Extent Descriptors are not supported yet");
        }

        boolean recorded = type == ExtentType.RECORDED_AND_ALLOCATED;
        segments.add(new DataSegment(
                recorded ? physicalOffset(volume, location) : 0,
                length,
                recorded
        ));
    }

    private void addExtendedSegments(
            Volume volume,
            ExtendedAllocationDescriptor descriptor,
            List<DataSegment> segments
    ) throws IOException {
        if (descriptor.informationLength() > descriptor.byteLength()
                || descriptor.recordedLength() > descriptor.informationLength()) {
            throw new IOException("Invalid ext_ad recorded/information/extent lengths");
        }
        if (descriptor.extentType() == ExtentType.NEXT_EXTENT_OF_ALLOCATION_DESCRIPTORS) {
            throw new IOException("Chained Allocation Extent Descriptors are not supported yet");
        }

        if (descriptor.extentType() == ExtentType.RECORDED_AND_ALLOCATED
                && descriptor.recordedLength() > 0) {
            segments.add(new DataSegment(
                    physicalOffset(volume, descriptor.extentLocation()),
                    descriptor.recordedLength(),
                    true
            ));
        }
        long zeroLength = descriptor.informationLength()
                - (descriptor.extentType() == ExtentType.RECORDED_AND_ALLOCATED
                ? descriptor.recordedLength() : 0);
        if (zeroLength > 0) {
            segments.add(new DataSegment(0, zeroLength, false));
        }
    }

    private long physicalOffset(Volume volume, LogicalBlockAddress address)
            throws IOException {
        PartitionDescriptor partition = resolvePartition(volume, address.partitionReferenceNumber());
        if (address.logicalBlockNumber() >= partition.length()) {
            throw new IOException("Logical block " + address.logicalBlockNumber()
                    + " lies outside partition " + partition.partitionNumber());
        }
        try {
            long physicalBlock = Math.addExact(
                    partition.startingLocation(),
                    address.logicalBlockNumber()
            );
            return Math.multiplyExact(physicalBlock, volume.logicalVolume().logicalBlockSize());
        } catch (ArithmeticException e) {
            throw new IOException("UDF physical offset overflow", e);
        }
    }

    private PartitionDescriptor resolvePartition(Volume volume, int referenceNumber)
            throws IOException {
        List<PartitionMap> maps = volume.logicalVolume().partitionMaps();
        if (referenceNumber < 0 || referenceNumber >= maps.size()) {
            throw new IOException("Invalid partition reference number: " + referenceNumber);
        }

        PartitionMap map = maps.get(referenceNumber);
        if (map instanceof Type1PartitionMap type1) {
            return volume.partitions().stream()
                    .filter(partition -> partition.partitionNumber() == type1.partitionNumber())
                    .findFirst()
                    .orElseThrow(() -> new IOException(
                            "Partition Descriptor not found for partition "
                                    + type1.partitionNumber()));
        }
        if (map instanceof Type2PartitionMap) {
            throw new IOException("Type 2 partition maps are not supported yet");
        }
        throw new IOException("Unsupported partition map type: " + map.type());
    }

    private static byte[] readAll(RandomAccessFile file, Content content) throws IOException {
        if (content.inlineData() != null) {
            return content.inlineData().clone();
        }
        if (content.length() > Integer.MAX_VALUE) {
            throw new IOException("Directory is too large to index: " + content.length());
        }

        byte[] result = new byte[(int) content.length()];
        int destination = 0;
        long remaining = content.length();
        for (DataSegment segment : content.segments()) {
            int count = (int) Math.min(segment.length(), remaining);
            if (count == 0) {
                break;
            }
            if (segment.recorded()) {
                requireImageRange(file.length(), segment.offset(), count, "directory extent");
                file.seek(segment.offset());
                file.readFully(result, destination, count);
            }
            destination += count;
            remaining -= count;
        }
        if (remaining != 0) {
            throw new EOFException("Directory allocation descriptors are truncated");
        }
        return result;
    }

    private void putEntry(String path, Entry entry) throws IOException {
        String normalized = UdfFileSystem.normalize(path);
        Entry existing = entries.putIfAbsent(normalized, entry);
        if (existing != null) {
            throw new IOException("Ambiguous duplicate UDF path: " + printablePath(path));
        }
    }

    private static void validateFileName(String name) throws IOException {
        if (name.isEmpty() || name.equals(".") || name.equals("..")
                || name.indexOf('/') >= 0 || name.indexOf('\\') >= 0) {
            throw new IOException("Invalid UDF file name: " + name);
        }
    }

    private static void requireDescriptorAlignment(byte[] data, int width, String type)
            throws IOException {
        if (data.length % width != 0) {
            throw new IOException(type + " array is truncated: " + data.length + " bytes");
        }
    }

    private static void requireRecordedExtent(ExtentType type, String description)
            throws IOException {
        if (type != ExtentType.RECORDED_AND_ALLOCATED) {
            throw new IOException(description + " is not recorded and allocated: " + type);
        }
    }

    private static void requireImageRange(
            SeekableBinaryReader reader,
            long offset,
            long length,
            String description
    ) throws IOException {
        requireImageRange(reader.length(), offset, length, description);
    }

    private static void requireImageRange(
            long imageLength,
            long offset,
            long length,
            String description
    ) throws IOException {
        if (offset < 0 || length < 0 || offset > imageLength
                || length > imageLength - offset) {
            throw new EOFException(description + " lies outside the image: offset="
                    + offset + ", length=" + length + ", imageLength=" + imageLength);
        }
    }

    private static boolean isAllZero(byte[] bytes) {
        for (byte value : bytes) {
            if (value != 0) {
                return false;
            }
        }
        return true;
    }

    private static String printablePath(String path) {
        return path.isEmpty() ? "/" : path;
    }

    private static void log(String message) {
        if (Gdx.app != null) {
            Gdx.app.log(LOG_TAG, message);
        }
    }

    record DataSegment(long offset, long length, boolean recorded) {
    }

    record Entry(
            String name,
            boolean directory,
            long length,
            List<DataSegment> segments,
            byte[] inlineData
    ) {
    }

    private record Content(long length, List<DataSegment> segments, byte[] inlineData) {
    }

    private record Volume(
            LogicalVolumeDescriptor logicalVolume,
            List<PartitionDescriptor> partitions
    ) {
    }

    private record IcbKey(long logicalBlockNumber, int partitionReferenceNumber) {
    }
}
