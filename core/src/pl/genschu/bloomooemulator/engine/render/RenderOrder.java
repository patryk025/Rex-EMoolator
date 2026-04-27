package pl.genschu.bloomooemulator.engine.render;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Tracks same-priority ordering for display objects.
 */
public final class RenderOrder {
    private static final AtomicLong NEXT = new AtomicLong();

    private RenderOrder() {}

    public static long next() {
        return NEXT.incrementAndGet();
    }
}
