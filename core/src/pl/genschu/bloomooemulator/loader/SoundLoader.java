package pl.genschu.bloomooemulator.loader;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

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
        try {
            InputStream is = fileHandle.read();
            byte[] chunkId = new byte[4];
            is.read(chunkId);
            if (!Arrays.equals(chunkId, "RIFF".getBytes())) throw new IllegalArgumentException("Not a RIFF file");
            readLittleEndianInt(is); // chunkSize
            byte[] wavFormat = new byte[4];
            is.read(wavFormat);
            if (!Arrays.equals(wavFormat, "WAVE".getBytes())) throw new IllegalArgumentException("Not a WAVE file");
            byte[] subChunk1Id = new byte[4];
            is.read(subChunk1Id);
            if (!Arrays.equals(subChunk1Id, "fmt ".getBytes())) throw new IllegalArgumentException("fmt chunk not found");
            int subChunk1Size = readLittleEndianInt(is);
            readLittleEndianShort(is); // audioFormat
            int numChannels = readLittleEndianShort(is);
            int sampleRate = readLittleEndianInt(is);
            readLittleEndianInt(is); // byteRate
            readLittleEndianShort(is); // blockAlign
            int bitsPerSample = readLittleEndianShort(is);
            if (subChunk1Size > 16) is.skip(subChunk1Size - 16);
            byte[] subChunk2Id = new byte[4];
            is.read(subChunk2Id);
            while (!Arrays.equals(subChunk2Id, "data".getBytes())) {
                int skip = readLittleEndianInt(is);
                is.skip(skip);
                is.read(subChunk2Id);
            }
            int subChunk2Size = readLittleEndianInt(is);
            is.close();
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
