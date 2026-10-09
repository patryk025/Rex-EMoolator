package pl.genschu.bloomooemulator.engine.update;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Tracks the order of slots in CAnimationManager. The original keeps them in a
 * CXArray that {@code CAnimationManager::add} appends to whenever a CAnimo is
 * created, and visits them in that order on every pass.
 */
public final class AnimationSlotOrder {
    private static final AtomicLong NEXT = new AtomicLong();

    private AnimationSlotOrder() {}

    public static long next() {
        return NEXT.incrementAndGet();
    }
}
