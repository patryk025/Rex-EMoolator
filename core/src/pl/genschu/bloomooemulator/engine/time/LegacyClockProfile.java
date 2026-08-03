package pl.genschu.bloomooemulator.engine.time;

import java.util.Locale;

/**
 * Presets for the observable granularity of the original {@code GetTickCount}
 * clock. They describe compatibility targets, not the operating system on
 * which Rex-EMoolator itself is running.
 */
public enum LegacyClockProfile {
    WINDOWS_64_HZ("Windows 11 / 64 Hz (15.625 ms)", 15_625_000L),
    WINDOWS_100_HZ("Windows XP / 100 Hz (10 ms)", 10_000_000L),
    EMULATOR_200_HZ("QEMU / 86Box measured in Windows 98 (5 ms)", 5_000_000L),
    WINDOWS_18_2_HZ("Windows 9x nominal (54.925 ms)", 54_925_400L),
    ONE_MILLISECOND("1 ms", 1_000_000L);

    private static final LegacyClockProfile DEFAULT = WINDOWS_64_HZ;

    private final String displayName;
    private final long quantumNanos;

    LegacyClockProfile(String displayName, long quantumNanos) {
        this.displayName = displayName;
        this.quantumNanos = quantumNanos;
    }

    public String displayName() {
        return displayName;
    }

    public long quantumNanos() {
        return quantumNanos;
    }

    public static LegacyClockProfile defaultProfile() {
        return DEFAULT;
    }

    public static LegacyClockProfile fromStored(String value) {
        if (value == null || value.isBlank()) {
            return DEFAULT;
        }
        String normalized = value.trim().toUpperCase(Locale.ROOT);
        for (LegacyClockProfile profile : values()) {
            if (profile.name().equals(normalized)) {
                return profile;
            }
        }
        return DEFAULT;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
