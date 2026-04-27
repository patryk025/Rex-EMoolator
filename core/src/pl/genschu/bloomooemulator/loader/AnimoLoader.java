package pl.genschu.bloomooemulator.loader;

import pl.genschu.bloomooemulator.interpreter.variable.AnimoVariable;
import pl.genschu.bloomooemulator.objects.Event;
import pl.genschu.bloomooemulator.objects.FrameData;
import pl.genschu.bloomooemulator.objects.Image;
import pl.genschu.bloomooemulator.utils.StringUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class AnimoLoader {

    public static AnimoVariable.AnimoData load(String filePath) throws IOException {
        try (FileInputStream f = new FileInputStream(filePath)) {
            return load(f);
        }
    }

    public static AnimoVariable.AnimoData load(InputStream input) throws IOException {
        // Header
        byte[] magicIdBytes = new byte[4];
        input.read(magicIdBytes);
        String magicId = new String(magicIdBytes, StandardCharsets.UTF_8);
        if (!magicId.equals("NVP\0")) {
            throw new IllegalArgumentException("Invalid animation file. Expected: NVP\\0, got: " + magicId);
        }

        byte[] headerBytes = new byte[44];
        input.read(headerBytes);
        ByteBuffer buffer = ByteBuffer.wrap(headerBytes).order(ByteOrder.LITTLE_ENDIAN);

        int imagesCount = buffer.getShort() & 0xFFFF;
        int colorDepth = buffer.getShort() & 0xFFFF;
        int eventsCount = buffer.getShort() & 0xFFFF;

        byte[] descriptionBytes = new byte[13];
        buffer.get(descriptionBytes);
        String description = StringUtils.convertNullTerminatedText(descriptionBytes, StandardCharsets.UTF_8);

        int fps = buffer.getInt();

        buffer.position(buffer.position() + 4);
        int opacity = buffer.get() & 0xFF;
        buffer.position(buffer.position() + 12);
        int signatureLength = buffer.getInt();

        byte[] signatureBytes = new byte[signatureLength];
        input.read(signatureBytes);
        String signature = new String(signatureBytes, StandardCharsets.UTF_8);

        input.skip(4); // padding

        // Events
        List<Event> events = readEvents(input, eventsCount);

        // Images
        List<Image> images = readImages(input, imagesCount, colorDepth, events);

        int maxWidth = 0, maxHeight = 0;
        for (Image img : images) {
            maxWidth = Math.max(maxWidth, img.width);
            maxHeight = Math.max(maxHeight, img.height);
        }

        return new AnimoVariable.AnimoData(
            events, images, images.size(), events.size(), colorDepth, fps, opacity, maxWidth, maxHeight, description, signature
        );
    }

    private static List<Event> readEvents(InputStream input, int eventsCount) throws IOException {
        List<Event> events = new ArrayList<>();

        for (int i = 0; i < eventsCount; i++) {
            Event event = new Event();
            byte[] eventBytes = new byte[67];
            input.read(eventBytes);
            ByteBuffer buffer = ByteBuffer.wrap(eventBytes).order(ByteOrder.LITTLE_ENDIAN);

            byte[] eventNameBytes = new byte[32];
            buffer.get(eventNameBytes);
            String eventName = StringUtils.convertNullTerminatedText(eventNameBytes, StandardCharsets.ISO_8859_1).toUpperCase();
            event.setName(eventName);

            event.setFramesCount(buffer.getShort() & 0xFFFF);
            buffer.position(buffer.position() + 4);
            event.setLoopStart(buffer.getShort() & 0xFFFF);
            event.setLoopEnd(buffer.getShort() & 0xFFFF);
            event.setRepeatCount(buffer.getShort() & 0xFFFF);
            event.setRepeatCounter(buffer.getShort() & 0xFFFF);
            buffer.position(buffer.position() + 4);
            event.setFlags(buffer.getInt());
            event.setOpacity(buffer.get() & 0xFF);
            buffer.position(buffer.position() + 12);

            int numberOfFrames = event.getFramesCount();
            List<Integer> assignedFrames = new ArrayList<>();
            byte[] assignedFramesBytes = new byte[numberOfFrames * 2];
            input.read(assignedFramesBytes);
            ByteBuffer afBuffer = ByteBuffer.wrap(assignedFramesBytes).order(ByteOrder.LITTLE_ENDIAN);
            for (int j = 0; j < numberOfFrames; j++) {
                assignedFrames.add(afBuffer.getShort() & 0xFFFF);
            }
            event.setFramesNumbers(assignedFrames);

            List<FrameData> frames = new ArrayList<>();
            for (int j = 0; j < numberOfFrames; j++) {
                FrameData frame = readFrameData(input);
                frames.add(frame);
            }
            event.setFrameData(frames);
            events.add(event);
        }

        return events;
    }

    private static FrameData readFrameData(InputStream input) throws IOException {
        byte[] frameBytes = new byte[34];
        input.read(frameBytes);
        ByteBuffer fbBuffer = ByteBuffer.wrap(frameBytes).order(ByteOrder.LITTLE_ENDIAN);

        FrameData frame = new FrameData();
        byte[] startingBytes = new byte[4];
        fbBuffer.get(startingBytes);
        frame.setStartingBytes(startingBytes);

        fbBuffer.position(fbBuffer.position() + 4);
        frame.setOffsetX(fbBuffer.getShort());
        frame.setOffsetY(fbBuffer.getShort());
        fbBuffer.position(fbBuffer.position() + 4);
        frame.setSfxRandomSeed(fbBuffer.getInt());
        fbBuffer.position(fbBuffer.position() + 4);
        frame.setOpacity(fbBuffer.get() & 0xFF);
        fbBuffer.position(fbBuffer.position() + 5);
        int nameLength = fbBuffer.getInt();

        byte[] nameBytes = new byte[nameLength];
        input.read(nameBytes);
        frame.setName(StringUtils.convertNullTerminatedText(nameBytes, StandardCharsets.UTF_8));

        if (frame.getSfxRandomSeed() > 0) {
            byte[] sfxDescriptionLengthBytes = new byte[4];
            input.read(sfxDescriptionLengthBytes);
            int sfxDescriptionLength = ByteBuffer.wrap(sfxDescriptionLengthBytes).order(ByteOrder.LITTLE_ENDIAN).getInt();
            byte[] sfxDescriptionBytes = new byte[sfxDescriptionLength];
            input.read(sfxDescriptionBytes);
            String sfxDescription = StringUtils.convertNullTerminatedText(sfxDescriptionBytes, StandardCharsets.UTF_8);
            frame.setSfxDescription(sfxDescription);
        }
        frame.setSfxAudio(new ArrayList<>());

        return frame;
    }

    private static List<Image> readImages(InputStream input, int imagesCount, int colorDepth, List<Event> events) throws IOException {
        List<Map<String, Object>> imagesMetadata = new ArrayList<>();

        for (int i = 0; i < imagesCount; i++) {
            byte[] metadataBytes = new byte[52];
            input.read(metadataBytes);
            ByteBuffer buffer = ByteBuffer.wrap(metadataBytes).order(ByteOrder.LITTLE_ENDIAN);

            Map<String, Object> metadata = new HashMap<>();
            metadata.put("width", buffer.getShort() & 0xFFFF);
            metadata.put("height", buffer.getShort() & 0xFFFF);
            metadata.put("offset_x", (int) buffer.getShort());
            metadata.put("offset_y", (int) buffer.getShort());
            metadata.put("compression_type", buffer.getShort() & 0xFFFF);
            metadata.put("image_size", buffer.getInt());
            buffer.position(buffer.position() + 14);
            metadata.put("alpha_size", buffer.getInt());

            byte[] nameBytes = new byte[20];
            buffer.get(nameBytes);
            metadata.put("name", StringUtils.convertNullTerminatedText(nameBytes, StandardCharsets.ISO_8859_1));

            imagesMetadata.add(metadata);
        }

        List<Image> images = new ArrayList<>();
        for (Map<String, Object> imageMetadata : imagesMetadata) {
            byte[] imageData = new byte[(int) imageMetadata.get("image_size")];
            input.read(imageData);
            byte[] alphaData = new byte[(int) imageMetadata.get("alpha_size")];
            input.read(alphaData);

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
