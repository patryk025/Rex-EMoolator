package pl.genschu.bloomooemulator.utils;

import com.badlogic.gdx.Gdx;
import pl.genschu.bloomooemulator.engine.Game;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileUtils {
    public static String convertToPlatformPath(String path) {
        return path.replace("/", File.separator).replace("\\", File.separator);
    }

    public static File findRelativeFileIgnoreCase(File baseDirectory, String relativePath) {
        String platformPath = convertToPlatformPath(relativePath);
        File targetFile = new File(baseDirectory, platformPath);

        if (targetFile.exists()) {
            return targetFile;
        }

        Path basePath = baseDirectory.toPath();
        Path resolvedPath = resolveCaseInsensitive(basePath, Paths.get(platformPath));

        return resolvedPath != null ? resolvedPath.toFile() : null;
    }

    private static Path resolveCaseInsensitive(Path basePath, Path relativePath) {
        Path currentPath = basePath.normalize();

        for (Path segment : relativePath.normalize()) {
            if (segment.toString().isEmpty()) {
                continue;
            }

            try {
                Path finalCurrentPath = currentPath;
                Path matchedPath = Files.list(currentPath)
                        .filter(p -> p.getFileName().toString().equalsIgnoreCase(segment.toString()))
                        .findFirst()
                        .orElse(null);

                if (matchedPath == null || !Files.exists(matchedPath)) {
                    Gdx.app.debug("FileUtils", "No case-insensitive match for: " + segment + " in " + finalCurrentPath);
                    return null;
                }

                currentPath = matchedPath;
            } catch (Exception e) {
                Gdx.app.error("FileUtils", "Error listing directory: " + currentPath, e);
                return null;
            }
        }

        return currentPath;
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
