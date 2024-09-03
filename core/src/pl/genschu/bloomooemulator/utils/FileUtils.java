package pl.genschu.bloomooemulator.utils;

import com.badlogic.gdx.Gdx;
import pl.genschu.bloomooemulator.interpreter.variable.Variable;

import java.io.File;

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

        String[] pathSegments = platformPath.split(File.separator.equals("\\") ? "\\\\" : "/");

        return findFileIgnoreCase(baseDirectory, pathSegments, 0);
    }

    private static File findFileIgnoreCase(File directory, String[] pathSegments, int index) {
        if (index >= pathSegments.length) {
            return directory;
        }

        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.getName().equalsIgnoreCase(pathSegments[index])) {
                    return findFileIgnoreCase(file, pathSegments, index + 1);
                }
            }
        }

        return null;
    }

    public static String resolveRelativePath(Variable variable) {
        String filePath = variable.getAttribute("FILENAME").getValue().toString();

        return resolveRelativePath(variable, filePath);
    }

    public static String resolveRelativePath(Variable variable, String filePath) {
        if(filePath.startsWith("$")) {
            if(filePath.contains("$COMMON"))
                filePath = filePath.replace("$COMMON", variable.getContext().getGame().getCommonFolder().getAbsolutePath());
            else if(filePath.contains("$WAVS"))
                filePath = filePath.replace("$WAVS", variable.getContext().getGame().getWavsFolder().getAbsolutePath());
            else
                filePath = filePath.replace("$", variable.getContext().getGame().getDaneFolder().getParentFile().getAbsolutePath());
        }
        else {
            // probably is relative path
            if(variable.getContext().getGame().getCurrentSceneFile() != null)
                filePath = variable.getContext().getGame().getCurrentSceneFile().getAbsolutePath() + "/" + filePath;
            else if(variable.getContext().getGame().getCurrentEpisodeFile() != null)
                filePath = variable.getContext().getGame().getCurrentEpisodeFile().getAbsolutePath() + "/" + filePath;
            else if(variable.getContext().getGame().getCurrentApplicationFile() != null)
                filePath = variable.getContext().getGame().getCurrentApplicationFile().getAbsolutePath() + "/" + filePath;
            else
                filePath = variable.getContext().getGame().getDaneFolder().getAbsolutePath() + "/" + filePath;
        }

        return convertToPlatformPath(filePath);
    }
}
