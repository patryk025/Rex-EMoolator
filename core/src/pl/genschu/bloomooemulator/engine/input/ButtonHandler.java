package pl.genschu.bloomooemulator.engine.input;

import com.badlogic.gdx.graphics.Cursor;
import pl.genschu.bloomooemulator.engine.Game;
import pl.genschu.bloomooemulator.engine.decision.events.ButtonEvent;
import pl.genschu.bloomooemulator.engine.decision.states.ButtonState;
import pl.genschu.bloomooemulator.geometry.shapes.Box2D;
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
                                 boolean justReleased, MouseVariable mouseVariable, boolean mouseEnabled) {
        Context sceneContext = (Context) game.getCurrentSceneContext();

        // Get all buttons from the context and class instances
        @SuppressWarnings("unchecked")
        List<Variable> buttons = new ArrayList<>((Collection<? extends Variable>) sceneContext.getButtonsVariables().values());

        // A button defined inside a class instance lives in that instance context, not
        // the scene; use the concrete variable instance so duplicate names do not
        // accidentally resolve to a different owner.
        List<ScopedButton> scopedButtons = new ArrayList<>(buttons.size());
        for (int i = 0; i < buttons.size(); i++) {
            Variable button = buttons.get(i);
            Context owner = sceneContext.findOwningContext(button);
            scopedButtons.add(new ScopedButton(button, owner != null ? owner : sceneContext, i));
        }

        // Get the priority ranges of hotspots from the scene
        int minHSPriority = game.getCurrentSceneVariable().minHotSpotZ();
        int maxHSPriority = game.getCurrentSceneVariable().maxHotSpotZ();

        // Process button interactions
        processButtonInteractions(scopedButtons, x, y, isPressed, justPressed, justReleased,
                mouseVariable, minHSPriority, maxHSPriority, mouseEnabled);

        // Handle button release
        handleButtonRelease(justReleased, scopedButtons);
    }

    private record ScopedButton(Variable variable, Context owner, int order) {}

    private Variable getButtonGfx(Variable button, Context context) {
        if (button instanceof ButtonVariable btn) {
            // Hit testing always uses GFXSTANDARD: the trigger silhouette is fixed
            // by the standard graphic, even while GFXONMOVE/GFXONCLICK is displayed.
            String gfxName = btn.state().gfxStandardName;
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

    private void processButtonInteractions(List<ScopedButton> buttons,
                                           int x, int y, boolean isPressed,
                                           boolean justPressed, boolean justReleased,
                                           MouseVariable mouseVariable, int minHSPriority, int maxHSPriority,
                                           boolean mouseEnabled) {
        List<ScopedButton> hitTestOrder = new ArrayList<>(buttons);
        hitTestOrder.sort((left, right) -> {
            int priorityComparison = Integer.compare(
                    getHitPriority(right.variable(), right.owner()),
                    getHitPriority(left.variable(), left.owner())
            );
            if (priorityComparison != 0) {
                return priorityComparison;
            }

            int orderComparison = Long.compare(
                    getHitRenderOrder(right.variable(), right.owner()),
                    getHitRenderOrder(left.variable(), left.owner())
            );
            if (orderComparison != 0) {
                return orderComparison;
            }

            return Integer.compare(right.order(), left.order());
        });

        // find topmost button under cursor.
        // When the mouse is disabled, no button can be focused or
        // clicked: leaving focusedButton null makes the loop below drop focus from
        // every button and prevents any press from registering.
        ScopedButton focusedButton = null;

        for (ScopedButton scopedButton : (mouseEnabled ? hitTestOrder : List.<ScopedButton>of())) {
            Variable variable = scopedButton.variable();
            if (variable instanceof ButtonVariable btn) {
                Variable image = getButtonGfx(btn, scopedButton.owner());

                // Filter by hotspot priority
                if (image != null) {
                    int priority = getPriority(image);
                    if (priority < minHSPriority || priority > maxHSPriority) continue;
                    if (pixelPerfect) {
                        if (getAlpha(image, x, y) == 0) continue;
                    }
                }

                // Check if button is enabled
                if (btn.isEnabled() && btn.getRect() != null && btn.getRect().contains(x, y)) {
                    focusedButton = scopedButton;
                    break;
                }
            } else if (variable instanceof AnimoVariable animo) {
                // Filter by hotspot priority
                int priority = animo.getPriority();
                if (priority < minHSPriority || priority > maxHSPriority) continue;

                // ANIMO via SETASBUTTON uses a plain bounding-box trigger: no alpha test.
                // The transparent areas of the sprite are still clickable. The rect is live
                // so the trigger follows the moving sprite.
                Box2D hitRect = animo.getRect();
                if (hitRect != null && hitRect.contains(x, y)) {
                    focusedButton = scopedButton;
                    break;
                }
            }
        }

        boolean isMouseVisible = inputManager.isMouseVisible();

        // Set hand cursor
        if (focusedButton != null && isMouseVisible) {
            if (focusedButton.variable() instanceof AnimoVariable animo) {
                if (animo.isChangeCursor()) {
                    inputManager.applyMouseCursor(Cursor.SystemCursor.Hand);
                } else {
                    inputManager.applyMouseCursor(Cursor.SystemCursor.Arrow);
                }
            } else {
                inputManager.applyMouseCursor(Cursor.SystemCursor.Hand);
            }
        } else if (isMouseVisible) {
            inputManager.applyMouseCursor(null);
        } else {
            inputManager.applyMouseCursor(null);
        }

        // Process 'em all!
        for (ScopedButton scopedButton : buttons) {
            Variable variable = scopedButton.variable();
            Context owner = scopedButton.owner();
            if (scopedButton == focusedButton) {
                if (variable instanceof ButtonVariable btn) {
                    processButtonVariable(btn, owner, justPressed, true);
                } else if (variable instanceof AnimoVariable animo) {
                    processAnimoVariable(animo, owner, justPressed, true);
                }
            } else {
                // Take down focus
                if (variable instanceof ButtonVariable btn) {
                    btn.changeState(ButtonEvent.FOCUS_OFF, owner);
                } else if (variable instanceof AnimoVariable animo) {
                    animo.changeButtonState(ButtonEvent.FOCUS_OFF, owner);
                }
            }
        }
    }

    private int getHitPriority(Variable variable, Context context) {
        if (variable instanceof ButtonVariable btn) {
            Variable gfx = getButtonGfx(btn, context);
            return gfx != null ? getPriority(gfx) : 0;
        }
        if (variable instanceof AnimoVariable animo) {
            return animo.getPriority();
        }
        return 0;
    }

    private long getHitRenderOrder(Variable variable, Context context) {
        if (variable instanceof ButtonVariable btn) {
            Variable gfx = getButtonGfx(btn, context);
            return getRenderOrder(gfx);
        }
        return getRenderOrder(variable);
    }

    private long getRenderOrder(Variable variable) {
        if (variable instanceof ImageVariable img) {
            return img.getRenderOrder();
        }
        if (variable instanceof AnimoVariable animo) {
            return animo.getRenderOrder();
        }
        return 0;
    }

    private void processButtonVariable(ButtonVariable button, Context context,
                                       boolean justPressed, boolean shouldFocus) {
        if (!button.isEnabled()) return;

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

    private void processAnimoVariable(AnimoVariable animo, Context context,
                                      boolean justPressed, boolean shouldFocus) {
        if (shouldFocus) {
            if (justPressed) {
                if (inputManager.getActiveButton() == null) {
                    inputManager.setActiveButton(animo);
                    animo.changeButtonState(ButtonEvent.PRESSED, context);
                }
            } else if (animo.getButtonState() != ButtonState.HOVERED) {
                animo.changeButtonState(ButtonEvent.FOCUS_ON, context);
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

    private void handleButtonRelease(boolean justReleased, List<ScopedButton> buttons) {
        Object activeButton = inputManager.getActiveButton();
        if (justReleased && activeButton != null) {
            Context owner = buttons.stream()
                    .filter(button -> button.variable() == activeButton)
                    .findFirst()
                    .map(ScopedButton::owner)
                    .orElse((Context) game.getCurrentSceneContext());
            if (activeButton instanceof ButtonVariable btn) {
                btn.changeState(ButtonEvent.RELEASED, owner);
            } else if (activeButton instanceof AnimoVariable animo) {
                animo.changeButtonState(ButtonEvent.RELEASED, owner);
            }
            inputManager.setActiveButton(null);
        }
    }
}
