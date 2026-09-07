package pl.genschu.bloomooemulator.engine.input;

import com.badlogic.gdx.Input;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PointerInputTest {
    @Test void preservesFastTapAndCoordinatesBetweenPulses() {
        PointerInput input = new PointerInput();
        input.touchDown(10, 20, 0, Input.Buttons.LEFT);
        input.touchUp(11, 21, 0, Input.Buttons.LEFT);
        assertEquals(new PointerInput.Event(PointerInput.Kind.DOWN, 10, 20), input.poll());
        assertEquals(new PointerInput.Event(PointerInput.Kind.UP, 11, 21), input.poll());
        assertNull(input.poll());
    }

    @Test void secondFingerCannotMoveOrReleaseFirstContact() {
        PointerInput input = new PointerInput();
        input.touchDown(10, 20, 0, Input.Buttons.LEFT);
        input.touchDown(80, 90, 1, Input.Buttons.LEFT);
        input.touchDragged(81, 91, 1);
        input.touchUp(81, 91, 1, Input.Buttons.LEFT);
        input.touchDragged(12, 22, 0);
        input.touchCancelled(12, 22, 0, Input.Buttons.LEFT);
        assertEquals(PointerInput.Kind.DOWN, input.poll().kind());
        assertEquals(new PointerInput.Event(PointerInput.Kind.MOVE, 12, 22), input.poll());
        assertEquals(PointerInput.Kind.CANCEL, input.poll().kind());
        assertNull(input.poll());
    }
}
