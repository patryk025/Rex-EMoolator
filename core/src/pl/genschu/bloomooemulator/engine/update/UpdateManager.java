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
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Set;

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

        // check collisions for objects that moved since last frame
        updateCollisions();

        // update audio
        updateAudio(deltaTime);
    }

    private void updateTimers() {
        timerManager.updateTimers();
    }

    private void updateCollisions() {
        Set<EngineVariable> dirty = game.getDirtyCollisionObjects();
        if (dirty.isEmpty()) {
            return;
        }
        // Rebuild QuadTree once so all positions are consistent
        game.rebuildCollisionQuadTree();
        for (EngineVariable object : new ArrayList<>(dirty)) {
            collisionManager.checkCollisions(object);
        }
        dirty.clear();
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
            // Check if the object is still monitored
            if (!game.getCollisionMonitoredVariables().contains(object)) {
                return;
            }

            List<EngineVariable> potentialCollisions = game.getQuadTree().retrieve(new ArrayList<>(), object);

            // Deduplicate (QuadTree can return the same object from multiple nodes)
            Set<EngineVariable> seen = Collections.newSetFromMap(new IdentityHashMap<>());

            for (EngineVariable other : potentialCollisions) {
                if (other == object || !seen.add(other)) {
                    continue;
                }
                if (checkCollision(object, other)) {
                    if (!game.isColliding(object, other)) {
                        game.setColliding(object, other);
                        // Re-check: signal handler may have removed this object from monitoring
                        if (!game.getCollisionMonitoredVariables().contains(object)) {
                            return;
                        }
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
                    if (sound.update()) {
                        game.getPlayingAudios().remove(ev);
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
