package pl.genschu.bloomooemulator.patch;

import com.badlogic.gdx.utils.Json;
import com.github.junrar.Archive;
import com.github.junrar.exception.RarException;
import com.github.junrar.rarfile.FileHeader;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.function.Predicate;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Installs a patch described by a {@link PatchManifest} into the on-disk layout
 * {@link PatchManager} expects: {@code <patchesRoot>/<id>/patch.json} plus an
 * overlay tree under {@code <patchesRoot>/<id>/<filesRoot>}.
 *
 * <p>Supports ZIP and RAR payloads (detected by magic bytes, not extension).
 * The overlay {@linkplain #DEFAULT_FILTER filter} drops engine/installer
 * binaries that Aidem's "drop-into-the-game-folder" archives bundle alongside
 * the actual game data — the emulator provides its own engine, and a patched
 * {@code *.dll} has no business shadowing anything in the VFS.
 *
 * <p>Stateless; all entry points are static. Network and disk work is
 * synchronous, so callers should run installs off the render thread.
 */
public final class PatchInstaller {
    private PatchInstaller() {}

    /** Keeps every archive entry except engine/installer binaries. */
    public static final Predicate<String> DEFAULT_FILTER = path -> {
        String lower = path.toLowerCase(Locale.ROOT);
        if (lower.endsWith(".dll") || lower.endsWith(".exe")) {
            return false;
        }
        String base = lower.substring(lower.lastIndexOf('/') + 1);
        return !base.equals("install.ini");
    };

    private enum Format { ZIP, RAR, UNKNOWN }

    /**
     * Downloads {@code manifest.source} and installs it. Supports {@link PatchSourceType#URL}
     * (direct archive URL) and {@link PatchSourceType#GDRIVE} (Google Drive share link,
     * resolved via {@link GoogleDriveDownloader}). GITHUB remains out of scope.
     */
    public static InstalledPatch installFromSource(PatchManifest manifest, File patchesRoot) throws IOException {
        return installFromSource(manifest, patchesRoot, null);
    }

    /** {@link #installFromSource(PatchManifest, File)} with a download-progress callback. */
    public static InstalledPatch installFromSource(PatchManifest manifest, File patchesRoot,
                                                   DownloadProgress progress) throws IOException {
        PatchSource source = manifest == null ? null : manifest.getSource();
        if (source == null || source.getType() == null || source.getType() == PatchSourceType.LOCAL) {
            throw new IOException("Patch has no remote source to install from");
        }
        File archive;
        if (source.getType() == PatchSourceType.URL) {
            archive = download(source.getUrl(), patchesRoot, progress);
        } else if (source.getType() == PatchSourceType.GDRIVE) {
            archive = GoogleDriveDownloader.download(source.getUrl(), patchesRoot, progress);
        } else {
            throw new IOException("Unsupported source type for install: " + source.getType());
        }
        try {
            return installFromArchive(manifest, archive, patchesRoot);
        } finally {
            //noinspection ResultOfMethodCallIgnored
            archive.delete();
        }
    }

    public static InstalledPatch installFromArchive(PatchManifest manifest, File archive, File patchesRoot) throws IOException {
        return installFromArchive(manifest, archive, patchesRoot, DEFAULT_FILTER);
    }

    /**
     * Extracts {@code archive} into {@code <patchesRoot>/<id>/<filesRoot>} (replacing
     * any previous overlay), keeping only entries accepted by {@code keep}, then writes
     * the manifest as {@code patch.json}.
     *
     * @return the freshly installed patch, ready for {@link PatchManager#rescan()}.
     */
    public static InstalledPatch installFromArchive(PatchManifest manifest, File archive, File patchesRoot,
                                                    Predicate<String> keep) throws IOException {
        if (manifest == null || manifest.getId() == null || manifest.getId().isBlank()) {
            throw new IOException("Patch manifest has no id");
        }
        File patchDir = new File(patchesRoot, manifest.getId());
        File filesDir = new File(patchDir, manifest.getFilesRoot());
        deleteRecursively(filesDir);
        if (!filesDir.mkdirs() && !filesDir.isDirectory()) {
            throw new IOException("Cannot create overlay directory " + filesDir);
        }

        Predicate<String> filter = keep == null ? DEFAULT_FILTER : keep;
        switch (detectFormat(archive)) {
            case ZIP -> extractZip(archive, filesDir, filter);
            case RAR -> extractRar(archive, filesDir, filter);
            default -> throw new IOException("Unsupported archive format: " + archive.getName());
        }

        writeManifest(manifest, new File(patchDir, "patch.json"));
        return new InstalledPatch(manifest, patchDir);
    }

    /** Downloads {@code url} into a temp file under {@code destDir}. Caller deletes it. */
    public static File download(String url, File destDir) throws IOException {
        return download(url, destDir, null);
    }

    /** {@link #download(String, File)} reporting byte progress to {@code progress} (may be null). */
    public static File download(String url, File destDir, DownloadProgress progress) throws IOException {
        if (url == null || url.isBlank()) {
            throw new IOException("Empty source URL");
        }
        if (!destDir.isDirectory() && !destDir.mkdirs()) {
            throw new IOException("Cannot create " + destDir);
        }
        HttpURLConnection conn = (HttpURLConnection) URI.create(url).toURL().openConnection();
        conn.setInstanceFollowRedirects(true);
        conn.setConnectTimeout(15000);
        conn.setReadTimeout(60000);
        conn.setRequestProperty("User-Agent", "RexEMoolator");
        try {
            int code = conn.getResponseCode();
            if (code != HttpURLConnection.HTTP_OK) {
                throw new IOException("HTTP " + code + " downloading " + url);
            }
            long total = conn.getContentLengthLong();
            File out = File.createTempFile("patch-", ".archive", destDir);
            try (InputStream in = new BufferedInputStream(conn.getInputStream());
                 OutputStream os = new BufferedOutputStream(new FileOutputStream(out))) {
                copy(in, os, progress, total);
            }
            return out;
        } finally {
            conn.disconnect();
        }
    }

    private static void extractZip(File archive, File baseDir, Predicate<String> keep) throws IOException {
        try (ZipInputStream zis = new ZipInputStream(new BufferedInputStream(new FileInputStream(archive)))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                if (entry.isDirectory()) {
                    continue;
                }
                String name = entry.getName().replace('\\', '/');
                if (!keep.test(name)) {
                    continue;
                }
                writeTo(resolveSafe(baseDir, name), zis);
            }
        }
    }

    private static void extractRar(File archive, File baseDir, Predicate<String> keep) throws IOException {
        try (Archive rar = new Archive(archive)) {
            FileHeader header;
            while ((header = rar.nextFileHeader()) != null) {
                if (header.isDirectory()) {
                    continue;
                }
                String name = header.getFileName().replace('\\', '/');
                if (!keep.test(name)) {
                    continue;
                }
                File target = resolveSafe(baseDir, name);
                ensureParent(target);
                try (OutputStream os = new BufferedOutputStream(new FileOutputStream(target))) {
                    rar.extractFile(header, os);
                }
            }
        } catch (RarException e) {
            throw new IOException("Failed to extract RAR " + archive.getName(), e);
        }
    }

    private static Format detectFormat(File archive) throws IOException {
        byte[] sig = new byte[7];
        int read = 0;
        try (InputStream in = new FileInputStream(archive)) {
            // Fill the signature buffer manually; InputStream.readNBytes is Android API 34+.
            int r;
            while (read < sig.length && (r = in.read(sig, read, sig.length - read)) != -1) {
                read += r;
            }
        }
        if (read >= 4 && sig[0] == 'P' && sig[1] == 'K' && sig[2] == 3 && sig[3] == 4) {
            return Format.ZIP;
        }
        // RAR4 ("Rar!\x1A\x07\x00") and RAR5 ("Rar!\x1A\x07\x01\x00") share the first six bytes.
        if (read >= 6 && sig[0] == 'R' && sig[1] == 'a' && sig[2] == 'r' && sig[3] == '!'
                && (sig[4] & 0xFF) == 0x1A && (sig[5] & 0xFF) == 0x07) {
            return Format.RAR;
        }
        return Format.UNKNOWN;
    }

    private static void writeManifest(PatchManifest manifest, File dest) throws IOException {
        String json = new Json().toJson(manifest, PatchManifest.class);
        try (Writer writer = new OutputStreamWriter(new FileOutputStream(dest), StandardCharsets.UTF_8)) {
            writer.write(json);
        }
    }

    private static void writeTo(File target, InputStream in) throws IOException {
        ensureParent(target);
        try (OutputStream os = new BufferedOutputStream(new FileOutputStream(target))) {
            copy(in, os);
        }
    }

    /** Buffered stream copy. Avoids {@code InputStream.transferTo} (Java 9 / Android API 33+). */
    private static void copy(InputStream in, OutputStream out) throws IOException {
        copy(in, out, null, -1);
    }

    /** Buffered stream copy that reports cumulative bytes to {@code progress} (may be null). */
    private static void copy(InputStream in, OutputStream out, DownloadProgress progress, long total) throws IOException {
        byte[] buffer = new byte[8192];
        long readTotal = 0;
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
            readTotal += read;
            if (progress != null) {
                progress.onProgress(readTotal, total);
            }
        }
    }

    private static void ensureParent(File target) throws IOException {
        File parent = target.getParentFile();
        if (parent != null && !parent.isDirectory() && !parent.mkdirs()) {
            throw new IOException("Cannot create " + parent);
        }
    }

    /** Resolves {@code entryPath} under {@code baseDir}, rejecting traversal outside it (zip-slip). */
    private static File resolveSafe(File baseDir, String entryPath) throws IOException {
        File base = baseDir.getCanonicalFile();
        File target = new File(base, entryPath).getCanonicalFile();
        String basePath = base.getPath() + File.separator;
        if (!target.getPath().startsWith(basePath)) {
            throw new IOException("Archive entry escapes target directory: " + entryPath);
        }
        return target;
    }

    private static void deleteRecursively(File file) throws IOException {
        if (file == null || !file.exists()) {
            return;
        }
        File[] children = file.listFiles();
        if (children != null) {
            for (File child : children) {
                deleteRecursively(child);
            }
        }
        if (!file.delete()) {
            throw new IOException("Cannot delete " + file);
        }
    }
}
