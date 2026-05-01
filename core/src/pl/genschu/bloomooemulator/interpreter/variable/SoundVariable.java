package pl.genschu.bloomooemulator.interpreter.variable;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.TimeUtils;
import pl.genschu.bloomooemulator.annotations.InternalMutable;
import pl.genschu.bloomooemulator.interpreter.context.Context;
import pl.genschu.bloomooemulator.interpreter.helpers.ArgumentHelper;
import pl.genschu.bloomooemulator.interpreter.values.*;
import pl.genschu.bloomooemulator.interpreter.variable.capabilities.Initializable;
import pl.genschu.bloomooemulator.loader.SoundLoader;
import pl.genschu.bloomooemulator.utils.FileUtils;

import java.util.*;

/**
 * SoundVariable represents a sound effect in the game.
 * Uses mutable SoundState for playback state and loaded Sound handle.
 */
public record SoundVariable(
    String name,
    @InternalMutable SoundState state,
    Map<String, SignalHandler> signals
) implements Variable, Initializable {

    /**
     * Mutable state for sound playback.
     */
    public static final class SoundState {
        public Sound sound;
        public boolean playing = false;
        public long soundId = -1;
        public int sampleRate = 22050;
        public int currentSampleRate = 22050;
        public int channels = 1;
        public int bitsPerSample = 16;
        public float duration = 0f;
        public float playStartTime = 0f;
        public String filename = "";

        // Playback observers
        public List<PlaybackObserver> observers = new ArrayList<>();

        public SoundState() {}

        public SoundState copy() {
            SoundState copy = new SoundState();
            copy.sound = this.sound;
            copy.playing = false;  // clones start stopped
            copy.soundId = -1;
            copy.sampleRate = this.sampleRate;
            copy.currentSampleRate = this.currentSampleRate;
            copy.channels = this.channels;
            copy.bitsPerSample = this.bitsPerSample;
            copy.duration = this.duration;
            copy.filename = this.filename;
            return copy;
        }

        public void dispose() {
            if (sound != null) {
                sound.stop();
                sound.dispose();
                sound = null;
            }
            playing = false;
        }
    }

    public SoundVariable {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Variable name cannot be null or empty");
        }
        if (state == null) {
            state = new SoundState();
        }
        if (signals == null) {
            signals = Map.of();
        } else {
            signals = Map.copyOf(signals);
        }
    }

    public SoundVariable(String name) {
        this(name, new SoundState(), Map.of());
    }

    public SoundVariable(String name, String filename) {
        this(name);
        state.filename = filename != null ? filename : "";
    }

    // ========================================
    // INTERFACE IMPLEMENTATION
    // ========================================

    @Override
    public Value value() {
        return NullValue.INSTANCE;
    }

    @Override
    public VariableType type() {
        return VariableType.SOUND;
    }

    @Override
    public Variable withValue(Value newValue) {
        return this;
    }

    @Override
    public Map<String, MethodSpec> methods() {
        return METHODS;
    }

    @Override
    public Variable withSignal(String signalName, SignalHandler handler) {
        Map<String, SignalHandler> newSignals = new HashMap<>(signals);
        if (handler != null) {
            newSignals.put(signalName, handler);
        } else {
            newSignals.remove(signalName);
        }
        return new SoundVariable(name, state, newSignals);
    }

    @Override
    public Variable copyAs(String newName) {
        return new SoundVariable(newName, state.copy(), new HashMap<>(signals));
    }

    // ========================================
    // INITIALIZABLE
    // ========================================

    @Override
    public void init(Context context) {
        String filename = context.attributes().get(name, "FILENAME");
        if (filename != null) {
            state.filename = filename;
        }

        if (!state.filename.isEmpty()) {
            loadSound(context);
        }
    }

    private void loadSound(Context context) {
        try {
            String filename = state.filename;
            pl.genschu.bloomooemulator.interpreter.variable.Variable ref = context.getVariable(filename);
            if (ref != null) {
                filename = ref.value().toDisplayString();
            }
            if (!filename.startsWith("$")) {
                filename = "$WAVS\\" + filename;
            }
            String vfsPath = FileUtils.resolveVfsPath(context.getGame(), filename);
            SoundLoader.loadSound(this, context.getGame().getAudioFileHandle(vfsPath));
        } catch (Exception e) {
            Gdx.app.error("SoundVariable", "Error loading SOUND: " + state.filename, e);
        }
    }

    // ========================================
    // PLAYBACK METHODS (for managers)
    // ========================================

    public void play() {
        if (state.sound == null) return;
        try {
            state.sound.stop();
            state.soundId = state.sound.play();
            state.playing = true;
            state.sound.setVolume(state.soundId, 1.0f);
            state.sound.setPitch(state.soundId, (float) state.currentSampleRate / state.sampleRate);
            state.sound.setLooping(state.soundId, false);
            state.playStartTime = TimeUtils.nanoTime() / 1_000_000_000f;
            emitSignal("ONSTARTED");
            notifyObserversStarted();
        } catch (Exception e) {
            Gdx.app.log("SoundVariable", "Error on playing sound: " + e.getMessage(), e);
            emitSignal("ONFINISHED");
            notifyObserversFinished();
        }
    }

    public void pause() {
        if (state.sound != null) {
            state.sound.pause();
        }
        state.playing = false;
    }

    public void resume() {
        if (state.sound != null) {
            state.sound.resume();
        }
        state.playing = true;
    }

    public void stop(boolean emitSignal) {
        try {
            if (state.sound != null) state.sound.stop();
        } catch (Exception ignored) {}
        state.playing = false;
        if (emitSignal) {
            emitSignal("ONFINISHED");
            notifyObserversFinished();
        }
    }

    /**
     * Called by UpdateManager to check if sound has finished playing.
     */
    /**
     * Called by UpdateManager to check if sound has finished playing.
     * Returns true if this sound should be removed from playingAudios.
     */
    public boolean update() {
        if (state.playing && state.playStartTime > 0) {
            float now = TimeUtils.nanoTime() / 1_000_000_000f;
            float adjustedDuration = state.duration / ((float) state.currentSampleRate / state.sampleRate);
            if (now - state.playStartTime >= adjustedDuration) {
                state.playing = false;
                emitSignal("ONFINISHED");
                notifyObserversFinished();
                return true;
            }
        }
        return false;
    }

    // ========================================
    // CONVENIENT ACCESSORS
    // ========================================

    public Sound getSound() { return state.sound; }
    public boolean isPlaying() { return state.playing; }

    // ========================================
    // PLAYBACK OBSERVERS
    // ========================================

    public void addObserver(PlaybackObserver observer) {
        if (!state.observers.contains(observer)) {
            state.observers.add(observer);
        }
    }

    public void removeObserver(PlaybackObserver observer) {
        state.observers.remove(observer);
    }

    private void notifyObserversFinished() {
        for (int i = state.observers.size() - 1; i >= 0; i--) {
            state.observers.get(i).onPlaybackFinished(this, name);
        }
    }

    private void notifyObserversStarted() {
        for (int i = state.observers.size() - 1; i >= 0; i--) {
            state.observers.get(i).onPlaybackStarted(this, name);
        }
    }

    // ========================================
    // METHODS DEFINITION
    // ========================================

    private static final Map<String, MethodSpec> METHODS = Map.ofEntries(
        Map.entry("ISPLAYING", MethodSpec.of((self, args, ctx) ->
            MethodResult.returns(BoolValue.of(((SoundVariable) self).state.playing))
        )),

        Map.entry("LOAD", MethodSpec.of((self, args, ctx) -> {
            SoundVariable snd = (SoundVariable) self;
            if (snd.state.sound != null) {
                snd.state.sound.stop();
                snd.state.sound.dispose();
                snd.state.sound = null;
            }
            snd.state.playing = false;
            String path = ArgumentHelper.getString(args.get(0));
            snd.state.filename = path;
            if (!path.startsWith("$")) {
                path = "$WAVS\\" + path;
            }
            String vfsPath = FileUtils.resolveVfsPath(ctx.getGame(), path);
            SoundLoader.loadSound(snd, ctx.getGame().getVfs().getFileHandle(vfsPath));
            return MethodResult.noReturn();
        })),

        Map.entry("PAUSE", MethodSpec.of((self, args, ctx) -> {
            ((SoundVariable) self).pause();
            return MethodResult.noReturn();
        })),

        Map.entry("PLAY", MethodSpec.of((self, args, ctx) -> {
            SoundVariable snd = (SoundVariable) self;
            snd.play();
            // Register in playingAudios so UpdateManager can poll for ONFINISHED
            if (ctx != null && ctx.getGame() != null) {
                ctx.getGame().getPlayingAudios().add(snd);
            }
            return MethodResult.noReturn();
        })),

        Map.entry("RESUME", MethodSpec.of((self, args, ctx) -> {
            ((SoundVariable) self).resume();
            return MethodResult.noReturn();
        })),

        Map.entry("SETFREQ", MethodSpec.of((self, args, ctx) -> {
            SoundVariable snd = (SoundVariable) self;
            snd.state.currentSampleRate = ArgumentHelper.getInt(args.get(0));
            if (snd.state.sound != null && snd.state.soundId != -1) {
                snd.state.sound.setPitch(snd.state.soundId, (float) snd.state.currentSampleRate / snd.state.sampleRate);
            }
            return MethodResult.noReturn();
        })),

        Map.entry("STOP", MethodSpec.of((self, args, ctx) -> {
            SoundVariable snd = (SoundVariable) self;
            boolean emit = !args.isEmpty() && ArgumentHelper.getBoolean(args.get(0));
            snd.stop(emit);
            if (ctx != null && ctx.getGame() != null) {
                ctx.getGame().getPlayingAudios().remove(snd);
            }
            return MethodResult.noReturn();
        }))
    );

    @Override
    public String toString() {
        return "SoundVariable[" + name + ", file=" + state.filename + ", playing=" + state.playing + "]";
    }
}
