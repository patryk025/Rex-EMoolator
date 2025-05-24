package pl.genschu.bloomooemulator.loader;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import pl.genschu.bloomooemulator.interpreter.variable.Attribute;
import pl.genschu.bloomooemulator.interpreter.variable.Variable;
import pl.genschu.bloomooemulator.interpreter.variable.types.SoundVariable;
import pl.genschu.bloomooemulator.utils.FileUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

public class SoundLoader {
    public static void loadSound(SoundVariable variable) {
        try {
            Attribute filename = variable.getAttribute("FILENAME");
            String filenameString = filename.getValue().toString();
            Variable value = variable.getContext().getVariable(filenameString);
            if(value != null) {
                filenameString = value.getValue().toString();
            }
            if (!filenameString.startsWith("$")) {
                filename.setValue("$WAVS\\" + filenameString);
            }
            String filePath = FileUtils.resolveRelativePath(variable);
            FileHandle soundFileHandle = Gdx.files.absolute(filePath);

            variable.setSound(Gdx.audio.newSound(soundFileHandle));
            parseWavHeader(soundFileHandle, variable);
        } catch (Exception e) {
            Gdx.app.error("SoundLoader", "Error while loading sound: " + e.getMessage());
        }
    }

    private static void parseWavHeader(FileHandle fileHandle, SoundVariable variable) {
        try {
            InputStream is = fileHandle.read();

            // Main Header
            byte[] chunkId = new byte[4];
            is.read(chunkId);
            if (!Arrays.equals(chunkId, "RIFF".getBytes())) {
                throw new IllegalArgumentException("Not a RIFF file");
            }

            int chunkSize = readLittleEndianInt(is);

            byte[] wavFormat = new byte[4];
            is.read(wavFormat);
            if (!Arrays.equals(wavFormat, "WAVE".getBytes())) {
                throw new IllegalArgumentException("Not a WAVE file");
            }

            // Sub Chunk 1 (fmt)
            byte[] subChunk1Id = new byte[4];
            is.read(subChunk1Id);
            if (!Arrays.equals(subChunk1Id, "fmt ".getBytes())) {
                throw new IllegalArgumentException("fmt chunk not found");
            }

            int subChunk1Size = readLittleEndianInt(is);
            int audioFormat = readLittleEndianShort(is);
            int numChannels = readLittleEndianShort(is);
            int sampleRate = readLittleEndianInt(is);
            int byteRate = readLittleEndianInt(is);
            int blockAlign = readLittleEndianShort(is);
            int bitsPerSample = readLittleEndianShort(is);

            // Skip any extra format bytes
            if (subChunk1Size > 16) {
                is.skip(subChunk1Size - 16);
            }

            // Sub Chunk 2 (data)
            byte[] subChunk2Id = new byte[4];
            is.read(subChunk2Id);

            // If not data chunk, skip it
            while (!Arrays.equals(subChunk2Id, "data".getBytes())) {
                int chunkSizeToSkip = readLittleEndianInt(is);
                is.skip(chunkSizeToSkip);
                is.read(subChunk2Id);
            }

            int subChunk2Size = readLittleEndianInt(is);
            is.close();

            // Calculate duration
            int bytesPerSample = bitsPerSample / 8;
            long totalSamples = subChunk2Size / ((long) numChannels * bytesPerSample);
            float duration = (float) totalSamples / sampleRate;

            variable.setSampleRate(sampleRate);
            variable.setChannels(numChannels);
            variable.setBitsPerSample(bitsPerSample);
            variable.setDuration(duration);
        } catch (Exception e) {
            Gdx.app.error("SoundLoader", "Error parsing WAV: " + e.getMessage(), e);
            // Fallback values
            variable.setSampleRate(22050);
            variable.setChannels(1);
            variable.setBitsPerSample(16);
            variable.setDuration(0);
        }
    }

    private static int readLittleEndianInt(InputStream is) throws IOException {
        byte[] bytes = new byte[4];
        is.read(bytes);
        return (bytes[0] & 0xFF) |
                ((bytes[1] & 0xFF) << 8) |
                ((bytes[2] & 0xFF) << 16) |
                ((bytes[3] & 0xFF) << 24);
    }

    private static int readLittleEndianShort(InputStream is) throws IOException {
        byte[] bytes = new byte[2];
        is.read(bytes);
        return (bytes[0] & 0xFF) | ((bytes[1] & 0xFF) << 8);
    }
}
