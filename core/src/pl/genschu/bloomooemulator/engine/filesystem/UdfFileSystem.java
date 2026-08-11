package pl.genschu.bloomooemulator.engine.filesystem;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class UdfFileSystem implements IFileSystem {
    private static final int SECTOR_SIZE = 2048;

    private final File isoFile;

    public UdfFileSystem(File isoFile) {
        if (isoFile == null) {
            throw new IllegalArgumentException("isoFile cannot be null");
        }
        this.isoFile = isoFile;
    }

    @Override
    public InputStream open(String path) throws IOException {
        return null;
    }

    @Override
    public boolean exists(String path) {
        return false;
    }

    @Override
    public boolean isDirectory(String path) {
        return false;
    }

    @Override
    public String[] list(String path) {
        return new String[0];
    }

    @Override
    public long length(String path) {
        return 0;
    }

    private int crc16CCITT(byte[] data, int offset, int length) {
        int crc = 0x0000;
        for (int i = offset; i < offset + length; i++) {
            crc ^= (data[i] & 0xFF) << 8;
            for (int j = 0; j < 8; j++) {
                if ((crc & 0x8000) != 0) {
                    crc = (crc << 1) ^ 0x1021;
                } else {
                    crc <<= 1;
                }
            }
        }
        return crc & 0xFFFF;
    }

    private record Entry(boolean directory, long offset, long length) {}
}
