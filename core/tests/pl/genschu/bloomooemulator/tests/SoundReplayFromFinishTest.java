package pl.genschu.bloomooemulator.tests;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.TimeUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import pl.genschu.bloomooemulator.TestEnvironment;
import pl.genschu.bloomooemulator.builders.MethodHelper;
import pl.genschu.bloomooemulator.engine.Game;
import pl.genschu.bloomooemulator.engine.update.UpdateManager;
import pl.genschu.bloomooemulator.interpreter.context.Context;
import pl.genschu.bloomooemulator.interpreter.runtime.ExecutionContext;
import pl.genschu.bloomooemulator.interpreter.variable.PlaybackObserver;
import pl.genschu.bloomooemulator.interpreter.variable.SoundVariable;
import pl.genschu.bloomooemulator.interpreter.variable.Variable;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * RiWC Arcade dialogue plays every line through one SOUND (SNDATGOAL) and starts
 * the next line from that sound's ONFINISHED. The re-registration done by that
 * PLAY must survive the poll loop, or the next ONFINISHED never fires.
 */
class SoundReplayFromFinishTest {
    @BeforeAll static void boot() { TestEnvironment.init(); }

    @Test void soundReplayedFromItsOwnFinishFiresFinishAgain() {
        try (var time = mockStatic(TimeUtils.class)) {
            AtomicLong now = new AtomicLong(1_000_000_000L);
            time.when(TimeUtils::nanoTime).thenAnswer(invocation -> now.get());

            Game game = new Game(null, null, now::get);
            Context scene = new Context(new ExecutionContext(), null, game);
            SoundVariable snd = new SoundVariable("SNDATGOAL");
            snd.state().sound = mock(Sound.class);
            snd.state().duration = 1;
            scene.setVariable(snd.name(), snd);

            AtomicInteger finished = new AtomicInteger();
            snd.addObserver(new PlaybackObserver() {
                @Override public void onPlaybackStarted(Variable source, String eventName) {}
                @Override public void onPlaybackFinished(Variable source, String eventName) {
                    if (finished.incrementAndGet() == 1) {
                        // next dialogue line: STOP, then PLAY the same object again
                        MethodHelper.callWithContext(scene, snd, "STOP");
                        MethodHelper.callWithContext(scene, snd, "PLAY");
                    }
                }
            });

            MethodHelper.callWithContext(scene, snd, "PLAY");
            UpdateManager.AudioManager audio = new UpdateManager.AudioManager(game);

            now.addAndGet(2_000_000_000L);
            audio.update();
            assertEquals(1, finished.get());
            assertTrue(snd.isPlaying());
            assertEquals(1, game.getPlayingAudios().size(), "re-played sound must stay registered");

            now.addAndGet(2_000_000_000L);
            audio.update();
            assertEquals(2, finished.get());
            assertTrue(game.getPlayingAudios().isEmpty());
        }
    }

    @Test void playWithoutStopDoesNotDuplicateRegistration() {
        Game game = new Game(null, null, () -> 0L);
        Context scene = new Context(new ExecutionContext(), null, game);
        SoundVariable snd = new SoundVariable("LOOP");
        snd.state().sound = mock(Sound.class);
        snd.state().duration = 1;
        scene.setVariable(snd.name(), snd);

        MethodHelper.callWithContext(scene, snd, "PLAY");
        MethodHelper.callWithContext(scene, snd, "PLAY");
        assertEquals(1, game.getPlayingAudios().size());
    }
}
