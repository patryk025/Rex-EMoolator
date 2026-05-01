package pl.genschu.bloomooemulator.engine.filesystem;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Locale;

/**
 * Resolves a VFS-backed audio asset to a {@link FileHandle} that libGDX
 * audio APIs can consume on every backend.
 *
 * The libGDX Android backend casts the {@link FileHandle} passed to
 * {@code Gdx.audio.newSound}/{@code newMusic} to {@code AndroidFileHandle}
 * and feeds it to {@code SoundPool}/{@code MediaPlayer}, which require a
 * real file or asset descriptor. {@link VFSFileHandle} satisfies neither
 * (its bytes may live in an ISO image, a non-asset directory, etc.). To
 * bridge that gap on Android, we materialize VFS bytes once into a stable
 * cache file under {@code audio_cache/<namespace>/} and return its real
 * handle.
 *
 * On non-Android backends (e.g. desktop OpenAL) audio loaders read through
 * {@link FileHandle#read()}, so the VFS handle is passed through unchanged.
 */
public final class AudioFileResolver {
    private static final String CACHE_ROOT = "audio_cache";
    private static final int COPY_BUFFER = 16 * 1024;
    private static final Object CACHE_LOCK = new Object();

    private AudioFileResolver() {}

    /**
     * @param vfs            the VFS to read source bytes from
     * @param cacheNamespace per-game subdirectory key (typically the game's
     *                       storage UUID) so unrelated games can't collide
     *                       on a shared relative path
     * @param vfsPath        VFS-relative asset path
     */
    public static FileHandle resolveForPlayback(VFS vfs, String cacheNamespace, String vfsPath) {
        if (Gdx.app == null || Gdx.app.getType() != Application.ApplicationType.Android) {
            return vfs.getFileHandle(vfsPath);
        }

        long expectedLength = vfs.length(vfsPath);
        FileHandle cached = Gdx.files.local(CACHE_ROOT + "/" + cacheNamespace + "/" + cacheKey(vfsPath, expectedLength));
        if (cached.exists() && cached.length() == expectedLength) {
            return cached;
        }

        synchronized (CACHE_LOCK) {
            if (cached.exists() && cached.length() == expectedLength) {
                return cached;
            }
            try (InputStream in = vfs.openRead(vfsPath);
                 OutputStream out = cached.write(false)) {
                byte[] buffer = new byte[COPY_BUFFER];
                int read;
                while ((read = in.read(buffer)) > 0) {
                    out.write(buffer, 0, read);
                }
            } catch (IOException e) {
                throw new RuntimeException("Failed to materialize audio asset: " + vfsPath, e);
            }
        }
        return cached;
    }

    private static String cacheKey(String vfsPath, long length) {
        String ext = "";
        int dot = vfsPath.lastIndexOf('.');
        if (dot >= 0) ext = vfsPath.substring(dot);
        // path.hashCode + length keeps the key short and invalidates on size change.
        return String.format(Locale.ROOT, "%08x_%d%s", vfsPath.hashCode(), length, ext);
    }
}
