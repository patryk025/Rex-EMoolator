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

    @Override public boolean touchDown(int x, int y, int pointer, int button) {
        if (button == Input.Buttons.LEFT && activePointer == -1) {
            activePointer = pointer;
            events.add(new Event(Kind.DOWN, x, y));
        }
        return false;
    }
    @Override public boolean touchDragged(int x, int y, int pointer) {
        if (pointer == activePointer) events.add(new Event(Kind.MOVE, x, y));
        return false;
    }
    @Override public boolean touchUp(int x, int y, int pointer, int button) {
        if (pointer == activePointer && button == Input.Buttons.LEFT) {
            events.add(new Event(Kind.UP, x, y));
            activePointer = -1;
        }
        return false;
    }
    @Override public boolean touchCancelled(int x, int y, int pointer, int button) {
        if (pointer == activePointer) {
            events.add(new Event(Kind.CANCEL, x, y));
            activePointer = -1;
        }
        return false;
    }
    @Override public boolean mouseMoved(int x, int y) {
        if (activePointer == -1) events.add(new Event(Kind.MOVE, x, y));
        return false;
    }
    Event poll() { return events.poll(); }
    void clear() { events.clear(); activePointer = -1; }
}
