package pl.genschu.bloomooemulator.interpreter.variable;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import pl.genschu.bloomooemulator.engine.Game;
import pl.genschu.bloomooemulator.engine.decision.events.AnimoEvent;
import pl.genschu.bloomooemulator.engine.decision.states.AnimoState;
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
    public Map<String, MethodSpec> methods() {
        return METHODS;
    }

    @Override
    public Variable withSignal(String signalName, SignalHandler handler) {
        Map<String, SignalHandler> newSignals = new HashMap<>(signals);
        if (handler == null) {
            newSignals.remove(signalName);
        } else {
            newSignals.put(signalName, handler);
        }
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

    private static Music currentMusic(MethodContext ctx) {
        Game game = ctx.getGame();
        return game != null ? game.getCurrentSceneMusic() : null;
    }

    // ========================================
    // METHODS DEFINITION
    // ========================================

    private static final Map<String, MethodSpec> METHODS = Map.ofEntries(
        Map.entry("GETMINHSPRIORITY", MethodSpec.of((self, args, ctx) -> {
            SceneVariable thisVar = (SceneVariable) self;
            return MethodResult.returns(new IntValue(thisVar.minHotSpotZ));
        })),

        Map.entry("GETMAXHSPRIORITY", MethodSpec.of((self, args, ctx) -> {
            SceneVariable thisVar = (SceneVariable) self;
            return MethodResult.returns(new IntValue(thisVar.maxHotSpotZ));
        })),

        Map.entry("GETPLAYINGANIMO", MethodSpec.of((self, args, ctx) -> {
            if (args.isEmpty()) {
                throw new IllegalArgumentException("GETPLAYINGANIMO requires 1 argument");
            }
            String groupName = ArgumentHelper.getString(args.get(0));
            Variable groupVar = ctx.getVariable(groupName);
            if (!(groupVar instanceof GroupVariable group)) {
                Gdx.app.log("SceneVariable", "Variable " + groupName + " is not GROUP. Ignoring it.");
                return MethodResult.noReturn();
            }
            group.variableNames().clear();
            group.markerHolder()[0] = -1;
            for (Variable variable : ctx.context().getGraphicsVariables().values()) {
                if (variable instanceof AnimoVariable animo && animo.isPlaying()
                        && !group.variableNames().contains(animo.name())) {
                    group.variableNames().add(animo.name());
                }
            }
            if (!group.variableNames().isEmpty()) {
                group.markerHolder()[0] = 0;
            }
            return MethodResult.noReturn();
        })),

        Map.entry("PAUSE", MethodSpec.of((self, args, ctx) -> {
            Music music = currentMusic(ctx);
            if (music != null && music.isPlaying()) {
                music.pause();
            }
            for (Variable variable : ctx.context().getGraphicsVariables().values()) {
                if (variable instanceof AnimoVariable animo && animo.isPlaying()) {
                    animo.changeAnimoState(AnimoEvent.PAUSE);
                }
            }
            return MethodResult.noReturn();
        })),

        Map.entry("REMOVECLONES", MethodSpec.of((self, args, ctx) -> {
            if (args.size() < 3) {
                throw new IllegalArgumentException("REMOVECLONES requires 3 arguments: varName, firstId, lastId");
            }
            String varName = ArgumentHelper.getString(args.get(0));
            int firstId = ArgumentHelper.getInt(args.get(1));
            int lastId = ArgumentHelper.getInt(args.get(2));

            List<String> cloneNames = ctx.clones().getCloneNames(varName);
            if (cloneNames.isEmpty()) {
                return MethodResult.noReturn();
            }

            int from = Math.max(firstId, 1);
            int to = lastId < 0 ? cloneNames.size() : Math.min(lastId, cloneNames.size());

            for (int i = from; i <= to; i++) {
                String cloneName = varName + "_" + i;
                if (ctx.getVariable(cloneName) == null) {
                    continue;
                }
                ctx.removeVariable(cloneName);
                ctx.clones().removeClone(varName, cloneName);
            }
            return MethodResult.noReturn();
        })),

        Map.entry("RESUME", MethodSpec.of((self, args, ctx) -> {
            SceneVariable thisVar = (SceneVariable) self;
            Music music = currentMusic(ctx);
            if (music != null && !music.isPlaying()) {
                music.setVolume(thisVar.musicVolume / 1000.0f);
                music.play();
            }
            for (Variable variable : ctx.context().getGraphicsVariables().values()) {
                if (variable instanceof AnimoVariable animo
                        && animo.getAnimationState() == AnimoState.PAUSED) {
                    animo.changeAnimoState(AnimoEvent.PLAY);
                }
            }
            return MethodResult.noReturn();
        })),

        Map.entry("RESUMEONLY", MethodSpec.of((self, args, ctx) -> {
            if (args.isEmpty()) {
                throw new IllegalArgumentException("RESUMEONLY requires 1 argument");
            }
            String groupName = ArgumentHelper.getString(args.get(0));
            Variable groupVar = ctx.getVariable(groupName);
            if (!(groupVar instanceof GroupVariable group)) {
                Gdx.app.log("SceneVariable", "Variable " + groupName + " is not GROUP. Ignoring it.");
                return MethodResult.noReturn();
            }
            for (String memberName : group.variableNames()) {
                Variable member = ctx.getVariable(memberName);
                if (member instanceof AnimoVariable animo
                        && animo.getAnimationState() == AnimoState.PAUSED) {
                    animo.changeAnimoState(AnimoEvent.PLAY);
                }
            }
            return MethodResult.noReturn();
        })),

        Map.entry("RUN", MethodSpec.of((self, args, ctx) -> {
            if (args.size() < 2) {
                throw new IllegalArgumentException("RUN requires at least 2 arguments");
            }
            String varName = ArgumentHelper.getString(args.get(0));
            String methodName = ArgumentHelper.getString(args.get(1));
            List<Value> params = args.size() > 2 ? args.subList(2, args.size()) : List.of();
            Variable target = ctx.getVariable(varName);
            return target.callMethod(methodName, params, ctx);
        })),

        Map.entry("RUNCLONES", MethodSpec.of((self, args, ctx) -> {
            if (args.size() < 4) {
                throw new IllegalArgumentException("RUNCLONES requires 4 arguments: varName, firstId, lastId, behaviourName");
            }
            String varName = ArgumentHelper.getString(args.get(0));
            int firstId = ArgumentHelper.getInt(args.get(1));
            int lastId = ArgumentHelper.getInt(args.get(2));
            String behaviourName = ArgumentHelper.getString(args.get(3));

            List<String> cloneNames = ctx.clones().getCloneNames(varName);
            if (cloneNames.isEmpty()) {
                return MethodResult.noReturn();
            }

            Variable behaviourVar = ctx.getVariable(behaviourName);
            if (!(behaviourVar instanceof BehaviourVariable behaviour)) {
                return MethodResult.noReturn();
            }

            int from = Math.max(firstId, 1);
            int to = lastId < 0 ? cloneNames.size() : Math.min(lastId, cloneNames.size());

            for (int i = from; i <= to; i++) {
                String cloneName = varName + "_" + i;
                Variable clone = ctx.getVariable(cloneName);
                if (clone == null) {
                    continue;
                }
                ctx.runBehaviour("RUNCLONES:" + behaviourName + "@" + cloneName, clone, behaviour, List.of(new StringValue(cloneName)));
            }
            return MethodResult.noReturn();
        })),

        Map.entry("SETMINHSPRIORITY", MethodSpec.of((self, args, ctx) -> {
            SceneVariable thisVar = (SceneVariable) self;
            if (args.isEmpty()) {
                throw new IllegalArgumentException("SETMINHSPRIORITY requires 1 argument");
            }
            int newMin = ArgumentHelper.getInt(args.get(0));
            ctx.updateVariable(thisVar.name(), thisVar.withMinHotSpotZ(newMin));
            return MethodResult.noReturn();
        })),

        Map.entry("SETMAXHSPRIORITY", MethodSpec.of((self, args, ctx) -> {
            SceneVariable thisVar = (SceneVariable) self;
            if (args.isEmpty()) {
                throw new IllegalArgumentException("SETMAXHSPRIORITY requires 1 argument");
            }
            int newMax = ArgumentHelper.getInt(args.get(0));
            ctx.updateVariable(thisVar.name(), thisVar.withMaxHotSpotZ(newMax));
            return MethodResult.noReturn();
        })),

        Map.entry("SETMUSICVOLUME", MethodSpec.of((self, args, ctx) -> {
            SceneVariable thisVar = (SceneVariable) self;
            if (args.isEmpty()) {
                throw new IllegalArgumentException("SETMUSICVOLUME requires 1 argument");
            }
            int newVolume = ArgumentHelper.getInt(args.get(0));
            ctx.updateVariable(thisVar.name(), thisVar.withMusicVolume(newVolume));
            Music music = currentMusic(ctx);
            if (music != null) {
                music.setVolume(newVolume / 1000.0f);
            }
            return MethodResult.noReturn();
        })),

        Map.entry("STARTMUSIC", MethodSpec.of((self, args, ctx) -> {
            SceneVariable thisVar = (SceneVariable) self;
            if (args.isEmpty()) {
                throw new IllegalArgumentException("STARTMUSIC requires 1 argument");
            }
            String musicFile = ArgumentHelper.getString(args.get(0));
            ctx.updateVariable(thisVar.name(), thisVar.withMusic(musicFile));
            return MethodResult.noReturn();
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
