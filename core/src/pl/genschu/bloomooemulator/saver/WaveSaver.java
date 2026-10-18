package pl.genschu.bloomooemulator.saver;

/**
 * Writes PCM WAV files with the chunk layout produced by the DirectX SDK
 * {@code CWaveFile} class that the Reksio i Kapitan Nemo installer uses to
 * store sounds unpacked from {@code wav.snd}:
 *
 * <pre>
 * "RIFF" size "WAVE"
 * "fmt "  16  PCMWAVEFORMAT (tag 1, channels, rate, rate*block, block, bits)
 * "fact"   4  0              (written as -1 on create, zeroed by Close)
 * "data"  n   samples        (padded to an even length by mmioAscend)
 * </pre>
 *
 * Sources: setup.exe FUN_004085a1 (WriteMMIO), FUN_00408113 (ResetFile) and
 * FUN_004083dd (Close); see docs/reverse-engineering/nemo-setup-wav-snd.md.
 */
public final class WaveSaver {
    /** Bytes in front of the sample data for the installer layout. */
    public static final int INSTALLER_HEADER_SIZE = 56;

    private WaveSaver() {}

    public static long installerWaveLength(long sampleBytes) {
        return INSTALLER_HEADER_SIZE + sampleBytes + (sampleBytes & 1);
    }

    public static byte[] saveInstallerWave(int channels, int sampleRate, int bitsPerSample, byte[] samples) {
        long total = installerWaveLength(samples.length);
        if (total > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("WAV too large: " + total + " bytes");
        }
        byte[] out = new byte[(int) total];
        writeInstallerHeader(out, channels, sampleRate, bitsPerSample, samples.length);
        System.arraycopy(samples, 0, out, INSTALLER_HEADER_SIZE, samples.length);
        return out;
    }

    /**
     * Fills the first {@link #INSTALLER_HEADER_SIZE} bytes of {@code out} with the
     * header for {@code sampleBytes} of data, so samples can be decoded straight
     * into the space behind it. A pad byte for odd sizes is the caller's job.
     */
    public static void writeInstallerHeader(byte[] out, int channels, int sampleRate, int bitsPerSample,
                                            long sampleBytes) {
        long total = installerWaveLength(sampleBytes);
        if (total > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("WAV too large: " + total + " bytes");
        }
        int blockAlign = channels * (bitsPerSample / 8);
        int pos = 0;
        pos = putTag(out, pos, "RIFF");
        pos = putI32(out, pos, (int) (total - 8));
        pos = putTag(out, pos, "WAVE");
        pos = putTag(out, pos, "fmt ");
        pos = putI32(out, pos, 16);
        pos = putI16(out, pos, 1);
        pos = putI16(out, pos, channels);
        pos = putI32(out, pos, sampleRate);
        pos = putI32(out, pos, sampleRate * blockAlign);
        pos = putI16(out, pos, blockAlign);
        pos = putI16(out, pos, bitsPerSample);
        pos = putTag(out, pos, "fact");
        pos = putI32(out, pos, 4);
        pos = putI32(out, pos, 0);
        pos = putTag(out, pos, "data");
        putI32(out, pos, (int) sampleBytes);
    }

    private static int putTag(byte[] out, int pos, String tag) {
        for (int i = 0; i < 4; i++) {
            out[pos + i] = (byte) tag.charAt(i);
        }
        return pos + 4;
    }

    private static int putI16(byte[] out, int pos, int value) {
        out[pos] = (byte) value;
        out[pos + 1] = (byte) (value >> 8);
        return pos + 2;
    }

    private static int putI32(byte[] out, int pos, int value) {
        out[pos] = (byte) value;
        out[pos + 1] = (byte) (value >> 8);
        out[pos + 2] = (byte) (value >> 16);
        out[pos + 3] = (byte) (value >> 24);
        return pos + 4;
    }
}
