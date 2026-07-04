package pl.genschu.bloomooemulator.tests;

import com.badlogic.gdx.utils.Json;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import pl.genschu.bloomooemulator.patch.InstalledPatch;
import pl.genschu.bloomooemulator.patch.PatchCatalog;
import pl.genschu.bloomooemulator.patch.PatchInstaller;
import pl.genschu.bloomooemulator.patch.PatchManager;
import pl.genschu.bloomooemulator.patch.PatchManagerController;
import pl.genschu.bloomooemulator.patch.PatchManifest;
import pl.genschu.bloomooemulator.patch.PatchRegistry;
import pl.genschu.bloomooemulator.patch.PatchRegistryEntry;
import pl.genschu.bloomooemulator.patch.PatchRowVM;
import pl.genschu.bloomooemulator.patch.PatchSource;
import pl.genschu.bloomooemulator.patch.PatchSourceType;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * UI-agnostic patch orchestration shared by the Swing dialog and the Android
 * activity: row filtering, the EXACT/FAMILY {@code forceEnable} rule, reordering,
 * and install preconditions. No network — installs are materialised from synthetic
 * ZIPs, and install() is only exercised for its precondition guards.
 */
class PatchManagerControllerTest {

    private static final String HASH = "A1B2C3D4E5F6A1B2C3D4E5F6A1B2C3D4E5F6A1B2";
    private static final String FAMILY = "test-family";

    @TempDir
    Path tmp;

    private File patchesRoot() {
        return tmp.resolve("patches").toFile();
    }

    private File indexFile() {
        return new File(patchesRoot(), "index.json");
    }

    private PatchManagerController controller(PatchCatalog catalog) {
        return new PatchManagerController(HASH, FAMILY, patchesRoot(), indexFile(), catalog);
    }

