package pl.genschu.bloomooemulator.engine.input;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import java.util.ArrayDeque;
import java.util.Queue;

/** Keeps complete pointer transitions between legacy pulses. One contact owns a gesture. */
final class PointerInput extends InputAdapter {
    enum Kind { DOWN, MOVE, UP, CANCEL }
    record Event(Kind kind, int x, int y) {}
    private final Queue<Event> events = new ArrayDeque<>();
    private int activePointer = -1;
    private int offsetX, offsetY;

    @Override public boolean touchDown(int x, int y, int pointer, int button) {
        if (button == Input.Buttons.LEFT && activePointer == -1) {
            activePointer = pointer;
            add(Kind.DOWN, x, y);
        }
        return false;
    }
    @Override public boolean touchDragged(int x, int y, int pointer) {
        if (pointer == activePointer) add(Kind.MOVE, x, y);
        return false;
    }
    @Override public boolean touchUp(int x, int y, int pointer, int button) {
        if (pointer == activePointer && button == Input.Buttons.LEFT) {
            add(Kind.UP, x, y);
            activePointer = -1;
        }
        return false;
    }
    @Override public boolean touchCancelled(int x, int y, int pointer, int button) {
        if (pointer == activePointer) {
            add(Kind.CANCEL, x, y);
            activePointer = -1;
        }
        return false;
    }
    @Override public boolean mouseMoved(int x, int y) {
        if (activePointer == -1) add(Kind.MOVE, x, y);
        return false;
    }
    private void add(Kind kind, int x, int y) {
        events.add(new Event(kind, x + offsetX, y + offsetY));
    }

    /** Preserve queued movement deltas when a script recentres the pointer. */
    void translate(int dx, int dy) {
        var translated = new ArrayDeque<Event>();
        for (Event event : events) {
            translated.add(new Event(event.kind(), event.x() + dx, event.y() + dy));
        }
        events.clear();
        events.addAll(translated);
        offsetX += dx;
        offsetY += dy;
    }

    // Native cursor warping makes subsequent callbacks use the new origin.
    void resetOffset() { offsetX = offsetY = 0; }

    Event poll() { return events.poll(); }
    void clear() { events.clear(); activePointer = -1; resetOffset(); }
}
