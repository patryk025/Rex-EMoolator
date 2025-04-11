package pl.genschu.bloomooemulator.engine.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Cursor;
import pl.genschu.bloomooemulator.engine.Game;
import pl.genschu.bloomooemulator.interpreter.Context;
import pl.genschu.bloomooemulator.interpreter.variable.Signal;
import pl.genschu.bloomooemulator.interpreter.variable.Variable;
import pl.genschu.bloomooemulator.interpreter.variable.types.AnimoVariable;
import pl.genschu.bloomooemulator.interpreter.variable.types.ButtonVariable;
import pl.genschu.bloomooemulator.interpreter.variable.types.MouseVariable;
import pl.genschu.bloomooemulator.interpreter.variable.types.StringVariable;

import java.util.ArrayList;
import java.util.List;

public class ButtonHandler {
    private final Game game;
    private final InputManager inputManager;

    public ButtonHandler(Game game, InputManager inputManager) {
        this.game = game;
        this.inputManager = inputManager;
    }

    public void handleMouseInput(int x, int y, boolean isPressed, boolean justPressed,
                                 boolean justReleased, MouseVariable mouseVariable) {
        Context context = game.getCurrentSceneContext();

        // Get all buttons from the context and class instances
        List<Variable> buttons = new ArrayList<>(context.getButtonsVariables().values());
        addClassInstanceButtons(buttons, context);

        // Get the priority ranges of hotspots from the scene
        int minHSPriority = game.getCurrentSceneVariable().getMinHotSpotZ();
        int maxHSPriority = game.getCurrentSceneVariable().getMaxHotSpotZ();

        // Sort buttons by priority
        sortButtonsByPriority(buttons);

        // Process button interactions
        processButtonInteractions(buttons, x, y, isPressed, justPressed, justReleased,
                mouseVariable, minHSPriority, maxHSPriority);

        // Handle button release
        handleButtonRelease(justReleased);
    }

    private void addClassInstanceButtons(List<Variable> buttons, Context context) {
        for (Variable variable : context.getClassInstances().values()) {
            List<Variable> classButtons = new ArrayList<>(variable.getContext().getButtonsVariables(false).values());
            buttons.addAll(classButtons);
        }
    }

    private void sortButtonsByPriority(List<Variable> buttons) {
        buttons.sort((o1, o2) -> {
            Variable image1 = getButtonImage(o1);
            Variable image2 = getButtonImage(o2);

            int priority1 = getPriority(image1);
            int priority2 = getPriority(image2);

            return Integer.compare(priority2, priority1);
        });
    }

    private Variable getButtonImage(Variable button) {
        if (button instanceof ButtonVariable) {
            return ((ButtonVariable) button).getCurrentImage();
        } else if (button instanceof AnimoVariable) {
            return button;
        }
        return null;
    }

    private int getPriority(Variable image) {
        if (image != null && image.getAttribute("PRIORITY") != null) {
            return Integer.parseInt(image.getAttribute("PRIORITY").getValue().toString());
        }
        return 0;
    }

    private void processButtonInteractions(List<Variable> buttons, int x, int y, boolean isPressed,
                                           boolean justPressed, boolean justReleased,
                                           MouseVariable mouseVariable, int minHSPriority, int maxHSPriority) {
        for (Variable variable : buttons) {
            if (variable instanceof ButtonVariable) {
                processButtonVariable((ButtonVariable) variable, x, y, isPressed, justPressed,
                        mouseVariable, minHSPriority, maxHSPriority);
            } else if (variable instanceof AnimoVariable) {
                processAnimoVariable((AnimoVariable) variable, x, y, isPressed, justPressed,
                        mouseVariable, minHSPriority, maxHSPriority);
            }
        }
    }

