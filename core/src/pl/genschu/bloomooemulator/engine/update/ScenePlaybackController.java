package pl.genschu.bloomooemulator.engine.update;

import com.badlogic.gdx.audio.Music;
import pl.genschu.bloomooemulator.engine.Game;
import pl.genschu.bloomooemulator.engine.context.EngineVariable;
import pl.genschu.bloomooemulator.engine.decision.events.AnimoEvent;
import pl.genschu.bloomooemulator.engine.decision.states.AnimoState;
import pl.genschu.bloomooemulator.interpreter.context.Context;
import pl.genschu.bloomooemulator.interpreter.variable.*;

import java.util.*;

/**
 * Engine playback scope, independent of the lexical context of a calling script.
 */
public final class ScenePlaybackController {
    private final Game game;
    private boolean paused;
    private Long timersPausedAt;
    private long timerOffset;
    private final Map<AnimoVariable.AnimoPlaybackState, Music> pausedSfx = new IdentityHashMap<>();

    public ScenePlaybackController(Game game) {
        this.game = game;
    }

    public boolean isPaused() { return paused; }
    public boolean areTimersEnabled() { return timersPausedAt == null; }

    public long timerTime(long rawTime) {
        return (timersPausedAt == null ? rawTime : timersPausedAt) - timerOffset;
    }

    private void enableTimers(boolean enabled) {
        if (!enabled && timersPausedAt == null) timersPausedAt = game.getEngineTimeMs();
        if (enabled && timersPausedAt != null) {
            timerOffset += game.getEngineTimeMs() - timersPausedAt;
            timersPausedAt = null;
        }
    }

    private Context live() {
        return (Context) game.getCurrentSceneContext();
    }

    private List<SoundVariable> sounds() {
        Set<SoundVariable.SoundState> seen = Collections.newSetFromMap(new IdentityHashMap<>());
        List<SoundVariable> result = new ArrayList<>();
        List<EngineVariable> registered = new ArrayList<>(game.getPlayingAudios());
        registered.addAll(live().getSoundVariablesForScheduling());
        for (EngineVariable variable : registered) {
            if (variable instanceof SoundVariable sound && seen.add(sound.state())) result.add(sound);
        }
        return result;
    }

    public void pause(boolean keepTimersRunning) {
        if (paused) return;
        paused = true;
        for (Variable variable : live().getGraphicsVariablesForScheduling()) {
            if (variable instanceof AnimoVariable animo) {
                if (animo.isPlaying()) animo.changeAnimoState(AnimoEvent.PAUSE);
                Music sfx = animo.state().currentSfx;
                if (sfx != null && sfx.isPlaying()) {
                    pausedSfx.put(animo.state(), sfx);
                    sfx.pause();
                }
            }
        }
        for (SoundVariable sound : sounds()) sound.pause();
        enableTimers(keepTimersRunning);
    }

    public void resume(GroupVariable group, boolean resumeTimers) {
        if (!paused) return;
        paused = false;
        Set<AnimoVariable.AnimoPlaybackState> selected = Collections.newSetFromMap(new IdentityHashMap<>());
        if (group != null) {
            for (String name : group.variableNames()) {
                if (live().getVariable(name) instanceof AnimoVariable animo) selected.add(animo.state());
            }
        }
        for (Variable variable : live().getGraphicsVariablesForScheduling()) {
            if (variable instanceof AnimoVariable animo) {
                if ((group == null || selected.contains(animo.state()))
                        && animo.getAnimationState() == AnimoState.PAUSED) {
                    animo.changeAnimoState(AnimoEvent.PLAY);
                }
                Music sfx = pausedSfx.get(animo.state());
                if (sfx != null && animo.state().currentSfx == sfx) sfx.play();
            }
        }
        pausedSfx.clear();
        for (SoundVariable sound : sounds()) sound.resume();
        enableTimers(resumeTimers);
    }

    /** A scene replacement releases the timer gate without reviving unloaded audio. */
    public void sceneChanged() {
        paused = false;
        pausedSfx.clear();
        enableTimers(true);
    }
}
