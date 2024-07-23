package pl.genschu.bloomooemulator.utils;

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

        String[] pathSegments = platformPath.split(File.separator.equals("\\") ? "\\\\" : File.separator);

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
}