    private void processButtonVariable(ButtonVariable button, int x, int y, boolean isPressed,
                                       boolean justPressed, MouseVariable mouseVariable,
                                       int minHSPriority, int maxHSPriority) {
        Variable image = button.getCurrentImage();
        if (image != null) {
            int priority = getPriority(image);
            if (priority < minHSPriority || priority > maxHSPriority) return;
        }

        if (!button.isEnabled()) return;

        if (button.getRect() != null && button.getRect().contains(x, y)) {
            if (mouseVariable == null || mouseVariable.isVisible()) {
                Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Hand);
            }

            if (justPressed) {
                if (inputManager.getActiveButton() == null) {
                    inputManager.setActiveButton(button);
                    inputManager.triggerSignal(button, "ONCLICKED");
                    inputManager.triggerSignal(button, "ONACTION");
                }
            }

            if (button == inputManager.getActiveButton()) {
                if (isPressed) {
                    if (button.isEnabled()) {
                        button.setPressed(true);
                    }
                }
            }

            if (!button.isFocused() && !isPressed && button.isEnabled()) {
                button.setFocused(true);
                if (mouseVariable == null || mouseVariable.isVisible()) {
                    Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Hand);
                }
                Signal onFocusSignal = button.getSignal("ONFOCUSON");
                if (onFocusSignal != null) {
                    onFocusSignal.execute(null);
                }
            }
        } else {
            if (button.isFocused() && !isPressed) {
                button.setFocused(false);
                if (mouseVariable == null || mouseVariable.isVisible()) {
                    Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow);
                }
                Signal onFocusLossSignal = button.getSignal("ONFOCUSOFF");
                if (onFocusLossSignal != null) {
                    onFocusLossSignal.execute(null);
                }
            } else {
                button.setFocused(false);
            }
        }
    }

    private void processAnimoVariable(AnimoVariable animo, int x, int y, boolean isPressed,
                                      boolean justPressed, MouseVariable mouseVariable,
                                      int minHSPriority, int maxHSPriority) {
        int priority = getPriority(animo);
        if (priority < minHSPriority || priority > maxHSPriority) return;

        if (animo.getRect() != null && animo.getRect().contains(x, y)) {
            if (animo.isChangeCursor() && (mouseVariable == null || mouseVariable.isVisible())) {
                Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Hand);
            }

            if (justPressed) {
                if (inputManager.getActiveButton() == null) {
                    inputManager.setActiveButton(animo);
                    inputManager.triggerSignal(animo, "ONCLICK");
                    animo.fireMethod("PLAY", new StringVariable("", "ONCLICK", animo.getContext()));
                    animo.setPlaying(false);
                }
            }

            if (animo == inputManager.getActiveButton()) {
                if (isPressed) {
                    animo.setPressed(true);
                }
            }

            if (!animo.isFocused() && !isPressed) {
                animo.setFocused(true);
                if (animo.isChangeCursor() && (mouseVariable == null || mouseVariable.isVisible())) {
                    Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Hand);
                }
                Signal onFocusSignal = animo.getSignal("ONFOCUSON");
                if (onFocusSignal != null) {
                    onFocusSignal.execute(null);
                }
                animo.fireMethod("PLAY", new StringVariable("", "ONFOCUSON", animo.getContext()));
                animo.setPlaying(false);
            }
        } else {
            if (animo.isFocused() && !isPressed) {
                animo.setFocused(false);
                if (mouseVariable == null || mouseVariable.isVisible()) {
                    Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow);
                }
                Signal onFocusLossSignal = animo.getSignal("ONFOCUSOFF");
                if (onFocusLossSignal != null) {
                    onFocusLossSignal.execute(null);
                }
                animo.fireMethod("PLAY", new StringVariable("", "ONFOCUSOFF", animo.getContext()));
                animo.setPlaying(false);
            }
        }
    }

    private void handleButtonRelease(boolean justReleased) {
        Variable activeButton = inputManager.getActiveButton();
        if (justReleased && activeButton != null) {
            if (activeButton instanceof ButtonVariable) {
                inputManager.triggerSignal(activeButton, "ONRELEASED");
                ((ButtonVariable) activeButton).setPressed(false);
                inputManager.triggerSignal(activeButton, "GFXONCLICK");
            } else if (activeButton instanceof AnimoVariable) {
                ((AnimoVariable) activeButton).setPressed(false);
            }
            inputManager.setActiveButton(null);
        }
    }
}