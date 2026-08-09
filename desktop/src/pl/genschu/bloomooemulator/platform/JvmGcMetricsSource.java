package pl.genschu.bloomooemulator.platform;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.util.List;
import java.util.function.ToLongFunction;

/** Reads cumulative HotSpot/OpenJDK GC and allocation counters. */
public final class JvmGcMetricsSource implements GcMetricsSource {
    private final List<GarbageCollectorMXBean> garbageCollectors =
            ManagementFactory.getGarbageCollectorMXBeans();
    private final com.sun.management.ThreadMXBean allocationBean;

    public JvmGcMetricsSource() {
        com.sun.management.ThreadMXBean candidate = null;
        if (ManagementFactory.getThreadMXBean() instanceof com.sun.management.ThreadMXBean bean
                && bean.isThreadAllocatedMemorySupported()) {
            try {
                if (!bean.isThreadAllocatedMemoryEnabled()) {
                    bean.setThreadAllocatedMemoryEnabled(true);
                }
                candidate = bean;
            } catch (RuntimeException ignored) {
                // A restricted runtime may expose the bean but forbid enabling it.
            }
        }
        allocationBean = candidate;
    }

    @Override
    public Sample sample() {
        long allocatedBytes = Sample.UNAVAILABLE;
        if (allocationBean != null) {
            long value = allocationBean.getTotalThreadAllocatedBytes();
            if (value >= 0L) {
                allocatedBytes = value;
            }
        }

        return new Sample(
                sumAvailable(GarbageCollectorMXBean::getCollectionCount),
                sumAvailable(GarbageCollectorMXBean::getCollectionTime),
                allocatedBytes,
                Sample.UNAVAILABLE,
                Sample.UNAVAILABLE,
                Sample.UNAVAILABLE);
    }

    private long sumAvailable(ToLongFunction<GarbageCollectorMXBean> counter) {
        long total = 0L;
        boolean available = false;
        for (GarbageCollectorMXBean garbageCollector : garbageCollectors) {
            long value = counter.applyAsLong(garbageCollector);
            if (value >= 0L) {
                total += value;
                available = true;
            }
        }
        return available ? total : Sample.UNAVAILABLE;
    }
}
