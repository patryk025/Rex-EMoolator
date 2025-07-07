package pl.genschu.bloomooemulator.engine.debug;

import com.badlogic.gdx.Gdx;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class PerformanceMonitor {
    private static final Map<String, Long> startTimes = new HashMap<>();
    private static final Map<String, Long> totalTimes = new HashMap<>();
    private static final Map<String, Integer> counts = new HashMap<>();

    public static void startOperation(String operationName) {
        startTimes.put(operationName, System.nanoTime());
    }

    public static void endOperation(String operationName) {
        Long startTime = startTimes.get(operationName);
        if (startTime != null) {
            long duration = System.nanoTime() - startTime;
            totalTimes.put(operationName, totalTimes.getOrDefault(operationName, 0L) + duration);
            counts.put(operationName, counts.getOrDefault(operationName, 0) + 1);

            // Log do konsoli
            Gdx.app.log("PerformanceMonitor", String.format(Locale.getDefault(), "%s took %.2f ms",
                    operationName, duration / 1_000_000.0));
        }
    }

    public static String printStats() {
        StringBuilder stats = new StringBuilder("=== Performance Statistics ===\n");
        totalTimes.entrySet().stream()
                .sorted((e1, e2) -> Double.compare(
                        ((double) e2.getValue() / counts.get(e2.getKey())),
                        ((double) e1.getValue() / counts.get(e1.getKey()))
                ))
                .forEach(entry -> {
                    long total = entry.getValue();
                    int count = counts.get(entry.getKey());
                    double avgMs = ((double) total / count) / 1_000_000.0;
                    stats.append(String.format(Locale.getDefault(), "%s: avg %.2f ms, total %.2f ms\n",
                            entry.getKey(), avgMs, total / 1_000_000.0));
                });
        return stats.toString();
    }
}
