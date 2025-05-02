package pl.genschu.bloomooemulator.utils;

import com.badlogic.gdx.Gdx;
import pl.genschu.bloomooemulator.interpreter.variable.Variable;
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

    public static String resolveRelativePath(Variable variable) {
        String filePath = variable.getAttribute("FILENAME").getString();
        return resolveRelativePath(variable, filePath);
    }

    public static String resolveRelativePath(Variable variable, String filePath) {
        File resolvedFile;

        if (filePath.startsWith("$")) {
            if (filePath.contains("$COMMON")) {
                resolvedFile = variable.getContext().getGame().getCommonFolder();
                filePath = filePath.replace("$COMMON", "");
            } else if (filePath.contains("$WAVS")) {
                resolvedFile = variable.getContext().getGame().getWavsFolder();
                filePath = filePath.replace("$WAVS", "");
            } else {
                resolvedFile = variable.getContext().getGame().getDaneFolder().getParentFile();
                filePath = filePath.replace("$", "");
            }
            filePath = filePath.trim();
            if (!filePath.isEmpty()) {
                resolvedFile = new File(resolvedFile, convertToPlatformPath(filePath));
            }
        } else {
            if (variable.getContext().getGame().getCurrentSceneFile() != null) {
                resolvedFile = variable.getContext().getGame().getCurrentSceneFile();
            } else if (variable.getContext().getGame().getCurrentEpisodeFile() != null) {
                resolvedFile = variable.getContext().getGame().getCurrentEpisodeFile();
            } else if (variable.getContext().getGame().getCurrentApplicationFile() != null) {
                resolvedFile = variable.getContext().getGame().getCurrentApplicationFile();
            } else {
                resolvedFile = variable.getContext().getGame().getDaneFolder();
            }
            resolvedFile = new File(resolvedFile, convertToPlatformPath(filePath));
        }

        File finalFile = findRelativeFileIgnoreCase(resolvedFile.getParentFile(), resolvedFile.getName());
        return finalFile != null ? finalFile.getAbsolutePath() : resolvedFile.getAbsolutePath();
    }
}