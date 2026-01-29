package pl.genschu.bloomooemulator.interpreter.variable;

import pl.genschu.bloomooemulator.interpreter.helpers.ArgumentHelper;
import pl.genschu.bloomooemulator.interpreter.runtime.effects.RunMethodEffect;
import pl.genschu.bloomooemulator.interpreter.values.*;
import pl.genschu.bloomooemulator.interpreter.variable.ArgKind;
import pl.genschu.bloomooemulator.interpreter.variable.MethodSpec;

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
    public Map<String, MethodSpec> methods() {
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

    private static final Map<String, MethodSpec> METHODS = Map.ofEntries(
        Map.entry("GETMINHSPRIORITY", MethodSpec.of((self, args) -> {
            SceneVariable thisVar = (SceneVariable) self;
            return MethodResult.noChange(new IntValue(thisVar.minHotSpotZ));
        })),

        Map.entry("GETMAXHSPRIORITY", MethodSpec.of((self, args) -> {
            SceneVariable thisVar = (SceneVariable) self;
            return MethodResult.noChange(new IntValue(thisVar.maxHotSpotZ));
        })),

        Map.entry("GETPLAYINGANIMO", MethodSpec.of((self, args) -> {
            // Get playing animations into group - handled by interpreter
            return MethodResult.noChange(NullValue.INSTANCE);
        })),

        Map.entry("PAUSE", MethodSpec.of((self, args) -> {
            // Pause scene - handled by interpreter/game
            return MethodResult.noChange(NullValue.INSTANCE);
        })),

        Map.entry("REMOVECLONES", MethodSpec.of((self, args) -> {
            // Remove clones - handled by interpreter
            return MethodResult.noChange(NullValue.INSTANCE);
        })),

        Map.entry("RESUME", MethodSpec.of((self, args) -> {
            // Resume scene - handled by interpreter/game
            return MethodResult.noChange(NullValue.INSTANCE);
        })),

        Map.entry("RESUMEONLY", MethodSpec.of((self, args) -> {
            // Resume only specified group - handled by interpreter/game
            return MethodResult.noChange(NullValue.INSTANCE);
        })),

        Map.entry("RUN", MethodSpec.of((self, args) -> {
            if (args.size() < 2) {
                throw new IllegalArgumentException("RUN requires at least 2 arguments");
            }
            String varName = ArgumentHelper.getString(args.get(0));
            String methodName = ArgumentHelper.getString(args.get(1));
            List<Value> params = args.size() > 2 ? args.subList(2, args.size()) : List.of();
            return MethodResult.effects(List.of(new RunMethodEffect(varName, methodName, params)));
        })),

        Map.entry("RUNCLONES", MethodSpec.of((self, args) -> {
            // Run behaviour on clones - handled by interpreter
            return MethodResult.noChange(NullValue.INSTANCE);
        })),

        Map.entry("SETMINHSPRIORITY", MethodSpec.of((self, args) -> {
            SceneVariable thisVar = (SceneVariable) self;
            if (args.isEmpty()) {
                throw new IllegalArgumentException("SETMINHSPRIORITY requires 1 argument");
            }

            int newMin = ArgumentHelper.getInt(args.get(0));
            return MethodResult.sets(thisVar.withMinHotSpotZ(newMin));
        })),

        Map.entry("SETMAXHSPRIORITY", MethodSpec.of((self, args) -> {
            SceneVariable thisVar = (SceneVariable) self;
            if (args.isEmpty()) {
                throw new IllegalArgumentException("SETMAXHSPRIORITY requires 1 argument");
            }

            int newMax = ArgumentHelper.getInt(args.get(0));
            return MethodResult.sets(thisVar.withMaxHotSpotZ(newMax));
        })),

        Map.entry("SETMUSICVOLUME", MethodSpec.of((self, args) -> {
            SceneVariable thisVar = (SceneVariable) self;
            if (args.isEmpty()) {
                throw new IllegalArgumentException("SETMUSICVOLUME requires 1 argument");
            }

            int newVolume = ArgumentHelper.getInt(args.get(0));
            // Actual music volume change handled by game
            return MethodResult.sets(thisVar.withMusicVolume(newVolume));
        })),

        Map.entry("STARTMUSIC", MethodSpec.of((self, args) -> {
            SceneVariable thisVar = (SceneVariable) self;
            if (args.isEmpty()) {
                throw new IllegalArgumentException("STARTMUSIC requires 1 argument");
            }

            String musicFile = ArgumentHelper.getString(args.get(0));
            // Actual music playback handled by game
            return MethodResult.sets(thisVar.withMusic(musicFile));
        }))
    );

    // ========================================
    // CONVENIENT ACCESSORS
    // ========================================

    @Override
    public String toString() {
        return "SceneVariable[" + name + ", bg=" + background + ", music=" + music + "]";
    }
}
