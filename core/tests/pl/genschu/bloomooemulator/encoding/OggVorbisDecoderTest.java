package pl.genschu.bloomooemulator.encoding;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class OggVorbisDecoderTest {
    /**
     * 0.25 s, 440 Hz sine at amplitude 1/8, 8 kHz mono, encoded with
     * {@code ffmpeg -f lavfi -i sine=frequency=440:sample_rate=8000:duration=0.25 -c:a libvorbis -q:a 4}.
     */
    static final String FIXTURE = "snd/tone440_8k_mono.ogg";

    static byte[] fixture() throws IOException {
        for (Path candidate : new Path[] {
                Path.of("../assets/test-assets/" + FIXTURE),
                Path.of("assets/test-assets/" + FIXTURE)
        }) {
            if (Files.isRegularFile(candidate)) {
                return Files.readAllBytes(candidate);
            }
        }
        throw new AssertionError("Missing fixture: assets/test-assets/" + FIXTURE);
    }

    @Test
    void conversionRoundsHalfToEvenLikeX87Fistp() {
        assertEquals(0, OggVorbisDecoder.toPcm16(0.5f / 32768f));
        assertEquals(2, OggVorbisDecoder.toPcm16(1.5f / 32768f));
        assertEquals(2, OggVorbisDecoder.toPcm16(2.5f / 32768f));
        assertEquals(-2, OggVorbisDecoder.toPcm16(-2.5f / 32768f));
        assertEquals(1, OggVorbisDecoder.toPcm16(0.75f / 32768f));
    }

    @Test
    void conversionScalesBy32768AndClampsToSixteenBits() {
        // ov_read multiplies by 32768 (not 32767 as JOrbis' VorbisFile does).
        assertEquals(16384, OggVorbisDecoder.toPcm16(0.5f));
        assertEquals(-32768, OggVorbisDecoder.toPcm16(-1.0f));
        assertEquals(32767, OggVorbisDecoder.toPcm16(1.0f));
        assertEquals(32767, OggVorbisDecoder.toPcm16(3.0f));
        assertEquals(-32768, OggVorbisDecoder.toPcm16(-3.0f));
        assertEquals(-32768, OggVorbisDecoder.toPcm16(Float.NaN));
    }

    @Test
    void decodesFixtureToInterleavedLittleEndianPcm() throws IOException {
        OggVorbisDecoder.Pcm pcm = OggVorbisDecoder.decode(new ByteArrayInputStream(fixture()));

        assertEquals(1, pcm.channels());
        assertEquals(8000, pcm.sampleRate());
        assertEquals(16, pcm.bitsPerSample());
        // The last page carries granulepos 2000, but every audio packet sits on that
        // page, so no earlier position exists to trim against: libvorbis 1.1
        // (setup.exe FUN_00440120) and JOrbis both return the full 2048 samples.
        assertEquals(2048 * 2, pcm.samples().length);

        // Cross-checked against the ffmpeg 7.1.1 native Vorbis decoder (s16), which
        // matched this output sample for sample.
        short[] expected = {-12, -1378, -2584, -3485, -3976, -3999, -3551, -2684};
        for (int i = 0; i < expected.length; i++) {
            assertEquals(expected[i], sample(pcm.samples(), 100 + i), "sample " + (100 + i));
        }

        double amplitude = 32767 / 8.0;
        for (int i = 0; i < 2000; i++) {
            double ideal = amplitude * Math.sin(2 * Math.PI * 440 * i / 8000.0);
            assertEquals(ideal, sample(pcm.samples(), i), 200, "sample " + i);
        }
    }

    @Test
    void rejectsDataThatIsNotOgg() {
        InputStream riff = new ByteArrayInputStream("RIFF\0\0\0\0WAVEfmt ".getBytes());
        assertThrows(IOException.class, () -> OggVorbisDecoder.decode(riff));
    }

    @Test
    void rejectsTruncatedHeaders() throws IOException {
        byte[] data = fixture();
        byte[] truncated = java.util.Arrays.copyOf(data, 200);
        assertThrows(IOException.class, () -> OggVorbisDecoder.decode(new ByteArrayInputStream(truncated)));
    }

    private static short sample(byte[] pcm, int index) {
        return (short) ((pcm[index * 2] & 0xFF) | (pcm[index * 2 + 1] << 8));
    }
}
