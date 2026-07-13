package pl.genschu.bloomooemulator.engine.input;

import pl.genschu.bloomooemulator.engine.Game;
import pl.genschu.bloomooemulator.engine.context.GameContext;
import pl.genschu.bloomooemulator.interpreter.context.Context;
import pl.genschu.bloomooemulator.interpreter.variable.AnimoVariable;
import pl.genschu.bloomooemulator.interpreter.variable.ButtonVariable;
import pl.genschu.bloomooemulator.interpreter.variable.ImageVariable;
import pl.genschu.bloomooemulator.interpreter.variable.Variable;

/** Coordinates the single graphics object currently dragged by a BUTTON. */
public final class DragManager {
    private static final int DRAG_PRIORITY = 10000;

    private final Game game;
    private DragSession active;

    public DragManager(Game game) {
        this.game = game;
    }

    public boolean start(ButtonVariable button, Context owner, int mouseX, int mouseY) {
        if (active != null || button == null || owner == null || !button.isDraggable()) return false;

        Variable graphics = resolveGraphics(button, owner);
        if (!isDraggableGraphics(graphics)) return false;

        int originalPriority = getPriority(graphics);
        active = new DragSession(
                button,
                owner,
                game.getCurrentSceneContext(),
                graphics,
                mouseX,
                mouseY,
                originalPriority);
        setPriority(graphics, DRAG_PRIORITY);
        button.emitSignal("ONSTARTDRAGGING");

        if (active != null && !isSessionValid(active)) cancel();
        return active != null;
    }

    public void update(int mouseX, int mouseY) {
        DragSession session = active;
        if (session == null) return;
        if (!isSessionValid(session)) {
            cancel();
            return;
        }

        int dx = mouseX - session.previousMouseX;
        int dy = mouseY - session.previousMouseY;
        if (dx == 0 && dy == 0) return;

        session.previousMouseX = mouseX;
        session.previousMouseY = mouseY;
        moveBy(session.graphics, dx, dy);
        session.button.emitSignal("ONDRAGGING");

        if (active == session && !isSessionValid(session)) cancel();
    }

    public void end(int mouseX, int mouseY) {
        DragSession session = active;
        if (session == null) return;
        if (!isSessionValid(session)) {
            cancel();
            return;
        }

        update(mouseX, mouseY);
        session = active;
        if (session == null) return;

        setPriority(session.graphics, session.originalPriority);
        session.button.emitSignal("ONENDDRAGGING");
        if (active == session) active = null;
    }

    /** Cleans up without dispatching a script callback, e.g. during a scene change. */
    public void cancel() {
        DragSession session = active;
        active = null;
        if (session != null && isDraggableGraphics(session.graphics)) {
            setPriority(session.graphics, session.originalPriority);
        }
    }

    public boolean isDragging() {
        return active != null;
    }

    public boolean isDragging(ButtonVariable button) {
        return active != null && active.button == button;
    }

    public String getDraggedName() {
        return active != null ? active.graphics.name() : "";
    }

    private Variable resolveGraphics(ButtonVariable button, Context owner) {
        for (String name : new String[]{
                button.state().dragName,
                button.state().gfxStandardName,
                button.state().gfxOnMoveName}) {
            if (name == null || name.isBlank()) continue;
            Variable candidate = owner.getVariable(name);
            if (isDraggableGraphics(candidate)) return candidate;
        }
        return null;
    }

    private boolean isSessionValid(DragSession session) {
        GameContext currentScene = game.getCurrentSceneContext();
        return currentScene == session.scene
                && session.owner.getVariable(session.graphics.name()) == session.graphics;
    }

    private static boolean isDraggableGraphics(Variable variable) {
        return variable instanceof ImageVariable || variable instanceof AnimoVariable;
    }

    private int getPriority(Variable graphics) {
        if (graphics instanceof ImageVariable image) return image.getPriority();
        return ((AnimoVariable) graphics).getPriority();
    }

    private void setPriority(Variable graphics, int priority) {
        if (graphics instanceof ImageVariable image) image.setPriority(priority);
        else if (graphics instanceof AnimoVariable animo) animo.setPriority(priority);
    }

    private void moveBy(Variable graphics, int dx, int dy) {
        if (graphics instanceof ImageVariable image) image.moveBy(dx, dy, game);
        else if (graphics instanceof AnimoVariable animo) animo.moveBy(dx, dy, game);
    }

    private static final class DragSession {
        private final ButtonVariable button;
        private final Context owner;
        private final GameContext scene;
        private final Variable graphics;
        private final int originalPriority;
        private int previousMouseX;
        private int previousMouseY;

        private DragSession(ButtonVariable button, Context owner, GameContext scene,
                            Variable graphics, int previousMouseX, int previousMouseY,
                            int originalPriority) {
            this.button = button;
            this.owner = owner;
            this.scene = scene;
            this.graphics = graphics;
            this.previousMouseX = previousMouseX;
            this.previousMouseY = previousMouseY;
            this.originalPriority = originalPriority;
        }
    }
}
