package pl.genschu.bloomooemulator.tests;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.utils.TimeUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import pl.genschu.bloomooemulator.TestEnvironment;
import pl.genschu.bloomooemulator.builders.MethodHelper;
import pl.genschu.bloomooemulator.engine.Game;
import pl.genschu.bloomooemulator.engine.decision.events.AnimoEvent;
import pl.genschu.bloomooemulator.engine.decision.states.AnimoState;
import pl.genschu.bloomooemulator.engine.update.UpdateManager;
import pl.genschu.bloomooemulator.engine.update.ScenePlaybackController;
import pl.genschu.bloomooemulator.interpreter.context.Context;
import pl.genschu.bloomooemulator.interpreter.runtime.ExecutionContext;
import pl.genschu.bloomooemulator.interpreter.values.*;
import pl.genschu.bloomooemulator.interpreter.variable.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ScenePauseTest {
    @BeforeAll static void boot() { TestEnvironment.init(); }

    private static final class Fixture {
        final AtomicLong now = new AtomicLong();
        final Game game = new Game(null, null, now::get);
        final Context parent = new Context(new ExecutionContext(), null, game);
        final Context scene = new Context(new ExecutionContext(), parent, game);
        final SceneVariable sceneObject = new SceneVariable("MINIGAME");
        final UpdateManager.TimerManager timers = new UpdateManager.TimerManager(game);

        Fixture() {
            game.setCurrentSceneContext(scene);
            parent.setVariable(sceneObject.name(), sceneObject);
        }

        void call(String method, Value... args) {
            MethodHelper.callWithContext(parent, sceneObject, method, args);
        }

        void pulse(long time) {
            now.set(time);
            timers.updateTimers(now::get);
        }

        AnimoVariable animation(Context owner, String name) {
            AnimoVariable animo = new AnimoVariable(name);
            owner.setVariable(name, animo);
            animo.changeAnimoState(AnimoEvent.PLAY);
            return animo;
        }
    }

    @Test void parentRunEnvPausesLiveSceneAndKeepsLexicalBindings() {
        Fixture f = new Fixture();
        AnimoVariable animo = f.animation(f.scene, "ANNJAZON");
        GroupVariable group = new GroupVariable("__GRHELP__");
        f.parent.setVariable(group.name(), group);
        f.parent.setVariable("MARK", new IntegerVariable("MARK", 0));
        f.parent.setVariable("BFITMP", BehaviourVariable.fromScript("BFITMP", "{MARK^SET(1);}", Map.of()));
        f.scene.setVariable("BFITMP", BehaviourVariable.fromScript("BFITMP", "{MARK^SET(2);}", Map.of()));
        f.parent.setVariable("B_PAUSE_START", BehaviourVariable.fromScript("B_PAUSE_START",
                "{MINIGAME^GETPLAYINGANIMO(\"__GRHELP__\");MINIGAME^PAUSE();BFITMP^RUN();}", Map.of()));
        f.parent.setVariable("B_PAUSE_END", BehaviourVariable.fromScript("B_PAUSE_END",
                "{MINIGAME^RESUMEONLY(\"__GRHELP__\");}", Map.of()));
        ApplicationVariable app = new ApplicationVariable("GAME");
        MethodHelper.callWithContext(f.scene, app, "RUNENV", new StringValue("MINIGAME"), new StringValue("B_PAUSE_START"));
        assertEquals(AnimoState.PAUSED, animo.getAnimationState());
        assertEquals(List.of("ANNJAZON"), group.variableNames());
        assertEquals(new IntValue(1), f.parent.getVariable("MARK").value());
        MethodHelper.callWithContext(f.scene, app, "RUNENV", new StringValue("MINIGAME"), new StringValue("B_PAUSE_END"));
        assertTrue(animo.isPlaying());
    }

    @Test void pauseCoversEqualNamesAndResumeOnlySelectsGroupMembers() {
        Fixture f = new Fixture();
        AnimoVariable inherited = f.animation(f.parent, "SAME");
        AnimoVariable local = f.animation(f.scene, "SAME");
        AnimoVariable selected = f.animation(f.scene, "SELECTED");
        f.parent.setVariable("GROUP", new GroupVariable("GROUP", List.of("SELECTED")));
        f.call("PAUSE");
        assertEquals(AnimoState.PAUSED, inherited.getAnimationState());
        assertEquals(AnimoState.PAUSED, local.getAnimationState());
        AnimoVariable dialog = f.animation(f.scene, "DIALOG");
        f.call("PAUSE"); // repeated pause must not capture newly started dialog media
        assertTrue(dialog.isPlaying());
        f.call("RESUMEONLY", new StringValue("GROUP"));
        assertTrue(selected.isPlaying());
        assertEquals(AnimoState.PAUSED, local.getAnimationState());
        assertEquals(AnimoState.PAUSED, inherited.getAnimationState());
        f.call("RESUME"); // original runner does nothing when no longer scene-paused
        assertEquals(AnimoState.PAUSED, local.getAnimationState());
    }

    @Test void timersKeepRemainingIntervalAndResetDuringPauseUsesFrozenTime() {
        Fixture f = new Fixture();
        TimerVariable timer = new TimerVariable("TIMER", 100);
        f.scene.setVariable(timer.name(), timer);
        f.pulse(40);
        f.call("PAUSE");
        f.pulse(1040);
        assertEquals(0, timer.currentTickCount());
        f.call("RESUME");
        f.pulse(1099);
        assertEquals(0, timer.currentTickCount());
        f.pulse(1100);
        assertEquals(1, timer.currentTickCount());
        f.call("PAUSE");
        f.now.set(2100);
        MethodHelper.callWithContext(f.scene, timer, "RESET");
        f.call("RESUME");
        f.pulse(2199);
        assertEquals(0, timer.currentTickCount());
        f.pulse(2200);
        assertEquals(1, timer.currentTickCount());
    }

    @Test void pauseInsideTimerCallbackStopsRemainingTimersInSamePulse() {
        Fixture f = new Fixture();
        TimerVariable first = new TimerVariable("FIRST", 0);
        first = (TimerVariable) first.withSignal("ONTICK", (v, s, a) -> f.call("PAUSE"));
        TimerVariable second = new TimerVariable("SECOND", 0);
        f.scene.setVariable(first.name(), first);
        f.scene.setVariable(second.name(), second);
        f.pulse(100);
        assertEquals(1, first.currentTickCount());
        assertEquals(0, second.currentTickCount());
    }

    @Test void optionalTimerArgumentsAndSceneReplacementReleaseGate() {
        Fixture f = new Fixture();
        TimerVariable timer = new TimerVariable("TIMER", 100);
        f.scene.setVariable(timer.name(), timer);
        f.call("PAUSE", new BoolValue(true));
        f.pulse(100);
        assertEquals(1, timer.currentTickCount());
        f.call("RESUME", new BoolValue(false));
        f.pulse(1000);
        assertEquals(1, timer.currentTickCount());
        f.game.setCurrentSceneContext(new Context(new ExecutionContext(), f.parent, f.game));
        assertTrue(f.game.getScenePlayback().areTimersEnabled());
        assertFalse(f.game.getScenePlayback().isPaused());
        assertEquals(100, f.game.getTimerTimeMs());
    }

    @Test void resumeOnlyAlsoResumesSoundAndDoesNotStartStoppedSound() {
        Fixture f = new Fixture();
        SoundVariable sound = new SoundVariable("VOICE");
        sound.state().sound = mock(Sound.class);
        sound.state().soundId = 7;
        sound.state().playing = true;
        SoundVariable stopped = new SoundVariable("STOPPED");
        f.scene.setVariable(sound.name(), sound);
        f.scene.setVariable(stopped.name(), stopped);
        f.parent.setVariable("GROUP", new GroupVariable("GROUP"));
        f.call("PAUSE");
        assertFalse(sound.isPlaying());
        assertFalse(sound.update());
        f.call("RESUMEONLY", new StringValue("GROUP"));
        assertTrue(sound.isPlaying());
        assertFalse(stopped.isPlaying());
        verify(sound.state().sound).pause(7);
        verify(sound.state().sound).resume(7);
    }

    @Test void soundCompletionExcludesPauseAndStopCancelsResume() {
        try (var time = mockStatic(TimeUtils.class)) {
            AtomicLong now = new AtomicLong(1_000_000_000L);
            time.when(TimeUtils::nanoTime).thenAnswer(invocation -> now.get());
            SoundVariable sound = new SoundVariable("VOICE");
            sound.state().sound = mock(Sound.class);
            sound.state().duration = 2;
            sound.play();
            now.set(2_000_000_000L);
            sound.pause();
            now.set(12_000_000_000L);
            assertFalse(sound.update());
            sound.resume();
            assertFalse(sound.update());
            now.set(13_000_000_000L);
            assertTrue(sound.update());
            sound.play();
            sound.pause();
            sound.stop(false);
            sound.resume();
            assertFalse(sound.isPlaying());
        }
    }

    @Test void scenePauseAndBothResumeVariantsLeaveBackgroundMusicAlone() {
        Game game = mock(Game.class);
        Context scene = new Context(new ExecutionContext(), null, game);
        Music music = mock(Music.class);
        when(game.getCurrentSceneContext()).thenReturn(scene);
        when(game.getCurrentSceneMusic()).thenReturn(music);
        when(game.getPlayingAudios()).thenReturn(List.of());
        when(music.isPlaying()).thenReturn(true);
        ScenePlaybackController playback = new ScenePlaybackController(game);
        playback.pause(false);
        playback.resume(null, true);
        playback.pause(false);
        playback.resume(new GroupVariable("GROUP"), true);
        verifyNoInteractions(music);
    }
}
