package pl.genschu.bloomooemulator.loader;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.files.FileHandle;
import pl.genschu.bloomooemulator.interpreter.variable.Attribute;
import pl.genschu.bloomooemulator.interpreter.variable.types.AnimoVariable;
import pl.genschu.bloomooemulator.objects.Event;
import pl.genschu.bloomooemulator.objects.FrameData;
import pl.genschu.bloomooemulator.objects.Image;
import pl.genschu.bloomooemulator.utils.FileUtils;
import pl.genschu.bloomooemulator.utils.StringUtils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class AnimoLoader {

    public static void loadAnimo(AnimoVariable variable) {
        if(variable.isLoaded()) {
            return;
        }
        Gdx.app.log("AnimoLoader", "Loading ANIMO: " + variable.getName());
        String filePath = FileUtils.resolveRelativePath(variable);
        try (FileInputStream f = new FileInputStream(filePath)) {
            readHeader(variable, f);
            readEvents(variable, f);
            readImagesMetadata(variable, f);
            Gdx.app.log("AnimoLoader", "Loaded ANIMO: " + variable.getName());
            variable.setPlaying(false);
            variable.setLoaded(true);
            if(variable.getAttribute("VISIBLE") == null)
                variable.setAttribute("VISIBLE", new Attribute("BOOL", "FALSE"));
        } catch (Exception e) {
            Gdx.app.error("AnimoLoader", "Error while loading ANIMO: " + e.getMessage(), e);
            if(e instanceof FileNotFoundException) {
                variable.setLoaded(true);
            }
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

        String description = StringUtils.convertNullTerminatedText(descriptionBytes, StandardCharsets.UTF_8);

        variable.setDescription(description);

        variable.setFps(buffer.getInt());

        buffer.position(buffer.position() + 4);
        variable.setOpacity(buffer.get() & 0xFF);
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
            String eventName = StringUtils.convertNullTerminatedText(eventNameBytes, StandardCharsets.ISO_8859_1).toUpperCase();
            event.setName(eventName);

            event.setFramesCount(buffer.getShort() & 0xFFFF);
            buffer.position(buffer.position() + 6);
            event.setLoopBy(buffer.getInt());
            buffer.position(buffer.position() + 4); // weird numbers
            buffer.position(buffer.position() + 6);
            event.setOpacity(buffer.get() & 0xFF);

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
                frame.setOpacity(fbBuffer.get() & 0xFF);
                fbBuffer.position(fbBuffer.position() + 5);
                int nameLength = fbBuffer.getInt();

                byte[] nameBytes = new byte[nameLength];
                f.read(nameBytes);
                frame.setName(StringUtils.convertNullTerminatedText(nameBytes, StandardCharsets.UTF_8));

                if (frame.getSfxRandomSeed() > 0) {
                    byte[] sfxDescriptionLengthBytes = new byte[4];
                    f.read(sfxDescriptionLengthBytes);
                    int sfxDescriptionLength = ByteBuffer.wrap(sfxDescriptionLengthBytes).order(ByteOrder.LITTLE_ENDIAN).getInt();
                    byte[] sfxDescriptionBytes = new byte[sfxDescriptionLength];
                    f.read(sfxDescriptionBytes);
                    String sfxDescription = StringUtils.convertNullTerminatedText(sfxDescriptionBytes, StandardCharsets.UTF_8);
                    String[] sfxFiles = sfxDescription.split(";");
                    List<Music> sfxAudioList = new ArrayList<>();
                    for(String sfxFile : sfxFiles) {
                        if(sfxFile.isEmpty()) {
                            continue;
                        }
                        if(!sfxFile.toLowerCase().startsWith("sfx\\")) {
                            sfxFile = "sfx\\"+ sfxFile;
                        }
                        if(!sfxFile.startsWith("$")) {
                            sfxFile = "$WAVS\\"+ sfxFile;
                        }
                        sfxFile = FileUtils.resolveRelativePath(variable, sfxFile);
                        FileHandle soundFileHandle = Gdx.files.absolute(sfxFile);

                        try {
                            sfxAudioList.add(Gdx.audio.newMusic(soundFileHandle));
                        } catch (Exception e) {
                            Gdx.app.error("AnimoLoader", "Error while loading SFX: " + e.getMessage());
                        }
                    }
                    frame.setSfxAudio(sfxAudioList);
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

        readImages(imagesMetadata, variable, f);
    }

    private static void readImages(List<Map<String, Object>> imagesMetadata, AnimoVariable variable, InputStream f) throws IOException {
        List<Image> images = new ArrayList<>();

        int maxWidth = 0;
        int maxHeight = 0;

        for (Map<String, Object> imageMetadata : imagesMetadata) {
            byte[] imageData = new byte[(int) imageMetadata.get("image_size")];
            f.read(imageData);
            byte[] alphaData = new byte[(int) imageMetadata.get("alpha_size")];
            f.read(alphaData);

            Image image = new Image(
                    ((Integer) imageMetadata.get("width")).intValue(),
                    ((Integer) imageMetadata.get("height")).intValue(),
                    ((Integer) imageMetadata.get("offset_x")).intValue(),
                    ((Integer) imageMetadata.get("offset_y")).intValue(),
                    variable.getColorDepth(),
                    imageData,
                    alphaData,
                    ((Integer) imageMetadata.get("compression_type")).intValue()
            );
            images.add(image);

            maxWidth = Math.max(maxWidth, image.width);
            maxHeight = Math.max(maxHeight, image.height);
        }

        variable.setImages(images);

        variable.setMaxWidth(maxWidth);
        variable.setMaxHeight(maxHeight);

        // TODO: check default behaviour
        variable.setImagesCount(images.size());
        variable.setEventsCount(variable.getEvents().size());

        for(Event event : variable.getEvents()) {
            List<Image> eventFrames = new ArrayList<>();

            for(Integer frameNumber : event.getFramesNumbers()) {
                eventFrames.add(images.get(frameNumber));
            }

            event.setFrames(eventFrames);
        }
    }
}