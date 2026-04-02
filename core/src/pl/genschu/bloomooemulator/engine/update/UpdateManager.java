package pl.genschu.bloomooemulator.engine.update;

import com.badlogic.gdx.utils.Disposable;
import pl.genschu.bloomooemulator.engine.Game;
import pl.genschu.bloomooemulator.engine.config.EngineConfig;
import pl.genschu.bloomooemulator.engine.context.EngineVariable;
import pl.genschu.bloomooemulator.engine.context.GameContext;
import pl.genschu.bloomooemulator.interpreter.variable.*;
import pl.genschu.bloomooemulator.geometry.shapes.Box2D;
import pl.genschu.bloomooemulator.utils.CollisionChecker;

import java.util.ArrayList;
import java.util.List;

public class UpdateManager implements Disposable {
    private final Game game;
    private final EngineConfig config;

    private final CollisionManager collisionManager;
    private final TimerManager timerManager;
    private final AnimationManager animationManager;
    private final AudioManager audioManager;

    public UpdateManager(Game game, EngineConfig config) {
        this.game = game;
        this.config = config;

        this.collisionManager = new CollisionManager(game);
        this.timerManager = new TimerManager(game);
        this.animationManager = new AnimationManager(game);
        this.audioManager = new AudioManager(game);
    }

    public void update(float deltaTime) {
        GameContext context = game.getCurrentSceneContext();

        // update timers
        updateTimers();

        // update animations
        updateAnimations(deltaTime);

        // update audio
        updateAudio(deltaTime);
    }

    private void updateTimers() {
        timerManager.updateTimers();
    }

    public void checkCollisions(EngineVariable object) {
        collisionManager.checkCollisions(object);
    }

    private void updateAnimations(float deltaTime) {
        animationManager.updateAnimations(deltaTime);
    }

    private void updateAudio(float deltaTime) {
        audioManager.update(deltaTime);
    }

    @Override
    public void dispose() {
        // Dispose elements
        collisionManager.dispose();
        timerManager.dispose();
        animationManager.dispose();
        audioManager.dispose();
    }

    // Class managing collision
    public static class CollisionManager implements Disposable {
        private final Game game;

        public CollisionManager(Game game) {
            this.game = game;
        }

        public void checkCollisions(EngineVariable object) {
            List<EngineVariable> objects = new ArrayList<>(game.getCollisionMonitoredVariables());

            // Check if the object is in the list of objects to check
            if (!objects.contains(object)) {
                return;
            }

            List<EngineVariable> potentialCollisions = game.getQuadTree().retrieve(new ArrayList<>(), object);
            for (EngineVariable other : potentialCollisions) {
                if (other != object && checkCollision(object, other)) {
                    if (!game.isColliding(object, other)) {
                        game.setColliding(object, other);
                    }
                } else if (game.isColliding(object, other)) {
                    game.releaseColliding(object, other);
                }
            }
        }

        private boolean checkCollision(EngineVariable obj1, EngineVariable obj2) {
            return CollisionChecker.checkCollision(obj1, obj2);
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
            GameContext context = game.getCurrentSceneContext();
            for (EngineVariable variable : new ArrayList<>(context.getTimerVariables().values())) {
                if (variable instanceof TimerVariable timer) {
                    try {
                        timer.update();
                    } catch (Exception ignored) {
                        // simple break, nothing special
                    }
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
            GameContext context = game.getCurrentSceneContext();
            List<? extends EngineVariable> graphicsVariables = new ArrayList<>(context.getGraphicsVariables().values());

            for (EngineVariable variable : graphicsVariables) {
                if (variable instanceof AnimoVariable animoVariable) {
                    if (animoVariable.isPlaying()) {
                        animoVariable.updateAnimation(deltaTime);
                    }
                    // Refresh QuadTree position for collision-monitored variables
                    if (animoVariable.isMonitorCollision()) {
                        game.getQuadTree().remove(animoVariable);
                        game.getQuadTree().insert(animoVariable);
                    }
                }
            }
        }

        @Override
        public void dispose() {
            // Implementation not needed
        }
    }

    // Class managing audio
    public static class AudioManager implements Disposable {
        private final Game game;

        public AudioManager(Game game) {
            this.game = game;
        }

        public void update(float deltaTime) {
            List<EngineVariable> playingAudios = new ArrayList<>(game.getPlayingAudios());
            for (EngineVariable ev : playingAudios) {
                if (ev instanceof SoundVariable sound) {
                    sound.update();
                } else if (ev instanceof pl.genschu.bloomooemulator.interpreter.v1.variable.types.SoundVariable v1Sound) {
                    v1Sound.update();
                }
            }
        }

        @Override
        public void dispose() {
            // Implementation not needed
        }
    }
}
