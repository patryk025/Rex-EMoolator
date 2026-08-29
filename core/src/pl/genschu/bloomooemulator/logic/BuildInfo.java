package pl.genschu.bloomooemulator.logic;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Build metadata stamped in by the {@code generateBuildInfo} Gradle task.
 *
 * <p>A release build carries the tag it was built from; a build made from a git
 * clone carries a nightly stamp (commit date plus short hash) so it cannot be
 * mistaken for a release. A build with neither - a source archive without
 * {@code .git}, or a classpath assembled by hand - reports {@code "dev"}.
 */
public final class BuildInfo {
    private static final String RESOURCE = "/rex-emoolator-build.properties";

    private static final String CHANNEL;
    private static final String VERSION;
    private static final String COMMIT;
    private static final String COMMIT_DATE;
    private static final boolean DIRTY;

    static {
        Properties properties = new Properties();
        try (InputStream is = BuildInfo.class.getResourceAsStream(RESOURCE)) {
            if (is != null) {
                properties.load(is);
            }
        } catch (IOException ignored) {
            // Metadata is cosmetic - a build without it still runs.
        }
        CHANNEL = properties.getProperty("channel", "dev");
        VERSION = properties.getProperty("version", "");
        COMMIT = properties.getProperty("commit", "");
        COMMIT_DATE = properties.getProperty("commitDate", "");
        DIRTY = Boolean.parseBoolean(properties.getProperty("dirty", "false"));
    }

    private BuildInfo() {}

    /** {@code "release"}, {@code "nightly"} or {@code "dev"}. */
    public static String channel() {
        return CHANNEL;
    }

    /** Raw stamp: a release tag, {@code git describe} output, or the Gradle version. */
    public static String version() {
        return VERSION;
    }

    /** Short commit hash, or an empty string when the build had no git checkout. */
    public static String commit() {
        return COMMIT;
    }

    /** Commit date as {@code yyyy-MM-dd}, or an empty string. */
    public static String commitDate() {
        return COMMIT_DATE;
    }

    /** Whether the checkout had uncommitted changes when the build ran. */
    public static boolean dirty() {
        return DIRTY;
    }

    /**
     * Human-readable version for the startup screen and about boxes:
     * {@code "v0.4.2"}, {@code "nightly 2026-08-29 (d1c477b)"} or {@code "dev"}.
     */
    public static String displayVersion() {
        String suffix = DIRTY ? "+local" : "";
        switch (CHANNEL) {
            case "release":
                return VERSION.isEmpty() ? "dev" : VERSION + suffix;
            case "nightly":
                if (COMMIT_DATE.isEmpty() || COMMIT.isEmpty()) {
                    return (VERSION.isEmpty() ? "nightly" : "nightly " + VERSION) + suffix;
                }
                return "nightly " + COMMIT_DATE + " (" + COMMIT + ")" + suffix;
            default:
                return "dev";
        }
    }
}
