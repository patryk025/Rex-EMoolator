package pl.genschu.bloomooemulator.utils;

import pl.genschu.bloomooemulator.interpreter.variable.Variable;
import pl.genschu.bloomooemulator.interpreter.variable.types.AnimoVariable;
import pl.genschu.bloomooemulator.interpreter.variable.types.ImageVariable;
import pl.genschu.bloomooemulator.objects.Rectangle;

public class CollisionChecker {
    public static boolean checkCollision(Variable obj1, Variable obj2) {
        Rectangle rect1 = getRect(obj1);
        Rectangle rect2 = getRect(obj2);
        return rect1.intersects(rect2);
    }

    // Alpha collision check
    public static boolean checkAlphaCollision(Variable obj1, Variable obj2) {
        if (obj1.getAttribute("MONITORCOLLISIONALPHA") != null && obj1.getAttribute("MONITORCOLLISIONALPHA").getValue().equals("TRUE")) {
            // Perform pixel-perfect alpha comparison
            // Get Pixmap from obj1 and obj2
            // Check pixel data in the overlapping area for alpha transparency
        }
        return true; // Simplified for now
    }

    private static Rectangle getRect(Variable obj) {
        if(obj instanceof ImageVariable) {
            return ((ImageVariable) obj).getRect();
        } else if(obj instanceof AnimoVariable) {
            return ((AnimoVariable) obj).getRect();
        } else {
            return null;
        }
    }
}
