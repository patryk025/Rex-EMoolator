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

    @Test
    public void startSceneMusicStopsPreviousTrackAndStartsRequestedTrack() throws Exception {
        Files.createFile(tempDir.resolve("PAGORKI.WAV"));
        Files.createFile(tempDir.resolve("TELEPORTKI.WAV"));

        Game game = new Game(null, null);
        game.setLanguage("POL");
        game.getVfs().mountAssets(new LocalFileSystem(tempDir.toFile()));

        Audio originalAudio = Gdx.audio;
        Audio audio = mock(Audio.class);
        Music oldMusic = mock(Music.class);
        Music newMusic = mock(Music.class);
        try {
            Gdx.audio = audio;
            when(audio.newMusic(org.mockito.ArgumentMatchers.any(FileHandle.class)))
                .thenReturn(oldMusic, newMusic);
            when(oldMusic.isPlaying()).thenReturn(false, true);
            when(newMusic.isPlaying()).thenReturn(false);

            game.startSceneMusic("PAGORKI.WAV", 1000);
            game.startSceneMusic("TELEPORTKI.WAV", 678);

            verify(oldMusic).stop();
            verify(newMusic).setVolume(0.678f);
            verify(newMusic).play();
            assertSame(newMusic, game.getCurrentSceneMusic());
        } finally {
            Gdx.audio = originalAudio;
        }
    }
}
