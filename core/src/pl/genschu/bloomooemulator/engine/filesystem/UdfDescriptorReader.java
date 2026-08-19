package pl.genschu.bloomooemulator.engine.filesystem;

import pl.genschu.bloomooemulator.loader.helpers.BinaryReader;
import pl.genschu.bloomooemulator.utils.Checksums;

import java.io.IOException;

import static pl.genschu.bloomooemulator.engine.filesystem.UdfStructures.DescriptorTag;
import static pl.genschu.bloomooemulator.engine.filesystem.UdfStructures.TagIdentifier;

/** Reads and validates the descriptor tag and its CRC-protected payload. */
final class UdfDescriptorReader {
    static final int TAG_LENGTH = 16;

    private UdfDescriptorReader() {
    }

    static TaggedDescriptor read(BinaryReader reader) throws IOException {
        byte[] rawTag = reader.readBytes(TAG_LENGTH);
        DescriptorTag tag = DescriptorTag.parse(rawTag);
        validateTagChecksum(rawTag, tag);

        byte[] payload = reader.readBytes(tag.descriptorCrcLength());
        int actualCrc = Checksums.crc16Ccitt(payload);
        if (actualCrc != tag.descriptorCrc()) {
            throw new IOException("Invalid descriptor CRC for tag " + tag.identifier()
                    + " at logical block " + tag.location() + ": expected 0x"
                    + hex(tag.descriptorCrc()) + ", calculated 0x" + hex(actualCrc));
        }
        return new TaggedDescriptor(tag, payload);
    }

    static TaggedDescriptor readExpected(BinaryReader reader, TagIdentifier expected)
            throws IOException {
        TaggedDescriptor descriptor = read(reader);
        if (descriptor.tag().type() != expected) {
            throw new IOException("Expected " + expected + " descriptor, got tag "
                    + descriptor.tag().identifier() + " at logical block "
                    + descriptor.tag().location());
        }
        return descriptor;
    }

    private static void validateTagChecksum(byte[] rawTag, DescriptorTag tag)
            throws IOException {
        int calculated = 0;
        for (int i = 0; i < rawTag.length; i++) {
            if (i != 4) {
                calculated = (calculated + (rawTag[i] & 0xFF)) & 0xFF;
            }
        }

        if (calculated != tag.checksum()) {
            throw new IOException("Invalid descriptor tag checksum for tag " + tag.identifier()
                    + " at logical block " + tag.location() + ": expected 0x"
                    + hex(tag.checksum()) + ", calculated 0x" + hex(calculated));
        }
    }

    private static String hex(int value) {
        return String.format("%04X", value);
    }

    record TaggedDescriptor(DescriptorTag tag, byte[] payload) {
    }
}
