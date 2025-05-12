package pl.genschu.bloomooemulator.engine.update;

import com.badlogic.gdx.utils.Disposable;
import pl.genschu.bloomooemulator.engine.Game;
import pl.genschu.bloomooemulator.engine.config.EngineConfig;
import pl.genschu.bloomooemulator.interpreter.Context;
import pl.genschu.bloomooemulator.interpreter.exceptions.BreakException;
import pl.genschu.bloomooemulator.interpreter.variable.Variable;
import pl.genschu.bloomooemulator.interpreter.variable.types.*;
import pl.genschu.bloomooemulator.objects.Rectangle;
import pl.genschu.bloomooemulator.utils.CollisionChecker;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class UpdateManager implements Disposable {
    private final Game game;
    private final EngineConfig config;

    private final CollisionManager collisionManager;
    private final TimerManager timerManager;
    private final AnimationManager animationManager;

    public UpdateManager(Game game, EngineConfig config) {
        this.game = game;
        this.config = config;

        this.collisionManager = new CollisionManager(game);
        this.timerManager = new TimerManager(game);
        this.animationManager = new AnimationManager(game);
    }

    public void update(float deltaTime) {
        Context context = game.getCurrentSceneContext();

        // update timers
        updateTimers();

        // update animations
        updateAnimations(deltaTime);
    }

    private void updateTimers() {
        timerManager.updateTimers();
    }

    public void checkCollisions(Variable object) {
        collisionManager.checkCollisions(object);
    }

    private void updateAnimations(float deltaTime) {
        animationManager.updateAnimations(deltaTime);
    }

    @Override
    public void dispose() {
        // Dispose elements
        collisionManager.dispose();
        timerManager.dispose();
        animationManager.dispose();
    }

    // Class managing collision
    public static class CollisionManager implements Disposable {
        private final Game game;

        public CollisionManager(Game game) {
            this.game = game;
        }

        public void checkCollisions(Variable object) {
            List<Variable> objects = new ArrayList<>(game.getCollisionMonitoredVariables());

            // Check if the object is in the list of objects to check
            if (!objects.contains(object)) {
                return;
            }

            List<Variable> potentialCollisions = game.getQuadTree().retrieve(new ArrayList<>(), object);
            for (Variable other : potentialCollisions) {
                if (other != object && checkCollision(object, other)) {
                    if (!object.isColliding(other)) {
                        object.setColliding(other);
                    }
                } else if (object.isColliding(other)) {
                    object.releaseColliding(other);
                }
            }
        }

        private boolean checkCollision(Variable obj1, Variable obj2) {
            return CollisionChecker.checkCollision(obj1, obj2);
        }

        public Rectangle getRect(Variable variable) {
            if (variable instanceof ImageVariable) {
                return ((ImageVariable) variable).getRect();
            } else if (variable instanceof AnimoVariable) {
                return ((AnimoVariable) variable).getRect();
            }
            return null;
        }

        @Override
        public void dispose() {
            // Implementation not needed
        }
    }

    // Class managing timers
    public static class TimerManager implements Disposable {
        private final Game game;

        public TimerManager(Game game) {
            this.game = game;
        }

        public void updateTimers() {
            Context context = game.getCurrentSceneContext();
            for (Variable variable : new ArrayList<>(context.getTimerVariables().values())) {
                TimerVariable timer = (TimerVariable) variable;
                try {
                    timer.update();
                } catch (BreakException ignored) {
                    // simple break, nothing special
                }
            }
        }

        @Override
        public void dispose() {
            // Implementation not needed
        }
    }

    // Class managing animations
    public static class AnimationManager implements Disposable {
        private final Game game;

        public AnimationManager(Game game) {
            this.game = game;
        }

        public void updateAnimations(float deltaTime) {
            Context context = game.getCurrentSceneContext();
            List<Variable> graphicsVariables = new ArrayList<>(context.getGraphicsVariables().values());

            for (Variable variable : graphicsVariables) {
                if (variable instanceof AnimoVariable) {
                    AnimoVariable animoVariable = (AnimoVariable) variable;
                    if (animoVariable.isPlaying()) {
                        animoVariable.updateAnimation(deltaTime);
                    }
                }
            }
        }

        @Override
        public void dispose() {
            // Implementation not needed
        }
    }
}