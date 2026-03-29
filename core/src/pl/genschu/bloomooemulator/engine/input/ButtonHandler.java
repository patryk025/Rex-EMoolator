package pl.genschu.bloomooemulator.engine.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Cursor;
import pl.genschu.bloomooemulator.engine.Game;
import pl.genschu.bloomooemulator.engine.decision.events.ButtonEvent;
import pl.genschu.bloomooemulator.engine.decision.states.ButtonState;
import pl.genschu.bloomooemulator.engine.context.GameContext;
import pl.genschu.bloomooemulator.interpreter.context.Context;
import pl.genschu.bloomooemulator.interpreter.variable.*;
import pl.genschu.bloomooemulator.objects.Event;
import pl.genschu.bloomooemulator.objects.FrameData;
import pl.genschu.bloomooemulator.objects.Image;

import java.util.ArrayList;
import java.util.Collection;
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
        GameContext context = game.getCurrentSceneContext();

        // Get all buttons from the context and class instances
        @SuppressWarnings("unchecked")
        List<Variable> buttons = new ArrayList<>((Collection<? extends Variable>) context.getButtonsVariables().values());

        // Get the priority ranges of hotspots from the scene
        int minHSPriority = game.getCurrentSceneVariable().minHotSpotZ();
        int maxHSPriority = game.getCurrentSceneVariable().maxHotSpotZ();

        // Process button interactions
        processButtonInteractions(buttons, x, y, isPressed, justPressed, justReleased,
                mouseVariable, minHSPriority, maxHSPriority);

        // Handle button release
        handleButtonRelease(justReleased);
    }

    private Variable getButtonGfx(Variable button, Context context) {
        if (button instanceof ButtonVariable btn) {
            String gfxName = btn.getCurrentGfxName();
            if (gfxName != null) {
                return context.getVariable(gfxName);
            }
            return null;
        } else if (button instanceof AnimoVariable) {
            return button;
        }
        return null;
    }

    private int getPriority(Variable variable) {
        if (variable instanceof ImageVariable img) {
            return img.state().priority;
        } else if (variable instanceof AnimoVariable animo) {
            return animo.getPriority();
        }
        return 0;
    }

    private void processButtonInteractions(List<Variable> buttons, int x, int y, boolean isPressed,
                                           boolean justPressed, boolean justReleased,
                                           MouseVariable mouseVariable, int minHSPriority, int maxHSPriority) {
        Context context = (Context) game.getCurrentSceneContext();

        // find first button under cursor
        Variable focusedButton = null;

        for (Variable variable : buttons) {
            if (variable instanceof ButtonVariable btn) {
                Variable image = getButtonGfx(btn, context);

                // Filter by hotspot priority
                if (image != null) {
                    int priority = getPriority(image);
                    if (priority < minHSPriority || priority > maxHSPriority) continue;
                    if (pixelPerfect && btn.state().rectVarName != null) {
                        if (getAlpha(image, x, y) == 0) continue;
                    }
                }

                // Check if button is enabled
                if (btn.isEnabled() && btn.getRect() != null && btn.getRect().contains(x, y)) {
                    focusedButton = btn;
                    break;
                }
            } else if (variable instanceof AnimoVariable animo) {
                // Filter by hotspot priority
                int priority = animo.getPriority();
                if (priority < minHSPriority || priority > maxHSPriority) continue;

                // Check if animo is under cursor
                if (animo.getRect() != null && animo.getRect().contains(x, y)) {
                    if (pixelPerfect) {
                        if (getAlpha(animo, x, y) == 0) continue;
                    }
                    focusedButton = animo;
                    break;
                }
            }
        }

        boolean isMouseVisible = inputManager.isMouseVisible();

        // Set hand cursor
        if (focusedButton != null && isMouseVisible) {
            if (focusedButton instanceof AnimoVariable animo) {
                if (animo.isChangeCursor()) {
                    Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Hand);
                } else {
                    Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow);
                }
            } else {
                Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Hand);
            }
        } else if (isMouseVisible) {
            Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow);
        } else {
            Gdx.graphics.setSystemCursor(Cursor.SystemCursor.None);
        }

        // Process 'em all!
        for (Variable variable : buttons) {
            if (variable == focusedButton) {
                if (variable instanceof ButtonVariable btn) {
                    processButtonVariable(btn, x, y, isPressed, justPressed, mouseVariable, true);
                } else if (variable instanceof AnimoVariable animo) {
                    processAnimoVariable(animo, x, y, isPressed, justPressed, mouseVariable, true);
                }
            } else {
                // Take down focus
                if (variable instanceof ButtonVariable btn) {
                    btn.changeState(ButtonEvent.FOCUS_OFF, context);
                } else if (variable instanceof AnimoVariable animo) {
                    animo.changeButtonState(ButtonEvent.FOCUS_OFF);
                }
            }
        }
    }

    private void processButtonVariable(ButtonVariable button, int x, int y, boolean isPressed,
                                       boolean justPressed, MouseVariable mouseVariable,
                                       boolean shouldFocus) {
        if (!button.isEnabled()) return;
        Context context = (Context) game.getCurrentSceneContext();

        if (shouldFocus) {
            if (justPressed) {
                if (inputManager.getActiveButton() == null) {
                    inputManager.setActiveButton(button);
                    button.changeState(ButtonEvent.PRESSED, context);
                }
            } else if (button.getButtonState() != ButtonState.HOVERED) {
                button.changeState(ButtonEvent.FOCUS_ON, context);
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
        if (image instanceof ImageVariable img) {
            return img.getAlpha(x - img.getPosX(), y - img.getPosY());
        }
        if (image instanceof AnimoVariable animo) {
            Event noEvent = animo.getEvent("ONNOEVENT");
            if (noEvent != null) {
                Image noEventImage = noEvent.getFrames().get(0);
                FrameData frameData = !noEvent.getFrameData().isEmpty()
                        ? noEvent.getFrameData().get(0)
                        : null;

                int frameOffsetX = frameData != null ? frameData.getOffsetX() : 0;
                int frameOffsetY = frameData != null ? frameData.getOffsetY() : 0;

                int offsetX = animo.getPosX() + frameOffsetX + noEventImage.offsetX;
                int offsetY = animo.getPosY() + frameOffsetY + noEventImage.offsetY;

                return animo.getAlpha(noEventImage, x - offsetX, y - offsetY);
            }
            return animo.getAlpha(x - animo.getRect().getXLeft(), y - animo.getRect().getYTop());
        }
        return 0;
    }

    private void handleButtonRelease(boolean justReleased) {
        Object activeButton = inputManager.getActiveButton();
        if (justReleased && activeButton != null) {
            Context context = (Context) game.getCurrentSceneContext();
            if (activeButton instanceof ButtonVariable btn) {
                btn.changeState(ButtonEvent.RELEASED, context);
            } else if (activeButton instanceof AnimoVariable animo) {
                inputManager.triggerSignal(animo, "ONRELEASE");
                animo.changeButtonState(ButtonEvent.RELEASED);
            }
            inputManager.setActiveButton(null);
        }
    }
}
