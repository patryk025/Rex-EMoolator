package pl.genschu.bloomooemulator.engine.debug;

import com.badlogic.gdx.Gdx;

import java.util.*;

public class PerformanceMonitor {
    private static final Map<String, Long> startTimes = new HashMap<>();
    private static final Map<String, Long> durationTimes = new HashMap<>();
    private static final Map<String, Deque<Long>> recentDurations = new HashMap<>();
    private static final int MAX_SAMPLES = 100;

    public static void startOperation(String operationName) {
        startTimes.put(operationName, System.nanoTime());
    }

    public static void endOperation(String operationName) {
        Long startTime = startTimes.get(operationName);
        if (startTime != null) {
            long duration = System.nanoTime() - startTime;
            durationTimes.put(operationName, duration);

            Deque<Long> durations = recentDurations.computeIfAbsent(operationName, k -> new ArrayDeque<>());
            if (durations.size() >= MAX_SAMPLES) {
                durations.pollFirst();
            }
            durations.addLast(duration);
        }
    }

    public static String printStats() {
        StringBuilder stats = new StringBuilder("=== Performance Statistics ===\n");

        recentDurations.entrySet().stream()
                .filter(entry -> entry.getKey().startsWith("Render - "))
                .forEach(entry -> {
                    String name = entry.getKey();
                    float current = durationTimes.getOrDefault(name, 0L) / 1_000_000.0f;
                    double avgMs = average(entry.getValue()) / 1_000_000.0;
                    if (name.contains("frame time")) {
                        stats.append(String.format(Locale.getDefault(), "%s: current %.2f ms (%.2f FPS)\n",
                                name, current, 1.0f / (current / 1000.0f)));
                        stats.append(String.format(Locale.getDefault(), "%s: avg %.2f ms (%.2f FPS)\n",
                                name, avgMs, 1.0f / (avgMs / 1000.0f)));
                    } else {
                        stats.append(String.format(Locale.getDefault(), "%s: current %.2f ms\n", name, current));
                        stats.append(String.format(Locale.getDefault(), "%s: avg %.2f ms\n", name, avgMs));
                    }
                });

        recentDurations.entrySet().stream()
                .filter(entry -> !entry.getKey().startsWith("Render - "))
                .sorted((e1, e2) -> Double.compare(
                        average(e2.getValue()),
                        average(e1.getValue())
                ))
                .forEach(entry -> {
                    String name = entry.getKey();
                    double avgMs = average(entry.getValue()) / 1_000_000.0;
                    double totalMs = sum(entry.getValue()) / 1_000_000.0;
                    stats.append(String.format(Locale.getDefault(), "%s: avg %.2f ms, recent total %.2f ms\n",
                            name, avgMs, totalMs));
                });

        return stats.toString();
    }

    private static double average(Deque<Long> values) {
        if (values.isEmpty()) return 0;
        return sum(values) / (double) values.size();
    }

    private static long sum(Deque<Long> values) {
        long total = 0;
        for (long v : values) total += v;
        return total;
    }
}
