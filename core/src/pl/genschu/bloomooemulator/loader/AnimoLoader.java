package pl.genschu.bloomooemulator.loader;

import pl.genschu.bloomooemulator.interpreter.variable.AnimoVariable;
import pl.genschu.bloomooemulator.objects.Event;
import pl.genschu.bloomooemulator.objects.FrameData;
import pl.genschu.bloomooemulator.objects.Image;
import pl.genschu.bloomooemulator.loader.helpers.BinaryReader;
import pl.genschu.bloomooemulator.loader.helpers.InputStreamBinaryReader;
import pl.genschu.bloomooemulator.utils.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class AnimoLoader {

    public static AnimoVariable.AnimoData load(InputStream input) throws IOException {
        BinaryReader reader = new InputStreamBinaryReader(input);
        // Header
        String magicId = reader.readFixedString(4, StandardCharsets.UTF_8, false);
        if (!magicId.equals("NVP\0")) {
            throw new IllegalArgumentException("Invalid animation file. Expected: NVP\\0, got: " + magicId);
        }

        int imagesCount = reader.readU16LE();
        int colorDepth = reader.readU16LE();
        int eventsCount = reader.readU16LE();

        byte[] descriptionBytes = reader.readBytes(13);
        String description = StringUtils.convertNullTerminatedText(descriptionBytes, StandardCharsets.UTF_8);

        int fps = reader.readI32LE();
        reader.skipFully(4);
        int opacity = reader.readU8();
        reader.skipFully(12);
        int signatureLength = reader.readI32LE();

        byte[] signatureBytes = reader.readBytes(signatureLength);
        String signature = new String(signatureBytes, StandardCharsets.UTF_8);

        reader.skipFully(4); // padding

        // Events
        List<Event> events = readEvents(reader, eventsCount);

        // Images
        List<Image> images = readImages(reader, imagesCount, colorDepth, events);

        int maxWidth = 0, maxHeight = 0;
        for (Image img : images) {
            maxWidth = Math.max(maxWidth, img.width);
            maxHeight = Math.max(maxHeight, img.height);
        }

        return new AnimoVariable.AnimoData(
            events, images, images.size(), events.size(), colorDepth, fps, opacity, maxWidth, maxHeight, description, signature
        );
    }

    private static List<Event> readEvents(BinaryReader reader, int eventsCount) throws IOException {
        List<Event> events = new ArrayList<>();

        for (int i = 0; i < eventsCount; i++) {
            Event event = new Event();
            byte[] eventNameBytes = reader.readBytes(32);
            String eventName = StringUtils.convertNullTerminatedText(eventNameBytes, StandardCharsets.ISO_8859_1).toUpperCase();
            event.setName(eventName);

            event.setFramesCount(reader.readU16LE());
            reader.skipFully(4);
            event.setLoopStart(reader.readU16LE());
            event.setLoopEnd(reader.readU16LE());
            event.setRepeatCount(reader.readU16LE());
            event.setRepeatCounter(reader.readU16LE());
            reader.skipFully(4);
            event.setFlags(reader.readI32LE());
            event.setOpacity(reader.readU8());
            reader.skipFully(12);

            int numberOfFrames = event.getFramesCount();
            List<Integer> assignedFrames = new ArrayList<>();
            for (int j = 0; j < numberOfFrames; j++) {
                assignedFrames.add(reader.readU16LE());
            }
            event.setFramesNumbers(assignedFrames);

            List<FrameData> frames = new ArrayList<>();
            for (int j = 0; j < numberOfFrames; j++) {
                FrameData frame = readFrameData(reader);
                frames.add(frame);
            }
            event.setFrameData(frames);
            events.add(event);
        }

        return events;
    }

    private static FrameData readFrameData(BinaryReader reader) throws IOException {
        FrameData frame = new FrameData();
        frame.setStartingBytes(reader.readBytes(4));
        reader.skipFully(4);
        frame.setOffsetX(reader.readI16LE());
        frame.setOffsetY(reader.readI16LE());
        reader.skipFully(4);
        frame.setSfxRandomSeed(reader.readI32LE());
        reader.skipFully(4);
        frame.setOpacity(reader.readU8());
        reader.skipFully(5);
        int nameLength = reader.readI32LE();

        byte[] nameBytes = reader.readBytes(nameLength);
        frame.setName(StringUtils.convertNullTerminatedText(nameBytes, StandardCharsets.UTF_8));

        if (frame.getSfxRandomSeed() > 0) {
            int sfxDescriptionLength = reader.readI32LE();
            byte[] sfxDescriptionBytes = reader.readBytes(sfxDescriptionLength);
            String sfxDescription = StringUtils.convertNullTerminatedText(sfxDescriptionBytes, StandardCharsets.UTF_8);
            frame.setSfxDescription(sfxDescription);
        }
        frame.setSfxAudio(new ArrayList<>());

        return frame;
    }

    private static List<Image> readImages(BinaryReader reader, int imagesCount, int colorDepth, List<Event> events) throws IOException {
        List<Map<String, Object>> imagesMetadata = new ArrayList<>();

        for (int i = 0; i < imagesCount; i++) {
            Map<String, Object> metadata = new HashMap<>();
            metadata.put("width", reader.readU16LE());
            metadata.put("height", reader.readU16LE());
            metadata.put("offset_x", (int) reader.readI16LE());
            metadata.put("offset_y", (int) reader.readI16LE());
            metadata.put("compression_type", reader.readU16LE());
            metadata.put("image_size", reader.readI32LE());
            reader.skipFully(14);
            metadata.put("alpha_size", reader.readI32LE());

            byte[] nameBytes = reader.readBytes(20);
            metadata.put("name", StringUtils.convertNullTerminatedText(nameBytes, StandardCharsets.ISO_8859_1));

            imagesMetadata.add(metadata);
        }

        List<Image> images = new ArrayList<>();
        for (Map<String, Object> imageMetadata : imagesMetadata) {
            byte[] imageData = reader.readBytes((int) imageMetadata.get("image_size"));
            byte[] alphaData = reader.readBytes((int) imageMetadata.get("alpha_size"));

            Image image = new Image(
                (int) imageMetadata.get("width"),
                (int) imageMetadata.get("height"),
                (int) imageMetadata.get("offset_x"),
                (int) imageMetadata.get("offset_y"),
                colorDepth,
                imageData,
                alphaData,
                (int) imageMetadata.get("compression_type")
            );
            images.add(image);
        }

        // Assign image references to events
        for (Event event : events) {
            List<Image> eventFrames = new ArrayList<>();
            for (Integer frameNumber : event.getFramesNumbers()) {
                eventFrames.add(images.get(frameNumber));
            }
            event.setFrames(eventFrames);
        }

        return images;
    }
}
