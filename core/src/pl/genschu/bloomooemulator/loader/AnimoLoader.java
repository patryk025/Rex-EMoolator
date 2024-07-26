package pl.genschu.bloomooemulator.loader;

import com.badlogic.gdx.Gdx;
import pl.genschu.bloomooemulator.interpreter.variable.types.AnimoVariable;
import pl.genschu.bloomooemulator.objects.Event;
import pl.genschu.bloomooemulator.objects.FrameData;
import pl.genschu.bloomooemulator.objects.Image;
import pl.genschu.bloomooemulator.utils.FileUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class AnimoLoader {

    public static void loadAnimo(AnimoVariable variable) {
        String filePath = FileUtils.resolveRelativePath(variable);
        try (FileInputStream f = new FileInputStream(filePath)) {
            readHeader(variable, f);
            readEvents(variable, f);
            readImagesMetadata(variable, f);
        } catch (IOException e) {
            Gdx.app.error("Animo loader", "Error while loading ANIMO: " + e.getMessage());
        }
    }

    private static void readHeader(AnimoVariable variable, InputStream f) throws IOException {
        byte[] magicIdBytes = new byte[4];
        f.read(magicIdBytes);
        String magicId = new String(magicIdBytes, StandardCharsets.UTF_8);
        if (!magicId.equals("NVP\0")) {
            throw new IllegalArgumentException("To nie jest poprawny plik animacji. Oczekiwano: NVP\\0, otrzymano: " + magicId);
        }

        byte[] headerBytes = new byte[44];
        f.read(headerBytes);
        ByteBuffer buffer = ByteBuffer.wrap(headerBytes).order(ByteOrder.LITTLE_ENDIAN);

        variable.setImagesCount(buffer.getShort() & 0xFFFF);
        variable.setColorDepth(buffer.getShort() & 0xFFFF);
        variable.setEventsCount(buffer.getShort() & 0xFFFF);

        byte[] descriptionBytes = new byte[13];
        buffer.get(descriptionBytes);
        String description = new String(descriptionBytes, StandardCharsets.UTF_8).split("\0")[0];

        variable.setDescription(description);

        variable.setFps(buffer.getInt());

        buffer.position(buffer.position() + 4);
        variable.setOpacity((int) buffer.get() & 0xFF);
        buffer.position(buffer.position() + 12);
        int signature_length = buffer.getInt();

        byte[] signatureBytes = new byte[signature_length];
        f.read(signatureBytes);
        String signature = new String(signatureBytes, StandardCharsets.UTF_8);

        variable.setSignature(signature);

        f.skip(4); // padding
    }

    private static void readEvents(AnimoVariable variable, InputStream f) throws IOException {
        List<Event> events = new ArrayList<>();

        for (int i = 0; i < variable.getEventsCount(); i++) {
            Event event = new Event();
            byte[] eventBytes = new byte[67];
            f.read(eventBytes);
            ByteBuffer buffer = ByteBuffer.wrap(eventBytes).order(ByteOrder.LITTLE_ENDIAN);

            byte[] eventNameBytes = new byte[32];
            buffer.get(eventNameBytes);
            String eventName = new String(eventNameBytes, StandardCharsets.ISO_8859_1).split("\0")[0];
            event.setName(eventName);

            event.setFramesCount(buffer.getShort() & 0xFFFF);
            buffer.position(buffer.position() + 6);
            event.setLoopBy(buffer.getInt());
            f.skip(4); // weird numbers
            buffer.position(buffer.position() + 6);
            event.setOpacity((int) buffer.get() & 0xFF);

            buffer.position(buffer.position() + 12);

            int numberOfFrames = event.getFramesCount();
            List<Integer> assignedFrames = new ArrayList<>();
            byte[] assignedFramesBytes = new byte[numberOfFrames * 2];
            f.read(assignedFramesBytes);
            ByteBuffer afBuffer = ByteBuffer.wrap(assignedFramesBytes).order(ByteOrder.LITTLE_ENDIAN);
            for (int j = 0; j < numberOfFrames; j++) {
                assignedFrames.add(afBuffer.getShort() & 0xFFFF);
            }
            event.setFramesNumbers(assignedFrames);

            List<FrameData> frames = new ArrayList<>();
            for (int j = 0; j < numberOfFrames; j++) {
                byte[] frameBytes = new byte[34];
                f.read(frameBytes);
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
                frame.setOpacity((int) fbBuffer.get() & 0xFF);
                fbBuffer.position(fbBuffer.position() + 5);
                int nameLength = fbBuffer.getInt();

                byte[] nameBytes = new byte[nameLength];
                f.read(nameBytes);
                frame.setName(new String(nameBytes, StandardCharsets.UTF_8).split("\0")[0]);

                if (frame.getSfxRandomSeed() > 0) {
                    byte[] sfxDescriptionLengthBytes = new byte[4];
                    f.read(sfxDescriptionLengthBytes);
                    int sfxDescriptionLength = ByteBuffer.wrap(sfxDescriptionLengthBytes).order(ByteOrder.LITTLE_ENDIAN).getInt();
                    byte[] sfxDescriptionBytes = new byte[sfxDescriptionLength];
                    f.read(sfxDescriptionBytes);
                    String sfxDescription = new String(sfxDescriptionBytes, StandardCharsets.UTF_8).split("\0")[0];
                    frame.setSfxAudio(Arrays.asList(sfxDescription.split(";")));
                } else {
                    frame.setSfxAudio(new ArrayList<>());
                }

                frames.add(frame);
            }
            event.setFrameData(frames);
            events.add(event);
        }

        variable.setEvents(events);
    }

    private static void readImagesMetadata(AnimoVariable variable, InputStream f) throws IOException {
        List<Map<String, Object>> imagesMetadata = new ArrayList<>();

        for (int i = 0; i < variable.getImagesCount(); i++) {
            byte[] metadataBytes = new byte[52];
            f.read(metadataBytes);
            ByteBuffer buffer = ByteBuffer.wrap(metadataBytes).order(ByteOrder.LITTLE_ENDIAN);

            Map<String, Object> metadata = new HashMap<>();
            metadata.put("width", buffer.getShort() & 0xFFFF);
            metadata.put("height", buffer.getShort() & 0xFFFF);
            metadata.put("offset_x", buffer.getShort());
            metadata.put("offset_y", buffer.getShort());
            metadata.put("compression_type", buffer.getShort() & 0xFFFF);
            metadata.put("image_size", buffer.getInt());
            buffer.position(buffer.position() + 10);
            metadata.put("alpha_size", buffer.getInt());

            byte[] nameBytes = new byte[20];
            buffer.get(nameBytes);
            metadata.put("name", new String(nameBytes, StandardCharsets.ISO_8859_1).split("\0")[0]);

            imagesMetadata.add(metadata);
        }

        readImages(imagesMetadata, variable, f);
    }

    private static void readImages(List<Map<String, Object>> imagesMetadata, AnimoVariable variable, InputStream f) throws IOException {
        List<Image> images = new ArrayList<>();

        for (Map<String, Object> imageMetadata : imagesMetadata) {
            byte[] imageData = new byte[(int) imageMetadata.get("image_size")];
            f.read(imageData);
            byte[] alphaData = new byte[(int) imageMetadata.get("alpha_size")];
            f.read(alphaData);

            Image image = new Image(
                    (int) imageMetadata.get("width"),
                    (int) imageMetadata.get("height"),
                    (int) imageMetadata.get("offset_x"),
                    (int) imageMetadata.get("offset_y"),
                    (int) imageMetadata.get("compression_type"),
                    imageData,
                    alphaData,
                    (int) imageMetadata.get("compression_type")
            );
            images.add(image);
        }

        variable.setImages(images);
    }
}