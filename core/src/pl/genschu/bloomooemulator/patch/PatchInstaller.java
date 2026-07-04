package pl.genschu.bloomooemulator.patch;

import com.badlogic.gdx.utils.Json;
import com.github.junrar.Archive;
import com.github.junrar.exception.RarException;
import com.github.junrar.rarfile.FileHeader;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
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

    private static final String MANIFEST_FILE = "patch.json";

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
     * Installs a self-contained local patch archive. The archive must contain a
     * {@code patch.json} manifest plus the manifest's {@code filesRoot} directory
     * next to it, optionally wrapped in one top-level folder.
     */
    public static InstalledPatch installFromPackagedArchive(File archive, File patchesRoot) throws IOException {
        PackagedManifest packaged = readPackagedManifest(archive);
        return installFromPackagedArchive(packaged.manifest, archive, patchesRoot, packaged.root, DEFAULT_FILTER);
    }

    /**
     * Reads the {@code patch.json} from a self-contained local patch archive without
     * installing it. Useful for UI/controller preflight checks.
     */
    public static PatchManifest readManifestFromArchive(File archive) throws IOException {
        return readPackagedManifest(archive).manifest;
    }

    /**
     * Reads {@code patch.json} from a local patch folder. Returns {@code null} when
     * the folder has no manifest, which lets development overlays be mounted directly.
     */
    public static PatchManifest readManifestFromDirectory(File rootDir) throws IOException {
        File manifestFile = new File(rootDir, MANIFEST_FILE);
        if (!manifestFile.isFile()) {
            return null;
        }
        try (InputStream in = new BufferedInputStream(new FileInputStream(manifestFile))) {
            Json mapper = new Json();
            mapper.setIgnoreUnknownFields(true);
            PatchManifest manifest = mapper.fromJson(PatchManifest.class, readString(in));
            if (manifest == null || manifest.getId() == null || manifest.getId().isBlank()) {
                throw new IOException("Patch manifest has no id");
            }
            if (!isSafePatchId(manifest.getId())) {
                throw new IOException("Patch id must be a simple directory name: " + manifest.getId());
            }
            return manifest;
        } catch (RuntimeException ex) {
            throw new IOException("Invalid " + MANIFEST_FILE + " in " + rootDir, ex);
        }
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
        File patchDir = patchDir(patchesRoot, manifest);
        File filesDir = filesDir(patchDir, manifest);
        deleteRecursively(filesDir);
        if (!filesDir.mkdirs() && !filesDir.isDirectory()) {
            throw new IOException("Cannot create overlay directory " + filesDir);
        }

        Predicate<String> filter = keep == null ? DEFAULT_FILTER : keep;
        String archiveRoot = normalizeRoot(manifest.getArchiveRoot());
        switch (detectFormat(archive)) {
            case ZIP -> extractZip(archive, filesDir, filter, archiveRoot);
            case RAR -> extractRar(archive, filesDir, filter, archiveRoot);
            default -> throw new IOException("Unsupported archive format: " + archive.getName());
        }

        writeManifest(manifest, new File(patchDir, "patch.json"));
        return new InstalledPatch(manifest, patchDir);
    }

    private static InstalledPatch installFromPackagedArchive(PatchManifest manifest, File archive, File patchesRoot,
                                                             String packageRoot, Predicate<String> keep) throws IOException {
        if (manifest == null || manifest.getId() == null || manifest.getId().isBlank()) {
            throw new IOException("Patch manifest has no id");
        }
        File patchDir = patchDir(patchesRoot, manifest);
        File filesDir = filesDir(patchDir, manifest);
        deleteRecursively(filesDir);
        if (!filesDir.mkdirs() && !filesDir.isDirectory()) {
            throw new IOException("Cannot create overlay directory " + filesDir);
        }

        Predicate<String> filter = keep == null ? DEFAULT_FILTER : keep;
        String filesRootInArchive = joinRoots(packageRoot, manifest.getFilesRoot());
        int extracted;
        switch (detectFormat(archive)) {
            case ZIP -> extracted = extractZip(archive, filesDir, filter, filesRootInArchive);
            case RAR -> extracted = extractRar(archive, filesDir, filter, filesRootInArchive);
            default -> throw new IOException("Unsupported archive format: " + archive.getName());
        }
        if (extracted == 0) {
            throw new IOException("Patch archive does not contain " + filesRootInArchive + "/");
        }

        writeManifest(manifest, new File(patchDir, MANIFEST_FILE));
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

    private static int extractZip(File archive, File baseDir, Predicate<String> keep, String archiveRoot) throws IOException {
        int extracted = 0;
        try (ZipInputStream zis = new ZipInputStream(new BufferedInputStream(new FileInputStream(archive)))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                if (entry.isDirectory()) {
                    continue;
                }
                String name = relativize(entry.getName().replace('\\', '/'), archiveRoot);
                if (name == null || !keep.test(name)) {
                    continue;
                }
                writeTo(resolveSafe(baseDir, name), zis);
                extracted++;
            }
        }
        return extracted;
    }

    private static int extractRar(File archive, File baseDir, Predicate<String> keep, String archiveRoot) throws IOException {
        int extracted = 0;
        try (Archive rar = new Archive(archive)) {
            FileHeader header;
            while ((header = rar.nextFileHeader()) != null) {
                if (header.isDirectory()) {
                    continue;
                }
                String name = relativize(header.getFileName().replace('\\', '/'), archiveRoot);
                if (name == null || !keep.test(name)) {
                    continue;
                }
                File target = resolveSafe(baseDir, name);
                ensureParent(target);
                try (OutputStream os = new BufferedOutputStream(new FileOutputStream(target))) {
                    rar.extractFile(header, os);
                }
                extracted++;
            }
        } catch (RarException e) {
            throw new IOException("Failed to extract RAR " + archive.getName(), e);
        }
        return extracted;
    }

    /**
     * Maps an archive entry path to its overlay-relative path. With no {@code archiveRoot}
     * the path is unchanged; otherwise the entry must live under {@code archiveRoot/} —
     * that prefix is stripped and entries outside it return {@code null} (skipped).
     */
    private static String relativize(String name, String archiveRoot) {
        if (archiveRoot == null || archiveRoot.isEmpty()) {
            return name;
        }
        String prefix = archiveRoot + "/";
        if (name.length() <= prefix.length()
                || !name.regionMatches(true, 0, prefix, 0, prefix.length())) {
            return null;
        }
        return name.substring(prefix.length());
    }

    /** Normalises a manifest {@code archiveRoot}: backslashes→/, strips surrounding slashes. */
    private static String normalizeRoot(String root) {
        if (root == null) {
            return null;
        }
        String r = root.replace('\\', '/').trim();
        while (r.startsWith("/")) {
            r = r.substring(1);
        }
        while (r.endsWith("/")) {
            r = r.substring(0, r.length() - 1);
        }
        return r.isEmpty() ? null : r;
    }

    private static String joinRoots(String first, String second) {
        String a = normalizeRoot(first);
        String b = normalizeRoot(second);
        if (a == null) {
            return b;
        }
        if (b == null) {
            return a;
        }
        return a + "/" + b;
    }

    private static PackagedManifest readPackagedManifest(File archive) throws IOException {
        return switch (detectFormat(archive)) {
            case ZIP -> readZipManifest(archive);
            case RAR -> readRarManifest(archive);
            default -> throw new IOException("Unsupported archive format: " + archive.getName());
        };
    }

    private static PackagedManifest readZipManifest(File archive) throws IOException {
        ManifestCandidate best = null;
        try (ZipInputStream zis = new ZipInputStream(new BufferedInputStream(new FileInputStream(archive)))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                if (entry.isDirectory()) {
                    continue;
                }
                String name = entry.getName().replace('\\', '/');
                if (!isManifestPath(name)) {
                    continue;
                }
                ManifestCandidate candidate = parseManifestCandidate(name, readString(zis));
                if (candidate != null && (best == null || candidate.depth < best.depth)) {
                    best = candidate;
                }
            }
        }
        if (best == null) {
            throw new IOException("Patch archive has no valid " + MANIFEST_FILE);
        }
        return new PackagedManifest(best.manifest, best.root);
    }

    private static PackagedManifest readRarManifest(File archive) throws IOException {
        ManifestCandidate best = null;
        try (Archive rar = new Archive(archive)) {
            FileHeader header;
            while ((header = rar.nextFileHeader()) != null) {
                if (header.isDirectory()) {
                    continue;
                }
                String name = header.getFileName().replace('\\', '/');
                if (!isManifestPath(name)) {
                    continue;
                }
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                rar.extractFile(header, out);
                ManifestCandidate candidate = parseManifestCandidate(
                        name, new String(out.toByteArray(), StandardCharsets.UTF_8));
                if (candidate != null && (best == null || candidate.depth < best.depth)) {
                    best = candidate;
                }
            }
        } catch (RarException e) {
            throw new IOException("Failed to read RAR " + archive.getName(), e);
        }
        if (best == null) {
            throw new IOException("Patch archive has no valid " + MANIFEST_FILE);
        }
        return new PackagedManifest(best.manifest, best.root);
    }

    private static boolean isManifestPath(String name) {
        String normalized = normalizeRoot(name);
        if (normalized == null) {
            return false;
        }
        String lower = normalized.toLowerCase(Locale.ROOT);
        return lower.equals(MANIFEST_FILE) || lower.endsWith("/" + MANIFEST_FILE);
    }

    private static ManifestCandidate parseManifestCandidate(String path, String json) {
        PatchManifest manifest;
        try {
            Json mapper = new Json();
            mapper.setIgnoreUnknownFields(true);
            manifest = mapper.fromJson(PatchManifest.class, json);
        } catch (RuntimeException ex) {
            return null;
        }
        if (manifest == null || manifest.getId() == null || manifest.getId().isBlank()
                || !isSafePatchId(manifest.getId())) {
            return null;
        }
        String root = manifestRoot(path);
        return new ManifestCandidate(manifest, root, rootDepth(root));
    }

    private static String manifestRoot(String path) {
        String normalized = normalizeRoot(path);
        if (normalized == null) {
            return null;
        }
        int slash = normalized.lastIndexOf('/');
        return slash < 0 ? null : normalized.substring(0, slash);
    }

    private static int rootDepth(String root) {
        if (root == null || root.isEmpty()) {
            return 0;
        }
        int depth = 1;
        for (int i = 0; i < root.length(); i++) {
            if (root.charAt(i) == '/') {
                depth++;
            }
        }
        return depth;
    }

    public static boolean isSafePatchId(String id) {
        return id != null && !id.isBlank()
                && !id.equals(".") && !id.equals("..")
                && id.indexOf('/') < 0 && id.indexOf('\\') < 0;
    }

    private static File patchDir(File patchesRoot, PatchManifest manifest) throws IOException {
        if (patchesRoot == null) {
            throw new IOException("Missing patches root");
        }
        if (!isSafePatchId(manifest.getId())) {
            throw new IOException("Patch id must be a simple directory name: " + manifest.getId());
        }
        File base = patchesRoot.getCanonicalFile();
        File patchDir = new File(base, manifest.getId()).getCanonicalFile();
        String basePath = base.getPath() + File.separator;
        if (!patchDir.getPath().startsWith(basePath)) {
            throw new IOException("Patch id escapes patches directory: " + manifest.getId());
        }
        return patchDir;
    }

    private static File filesDir(File patchDir, PatchManifest manifest) throws IOException {
        String filesRoot = normalizeRoot(manifest.getFilesRoot());
        if (filesRoot == null) {
            throw new IOException("Patch manifest has no filesRoot");
        }
        return resolveSafe(patchDir, filesRoot);
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

    private static String readString(InputStream in) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        copy(in, out);
        return new String(out.toByteArray(), StandardCharsets.UTF_8);
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

    private static final class PackagedManifest {
        private final PatchManifest manifest;
        private final String root;

        private PackagedManifest(PatchManifest manifest, String root) {
            this.manifest = manifest;
            this.root = root;
        }
    }

    private static final class ManifestCandidate {
        private final PatchManifest manifest;
        private final String root;
        private final int depth;

        private ManifestCandidate(PatchManifest manifest, String root, int depth) {
            this.manifest = manifest;
            this.root = root;
            this.depth = depth;
        }
    }
}
