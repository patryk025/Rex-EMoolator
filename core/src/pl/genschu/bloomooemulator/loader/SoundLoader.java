package pl.genschu.bloomooemulator.loader;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import pl.genschu.bloomooemulator.loader.helpers.BinaryReader;
import pl.genschu.bloomooemulator.loader.helpers.InputStreamBinaryReader;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class SoundLoader {
    public static void loadSound(pl.genschu.bloomooemulator.interpreter.variable.SoundVariable variable, FileHandle handle) {
        try {
            variable.state().sound = Gdx.audio.newSound(handle);
            parseWavHeader(handle, variable);
        } catch (Exception e) {
            Gdx.app.error("SoundLoader", "Error while loading sound: " + e.getMessage());
        }
    }

    private static void parseWavHeader(FileHandle fileHandle, pl.genschu.bloomooemulator.interpreter.variable.SoundVariable variable) {
        try (InputStream input = fileHandle.read()) {
            BinaryReader reader = new InputStreamBinaryReader(input);
            if (!"RIFF".equals(reader.readFixedString(4, StandardCharsets.US_ASCII, false))) {
                throw new IllegalArgumentException("Not a RIFF file");
            }
            reader.readI32LE(); // chunkSize
            if (!"WAVE".equals(reader.readFixedString(4, StandardCharsets.US_ASCII, false))) {
                throw new IllegalArgumentException("Not a WAVE file");
            }
            if (!"fmt ".equals(reader.readFixedString(4, StandardCharsets.US_ASCII, false))) {
                throw new IllegalArgumentException("fmt chunk not found");
            }
            int subChunk1Size = reader.readI32LE();
            reader.readU16LE(); // audioFormat
            int numChannels = reader.readU16LE();
            int sampleRate = reader.readI32LE();
            reader.readI32LE(); // byteRate
            reader.readU16LE(); // blockAlign
            int bitsPerSample = reader.readU16LE();
            if (subChunk1Size > 16) reader.skipFully(subChunk1Size - 16L);
            String subChunk2Id = reader.readFixedString(4, StandardCharsets.US_ASCII, false);
            while (!"data".equals(subChunk2Id)) {
                long skip = reader.readU32LE();
                reader.skipFully(skip);
                subChunk2Id = reader.readFixedString(4, StandardCharsets.US_ASCII, false);
            }
            long subChunk2Size = reader.readU32LE();
            int bytesPerSample = bitsPerSample / 8;
            long totalSamples = subChunk2Size / ((long) numChannels * bytesPerSample);
            float duration = (float) totalSamples / sampleRate;
            variable.state().sampleRate = sampleRate;
            variable.state().currentSampleRate = sampleRate;
            variable.state().channels = numChannels;
            variable.state().bitsPerSample = bitsPerSample;
            variable.state().duration = duration;
        } catch (Exception e) {
            Gdx.app.error("SoundLoader", "Error parsing WAV: " + e.getMessage(), e);
            variable.state().sampleRate = 22050;
            variable.state().currentSampleRate = 22050;
            variable.state().channels = 1;
            variable.state().bitsPerSample = 16;
            variable.state().duration = 0;
        }
    }

}
