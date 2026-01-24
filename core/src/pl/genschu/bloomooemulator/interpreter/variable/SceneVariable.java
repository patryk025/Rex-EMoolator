package pl.genschu.bloomooemulator.interpreter.variable;

import pl.genschu.bloomooemulator.interpreter.helpers.ArgumentHelper;
import pl.genschu.bloomooemulator.interpreter.values.*;

import java.util.*;

/**
 * SceneVariable represents a scene in the game.
 * Contains background, music, hotspot settings and scene-level behavior.
 *
 * Most methods require access to game context and should be handled by the interpreter.
 **/
public record SceneVariable(
    String name,
    String background,
    String music,
    int musicVolume,
    int minHotSpotZ,
    int maxHotSpotZ,
    Map<String, SignalHandler> signals
) implements Variable {

    public SceneVariable {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Variable name cannot be null or empty");
        }
        if (background == null) background = "";
        if (music == null) music = "";
        if (signals == null) {
            signals = Map.of();
        } else {
            signals = Map.copyOf(signals);
        }
    }

    public SceneVariable(String name) {
        this(name, "", "", 1000, 0, 10000000, Map.of());
    }

    // ========================================
    // INTERFACE IMPLEMENTATION
    // ========================================

    @Override
    public Value value() {
        return new StringValue(name);
    }

    @Override
    public VariableType type() {
        return VariableType.SCENE;
    }

    @Override
    public Variable withValue(Value newValue) {
        return this;
    }

    @Override
    public Map<String, VariableMethod> methods() {
        return METHODS;
    }

    @Override
    public Variable withSignal(String signalName, SignalHandler handler) {
        Map<String, SignalHandler> newSignals = new HashMap<>(signals);
        newSignals.put(signalName, handler);
        return new SceneVariable(name, background, music, musicVolume, minHotSpotZ, maxHotSpotZ, newSignals);
    }

    // ========================================
    // HELPER METHODS
    // ========================================

    public SceneVariable withMusicVolume(int newVolume) {
        return new SceneVariable(name, background, music, newVolume, minHotSpotZ, maxHotSpotZ, signals);
    }

    public SceneVariable withMinHotSpotZ(int newMin) {
        return new SceneVariable(name, background, music, musicVolume, newMin, maxHotSpotZ, signals);
    }

    public SceneVariable withMaxHotSpotZ(int newMax) {
        return new SceneVariable(name, background, music, musicVolume, minHotSpotZ, newMax, signals);
    }

    public SceneVariable withMusic(String newMusic) {
        return new SceneVariable(name, background, newMusic, musicVolume, minHotSpotZ, maxHotSpotZ, signals);
    }

    // ========================================
    // METHODS DEFINITION
    // ========================================

    private static final Map<String, VariableMethod> METHODS = Map.ofEntries(
        Map.entry("GETMINHSPRIORITY", (self, args) -> {
            SceneVariable thisVar = (SceneVariable) self;
            return MethodResult.noChange(new IntValue(thisVar.minHotSpotZ));
        }),

        Map.entry("GETMAXHSPRIORITY", (self, args) -> {
            SceneVariable thisVar = (SceneVariable) self;
            return MethodResult.noChange(new IntValue(thisVar.maxHotSpotZ));
        }),

        Map.entry("GETPLAYINGANIMO", (self, args) -> {
            // Get playing animations into group - handled by interpreter
            return MethodResult.noChange(NullValue.INSTANCE);
        }),

        Map.entry("PAUSE", (self, args) -> {
            // Pause scene - handled by interpreter/game
            return MethodResult.noChange(NullValue.INSTANCE);
        }),

        Map.entry("REMOVECLONES", (self, args) -> {
            // Remove clones - handled by interpreter
            return MethodResult.noChange(NullValue.INSTANCE);
        }),

        Map.entry("RESUME", (self, args) -> {
            // Resume scene - handled by interpreter/game
            return MethodResult.noChange(NullValue.INSTANCE);
        }),

        Map.entry("RESUMEONLY", (self, args) -> {
            // Resume only specified group - handled by interpreter/game
            return MethodResult.noChange(NullValue.INSTANCE);
        }),

        Map.entry("RUN", (self, args) -> {
            // Run method on variable - handled by interpreter
            return MethodResult.noChange(NullValue.INSTANCE);
        }),

        Map.entry("RUNCLONES", (self, args) -> {
            // Run behaviour on clones - handled by interpreter
            return MethodResult.noChange(NullValue.INSTANCE);
        }),

        Map.entry("SETMINHSPRIORITY", (self, args) -> {
            SceneVariable thisVar = (SceneVariable) self;
            if (args.isEmpty()) {
                throw new IllegalArgumentException("SETMINHSPRIORITY requires 1 argument");
            }

            int newMin = ArgumentHelper.getInt(args.get(0));
            return MethodResult.sets(thisVar.withMinHotSpotZ(newMin));
        }),

        Map.entry("SETMAXHSPRIORITY", (self, args) -> {
            SceneVariable thisVar = (SceneVariable) self;
            if (args.isEmpty()) {
                throw new IllegalArgumentException("SETMAXHSPRIORITY requires 1 argument");
            }

            int newMax = ArgumentHelper.getInt(args.get(0));
            return MethodResult.sets(thisVar.withMaxHotSpotZ(newMax));
        }),

        Map.entry("SETMUSICVOLUME", (self, args) -> {
            SceneVariable thisVar = (SceneVariable) self;
            if (args.isEmpty()) {
                throw new IllegalArgumentException("SETMUSICVOLUME requires 1 argument");
            }

            int newVolume = ArgumentHelper.getInt(args.get(0));
            // Actual music volume change handled by game
            return MethodResult.sets(thisVar.withMusicVolume(newVolume));
        }),

        Map.entry("STARTMUSIC", (self, args) -> {
            SceneVariable thisVar = (SceneVariable) self;
            if (args.isEmpty()) {
                throw new IllegalArgumentException("STARTMUSIC requires 1 argument");
            }

            String musicFile = ArgumentHelper.getString(args.get(0));
            // Actual music playback handled by game
            return MethodResult.sets(thisVar.withMusic(musicFile));
        })
    );

    // ========================================
    // CONVENIENT ACCESSORS
    // ========================================

    @Override
    public String toString() {
        return "SceneVariable[" + name + ", bg=" + background + ", music=" + music + "]";
    }
}
