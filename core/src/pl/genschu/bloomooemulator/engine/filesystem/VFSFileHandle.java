package pl.genschu.bloomooemulator.engine.filesystem;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.GdxRuntimeException;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * libGDX FileHandle that delegates IO to a VFS instance.
 *
 * The super-constructor is invoked with a placeholder File so that
 * libGDX internals which read {@code file()}/{@code type()} directly
 * (e.g. some asset loaders) don't NPE. All real IO goes through VFS.
 */
public class VFSFileHandle extends FileHandle {
    private final VFS vfs;
    private final String vfsPath;

    public VFSFileHandle(VFS vfs, String path) {
        super(new File(path), FileType.Absolute);
        this.vfs = vfs;
        this.vfsPath = path;
    }

    @Override
    public InputStream read() {
        try {
            return vfs.openRead(vfsPath);
        } catch (Exception e) {
            throw new GdxRuntimeException("Error reading from VFS: " + vfsPath, e);
        }
    }

    @Override
    public OutputStream write(boolean append) {
        if (append) throw new UnsupportedOperationException("Append not supported in VFS");
        try {
            return vfs.openWrite(vfsPath);
        } catch (Exception e) {
            throw new GdxRuntimeException("Error writing to VFS: " + vfsPath, e);
        }
    }

    @Override
    public boolean exists() {
        return vfs.exists(vfsPath);
    }

    @Override
    public long length() {
        return vfs.length(vfsPath);
    }

    @Override
    public String path() {
        return vfsPath;
    }

    @Override
    public String name() {
        int slash = Math.max(vfsPath.lastIndexOf('/'), vfsPath.lastIndexOf('\\'));
        return slash < 0 ? vfsPath : vfsPath.substring(slash + 1);
    }

    @Override
    public String extension() {
        String name = name();
        int dot = name.lastIndexOf('.');
        return dot < 0 ? "" : name.substring(dot + 1);
    }

    @Override
    public String nameWithoutExtension() {
        String name = name();
        int dot = name.lastIndexOf('.');
        return dot < 0 ? name : name.substring(0, dot);
    }
}
