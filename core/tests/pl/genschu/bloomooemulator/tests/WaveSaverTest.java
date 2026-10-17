package pl.genschu.bloomooemulator.tests;

import org.junit.jupiter.api.Test;
import pl.genschu.bloomooemulator.saver.WaveSaver;

import static org.junit.jupiter.api.Assertions.*;

class WaveSaverTest {
    @Test
    void installerWaveUsesCWaveFileChunkLayout() {
        byte[] samples = {1, 2, 3, 4, 5, 6, 7, 8};

        byte[] wave = WaveSaver.saveInstallerWave(2, 22050, 16, samples);

        // RIFF/WAVE, 16-byte PCMWAVEFORMAT, fact chunk zeroed by CWaveFile::Close,
        // then data (setup.exe FUN_004085a1 / FUN_00408113 / FUN_004083dd).
        byte[] expected = {
                'R', 'I', 'F', 'F', 56, 0, 0, 0, 'W', 'A', 'V', 'E',
                'f', 'm', 't', ' ', 16, 0, 0, 0,
                1, 0, 2, 0, 0x22, 0x56, 0, 0, (byte) 0x88, 0x58, 0x01, 0, 4, 0, 16, 0,
                'f', 'a', 'c', 't', 4, 0, 0, 0, 0, 0, 0, 0,
                'd', 'a', 't', 'a', 8, 0, 0, 0,
                1, 2, 3, 4, 5, 6, 7, 8
        };
        assertArrayEquals(expected, wave);
        assertEquals(wave.length, WaveSaver.installerWaveLength(samples.length));
    }

    @Test
    void emptyStreamStillProducesFullHeader() {
        // Archive records such as _pusty_sfx0.wav decode to no samples at all.
        byte[] wave = WaveSaver.saveInstallerWave(1, 8000, 16, new byte[0]);

        assertEquals(WaveSaver.INSTALLER_HEADER_SIZE, wave.length);
        assertEquals(48, wave[4]);
        assertEquals(0, wave[52]);
    }
}
