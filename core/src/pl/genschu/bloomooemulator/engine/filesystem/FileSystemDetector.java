package pl.genschu.bloomooemulator.engine.filesystem;

import pl.genschu.bloomooemulator.loader.helpers.SeekableBinaryReader;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class FileSystemDetector {
    private static final int SECTOR_SIZE = 2048;

    public static FileSystemType detectFileSystemType(File path) throws IOException {
        if (path.isDirectory()) {
            return FileSystemType.DIRECTORY;
        }
        return detectFileSystemType(new FileDataSource(path));
    }

    /**
     * Sniffs the container format straight off the bytes, so a source nested in
     * another mount is detected the same way a file on disk is.
     */
    public static FileSystemType detectFileSystemType(DataSource source) throws IOException {
        try (SeekableBinaryReader reader = source.openReader()) {
            if (isIso9660(reader)) {
                return FileSystemType.ISO9660;
            }

            if (isUdf(reader)) {
                return FileSystemType.UDF;
            }

            if (isZip(reader)) {
                return FileSystemType.ZIP;
            }
        }

        throw new IOException(
                "Unsupported game asset source: " + source.name()
        );
    }

    private static boolean hasSignature(
            SeekableBinaryReader reader,
            long offset,
            String signature
    ) throws IOException {
        if (reader.length() < offset + signature.length()) {
            return false;
        }

        reader.seek(offset);
        byte[] buf = reader.readBytes(signature.length());

        return signature.equals(new String(buf, StandardCharsets.US_ASCII));
    }

    private static boolean isIso9660(SeekableBinaryReader reader) throws IOException {
        // Sector 16 + descriptor ID offset 1
        return hasSignature(
                reader,
                16L * SECTOR_SIZE + 1,
                "CD001"
        );
    }

    private static boolean isUdf(SeekableBinaryReader reader) throws IOException {
        final int maxSectorsToScan = 32;
        final long startSector = 16;

        for (int i = 0; i < maxSectorsToScan; i++) {
            long sectorOffset = (startSector + i) * SECTOR_SIZE;

            if (hasSignature(reader, sectorOffset + 1, "NSR02") ||
                    hasSignature(reader, sectorOffset + 1, "NSR03")) {
                return true;
            }
        }

        return false;
    }

    /**
     * Local file header, or an empty/spanned archive. A full validity check
     * would need a real file; {@link ZipFileSystem} rejects what it cannot open.
     */
    private static boolean isZip(SeekableBinaryReader reader) throws IOException {
        return hasSignature(reader, 0, "PK\u0003\u0004")
                || hasSignature(reader, 0, "PK\u0005\u0006")
                || hasSignature(reader, 0, "PK\u0007\u0008");
    }
}
