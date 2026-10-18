package pl.genschu.bloomooemulator.encoding;

import com.jcraft.jogg.Packet;
import com.jcraft.jogg.Page;
import com.jcraft.jogg.StreamState;
import com.jcraft.jogg.SyncState;
import com.jcraft.jorbis.Block;
import com.jcraft.jorbis.Comment;
import com.jcraft.jorbis.DspState;
import com.jcraft.jorbis.Info;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Decodes an Ogg Vorbis stream to 16-bit signed little-endian interleaved PCM,
 * the way the Reksio i Kapitan Nemo installer (setup.exe, statically linked
 * libvorbis 1.1-era vorbisfile) does when it unpacks {@code wav.snd}.
 *
 * The installer calls {@code ov_read(vf, buf, 0x1000, 0, 2, 1, &bitstream)}
 * (FUN_00425f50): every float sample is scaled by 32768, rounded by x87
 * {@code FISTP} in the default round-to-nearest-even mode and clamped to
 * [-32768, 32767]. JOrbis' own {@code VorbisFile.read} truncates {@code x*32767}
 * instead, so only the low-level synthesis API is used here and the conversion
 * is done by {@link #toPcm16(float)}.
 *
 * Approximation: JOrbis' synthesis (floor/residue/MDCT) is not bit-identical to
 * libvorbis, so individual samples may differ by a least significant bit.
 * End-of-stream trimming matches libvorbis, but JOrbis lacks the 1.1
 * "partial first frame" discard (setup.exe FUN_00440120, {@code sequence == 1});
 * no record of the known {@code wav.snd} reaches that branch. Only the first
 * logical stream is decoded; chained streams do not occur there either.
 */
public final class OggVorbisDecoder {
    private static final int READ_CHUNK = 4096;

    private OggVorbisDecoder() {}

    /** Decoded audio: {@code samples} holds interleaved 16-bit LE frames. */
    public record Pcm(int channels, int sampleRate, byte[] samples) {
        public int bitsPerSample() {
            return 16;
        }
    }

    /** Stream parameters from the identification header. */
    public record Format(int channels, int sampleRate) {
        public int bitsPerSample() {
            return 16;
        }
    }

    /** Decodes the whole stream into memory. The caller keeps ownership of {@code input}. */
    public static Pcm decode(InputStream input) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Format format = decode(input, out);
        return new Pcm(format.channels(), format.sampleRate(), out.toByteArray());
    }

    /**
     * Decodes the whole stream, writing PCM frames to {@code out} as they are
     * produced. The caller keeps ownership of both streams.
     */
    public static Format decode(InputStream input, OutputStream out) throws IOException {
        SyncState sync = new SyncState();
        StreamState stream = new StreamState();
        Page page = new Page();
        Packet packet = new Packet();
        Info info = new Info();
        Comment comment = new Comment();
        DspState dsp = new DspState();
        Block block = new Block(dsp);

        sync.init();
        try {
            if (!nextPage(input, sync, page)) {
                throw new IOException("Input does not appear to be an Ogg bitstream");
            }
            stream.init(page.serialno());
            info.init();
            comment.init();
            if (stream.pagein(page) < 0 || stream.packetout(packet) != 1
                    || info.synthesis_headerin(comment, packet) < 0) {
                throw new IOException("Ogg bitstream does not contain Vorbis audio");
            }

            int headers = 1;
            while (headers < 3) {
                int result = stream.packetout(packet);
                if (result == 0) {
                    if (!nextPage(input, sync, page)) {
                        throw new IOException("End of file before the Vorbis headers were complete");
                    }
                    stream.pagein(page);
                    continue;
                }
                if (result < 0 || info.synthesis_headerin(comment, packet) < 0) {
                    throw new IOException("Corrupt Vorbis header packet");
                }
                headers++;
            }

            dsp.synthesis_init(info);
            block.init(dsp);

            int channels = info.channels;
            float[][][] pcmHolder = new float[1][][];
            int[] offsets = new int[channels];
            byte[] frameBuffer = new byte[0];

            boolean endOfStream = false;
            while (!endOfStream) {
                int result = stream.packetout(packet);
                if (result == 0) {
                    if (stream.e_o_s != 0 || !nextPage(input, sync, page)) {
                        endOfStream = true;
                        continue;
                    }
                    // Pages of another logical stream are rejected by pagein.
                    stream.pagein(page);
                    continue;
                }
                if (result < 0) {
                    // A hole in the data: ov_read reports OV_HOLE and the installer
                    // simply keeps calling it, so decoding continues.
                    continue;
                }
                if (block.synthesis(packet) == 0) {
                    dsp.synthesis_blockin(block);
                }
                int samples;
                while ((samples = dsp.synthesis_pcmout(pcmHolder, offsets)) > 0) {
                    float[][] pcm = pcmHolder[0];
                    int length = samples * channels * 2;
                    if (frameBuffer.length < length) {
                        frameBuffer = new byte[length];
                    }
                    for (int channel = 0; channel < channels; channel++) {
                        float[] source = pcm[channel];
                        int base = offsets[channel];
                        int target = channel * 2;
                        for (int i = 0; i < samples; i++) {
                            int value = toPcm16(source[base + i]);
                            frameBuffer[target] = (byte) value;
                            frameBuffer[target + 1] = (byte) (value >> 8);
                            target += channels * 2;
                        }
                    }
                    out.write(frameBuffer, 0, length);
                    dsp.synthesis_read(samples);
                }
            }
            return new Format(channels, info.rate);
        } finally {
            block.clear();
            dsp.clear();
            stream.clear();
            info.clear();
            sync.clear();
        }
    }

    /**
     * {@code ov_read} sample conversion from setup.exe: {@code FISTP(x * 32768.0)}
     * clamped to the 16-bit range. A NaN stores the x87 "integer indefinite"
     * value 0x80000000, which the clamp turns into -32768.
     */
    static int toPcm16(float sample) {
        if (Float.isNaN(sample)) {
            return Short.MIN_VALUE;
        }
        double scaled = Math.rint(sample * 32768.0);
        if (scaled > Short.MAX_VALUE) {
            return Short.MAX_VALUE;
        }
        if (scaled < Short.MIN_VALUE) {
            return Short.MIN_VALUE;
        }
        return (int) scaled;
    }

    /** Pulls the next complete page, reading more input as needed. */
    private static boolean nextPage(InputStream input, SyncState sync, Page page) throws IOException {
        while (true) {
            int result = sync.pageout(page);
            if (result == 1) {
                return true;
            }
            // result < 0 means skipped garbage before a capture pattern; keep going.
            if (result < 0) {
                continue;
            }
            int index = sync.buffer(READ_CHUNK);
            int read = input.read(sync.data, index, READ_CHUNK);
            if (read <= 0) {
                return false;
            }
            sync.wrote(read);
        }
    }
}
