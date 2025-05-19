package pl.genschu.bloomooemulator.engine.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Cursor;
import pl.genschu.bloomooemulator.engine.Game;
import pl.genschu.bloomooemulator.engine.decision.events.ButtonEvent;
import pl.genschu.bloomooemulator.engine.decision.states.ButtonState;
import pl.genschu.bloomooemulator.interpreter.Context;
import pl.genschu.bloomooemulator.interpreter.variable.Signal;
import pl.genschu.bloomooemulator.interpreter.variable.Variable;
import pl.genschu.bloomooemulator.interpreter.variable.types.*;

import java.util.ArrayList;
import java.util.List;

public class ButtonHandler {
    private final Game game;
    private final InputManager inputManager;
    // TODO: debug, check pixel perfect
    private final boolean pixelPerfect = true;

    public ButtonHandler(Game game, InputManager inputManager) {
        this.game = game;
        this.inputManager = inputManager;
    }

    public void handleMouseInput(int x, int y, boolean isPressed, boolean justPressed,
                                 boolean justReleased, MouseVariable mouseVariable) {
        Context context = game.getCurrentSceneContext();

        // Get all buttons from the context and class instances
        List<Variable> buttons = new ArrayList<>(context.getButtonsVariables().values());

        // Get the priority ranges of hotspots from the scene
        int minHSPriority = game.getCurrentSceneVariable().getMinHotSpotZ();
        int maxHSPriority = game.getCurrentSceneVariable().getMaxHotSpotZ();

        // Process button interactions
        processButtonInteractions(buttons, x, y, isPressed, justPressed, justReleased,
                mouseVariable, minHSPriority, maxHSPriority);

        // Handle button release
        handleButtonRelease(justReleased);
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
        // find first button under cursor
        Variable focusedButton = null;

        for (Variable variable : buttons) {
            if (variable instanceof ButtonVariable) {
                ButtonVariable button = (ButtonVariable) variable;
                Variable image = button.getCurrentImage();

                // Filter by hotspot priority
                if (image != null) {
                    int priority = getPriority(image);
                    if (priority < minHSPriority || priority > maxHSPriority) continue;
                    if(pixelPerfect) {
                        //Gdx.app.debug("ButtonHandler", "Alpha for "+image.getName()+": " + getAlpha(image, x, y));
                        if(getAlpha(image, x, y) == 0) continue;
                    }
                }

                // Check if button is enabled
                if (button.isEnabled() && button.getRect() != null && button.getRect().contains(x, y)) {
                    focusedButton = button;
                    break;
                }
            } else if (variable instanceof AnimoVariable) {
                AnimoVariable animo = (AnimoVariable) variable;

                // Filter by hotspot priority
                int priority = getPriority(animo);
                if (priority < minHSPriority || priority > maxHSPriority) continue;

                // Check if animo is under cursor
                if (animo.getRect() != null && animo.getRect().contains(x, y)) {
                    if(pixelPerfect) {
                        //Gdx.app.debug("ButtonHandler", "Alpha for "+animo.getName()+": " + getAlpha(animo, x, y));
                        if(getAlpha(animo, x, y) == 0) continue;
                    }
                    focusedButton = animo;
                    break;
                }
            }
        }

        boolean isMouseVisible = inputManager.isMouseVisible();

        // Set hand cursor
        if (focusedButton != null && isMouseVisible) {
            if(focusedButton instanceof AnimoVariable) {
                if(((AnimoVariable) focusedButton).isChangeCursor()) {
                    Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Hand);
                }
                else {
                    Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow);
                }
            } else {
                Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Hand);
            }
        } else if (isMouseVisible) {
            Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow);
        }
        else {
            Gdx.graphics.setSystemCursor(Cursor.SystemCursor.None);
        }

        // Process 'em all!
        for (Variable variable : buttons) {
            if (variable == focusedButton) {
                if (variable instanceof ButtonVariable) {
                    processButtonVariable((ButtonVariable) variable, x, y, isPressed, justPressed,
                            mouseVariable, true);
                } else if (variable != null) {
                    processAnimoVariable((AnimoVariable) variable, x, y, isPressed, justPressed,
                            mouseVariable, true);
                }
            } else {
                // Take down focus
                if (variable instanceof ButtonVariable) {
                    ButtonVariable button = (ButtonVariable) variable;
                    button.changeState(ButtonEvent.FOCUS_OFF);
                } else if (variable instanceof AnimoVariable) {
                    AnimoVariable animo = (AnimoVariable) variable;
                    animo.changeButtonState(ButtonEvent.FOCUS_OFF);
                }
            }
        }
    }

    private void processButtonVariable(ButtonVariable button, int x, int y, boolean isPressed,
                                       boolean justPressed, MouseVariable mouseVariable,
                                       boolean shouldFocus) {
        if (!button.isEnabled()) return;

        if (shouldFocus) {
            if (justPressed) {
                if (inputManager.getActiveButton() == null) {
                    inputManager.setActiveButton(button);
                    button.changeState(ButtonEvent.PRESSED);
                }
            } else if (button.getState() != ButtonState.HOVERED) {
                button.changeState(ButtonEvent.FOCUS_ON);
            }
        }
    }

    private void processAnimoVariable(AnimoVariable animo, int x, int y, boolean isPressed,
                                      boolean justPressed, MouseVariable mouseVariable,
                                      boolean shouldFocus) {
        if (shouldFocus) {
            if (justPressed) {
                if (inputManager.getActiveButton() == null) {
                    inputManager.setActiveButton(animo);
                    animo.changeButtonState(ButtonEvent.PRESSED);
                }
            } else if (animo.getButtonState() != ButtonState.HOVERED) {
                animo.changeButtonState(ButtonEvent.FOCUS_ON);
            }
        }
    }

    private int getAlpha(Variable image, int x, int y) {
        if(image instanceof ImageVariable) {
            ImageVariable imageVariable = (ImageVariable) image;
            return imageVariable.getAlpha(x-imageVariable.getPosX(), y-imageVariable.getPosY());
        }
        if(image instanceof AnimoVariable) {
            AnimoVariable animoVariable = (AnimoVariable) image;
            return ((AnimoVariable) image).getAlpha(x-animoVariable.getRect().getXLeft(), y-animoVariable.getRect().getYTop());
        }
        return 0;
    }

    private void handleButtonRelease(boolean justReleased) {
        Variable activeButton = inputManager.getActiveButton();
        if (justReleased && activeButton != null) {
            if (activeButton instanceof ButtonVariable) {
                ((ButtonVariable) activeButton).changeState(ButtonEvent.RELEASED);
            } else if (activeButton instanceof AnimoVariable) {
                inputManager.triggerSignal(activeButton, "ONRELEASE");
                ((AnimoVariable) activeButton).changeButtonState(ButtonEvent.RELEASED);
            }
            inputManager.setActiveButton(null);
        }
    }
}