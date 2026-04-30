package pl.genschu.bloomooemulator.utils;

import com.badlogic.gdx.Gdx;
import pl.genschu.bloomooemulator.engine.Game;

import java.io.File;

public class FileUtils {
    public static String convertToPlatformPath(String path) {
        return path.replace("/", File.separator).replace("\\", File.separator);
    }

    /**
     * Resolves {@code relativePath} under {@code baseDirectory} matching path
     * segments case-insensitively. Returns the literal (possibly non-existent)
     * file when no match is found, so callers can still report a sensible path.
     *
     * Implemented with {@link java.io.File} only — {@code java.nio.file} APIs
     * require Android API 26+, but the project targets API 24.
     */
    public static File findRelativeFileIgnoreCase(File baseDirectory, String relativePath) {
        String platformPath = convertToPlatformPath(relativePath);
        File literal = new File(baseDirectory, platformPath);
        if (literal.exists()) return literal;

        File current = baseDirectory;
        for (String segment : platformPath.split(java.util.regex.Pattern.quote(File.separator))) {
            if (segment.isEmpty()) continue;

            File[] entries = current.listFiles();
            if (entries == null) {
                Gdx.app.debug("FileUtils", "Cannot list directory: " + current);
                return null;
            }

            File match = null;
            for (File entry : entries) {
                if (entry.getName().equalsIgnoreCase(segment)) {
                    match = entry;
                    break;
                }
            }

            if (match == null) {
                Gdx.app.debug("FileUtils", "No case-insensitive match for: " + segment + " in " + current);
                return null;
            }
            current = match;
        }
        return current;
    }

    /**
     * Translates a script path into a VFS-relative one.
     *
     * The {@code $} prefix denotes the game's installation root. Paths without
     * it are resolved relative to the currently loaded application, episode or
     * scene directory, matching the original File-based resolver.
     */
    public static String resolveVfsPath(Game game, String filePath) {
        if (filePath == null || filePath.isEmpty()) return null;
        String path = filePath.replace('\\', '/');
        if (path.startsWith("$")) {
            return path.substring(1).replaceFirst("^/+", "");
        }

        path = path.replaceFirst("^/+", "");
        if (game == null) {
            return path;
        }

        String baseDirectory = game.getCurrentResourceDirectory();
        if (baseDirectory == null || baseDirectory.isEmpty()) {
            baseDirectory = "";
        }
        baseDirectory = baseDirectory.replace('\\', '/').replaceFirst("/+$", "");

        String language = game.getLanguage();
        if (language != null && !language.isEmpty()) {
            String localizedPath = joinVfsPath(baseDirectory, language, path);
            if (game.getVfs().exists(localizedPath)) {
                return localizedPath;
            }
        }

        String resolvedPath = joinVfsPath(baseDirectory, path);
        if (game.getVfs().exists(resolvedPath)) {
            return resolvedPath;
        }
        return resolvedPath;
    }

    private static String joinVfsPath(String first, String... rest) {
        StringBuilder joined = new StringBuilder(first.replace('\\', '/').replaceFirst("/+$", ""));
        for (String segment : rest) {
            if (segment == null || segment.isEmpty()) continue;
            if (!joined.isEmpty()) joined.append('/');
            joined.append(segment.replace('\\', '/').replaceFirst("^/+", "").replaceFirst("/+$", ""));
        }
        return joined.toString();
    }

}
