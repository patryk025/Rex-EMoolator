package pl.genschu.bloomooemulator.tests;

import com.badlogic.gdx.Audio;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.files.FileHandle;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import pl.genschu.bloomooemulator.TestEnvironment;
import pl.genschu.bloomooemulator.engine.Game;
import pl.genschu.bloomooemulator.engine.filesystem.LocalFileSystem;

import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class GameMusicTest {

    @TempDir
    Path tempDir;

    @BeforeAll
    static void boot() {
        TestEnvironment.init();
    }

    @Test
    public void testLoadMusic_EnablesLoopingForSceneMusic() throws Exception {
        Path musicFile = tempDir.resolve("INTRO1.WAV");
        Files.createFile(musicFile);

        Game game = new Game(null, null);
        game.setLanguage("POL");
        game.getVfs().mountAssets(new LocalFileSystem(tempDir.toFile()));

        Audio originalAudio = Gdx.audio;
        Audio audio = mock(Audio.class);
        Music music = mock(Music.class);
        try {
            Gdx.audio = audio;
            when(audio.newMusic(org.mockito.ArgumentMatchers.any(FileHandle.class))).thenReturn(music);

            Method loadMusic = Game.class.getDeclaredMethod("loadMusic", String.class);
            loadMusic.setAccessible(true);

            Music result = (Music) loadMusic.invoke(game, "$\\INTRO1.WAV");

            assertSame(music, result);
            verify(audio).newMusic(argThat(handle ->
                handle != null && handle.exists() && "INTRO1.WAV".equals(handle.name())
            ));
            verify(music).setLooping(true);
        } finally {
            Gdx.audio = originalAudio;
        }
    }
}
