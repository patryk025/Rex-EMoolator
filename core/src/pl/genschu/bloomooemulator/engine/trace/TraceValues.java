package pl.genschu.bloomooemulator.engine.trace;

import java.util.*;
import java.util.stream.Collectors;
import pl.genschu.bloomooemulator.geometry.coordinates.CanvasRect;
import pl.genschu.bloomooemulator.interpreter.values.*;
import pl.genschu.bloomooemulator.interpreter.variable.*;

/** Explicit adapters: never evaluate expressions or serialize engine resources. */
public final class TraceValues {
    private TraceValues() {}

    public static Map<String, Object> snapshot(Variable variable) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("name", variable.name());
        result.put("type", variable.type().name());
        Object state;
        if (variable instanceof IntegerVariable || variable instanceof DoubleVariable
                || variable instanceof BoolVariable || variable instanceof StringVariable) {
            state = value(variable.value());
        } else if (variable instanceof ArrayVariable array) {
            state = array.elements().stream().map(TraceValues::value).collect(Collectors.toList());
        } else if (variable instanceof VectorVariable vector) {
            state = new ArrayList<>(vector.components());
        } else if (variable instanceof MultiArrayVariable array) {
            List<Object> elements = new ArrayList<>();
            for (Value element : array.getData()) elements.add(value(element == null ? NullValue.INSTANCE : element));
            state = Map.of("dimensions", integers(array.getDimensions()), "elements", elements);
        } else if (variable instanceof StructVariable struct) {
            state = Map.of("fields", new ArrayList<>(struct.fields()), "types", new ArrayList<>(struct.types()),
                    "values", struct.values().stream().map(v -> value(v == null ? NullValue.INSTANCE : v)).collect(Collectors.toList()));
        } else if (variable instanceof DatabaseCursorVariable cursor) {
            var db = cursor.db();
            state = Map.of("database", db.name(), "rowIndex", db.state().currentRowIndex(),
                    "columns", new ArrayList<>(db.state().columns()), "row", new ArrayList<>(db.state().currentRow()));
        } else if (variable instanceof MatrixVariable matrix) {
            var m = matrix.state();
            Map<String, Object> grid = new LinkedHashMap<>();
            grid.put("width", m.width);
            grid.put("height", m.height);
            grid.put("cellWidth", m.cellWidth);
            grid.put("cellHeight", m.cellHeight);
            grid.put("x", m.basePosX);
            grid.put("y", m.basePosY);
            grid.put("cells", integers(m.data));
            grid.put("startGate", List.of(m.startGateColumn, m.startGateRow));
            grid.put("endGate", List.of(m.endGateColumn, m.endGateRow));
            grid.put("currentMoveIndex", m.currentMoveIndex);
            grid.put("pendingMoves", m.pendingMoves.stream().map(TraceValues::integers).collect(Collectors.toList()));
            state = grid;
        } else if (variable instanceof ButtonVariable button) {
            var b = button.state();
            Map<String, Object> ui = new LinkedHashMap<>();
            ui.put("state", b.buttonState.name());
            ui.put("enabled", button.isEnabled());
            // Record the stored bounds and binding, without invoking an arbitrary bounds provider.
            ui.put("storedRect", rectangle(b.rect));
            ui.put("rectVariable", b.rectVarName);
            ui.put("hasBoundsProvider", b.rectProvider != null);
            ui.put("rectFollowsStandard", b.rectFollowsStandard);
            ui.put("hotspotPriority", b.hotspotPriority);
            ui.put("currentGfx", button.getCurrentGfxName());
            ui.put("standardGfx", b.gfxStandardName);
            ui.put("hoverGfx", b.gfxOnMoveName);
            ui.put("pressedGfx", b.gfxOnClickName);
            ui.put("draggable", b.draggable);
            ui.put("dragName", b.dragName);
            state = ui;
        } else if (variable instanceof ImageVariable image) {
            var i = image.state();
            Map<String, Object> graphics = new LinkedHashMap<>();
            graphics.put("filename", i.filename);
            graphics.put("x", i.posX);
            graphics.put("y", i.posY);
            graphics.put("anchorX", i.anchorX);
            graphics.put("anchorY", i.anchorY);
            graphics.put("visible", i.visible);
            graphics.put("opacity", (double)i.opacity);
            graphics.put("priority", i.priority);
            graphics.put("renderOrder", i.renderOrder);
            graphics.put("rect", rectangle(i.rect));
            graphics.put("clippingRect", rectangle(i.clippingRect));
            state = graphics;
        } else if (variable instanceof TextVariable text) {
            var t = text.state();
            Map<String, Object> graphics = new LinkedHashMap<>();
            graphics.put("text", t.text);
            graphics.put("visible", t.visible);
            graphics.put("toCanvas", t.toCanvas);
            graphics.put("priority", t.priority);
            graphics.put("renderOrder", t.renderOrder);
            graphics.put("storedRect", rectangle(t.rect));
            graphics.put("hasBoundsProvider", t.rectProvider != null);
            graphics.put("font", t.fontName);
            graphics.put("color", t.color);
            graphics.put("horizontalJustify", t.hJustify);
            graphics.put("verticalJustify", t.vJustify);
            state = graphics;
        } else if (variable instanceof DatabaseVariable database) {
            var db = database.state();
            state = Map.of("columns", new ArrayList<>(db.columns()),
                    "rows", db.data().stream().map(row -> new ArrayList<>(row)).collect(Collectors.toList()),
                    "currentRow", db.currentRowIndex());
        } else if (variable instanceof AnimoVariable animation) {
            state = Map.of("x", animation.getPosX(), "y", animation.getPosY(),
                    "frame", animation.getCurrentFrameNumber(), "image", animation.getCurrentImageNumber(),
                    "playback", animation.getAnimationState().name(),
                    "event", animation.getCurrentEvent() == null ? "" : animation.getCurrentEvent().getName(),
                    "visible", animation.isVisible());
        } else if (variable instanceof TimerVariable timer) {
            var t = timer.state();
            state = Map.of("enabled", t.enabled, "intervalMs", t.elapse,
                    "tickLimit", t.ticks, "tickCount", t.currentTickCount,
                    "lastTickTimeMs", t.lastTickTime);
        } else if (variable instanceof SoundVariable sound) {
            var s = sound.state();
            Map<String, Object> audio = new LinkedHashMap<>();
            audio.put("filename", s.filename);
            audio.put("loaded", s.sound != null);
            audio.put("playing", s.playing);
            audio.put("paused", s.paused);
            audio.put("durationSeconds", (double)s.duration);
            audio.put("playStartMonotonicSeconds", s.playStartTime);
            audio.put("pausedAtMonotonicSeconds", s.pausedAt);
            audio.put("sampleRate", s.sampleRate);
            audio.put("currentSampleRate", s.currentSampleRate);
            audio.put("channels", s.channels);
            audio.put("bitsPerSample", s.bitsPerSample);
            audio.put("timingSource", "engine-bookkeeping-not-audio-position");
            state = audio;
        } else if (variable instanceof SequenceVariable sequence) {
            var s = sequence.state();
            Map<String, Object> playback = new LinkedHashMap<>();
            playback.put("filename", s.filename);
            playback.put("playing", s.isPlaying);
            playback.put("paused", s.isPaused);
            playback.put("currentEvent", s.currentEvent == null ? null : s.currentEvent.getName());
            playback.put("pendingFinishedEvent", s.pendingFinishedEvent == null ? null : s.pendingFinishedEvent.getName());
            playback.put("pendingFinishedEvents", s.pendingFinishedEvents.stream()
                    .map(SequenceVariable.SequenceEvent::getName).collect(Collectors.toList()));
            playback.put("animosInSequence", new TreeSet<>(s.animosInSequence));
            playback.put("parametersMapping", new TreeMap<>(s.parametersMapping));
            List<Object> events = new ArrayList<>();
            Set<SequenceVariable.SequenceEvent> seen = Collections.newSetFromMap(new IdentityHashMap<>());
            for (var e : sequence.events()) sequenceEvent(e, events, seen);
            sequenceEvent(s.currentEvent, events, seen);
            playback.put("events", events);
            state = playback;
        } else {
            result.put("supported", false);
            return result;
        }
        result.put("supported", true);
        result.put("state", state);
        return result;
    }

    private static List<Integer> integers(int[] values) {
        List<Integer> copy = new ArrayList<>();
        if (values != null) for (int value : values) copy.add(value);
        return copy;
    }

    private static Object rectangle(CanvasRect rect) {
        return rect == null ? null : Map.of("left", rect.left(), "top", rect.top(),
                "right", rect.right(), "bottom", rect.bottom());
    }

    private static void sequenceEvent(SequenceVariable.SequenceEvent event, List<Object> output,
                                      Set<SequenceVariable.SequenceEvent> seen) {
        if (event == null || !seen.add(event)) return;
        var p = event.getPlayback();
        Map<String, Object> snapshot = new LinkedHashMap<>();
        snapshot.put("name", event.getName());
        snapshot.put("parent", event.getParent() == null ? null : event.getParent().getName());
        snapshot.put("type", event.getType().name());
        snapshot.put("mode", event.getMode().name());
        snapshot.put("waitMode", event.isWaitMode());
        snapshot.put("animation", event.getAnimationName());
        snapshot.put("sound", event.getSoundName());
        snapshot.put("playing", p.isPlaying);
        snapshot.put("paused", p.isPaused);
        snapshot.put("phase", p.currentPhase.name());
        snapshot.put("animationNumber", p.currentAnimationNumber);
        snapshot.put("activeAnimation", p.activeAnimationName);
        output.add(snapshot);
        for (var child : event.getSubEvents()) sequenceEvent(child, output, seen);
    }

    public static Map<String, Object> value(Value value) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("type", value.getType().name());
        result.put("value", switch (value) {
            case IntValue v -> v.value();
            case DoubleValue v -> v.value();
            case BoolValue v -> v.value();
            case StringValue v -> v.value();
            case NullValue v -> null;
            case VariableRef v -> v.name();
            case VariableValue v -> v.variable().name();
            default -> "<unsupported>";
        });
        return result;
    }
}
