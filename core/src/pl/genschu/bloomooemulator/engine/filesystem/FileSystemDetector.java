package pl.genschu.bloomooemulator.engine.filesystem;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.zip.ZipFile;

public class FileSystemDetector {
    private static final int SECTOR_SIZE = 2048;

    public static FileSystemType detectFileSystemType(File path) throws IOException {
        if(path.isDirectory()) {
            return FileSystemType.DIRECTORY;
        }

        try (RandomAccessFile raf = new RandomAccessFile(path, "r")) {
            if (isIso9660(raf)) {
                return FileSystemType.ISO9660;
            }

            if (isUdf(raf)) {
                return FileSystemType.UDF;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (isValidZip(path)) {
            return FileSystemType.ZIP;
        }

        throw new IOException(
                "Unsupported game asset source: " + path
        );
    }

    private static boolean hasSignature(
            RandomAccessFile raf,
            long offset,
            String signature
    ) throws IOException {
        if (raf.length() < offset + signature.length()) {
            return false;
        }

        byte[] buf = new byte[signature.length()];

        raf.seek(offset);
        raf.readFully(buf);

        return signature.equals(new String(buf, StandardCharsets.US_ASCII));
    }

    private static boolean isIso9660(RandomAccessFile raf) throws IOException {
        // Sector 16 + descriptor ID offset 1
        return hasSignature(
                raf,
                16L * SECTOR_SIZE + 1,
                "CD001"
        );
    }

    private static boolean isUdf(RandomAccessFile raf) throws IOException {
        final int maxSectorsToScan = 32;
        final long startSector = 16;

        for (int i = 0; i < maxSectorsToScan; i++) {
            long sectorOffset = (startSector + i) * SECTOR_SIZE;

            if (hasSignature(raf, sectorOffset + 1, "NSR02") ||
                    hasSignature(raf, sectorOffset + 1, "NSR03")) {
                return true;
            }
        }

        return false;
    }

    public static boolean isValidZip(File file) {
        try (ZipFile ignored = new ZipFile(file)) {
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
