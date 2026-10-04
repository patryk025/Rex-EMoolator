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
import pl.genschu.bloomooemulator.interpreter.context.Context;
import pl.genschu.bloomooemulator.interpreter.runtime.ExecutionContext;
import pl.genschu.bloomooemulator.interpreter.variable.BehaviourVariable;
import pl.genschu.bloomooemulator.interpreter.variable.SceneVariable;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

public class GameMusicTest {

    @TempDir
    Path tempDir;

    @BeforeAll
    static void boot() {
        TestEnvironment.init();
    }

    @Test
    void arcadeReloadKeepsScriptSelectedMusicPlayingAndSilentSceneStopsIt() throws Exception {
        Game game = new Game(null, null);
        Context episode = new Context(new ExecutionContext());
        episode.setGame(game);
        Field episodeField = Game.class.getDeclaredField("currentEpisodeContext");
        episodeField.setAccessible(true);
        episodeField.set(game, episode);
        SceneVariable arcade = new SceneVariable("ARCADE");
        episode.setVariable("ARCADE", arcade);
        episode.setVariable("__INIT__", BehaviourVariable.fromScript("__INIT__",
                "{ARCADE^STARTMUSIC(\"TELEPORTKI.WAV\");}", Map.of()));
        Music music = playingMusic();
        game.getMusicCache().put("$\\TELEPORTKI.WAV", music);
        loadScene(game, arcade);
        // STARTMUSIC replaced the immutable SCENE record; Game's load snapshot
        // still has an empty MUSIC attribute, as on the first ARCADE visit.
        loadScene(game, (SceneVariable) episode.getVariable("ARCADE"));
        verify(music, never()).stop();
        verify(music, times(1)).play();
        assertSame(music, game.getCurrentSceneMusic());

        // A loader with no declared MUSIC may select the same track in __INIT__.
        loadScene(game, new SceneVariable("OTHER_LOADER"));
        verify(music, never()).stop();
        verify(music, times(1)).play();
        episode.removeVariable("__INIT__");
        loadScene(game, new SceneVariable("SILENT"));
        verify(music).stop();
    }

    @Test
    void declaredMusicContinuesAcrossScenesAndDifferentTrackReplacesIt() throws Exception {
        Game game = new Game(null, null);
        Music first = playingMusic();
        Music second = playingMusic();
        game.getMusicCache().put("$\\FIRST.WAV", first);
        game.getMusicCache().put("$\\SECOND.WAV", second);
        loadScene(game, new SceneVariable("ONE").withMusic("FIRST.WAV"));
        loadScene(game, new SceneVariable("TWO").withMusic("FIRST.WAV"));
        verify(first, never()).stop();
        verify(first).play();
        loadScene(game, new SceneVariable("THREE").withMusic("SECOND.WAV"));
        verify(first).stop();
        verify(second).play();
    }

    private static Music playingMusic() {
        Music music = mock(Music.class);
        AtomicBoolean playing = new AtomicBoolean();
        when(music.isPlaying()).thenAnswer(call -> playing.get());
        doAnswer(call -> { playing.set(true); return null; }).when(music).play();
        doAnswer(call -> { playing.set(false); return null; }).when(music).stop();
        return music;
    }

    private static void loadScene(Game game, SceneVariable scene) throws Exception {
        Method load = Game.class.getDeclaredMethod("loadScene", SceneVariable.class);
        load.setAccessible(true);
        load.invoke(game, scene);
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