    private File zip(String name) throws Exception {
        File file = tmp.resolve(name).toFile();
        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(file))) {
            zos.putNextEntry(new ZipEntry("Dane/data.cnv"));
            zos.write("data".getBytes(StandardCharsets.UTF_8));
            zos.closeEntry();
        }
        return file;
    }

    private File packagedZip(String name, PatchManifest manifest) throws Exception {
        File file = tmp.resolve(name).toFile();
        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(file))) {
            zos.putNextEntry(new ZipEntry("patch.json"));
            zos.write(new Json().toJson(manifest, PatchManifest.class).getBytes(StandardCharsets.UTF_8));
            zos.closeEntry();
            zos.putNextEntry(new ZipEntry("files/Dane/local.cnv"));
            zos.write("local".getBytes(StandardCharsets.UTF_8));
            zos.closeEntry();
        }
        return file;
    }

    private File overlayFolder(String name) throws Exception {
        Path folder = tmp.resolve(name);
        Files.createDirectories(folder.resolve("Dane"));
        Files.writeString(folder.resolve("Dane/local.cnv"), "local", StandardCharsets.UTF_8);
        return folder.toFile();
    }

    /** Materialises a patch on disk so the manager discovers it as installed. */
    private void installOnDisk(String id, String[] hashes, String family) throws Exception {
        PatchManifest m = new PatchManifest();
        m.setId(id);
        m.setTargetHashes(hashes);
        m.setTargetFamily(family);
        PatchInstaller.installFromArchive(m, zip(id + ".zip"), patchesRoot());
    }

    private PatchManifest catalogEntry(String id, String[] hashes, String family, PatchSourceType type) {
        PatchManifest m = new PatchManifest();
        m.setId(id);
        m.setTargetHashes(hashes);
        m.setTargetFamily(family);
        PatchSource source = new PatchSource();
        source.setType(type);
        if (type == PatchSourceType.URL) {
            source.setUrl("https://example.invalid/" + id + ".zip");
        }
        m.setSource(source);
        return m;
    }

    private PatchCatalog catalog(PatchManifest... entries) {
        PatchCatalog catalog = new PatchCatalog();
        catalog.mergeFrom(List.of(entries));
        return catalog;
    }

    private static PatchRowVM rowById(List<PatchRowVM> rows, String id) {
        return rows.stream().filter(r -> r.getId().equals(id)).findFirst().orElse(null);
    }

    @Test
    void rowsKeepMatchingCatalogEntriesAndDropIrrelevantOnes() {
        PatchCatalog catalog = catalog(
                catalogEntry("exact", new String[]{HASH}, null, PatchSourceType.URL),
                catalogEntry("family", new String[0], FAMILY, PatchSourceType.URL),
                catalogEntry("other", new String[]{"DEADBEEF"}, "different", PatchSourceType.URL));

        List<PatchRowVM> rows = controller(catalog).rows();

        assertNotNull(rowById(rows, "exact"), "EXACT match must be listed");
        assertNotNull(rowById(rows, "family"), "FAMILY match must be listed");
        assertNull(rowById(rows, "other"), "non-matching, uninstalled patch must be hidden");
    }

    @Test
    void installedPatchIsListedAndAuthoritativeEvenIfCatalogDisagrees() throws Exception {
        installOnDisk("p", new String[]{HASH}, null);
        // Catalog claims a different (non-matching) target for the same id.
        PatchCatalog catalog = catalog(catalogEntry("p", new String[]{"DEADBEEF"}, null, PatchSourceType.URL));

        PatchRowVM row = rowById(controller(catalog).rows(), "p");

        assertNotNull(row);
        assertTrue(row.isInstalled());
    }

    @Test
    void enablingExactMatchDoesNotForceEnable() throws Exception {
        installOnDisk("exact", new String[]{HASH}, null);
        controller(catalog()).setEnabled("exact", true);

        PatchRegistry persisted = new PatchRegistry(indexFile());
        assertTrue(persisted.find(HASH, "exact").isEnabled());
        assertFalse(persisted.find(HASH, "exact").isForceEnable(),
                "EXACT matches mount without forceEnable");
    }

    @Test
    void enablingFamilyMatchAutoForcesEnable() throws Exception {
        installOnDisk("family", new String[0], FAMILY);
        controller(catalog()).setEnabled("family", true);

        PatchRegistry persisted = new PatchRegistry(indexFile());
        assertTrue(persisted.find(HASH, "family").isEnabled());
        assertTrue(persisted.find(HASH, "family").isForceEnable(),
                "FAMILY matches need forceEnable to actually mount");
    }

    @Test
    void disablingClearsForceEnable() throws Exception {
        installOnDisk("family", new String[0], FAMILY);
        PatchManagerController controller = controller(catalog());
        controller.setEnabled("family", true);
        controller.setEnabled("family", false);

        PatchRegistry persisted = new PatchRegistry(indexFile());
        assertFalse(persisted.find(HASH, "family").isEnabled());
        assertFalse(persisted.find(HASH, "family").isForceEnable());
    }

    @Test
    void moveSwapsMountOrderOfRegistryTrackedPatches() throws Exception {
        installOnDisk("first", new String[]{HASH}, null);
        installOnDisk("second", new String[]{HASH}, null);
        PatchManagerController controller = controller(catalog());
        controller.setEnabled("first", true);   // order 0
        controller.setEnabled("second", true);  // order 1

        assertEquals("first", controller.rows().get(0).getId());

        assertTrue(controller.move("second", -1));
        assertEquals("second", controller.rows().get(0).getId(), "moved patch should now mount first");
    }

    @Test
    void moveIsNoOpAtBoundary() throws Exception {
        installOnDisk("only", new String[]{HASH}, null);
        PatchManagerController controller = controller(catalog());
        controller.setEnabled("only", true);

        assertFalse(controller.move("only", -1), "nothing to swap with above the top row");
    }

    @Test
    void installRejectsAlreadyInstalledPatch() throws Exception {
        installOnDisk("p", new String[]{HASH}, null);

        assertThrows(IllegalStateException.class, () -> controller(catalog()).install("p"));
    }

    @Test
    void installRejectsNonDownloadableSource() {
        PatchCatalog catalog = catalog(catalogEntry("local", new String[]{HASH}, null, PatchSourceType.LOCAL));

        assertThrows(IllegalStateException.class, () -> controller(catalog).install("local"));
    }

    @Test
    void installRejectsUnknownPatch() {
        assertThrows(IllegalStateException.class, () -> controller(catalog()).install("ghost"));
    }

    @Test
    void importLocalArchiveInstallsAndListsSelfContainedPatch() throws Exception {
        PatchManifest manifest = new PatchManifest();
        manifest.setId("local");
        manifest.setTargetHashes(new String[]{HASH});
        File archive = packagedZip("local.zip", manifest);

        PatchManagerController controller = controller(catalog());
        controller.importLocalArchive(archive);

        PatchRowVM row = rowById(controller.rows(), "local");
        assertNotNull(row);
        assertTrue(row.isInstalled());
        assertEquals("local", row.getId());
        assertTrue(new File(patchesRoot(), "local/files/Dane/local.cnv").isFile());
    }

    @Test
    void importLocalArchiveRejectsAlreadyInstalledPatch() throws Exception {
        installOnDisk("local", new String[]{HASH}, null);
        PatchManifest manifest = new PatchManifest();
        manifest.setId("local");
        manifest.setTargetHashes(new String[]{HASH});
        File archive = packagedZip("local.zip", manifest);

        assertThrows(IllegalStateException.class, () -> controller(catalog()).importLocalArchive(archive));
    }

    @Test
    void linkLocalFolderMountsSourceDirectoryWithoutCopying() throws Exception {
        File folder = overlayFolder("work-mod");

        PatchManagerController controller = controller(catalog());
        InstalledPatch linked = controller.linkLocalFolder(folder);

        PatchRowVM row = rowById(controller.rows(), linked.getManifest().getId());
        assertNotNull(row);
        assertTrue(row.isInstalled());
        assertTrue(row.isEnabled(), "development folder should be enabled immediately");
        assertTrue(row.isLinkedLocal());

        PatchRegistryEntry persisted = new PatchRegistry(indexFile()).find(HASH, linked.getManifest().getId());
        assertNotNull(persisted);
        assertEquals(folder.getCanonicalPath(), new File(persisted.getLocalPath()).getCanonicalPath());

        PatchManager reloaded = new PatchManager(patchesRoot(), new PatchRegistry(indexFile()));
        InstalledPatch active = reloaded.activeFor(HASH, FAMILY).get(0);
        assertTrue(active.isLinkedLocal());
        assertEquals(folder.getCanonicalFile(), active.getFilesDir().getCanonicalFile());

        Files.writeString(folder.toPath().resolve("Dane/new.cnv"), "new", StandardCharsets.UTF_8);
        assertTrue(new File(active.getFilesDir(), "Dane/new.cnv").isFile(),
                "linked folders should expose edits without re-importing");
    }

    @Test
    void togglingLinkedFolderPreservesItsSourcePath() throws Exception {
        File folder = overlayFolder("toggle-mod");
        PatchManagerController controller = controller(catalog());
        String patchId = controller.linkLocalFolder(folder).getManifest().getId();

        controller.setEnabled(patchId, false);
        PatchRegistryEntry disabled = new PatchRegistry(indexFile()).find(HASH, patchId);
        assertFalse(disabled.isEnabled());
        assertNotNull(disabled.getLocalPath());

        controller.setEnabled(patchId, true);
        PatchManager reloaded = new PatchManager(patchesRoot(), new PatchRegistry(indexFile()));
        assertEquals(patchId, reloaded.activeFor(HASH, FAMILY).get(0).getManifest().getId());
    }

    @Test
    void unlinkLocalFolderDoesNotDeleteSourceDirectory() throws Exception {
        File folder = overlayFolder("unlink-mod");
        PatchManagerController controller = controller(catalog());
        String patchId = controller.linkLocalFolder(folder).getManifest().getId();

        assertTrue(controller.uninstall(patchId));

        assertTrue(folder.isDirectory(), "source folder must survive unlinking");
        assertNull(rowById(controller.rows(), patchId), "unlinked folder should disappear from the patch list");
        assertNull(new PatchRegistry(indexFile()).find(HASH, patchId));
    }
}
