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
     * Finds a file with language fallback mechanism.
     *
     * Search order:
     * 1. baseDirectory/{langCode}/{relativePath}
     * 2. baseDirectory/{relativePath}
     *
     * @param baseDirectory Base directory to search in
     * @param relativePath Relative path to the file
     * @param langCode Language code (e.g., "POL", "ENG", "DEU") - can be null
     * @return File if found, null otherwise
     */
    public static File findRelativeFileWithLanguageFallback(File baseDirectory, String relativePath, String langCode) {
        if (langCode != null && !langCode.isEmpty()) {
            // Try language-specific directory first
            String langPath = langCode + File.separator + relativePath;
            File langFile = findRelativeFileIgnoreCase(baseDirectory, langPath);

            if (langFile != null && langFile.exists()) {
                Gdx.app.log("FileUtils", "Found language-specific file: " + langFile.getAbsolutePath());
                return langFile;
            }
        }

        // Fallback to base directory
        File baseFile = findRelativeFileIgnoreCase(baseDirectory, relativePath);
        if (baseFile != null && baseFile.exists()) {
            Gdx.app.debug("FileUtils", "Using base file: " + baseFile.getAbsolutePath());
        }

        return baseFile;
    }

    /**
     * Translates a script path into a VFS-relative one.
     *
     * The {@code $} prefix denotes the game's installation root; everything
     * after it is the VFS path. Anything else passes through (with separator
     * normalization) and is interpreted relative to the VFS root.
     */
    public static String resolveVfsPath(Game game, String filePath) {
        if (filePath == null || filePath.isEmpty()) return null;
        String path = filePath;
        if (path.startsWith("$")) {
            path = path.substring(1).replaceFirst("^[/\\\\]+", "");
        }
        return path.replace('\\', '/');
    }

    public static String resolveRelativePath(Game game, String filePath) {
        File baseDirectory;
        String relativePath = filePath;

        // Handle $ prefix
        if (filePath.startsWith("$")) {
            // $ always maps to installation root (parent of dane folder)
            baseDirectory = game.getDaneFolder().getParentFile();

            // Remove $ and optional separator
            relativePath = filePath.substring(1).replaceFirst("^[/\\\\]+", "");
        } else {
            // Determine base directory based on current context
            if (game.getCurrentSceneFile() != null) {
                baseDirectory = game.getCurrentSceneFile();
            } else if (game.getCurrentEpisodeFile() != null) {
                baseDirectory = game.getCurrentEpisodeFile();
            } else if (game.getCurrentApplicationFile() != null) {
                File currentApplicationFile = game.getCurrentApplicationFile();
                baseDirectory = currentApplicationFile.isDirectory()
                        ? currentApplicationFile
                        : currentApplicationFile.getParentFile();
            } else {
                baseDirectory = game.getDaneFolder();
            }
        }

        // Get language code from game
        String langCode = null;
        try {
            langCode = game.getLanguage();
        } catch (NullPointerException ignored) {
            // Game or language not available, skip language fallback
        }

        // Use language fallback mechanism
        if (!relativePath.isEmpty()) {
            File finalFile = findRelativeFileWithLanguageFallback(baseDirectory, relativePath, langCode);
            return finalFile != null ? finalFile.getAbsolutePath() :
                   new File(baseDirectory, convertToPlatformPath(relativePath)).getAbsolutePath();
        }

        return baseDirectory.getAbsolutePath();
    }
}
