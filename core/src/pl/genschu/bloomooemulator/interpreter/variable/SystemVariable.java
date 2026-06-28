package pl.genschu.bloomooemulator.interpreter.variable;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import oshi.SystemInfo;
import pl.genschu.bloomooemulator.interpreter.values.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;

/**
 * SystemVariable provides system-level information (date, CPU, uptime).
 * This is a global variable — not bound to a scene context.
 *
 * <p>CPU/OS facts come from OSHI on desktop. OSHI's Linux backend loads native
 * code via JNA ({@code libudev}), which is absent on Android and throws
 * {@link NoClassDefFoundError}/{@link UnsatisfiedLinkError}. On Android we never
 * touch OSHI and read the same facts straight from the kernel pseudo-filesystems
 * ({@code /sys}, {@code /proc}), which are plain file reads.
 */
public record SystemVariable(
    String name,
    Map<String, SignalHandler> signals
) implements Variable {

    /** Monotonic origin for the last-resort uptime fallback. */
    private static final long START_NANOS = System.nanoTime();

    /** Cached CPU max frequency (MHz); 0 = not yet resolved. CPU freq is constant. */
    private static volatile int cachedMhz = 0;

    public SystemVariable {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Variable name cannot be null or empty");
        }
        if (signals == null) {
            signals = Map.of();
        } else {
            signals = Map.copyOf(signals);
        }
    }

    public SystemVariable(String name) {
        this(name, Map.of());
    }

    @Override
    public Value value() {
        return NullValue.INSTANCE;
    }

    @Override
    public VariableType type() {
        return VariableType.SYSTEM;
    }

    @Override
    public Variable withValue(Value newValue) {
        return this;
    }

    @Override
    public Map<String, MethodSpec> methods() {
        return METHODS;
    }

    @Override
    public Variable withSignal(String signalName, SignalHandler handler) {
        Map<String, SignalHandler> newSignals = new HashMap<>(signals);
        if (handler != null) {
            newSignals.put(signalName, handler);
        } else {
            newSignals.remove(signalName);
        }
        return new SystemVariable(name, newSignals);
    }

    private static final Map<String, MethodSpec> METHODS = Map.ofEntries(
        Map.entry("GETDATE", MethodSpec.of((self, args, ctx) -> {
            Calendar calendar = Calendar.getInstance();
            // date = (year-2000) * 10000 + month * 100 + day
            int date = (calendar.get(Calendar.YEAR) - 2000) * 10000
                    + (calendar.get(Calendar.MONTH) + 1) * 100
                    + calendar.get(Calendar.DAY_OF_MONTH);
            return MethodResult.returns(new IntValue(date));
        })),

        Map.entry("GETMHZ", MethodSpec.of((self, args, ctx) ->
            MethodResult.returns(new IntValue(getCpuMhz()))
        )),

        Map.entry("GETSYSTEMTIME", MethodSpec.of((self, args, ctx) -> {
            int uptimeInSeconds = (int) getUptimeSeconds();
            int milliseconds = (int) (new Date().getTime() % 1000);
            return MethodResult.returns(new IntValue(uptimeInSeconds * 1000 + milliseconds));
        }))
    );

    // ========================================
    // SYSTEM INFO (platform-aware, OSHI-free on Android)
    // ========================================

    private static boolean isAndroid() {
        return Gdx.app != null && Gdx.app.getType() == Application.ApplicationType.Android;
    }

    /** CPU max frequency in MHz. Memoized (it doesn't change at runtime). */
    private static int getCpuMhz() {
        int cached = cachedMhz;
        if (cached != 0) {
            return cached;
        }
        int mhz = 0;
        if (!isAndroid()) {
            try {
                long hz = new SystemInfo().getHardware().getProcessor().getMaxFreq();
                if (hz > 0) mhz = (int) (hz / 1_000_000);
            } catch (Throwable ignored) {
                // OSHI/JNA unavailable — fall through to the kernel/constant path.
            }
        }
        if (mhz <= 0) mhz = readSysCpuMaxMhz();
        if (mhz <= 0) mhz = 1000; // plausible default when nothing reports a frequency
        cachedMhz = mhz;
        return mhz;
    }

    /** Reads cpu0's max frequency from the Linux/Android cpufreq node (kHz → MHz). */
    private static int readSysCpuMaxMhz() {
        try (BufferedReader r = new BufferedReader(new FileReader(
                "/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq"))) {
            String line = r.readLine();
            if (line != null) {
                long khz = Long.parseLong(line.trim());
                if (khz > 0) return (int) (khz / 1000);
            }
        } catch (Throwable ignored) {
            // No cpufreq node (e.g. Windows desktop) — caller falls back further.
        }
        return -1;
    }

    /** OS uptime in seconds. */
    private static long getUptimeSeconds() {
        if (!isAndroid()) {
            try {
                long up = new SystemInfo().getOperatingSystem().getSystemUptime();
                if (up > 0) return up;
            } catch (Throwable ignored) {
                // OSHI/JNA unavailable — fall through.
            }
        }
        long proc = readProcUptimeSeconds();
        if (proc >= 0) return proc;
        // Last resort: monotonic time since this class loaded.
        return (System.nanoTime() - START_NANOS) / 1_000_000_000L;
    }

    /** Reads whole-system uptime (first field of /proc/uptime, in seconds). */
    private static long readProcUptimeSeconds() {
        try (BufferedReader r = new BufferedReader(new FileReader("/proc/uptime"))) {
            String line = r.readLine();
            if (line != null) {
                int sp = line.indexOf(' ');
                String first = sp < 0 ? line : line.substring(0, sp);
                return (long) Double.parseDouble(first.trim());
            }
        } catch (Throwable ignored) {
            // No /proc (e.g. Windows desktop) — caller falls back further.
        }
        return -1;
    }

    @Override
    public String toString() {
        return "SystemVariable[" + name + "]";
    }
}
