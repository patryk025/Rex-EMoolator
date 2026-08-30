package pl.genschu.bloomooemulator.engine.input;

import com.badlogic.gdx.graphics.Cursor;
import pl.genschu.bloomooemulator.engine.Game;
import pl.genschu.bloomooemulator.engine.decision.events.ButtonEvent;
import pl.genschu.bloomooemulator.engine.decision.states.ButtonState;
import pl.genschu.bloomooemulator.geometry.coordinates.CanvasRect;
import pl.genschu.bloomooemulator.interpreter.context.Context;
import pl.genschu.bloomooemulator.interpreter.variable.*;
import pl.genschu.bloomooemulator.objects.Event;
import pl.genschu.bloomooemulator.objects.FrameData;
import pl.genschu.bloomooemulator.objects.Image;

import java.util.ArrayList;
import java.util.List;

public class ButtonHandler {
    private final Game game;
    private final InputManager inputManager;
    private final boolean pixelPerfect;

    public ButtonHandler(Game game, InputManager inputManager) {
        this(game, inputManager, true);
    }

    ButtonHandler(Game game, InputManager inputManager, boolean pixelPerfect) {
        this.game = game;
        this.inputManager = inputManager;
        this.pixelPerfect = pixelPerfect;
    }

    public void handleMouseInput(int x, int y, boolean isPressed, boolean justPressed,
                                 boolean justReleased, MouseVariable mouseVariable, boolean mouseEnabled) {
        if (isPressed || justReleased) {
            inputManager.getDragManager().update(x, y);
        }

        Context sceneContext = (Context) game.getCurrentSceneContext();

        // Keep distinct buttons that happen to use the same script name in separate
        // class instances (for example MAINMENU.BTNEXIT and INVESTIGATION.BTNEXIT).
        List<Context.ScopedVariable> buttons = sceneContext.getScopedButtonVariablesForInput();

        // Resolve each hit-test graphic once per input pass. The sort comparator and
        // hit testing below reuse this snapshot instead of traversing the context graph
        // O(n log n) times.
        List<ScopedButton> scopedButtons = new ArrayList<>(buttons.size());
        for (int i = 0; i < buttons.size(); i++) {
            Context.ScopedVariable scoped = buttons.get(i);
            Variable button = scoped.variable();
            Variable hitGfx = getButtonGfx(button, scoped.owner());
            scopedButtons.add(new ScopedButton(
                    button,
                    scoped.owner(),
                    hitGfx,
                    getPriority(hitGfx),
                    getRenderOrder(hitGfx),
                    i
            ));
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

    private record ScopedButton(
            Variable variable,
            Context owner,
            Variable hitGfx,
            int hitPriority,
            long hitRenderOrder,
            int order
    ) {}

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
                    right.hitPriority(),
                    left.hitPriority()
            );
            if (priorityComparison != 0) {
                return priorityComparison;
            }

            int orderComparison = Long.compare(
                    right.hitRenderOrder(),
                    left.hitRenderOrder()
            );
            if (orderComparison != 0) {
                return orderComparison;
            }

            // A SETASBUTTON ANIMO can also be assigned as a BUTTON's standard
            // graphics via SETSTD. The original engine dispatches the CButton
            // CHotSpot before the ANIMO mouse listener in that arrangement, so
            // the BUTTON owns the click while ANIMO can still establish focus.
            if (left.hitGfx() == right.hitGfx()) {
                boolean leftOwnsGraphic = ownsAnimoHitGraphic(left);
                boolean rightOwnsGraphic = ownsAnimoHitGraphic(right);
                if (leftOwnsGraphic != rightOwnsGraphic) {
                    return leftOwnsGraphic ? -1 : 1;
                }
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
                Variable image = scopedButton.hitGfx();

                // Filter by hotspot priority
                if (image != null) {
                    int priority = scopedButton.hitPriority();
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
                CanvasRect hitRect = animo.getRect();
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
                    processButtonVariable(btn, owner, x, y, justPressed, true);
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

    private static boolean ownsAnimoHitGraphic(ScopedButton button) {
        return button.variable() instanceof ButtonVariable
                && button.hitGfx() instanceof AnimoVariable;
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

    private void processButtonVariable(ButtonVariable button, Context context, int mouseX, int mouseY,
                                       boolean justPressed, boolean shouldFocus) {
        if (!button.isEnabled()) return;

        if (shouldFocus) {
            if (justPressed) {
                if (inputManager.getActiveButton() == null) {
                    inputManager.setActiveButton(button);
                    inputManager.getDragManager().start(button, context, mouseX, mouseY);
                    if (inputManager.getActiveButton() == button) {
                        button.changeState(ButtonEvent.PRESSED, context);
                    }
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
            return animo.getAlpha(x - animo.getRect().left(), y - animo.getRect().top());
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
                inputManager.getDragManager().end(
                        (int) inputManager.getMousePosition().x(),
                        (int) inputManager.getMousePosition().y());
            } else if (activeButton instanceof AnimoVariable animo) {
                animo.changeButtonState(ButtonEvent.RELEASED, owner);
            }
            inputManager.setActiveButton(null);
        }
    }
}
