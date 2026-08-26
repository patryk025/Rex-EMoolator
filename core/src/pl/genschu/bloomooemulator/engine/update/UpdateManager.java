package pl.genschu.bloomooemulator.engine.update;

import com.badlogic.gdx.utils.Disposable;
import pl.genschu.bloomooemulator.engine.Game;
import pl.genschu.bloomooemulator.engine.config.EngineConfig;
import pl.genschu.bloomooemulator.engine.context.EngineVariable;
import pl.genschu.bloomooemulator.engine.context.GameContext;
import pl.genschu.bloomooemulator.engine.time.LegacyClock;
import pl.genschu.bloomooemulator.interpreter.variable.*;
import pl.genschu.bloomooemulator.utils.CollisionChecker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Set;

public class UpdateManager implements Disposable {
    private final Game game;
    private final EngineConfig config;
    private final LegacyClock legacyClock;

    private final CollisionManager collisionManager;
    private final TimerManager timerManager;
    private final AnimationManager animationManager;
    private final AudioManager audioManager;

    public UpdateManager(Game game, EngineConfig config) {
        this.game = game;
        this.config = config;
        this.legacyClock = game.getLegacyClock();

        this.collisionManager = new CollisionManager(game);
        this.timerManager = new TimerManager(game);
        this.animationManager = new AnimationManager(game);
        this.audioManager = new AudioManager(game);
    }

    /** Performs one legacy engine pulse. Missed pulses are never replayed. */
    public void pulse() {
        updateTimers();
        updateAnimations();
        updateAudio();
        updateCollisions();
    }

    private void updateTimers() {
        timerManager.updateTimers(legacyClock);
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

    private void updateAnimations() {
        animationManager.updateAnimations(legacyClock);
    }

    private void updateAudio() {
        audioManager.update();
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

            // Existing partners must also be checked after objects move into unrelated
            // QuadTree branches, otherwise ONCOLLISIONFINISHED would never be emitted.
            Set<EngineVariable> candidates = Collections.newSetFromMap(new IdentityHashMap<>());
            candidates.addAll(game.getQuadTree().retrieve(new ArrayList<>(), object));
            candidates.addAll(game.getCollidingWith(object));

            for (EngineVariable other : candidates) {
                if (other == object) {
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

        public void updateTimers(LegacyClock clock) {
            GameContext context = game.getCurrentSceneContext();
            Set<TimerVariable.TimerState> seenStates =
                    Collections.newSetFromMap(new IdentityHashMap<>());
            for (EngineVariable variable : new ArrayList<>(context.getTimerVariablesForScheduling())) {
                if (variable instanceof TimerVariable candidate
                        && seenStates.add(candidate.state())) {
                    TimerVariable timer = resolveCanonicalTimer(context, candidate);
                    try {
                        timer.update(clock);
                    } catch (Exception ignored) {
                        // simple break, nothing special
                    }
                }
            }
        }

        private TimerVariable resolveCanonicalTimer(GameContext context, TimerVariable candidate) {
            EngineVariable resolved = context.getVariable(candidate.name());
            if (resolved instanceof TimerVariable timer && timer.state() == candidate.state()) {
                return timer;
            }
            return candidate;
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

        public void updateAnimations(LegacyClock clock) {
            GameContext context = game.getCurrentSceneContext();
            List<? extends EngineVariable> graphicsVariables =
                    new ArrayList<>(context.getGraphicsVariablesForScheduling());
            List<ScheduledAnimo> dueAnimations = new ArrayList<>();
            Set<AnimoVariable.AnimoPlaybackState> seen =
                    Collections.newSetFromMap(new IdentityHashMap<>());
            List<ScheduledKolorowanka> kolorowankas = new ArrayList<>();
            Set<KolorowankaVariable.KolorowankaState> seenKolorowankaStates =
                    Collections.newSetFromMap(new IdentityHashMap<>());
            // CAnimationManager takes one GetTickCount sample for the entire
            // due scan. Only a selected slot gets a second sample on commit.
            long observedTime = clock.nowMillis();

            for (EngineVariable variable : graphicsVariables) {
                if (variable instanceof AnimoVariable animoVariable
                        && seen.add(animoVariable.state())) {
                    // Registration is independent of PLAYING. A stopped object
                    // keeps ageing in CAnimationManager and may be due as soon
                    // as PLAY starts later.
                    animoVariable.registerAnimationClock(observedTime);
                    if (animoVariable.prepareAnimationTick(observedTime, clock)) {
                        // withSignal/ADDBEHAVIOUR can replace the immutable
                        // record wrapper while retaining this mutable logical
                        // playable. The scheduler slot belongs to the state,
                        // not to a transient wrapper instance.
                        dueAnimations.add(new ScheduledAnimo(animoVariable.name(), animoVariable.state()));
                    }
                } else if (variable instanceof KolorowankaVariable kolorowanka
                        && seenKolorowankaStates.add(kolorowanka.state())) {
                    kolorowankas.add(new ScheduledKolorowanka(
                            kolorowanka.name(), kolorowanka.state()));
                }
            }

            // The original manager selects every due playable before running
            // any Next callback. Re-check registration because an earlier
            // callback can unload/remove a later object.
            for (ScheduledAnimo due : dueAnimations) {
                GameContext current = game.getCurrentSceneContext();
                EngineVariable resolved = current.getVariable(due.name());
                if (resolved instanceof AnimoVariable canonical
                        && canonical.state() == due.state()) {
                    canonical.advancePreparedAnimationTick();
                    continue;
                }
                current.getGraphicsVariablesForScheduling().stream()
                        .filter(AnimoVariable.class::isInstance)
                        .map(AnimoVariable.class::cast)
                        .filter(candidate -> candidate.state() == due.state())
                        .findFirst()
                        .ifPresent(AnimoVariable::advancePreparedAnimationTick);
            }

            // KOLOROWANKA has its own tick/callback phase. Keeping it outside
            // the ANIMO due scan prevents a fade callback from changing which
            // later animations were selected in this pass.
            for (ScheduledKolorowanka scheduled : kolorowankas) {
                GameContext current = game.getCurrentSceneContext();
                KolorowankaVariable kolorowanka = resolveKolorowanka(current, scheduled);
                if (kolorowanka != null) {
                    kolorowanka.update(clock.nowMillis());
                }
            }
        }

        private KolorowankaVariable resolveKolorowanka(
                GameContext context,
                ScheduledKolorowanka scheduled
        ) {
            EngineVariable resolved = context.getVariable(scheduled.name());
            if (resolved instanceof KolorowankaVariable canonical
                    && canonical.state() == scheduled.state()) {
                return canonical;
            }
            return context.getGraphicsVariablesForScheduling().stream()
                    .filter(KolorowankaVariable.class::isInstance)
                    .map(KolorowankaVariable.class::cast)
                    .filter(candidate -> candidate.state() == scheduled.state())
                    .findFirst()
                    .orElse(null);
        }

        private record ScheduledAnimo(
                String name,
                AnimoVariable.AnimoPlaybackState state
        ) {}

        private record ScheduledKolorowanka(
                String name,
                KolorowankaVariable.KolorowankaState state
        ) {}

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

        public void update() {
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
