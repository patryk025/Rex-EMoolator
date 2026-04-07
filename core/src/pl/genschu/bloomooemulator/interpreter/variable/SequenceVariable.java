package pl.genschu.bloomooemulator.interpreter.variable;

import com.badlogic.gdx.Gdx;
import pl.genschu.bloomooemulator.annotations.InternalMutable;
import pl.genschu.bloomooemulator.interpreter.context.Context;
import pl.genschu.bloomooemulator.interpreter.helpers.ArgumentHelper;
import pl.genschu.bloomooemulator.interpreter.values.*;
import pl.genschu.bloomooemulator.interpreter.variable.capabilities.Initializable;
import pl.genschu.bloomooemulator.loader.SequenceLoader;
import pl.genschu.bloomooemulator.objects.Event;

import java.util.*;

/**
 * SequenceVariable represents a sequence of animations with nested events.
 * Used for coordinating multiple animations together (e.g., lip-sync, conversations).
 */
public record SequenceVariable(
    String name,
    List<SequenceEvent> events,
    Map<String, SequenceEvent> eventsByName,
    @InternalMutable SequenceState state,
    Map<String, SignalHandler> signals
) implements Variable, Initializable, PlaybackObserver {

    // Character set for parameter mapping (from original implementation)
    public static final String PARAMS_CHARACTER_SET = "123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz{|}~";

    /**
     * Types of sequence events.
     */
    public enum EventType {
        SEQUENCE,   // Contains sub-events
        SIMPLE,     // Single animation play
        SPEAKING    // Animation with START/MAIN/END phases (lip-sync)
    }

    /**
     * Mode for selecting sub-events.
     */
    public enum SequenceMode {
        PARAMETER,  // Use parameter mapping to select
        SEQUENCE,   // Play sub-events in order
        RANDOM      // Select random sub-event
    }

    /**
     * Phase of speaking animation.
     */
    public enum PlaybackPhase {
        START,
        MAIN,
        END
    }

    /**
     * Mutable state for sequence event playback.
     */
    public static final class SequenceEventState {
        public boolean isPlaying = false;
        public boolean isPaused = false;
        public PlaybackPhase currentPhase = PlaybackPhase.START;
        public int currentAnimationNumber = 1;
        public boolean isOnFinishedWrapped = false;

        public SequenceEventState() {}

        public SequenceEventState copy() {
            SequenceEventState copy = new SequenceEventState();
            copy.isPlaying = this.isPlaying;
            copy.isPaused = this.isPaused;
            copy.currentPhase = this.currentPhase;
            copy.currentAnimationNumber = this.currentAnimationNumber;
            copy.isOnFinishedWrapped = this.isOnFinishedWrapped;
            return copy;
        }
    }

    /**
     * Represents a single event in the sequence.
     */
    public static final class SequenceEvent {
        private final String name;
        private final EventType type;
        private final SequenceMode mode;
        private final String prefix;
        private final boolean hasStartAnimation;
        private final boolean hasEndAnimation;
        private boolean waitMode;
        private final List<SequenceEvent> subEvents;
        private String animationName;  // Name of AnimoVariable (resolved at runtime)
        private String soundName;      // Name of SoundVariable (resolved at runtime)
        private SequenceEvent parent;
        @InternalMutable
        private final SequenceEventState playback;

        public SequenceEvent(String name, EventType type, SequenceMode mode, String prefix,
                            boolean hasStartAnimation, boolean hasEndAnimation) {
            this.name = name;
            this.type = type;
            this.mode = mode;
            this.prefix = prefix;
            this.hasStartAnimation = hasStartAnimation;
            this.hasEndAnimation = hasEndAnimation;
            this.subEvents = new ArrayList<>();
            this.playback = new SequenceEventState();
        }

        public SequenceEvent(String name, EventType type) {
            this(name, type, SequenceMode.SEQUENCE, null, false, false);
        }

        // Getters
        public String getName() { return name; }
        public EventType getType() { return type; }
        public SequenceMode getMode() { return mode; }
        public String getPrefix() { return prefix; }
        public boolean hasStartAnimation() { return hasStartAnimation; }
        public boolean hasEndAnimation() { return hasEndAnimation; }
        public List<SequenceEvent> getSubEvents() { return subEvents; }
        public String getAnimationName() { return animationName; }
        public String getSoundName() { return soundName; }
        public SequenceEvent getParent() { return parent; }
        public SequenceEventState getPlayback() { return playback; }
        public boolean isWaitMode() { return waitMode; }

        // Setters
        public void setAnimationName(String animationName) { this.animationName = animationName; }
        public void setSoundName(String soundName) { this.soundName = soundName; }
        public void setParent(SequenceEvent parent) { this.parent = parent; }
        public void setWaitMode(boolean waitMode) { this.waitMode = waitMode; }

        public void addSubEvent(SequenceEvent subEvent) {
            subEvent.setParent(this);
            subEvents.add(subEvent);
        }
    }

    /**
     * Mutable state for sequence playback.
     */
    public static final class SequenceState {
        public SequenceEvent currentEvent;
        public boolean isPlaying = false;
        public boolean isPaused = false;
        public SequenceEvent pendingFinishedEvent = null;
        public Set<String> animosInSequence = new HashSet<>();
        public Map<String, Integer> parametersMapping = new HashMap<>();
        public Random random = new Random();
        public String filename = "";
        public Context playbackContext;  // set during playback for observer callbacks

        public SequenceState() {}

        public SequenceState copy() {
            SequenceState copy = new SequenceState();
            copy.currentEvent = this.currentEvent;
            copy.isPlaying = this.isPlaying;
            copy.isPaused = this.isPaused;
            copy.pendingFinishedEvent = this.pendingFinishedEvent;
            copy.animosInSequence = new HashSet<>(this.animosInSequence);
            copy.parametersMapping = new HashMap<>(this.parametersMapping);
            copy.random = new Random();
            copy.filename = this.filename;
            copy.playbackContext = this.playbackContext;
            return copy;
        }
    }

    // Compact constructor
    public SequenceVariable {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Variable name cannot be null or empty");
        }
        if (events == null) {
            events = new ArrayList<>();
        }
        if (eventsByName == null) {
            eventsByName = new HashMap<>();
        }
        if (state == null) {
            state = new SequenceState();
        }
        if (signals == null) {
            signals = Map.of();
        } else {
            signals = Map.copyOf(signals);
        }
    }

    public SequenceVariable(String name) {
        this(name, new ArrayList<>(), new HashMap<>(), new SequenceState(), Map.of());
    }

    public SequenceVariable(String name, String filename) {
        this(name);
        this.state.filename = filename;
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
        return VariableType.SEQUENCE;
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
        return new SequenceVariable(name, events, eventsByName, state, newSignals);
    }

    @Override
    public void init(Context context) {
        String filename = state.filename;
        if ((filename == null || filename.isEmpty())) {
            String attr = context.getAttribute(name, "FILENAME");
            if (attr != null && !attr.isEmpty()) {
                filename = attr;
                state.filename = attr;
            }
        }

        if (filename == null || filename.isEmpty()) {
            return;
        }

        try {
            SequenceLoader.load(this, context);
        } catch (Exception e) {
            Gdx.app.error("SequenceVariable", "Failed to load SEQUENCE " + name + ": " + e.getMessage(), e);
        }
    }

    // ========================================
    // ACCESSORS
    // ========================================

    public String getFilename() { return state.filename; }
    public boolean isPlaying() { return state.isPlaying && !state.isPaused; }
    public boolean isPaused() { return state.isPaused; }
    public SequenceEvent getCurrentEvent() { return state.currentEvent; }
    public String getCurrentEventName() { return state.currentEvent != null ? state.currentEvent.getName() : ""; }
    public Set<String> getAnimosInSequence() { return state.animosInSequence; }
    public Map<String, Integer> getParametersMapping() { return state.parametersMapping; }

    public void setFilename(String filename) {
        state.filename = filename;
    }

    // ========================================
    // EVENT MANAGEMENT
    // ========================================

    public void addEvent(SequenceEvent event) {
        events.add(event);
        eventsByName.put(event.getName(), event);
    }

    public SequenceEvent getEventByName(String eventName) {
        return eventsByName.get(eventName);
    }

    // ========================================
    // PLAYBACK CONTROL
    // ========================================

    /**
     * Play a specific event by name.
     */
    public void playEvent(String eventName, Context context) {
        Gdx.app.log("SequenceVariable", "playEvent: " + eventName);
        SequenceEvent event = eventsByName.get(eventName);
        if (event != null) {
            stopSequence(false, context);
            state.playbackContext = context;
            state.currentEvent = event;
            state.isPlaying = true;
            startEvent(event, context);
        }
    }

    /**
     * Pause the current sequence.
     */
    public void pauseSequence(Context context) {
        state.isPaused = true;
        if (state.currentEvent != null) {
            pauseEvent(state.currentEvent, context);
        }
    }

    /**
     * Resume the paused sequence.
     */
    public void resumeSequence(Context context) {
        state.isPaused = false;
        if (state.currentEvent != null) {
            resumeEvent(state.currentEvent, context);
        }
    }

    /**
     * Stop the current sequence.
     */
    public void stopSequence(boolean emitSignal, Context context) {
        Gdx.app.log("SequenceVariable", "stopSequence: " + state.currentEvent);
        if (state.currentEvent != null && state.isPlaying) {
            stopEvent(state.currentEvent, context);
            if (emitSignal) {
                emitSignal("ONFINISHED", new StringValue(state.currentEvent.getName()));
            }
            state.currentEvent = null;
            state.isPlaying = false;
            state.isPaused = false;
            state.playbackContext = null;
        }
    }

    /**
     * Hide all animations in the sequence.
     */
    public void hideSequence(Context context) {
        for (String animoName : state.animosInSequence) {
            Variable animo = context.getVariable(animoName);
            if (animo != null) {
                animo.callMethod("HIDE");
            }
        }
    }

    /**
     * Show all animations in the sequence.
     */
    public void showSequence(Context context) {
        for (String animoName : state.animosInSequence) {
            Variable animo = context.getVariable(animoName);
            if (animo != null) {
                animo.callMethod("SHOW");
            }
        }
    }

    // ========================================
    // INTERNAL PLAYBACK METHODS
    // ========================================

    private void startEvent(SequenceEvent event, Context context) {
        Gdx.app.log("SequenceVariable", "startEvent: " + event.getName());
        event.getPlayback().isPlaying = true;
        emitSignal("ONSTARTED", new StringValue(event.getName()));

        switch (event.getType()) {
            case SEQUENCE -> {
                switch (event.getMode()) {
                    case SEQUENCE -> startSequentialEvent(event, context);
                    case RANDOM -> startRandomEvent(event, context);
                    case PARAMETER -> startParameterEvent(event, context);
                }
            }
            case SIMPLE, SPEAKING -> startBasicEvent(event, context);
        }
    }

    private void startBasicEvent(SequenceEvent event, Context context) {
        Gdx.app.log("SequenceVariable", "startBasicEvent: " + event.getName());
        if (event.getType() == EventType.SPEAKING) {
            startSpeakingEvent(event, context);
        } else {
            // SIMPLE event
            String animName = event.getAnimationName();
            if (animName != null) {
                Variable animo = context.getVariable(animName);
                if (animo instanceof AnimoVariable animoVar) {
                    animoVar.addObserver(this);

                    String prefix = event.getPrefix();
                    if (animoVar.hasEvent(prefix)) {
                        animo.callMethod("PLAY", new StringValue(prefix));
                    } else if (!animoVar.getEvents().isEmpty()) {
                        Event firstEvent = animoVar.getEvents().get(0);
                        animo.callMethod("PLAY", new StringValue(firstEvent.getName()));
                    }

                    if (state.pendingFinishedEvent != null) {
                        emitSignal("ONFINISHED", new StringValue(state.pendingFinishedEvent.getName()));
                        state.pendingFinishedEvent = null;
                    }
                }
            }

            String soundName = event.getSoundName();
            if (soundName != null) {
                Variable sound = context.getVariable(soundName);
                if (sound instanceof SoundVariable soundVar) {
                    soundVar.addObserver(this);
                    soundVar.play();
                    context.getGame().getPlayingAudios().add(soundVar);
                }
            }
        }
    }

    private void startSpeakingEvent(SequenceEvent event, Context context) {
        Gdx.app.log("SequenceVariable", "startSpeakingEvent: " + event.getName());
        SequenceEventState playback = event.getPlayback();
        playback.currentPhase = event.hasStartAnimation() ? PlaybackPhase.START : PlaybackPhase.MAIN;
        playback.currentAnimationNumber = 1;

        if (event.hasStartAnimation()) {
            playSpeakingStartAnimation(event, context);
        } else {
            startSpeakingMainPhase(event, context);
        }
    }

    private void playSpeakingStartAnimation(SequenceEvent event, Context context) {
        Gdx.app.log("SequenceVariable", "playSpeakingStartAnimation: " + event.getName());
        String startAnimName = event.getPrefix() + "_START";
        String animName = event.getAnimationName();

        if (animName != null) {
            Variable animo = context.getVariable(animName);
            if (animo instanceof AnimoVariable animoVar && animoVar.hasEvent(startAnimName)) {
                animoVar.addObserver(this);
                animo.callMethod("PLAY", new StringValue(startAnimName));

                if (state.pendingFinishedEvent != null) {
                    emitSignal("ONFINISHED", new StringValue(state.pendingFinishedEvent.getName()));
                    state.pendingFinishedEvent = null;
                }
            } else {
                startSpeakingMainPhase(event, context);
            }
        } else {
            startSpeakingMainPhase(event, context);
        }
    }

    private void startSpeakingMainPhase(SequenceEvent event, Context context) {
        Gdx.app.log("SequenceVariable", "startSpeakingMainPhase: " + event.getName());
        SequenceEventState playback = event.getPlayback();
        playback.currentPhase = PlaybackPhase.MAIN;

        String soundName = event.getSoundName();
        if (soundName != null) {
            Variable sound = context.getVariable(soundName);
            if (sound instanceof SoundVariable soundVar) {
                try {
                    soundVar.addObserver(this);
                    soundVar.play();
                    context.getGame().getPlayingAudios().add(soundVar);
                    playSpeakingMainAnimation(event, context);
                } catch (Exception e) {
                    Gdx.app.error("SequenceVariable", "Error playing audio for " + event.getName());
                    playback.currentPhase = PlaybackPhase.END;
                    if (event.hasEndAnimation()) {
                        playSpeakingEndAnimation(event, context);
                    } else {
                        handleEventFinished(event, context);
                    }
                }
            }
        } else {
            // No sound - skip to end
            playback.currentPhase = PlaybackPhase.END;
            if (event.hasEndAnimation()) {
                playSpeakingEndAnimation(event, context);
            } else {
                handleEventFinished(event, context);
            }
        }
    }

    private void playSpeakingMainAnimation(SequenceEvent event, Context context) {
        Gdx.app.log("SequenceVariable", "playSpeakingMainAnimation: " + event.getName());
        String animName = event.getAnimationName();
        if (animName == null) return;

        Variable animo = context.getVariable(animName);
        if (!(animo instanceof AnimoVariable animoVar)) return;

        List<Event> eventsWithPrefix = animoVar.getEventsWithPrefix(event.getPrefix());
        List<Integer> validNumbers = new ArrayList<>();
        for (Event e : eventsWithPrefix) {
            String name = e.getName();
            if (!name.endsWith("_START") && !name.endsWith("_STOP")) {
                try {
                    validNumbers.add(Integer.parseInt(name.substring(name.lastIndexOf("_") + 1)));
                } catch (NumberFormatException ignored) {}
            }
        }

        String mainAnimName;
        if (validNumbers.isEmpty()) {
            if (animoVar.hasEvent(event.getPrefix())) {
                mainAnimName = event.getPrefix();
            } else {
                Gdx.app.error("SequenceVariable", "No valid animations for: " + event.getPrefix());
                SequenceEventState playback = event.getPlayback();
                playback.currentPhase = PlaybackPhase.END;
                if (event.hasEndAnimation()) {
                    playSpeakingEndAnimation(event, context);
                } else {
                    handleEventFinished(event, context);
                }
                return;
            }
        } else {
            int nextAnimNumber = validNumbers.get(state.random.nextInt(validNumbers.size()));
            mainAnimName = event.getPrefix() + "_" + nextAnimNumber;
        }

        animoVar.addObserver(this);
        animo.callMethod("PLAY", new StringValue(mainAnimName));
    }

    private void playSpeakingEndAnimation(SequenceEvent event, Context context) {
        Gdx.app.log("SequenceVariable", "playSpeakingEndAnimation: " + event.getName());
        SequenceEventState playback = event.getPlayback();
        playback.currentPhase = PlaybackPhase.END;

        String endAnimName = event.getPrefix() + "_STOP";
        String animName = event.getAnimationName();

        if (animName != null) {
            Variable animo = context.getVariable(animName);
            if (animo instanceof AnimoVariable animoVar && animoVar.hasEvent(endAnimName)) {
                animoVar.addObserver(this);
                animo.callMethod("PLAY", new StringValue(endAnimName));
            } else {
                handleEventFinished(event, context);
            }
        } else {
            handleEventFinished(event, context);
        }
    }

    private void startSequentialEvent(SequenceEvent event, Context context) {
        Gdx.app.log("SequenceVariable", "startSequentialEvent: " + event.getName());
        if (!event.getSubEvents().isEmpty()) {
            startEvent(event.getSubEvents().get(0), context);
        }
    }

    private void startRandomEvent(SequenceEvent event, Context context) {
        Gdx.app.log("SequenceVariable", "startRandomEvent: " + event.getName());
        if (!event.getSubEvents().isEmpty()) {
            int index = state.random.nextInt(event.getSubEvents().size());
            startEvent(event.getSubEvents().get(index), context);
        }
    }

    private void startParameterEvent(SequenceEvent event, Context context) {
        if (!event.getSubEvents().isEmpty()) {
            Integer index = state.parametersMapping.get(event.getName());
            if (index != null && index >= 0 && index < event.getSubEvents().size()) {
                startEvent(event.getSubEvents().get(index), context);
            } else {
                Gdx.app.error("SequenceVariable", "Invalid PARAMETER index for event: " + event.getName());
            }
        }
    }

    private void handleEventFinished(SequenceEvent event, Context context) {
        Gdx.app.log("SequenceVariable", "handleEventFinished: " + event.getName());
        event.getPlayback().isPlaying = false;

        String animName = event.getAnimationName();
        if (animName != null) {
            Variable animo = context.getVariable(animName);
            if (animo != null) {
                animo.callMethod("PAUSE");
            }
        }

        SequenceEvent parentEvent = event.getParent();
        if (parentEvent != null) {
            int currentIndex = parentEvent.getSubEvents().indexOf(event);
            if (parentEvent.getMode() == SequenceMode.SEQUENCE &&
                currentIndex < parentEvent.getSubEvents().size() - 1) {
                state.pendingFinishedEvent = event;
                startEvent(parentEvent.getSubEvents().get(currentIndex + 1), context);
            } else {
                stopEvent(parentEvent, context);
                if (parentEvent.getMode() == SequenceMode.SEQUENCE &&
                    currentIndex == parentEvent.getSubEvents().size() - 1) {
                    emitSignal("ONFINISHED", new StringValue(parentEvent.getName()));
                    emitSignal("ONFINISHED", new StringValue(event.getName()));
                } else {
                    emitSignal("ONFINISHED", new StringValue(event.getName()));
                }
                if (parentEvent.getParent() == null) {
                    state.isPlaying = false;
                }
            }
        } else {
            stopEvent(event, context);
            emitSignal("ONFINISHED", new StringValue(state.currentEvent.getName()));
            state.isPlaying = false;
        }
    }

    private void pauseEvent(SequenceEvent event, Context context) {
        if (event.getType() == EventType.SEQUENCE) {
            for (SequenceEvent subEvent : event.getSubEvents()) {
                if (subEvent.getPlayback().isPlaying) {
                    pauseEventContent(subEvent, context);
                    subEvent.getPlayback().isPaused = true;
                    return;
                }
            }
        } else {
            pauseEventContent(event, context);
            event.getPlayback().isPaused = true;
        }
    }

    private void pauseEventContent(SequenceEvent event, Context context) {
        String animName = event.getAnimationName();
        if (animName != null) {
            Variable animo = context.getVariable(animName);
            if (animo != null) {
                animo.callMethod("PAUSE");
            }
        }
        String soundName = event.getSoundName();
        if (soundName != null) {
            Variable sound = context.getVariable(soundName);
            if (sound != null) {
                sound.callMethod("PAUSE");
            }
        }
    }

    private void resumeEvent(SequenceEvent event, Context context) {
        if (event.getType() == EventType.SEQUENCE) {
            for (SequenceEvent subEvent : event.getSubEvents()) {
                if (subEvent.getPlayback().isPlaying && subEvent.getPlayback().isPaused) {
                    resumeEventContent(subEvent, context);
                    subEvent.getPlayback().isPaused = false;
                    return;
                }
            }
        } else {
            if (event.getPlayback().isPlaying && event.getPlayback().isPaused) {
                resumeEventContent(event, context);
                event.getPlayback().isPaused = false;
            }
        }
    }

    private void resumeEventContent(SequenceEvent event, Context context) {
        String animName = event.getAnimationName();
        if (animName != null) {
            Variable animo = context.getVariable(animName);
            if (animo != null) {
                animo.callMethod("RESUME");
            }
        }
        String soundName = event.getSoundName();
        if (soundName != null) {
            Variable sound = context.getVariable(soundName);
            if (sound != null) {
                sound.callMethod("RESUME");
            }
        }
    }

    private void stopEvent(SequenceEvent event, Context context) {
        Gdx.app.log("SequenceVariable", "stopEvent: " + event.getName());
        SequenceEventState playback = event.getPlayback();
        playback.isPlaying = false;
        playback.currentPhase = PlaybackPhase.START;

        if (event.getType() == EventType.SEQUENCE) {
            for (SequenceEvent subEvent : event.getSubEvents()) {
                if (subEvent.getPlayback().isPlaying) {
                    stopEvent(subEvent, context);
                }
            }
        } else {
            String animName = event.getAnimationName();
            if (animName != null) {
                Variable animo = context.getVariable(animName);
                if (animo instanceof AnimoVariable animoVar) {
                    animoVar.removeObserver(this);
                    if (animoVar.isPlaying()) {
                        animo.callMethod("STOP");
                    }
                }
            }
            String soundName = event.getSoundName();
            if (soundName != null) {
                Variable sound = context.getVariable(soundName);
                if (sound instanceof SoundVariable soundVar) {
                    soundVar.removeObserver(this);
                    if (soundVar.isPlaying()) {
                        soundVar.stop(false);
                        context.getGame().getPlayingAudios().remove(soundVar);
                    }
                }
            }
        }
    }

    // ========================================
    // PLAYBACK OBSERVER IMPLEMENTATION
    // ========================================

    @Override
    public void onPlaybackStarted(Variable source, String eventName) {
        // Not used — sequence tracks starts internally via startEvent()
    }

    @Override
    public void onPlaybackFinished(Variable source, String eventName) {
        if (state.currentEvent == null || !state.isPlaying) return;
        Context context = state.playbackContext;
        if (context == null) return;

        SequenceEvent activeEvent = findActiveEventForSource(state.currentEvent, source.name());
        if (activeEvent == null) return;

        if (source instanceof SoundVariable) {
            // Sound finished — transition speaking event to END phase
            SequenceEventState playback = activeEvent.getPlayback();
            playback.currentPhase = PlaybackPhase.END;
            if (activeEvent.hasEndAnimation()) {
                playSpeakingEndAnimation(activeEvent, context);
            } else {
                handleEventFinished(activeEvent, context);
            }
        } else if (source instanceof AnimoVariable) {
            switch (activeEvent.getType()) {
                case SIMPLE -> handleEventFinished(activeEvent, context);
                case SPEAKING -> {
                    SequenceEventState playback = activeEvent.getPlayback();
                    switch (playback.currentPhase) {
                        case START -> startSpeakingMainPhase(activeEvent, context);
                        case MAIN -> {
                            // Check if sound is still playing — if so, loop main animation
                            String soundName = activeEvent.getSoundName();
                            Variable sound = soundName != null ? context.getVariable(soundName) : null;
                            boolean soundPlaying = false;
                            if (sound != null) {
                                MethodResult result = sound.callMethod("ISPLAYING");
                                soundPlaying = result.returnValue() instanceof BoolValue(boolean value) && value;
                            }
                            if (playback.isPlaying && soundPlaying) {
                                playSpeakingMainAnimation(activeEvent, context);
                            }
                        }
                        case END -> handleEventFinished(activeEvent, context);
                    }
                }
                default -> {} // SEQUENCE type events don't have direct animations
            }
        }
    }

    /**
     * Find the active (playing) leaf SequenceEvent that uses the given source variable name.
     */
    private SequenceEvent findActiveEventForSource(SequenceEvent event, String sourceName) {
        if (event.getType() == EventType.SEQUENCE) {
            for (SequenceEvent sub : event.getSubEvents()) {
                if (sub.getPlayback().isPlaying) {
                    SequenceEvent found = findActiveEventForSource(sub, sourceName);
                    if (found != null) return found;
                }
            }
            return null;
        }
        // Leaf event — check if it uses this source
        if (sourceName.equals(event.getAnimationName()) || sourceName.equals(event.getSoundName())) {
            return event;
        }
        return null;
    }

    // ========================================
    // METHODS DEFINITION
    // ========================================

    private static final Map<String, MethodSpec> METHODS = Map.ofEntries(
        Map.entry("PLAY", MethodSpec.of((self, args, ctx) -> {
            SequenceVariable thisVar = (SequenceVariable) self;
            if (args.isEmpty()) {
                throw new IllegalArgumentException("PLAY requires 1 argument");
            }
            String eventName = ArgumentHelper.getString(args.get(0));
            if (ctx != null) {
                thisVar.playEvent(eventName, ctx.context());
            } else {
                SequenceEvent event = thisVar.eventsByName.get(eventName);
                if (event != null) {
                    thisVar.state.currentEvent = event;
                    thisVar.state.isPlaying = true;
                    thisVar.emitSignal("ONSTARTED", new StringValue(eventName));
                }
            }
            return MethodResult.noReturn();
        })),

        Map.entry("PAUSE", MethodSpec.of((self, args, ctx) -> {
            SequenceVariable thisVar = (SequenceVariable) self;
            if (ctx != null) {
                thisVar.pauseSequence(ctx.context());
            } else {
                thisVar.state.isPaused = true;
            }
            return MethodResult.noReturn();
        })),

        Map.entry("RESUME", MethodSpec.of((self, args, ctx) -> {
            SequenceVariable thisVar = (SequenceVariable) self;
            if (ctx != null) {
                thisVar.resumeSequence(ctx.context());
            } else {
                thisVar.state.isPaused = false;
            }
            return MethodResult.noReturn();
        })),

        Map.entry("STOP", MethodSpec.of((self, args, ctx) -> {
            SequenceVariable thisVar = (SequenceVariable) self;
            boolean emitSignal = args.isEmpty() || ArgumentHelper.getBoolean(args.get(0));
            if (ctx != null) {
                thisVar.stopSequence(emitSignal, ctx.context());
            } else {
                if (thisVar.state.currentEvent != null) {
                    if (emitSignal) {
                        thisVar.emitSignal("ONFINISHED", new StringValue(thisVar.state.currentEvent.getName()));
                    }
                    thisVar.state.currentEvent = null;
                    thisVar.state.isPlaying = false;
                    thisVar.state.isPaused = false;
                }
            }
            return MethodResult.noReturn();
        })),

        Map.entry("SHOW", MethodSpec.of((self, args, ctx) -> {
            if (ctx != null) {
                ((SequenceVariable) self).showSequence(ctx.context());
            }
            return MethodResult.noReturn();
        })),

        Map.entry("HIDE", MethodSpec.of((self, args, ctx) -> {
            if (ctx != null) {
                ((SequenceVariable) self).hideSequence(ctx.context());
            }
            return MethodResult.noReturn();
        })),

        Map.entry("ISPLAYING", MethodSpec.of((self, args, ctx) -> {
            SequenceVariable thisVar = (SequenceVariable) self;
            return MethodResult.returns(new BoolValue(thisVar.isPlaying()));
        })),

        Map.entry("GETEVENTNAME", MethodSpec.of((self, args, ctx) -> {
            SequenceVariable thisVar = (SequenceVariable) self;
            return MethodResult.returns(new StringValue(thisVar.getCurrentEventName()));
        })),

        Map.entry("SETVOLUME", MethodSpec.of((self, args, ctx) -> {
            SequenceVariable seq = (SequenceVariable) self;
            int volume = ArgumentHelper.getInt(args.get(0));
            if (seq.state.currentEvent != null && ctx != null) {
                String soundName = seq.state.currentEvent.getSoundName();
                if (soundName != null) {
                    Variable sound = ctx.context().getVariable(soundName);
                    if (sound instanceof SoundVariable snd && snd.getSound() != null && snd.state().soundId != -1) {
                        // DLL formula: ((volume - 800) * 3200) / 800, clamped to -10000..0
                        // Normalize to libGDX 0.0-1.0 range
                        float normalized = Math.clamp(volume / 1600f, 0f, 1f);
                        snd.getSound().setVolume(snd.state().soundId, normalized);
                    }
                }
            }
            return MethodResult.noReturn();
        })),

        Map.entry("SETPAN", MethodSpec.of((self, args, ctx) -> {
            SequenceVariable seq = (SequenceVariable) self;
            int pan = ArgumentHelper.getInt(args.get(0));
            if (seq.state.currentEvent != null && ctx != null) {
                String soundName = seq.state.currentEvent.getSoundName();
                if (soundName != null) {
                    Variable sound = ctx.context().getVariable(soundName);
                    if (sound instanceof SoundVariable snd && snd.getSound() != null && snd.state().soundId != -1) {
                        // DLL formula: ((pan - 400) * 3200) / 1600
                        // Normalize to libGDX -1.0..1.0 range
                        float normalized = Math.clamp((pan - 400f) / 400f, -1f, 1f);
                        snd.getSound().setPan(snd.state().soundId, normalized, 1.0f);
                    }
                }
            }
            return MethodResult.noReturn();
        })),

        Map.entry("SETFREQ", MethodSpec.of((self, args, ctx) -> {
            SequenceVariable seq = (SequenceVariable) self;
            int freq = ArgumentHelper.getInt(args.get(0));
            if (seq.state.currentEvent != null && ctx != null) {
                String soundName = seq.state.currentEvent.getSoundName();
                if (soundName != null) {
                    Variable sound = ctx.context().getVariable(soundName);
                    if (sound != null) {
                        sound.callMethod("SETFREQ", new IntValue(freq));
                    }
                }
            }
            return MethodResult.noReturn();
        })),

        Map.entry("GETPLAYING", MethodSpec.of((self, args, ctx) -> {
            SequenceVariable seq = (SequenceVariable) self;
            if (seq.state.currentEvent != null) {
                String animName = seq.state.currentEvent.getAnimationName();
                if (animName != null) return MethodResult.returns(new StringValue(animName));
            }
            return MethodResult.returns(new StringValue(""));
        }))
    );

    @Override
    public String toString() {
        return "SequenceVariable[" + name + ", playing=" + state.isPlaying +
               ", event=" + (state.currentEvent != null ? state.currentEvent.getName() : "none") + "]";
    }
}
