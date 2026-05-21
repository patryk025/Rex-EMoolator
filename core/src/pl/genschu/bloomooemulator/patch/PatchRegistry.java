package pl.genschu.bloomooemulator.patch;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Json;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Persisted enable state for all (game, patch) pairings.
 *
 * On disk: {@code <userDir>/patches/index.json} — a flat list of
 * {@link PatchRegistryEntry} so libGDX {@code Json} round-trips cleanly.
 *
 * The registry is the source of truth for "is this patch active right now?".
 * Discovering patches on disk and matching compatibility lives in
 * {@link PatchManager}; this class only tracks user intent.
 */
public class PatchRegistry {
    private final File indexFile;
    private final List<PatchRegistryEntry> entries = new ArrayList<>();

    public PatchRegistry(File indexFile) {
        this.indexFile = indexFile;
        load();
    }

    /** Replaces the in-memory list from disk. Missing file → empty registry. */
    public void load() {
        entries.clear();
        if (indexFile == null || !indexFile.exists()) return;
        try (InputStreamReader reader = new InputStreamReader(new FileInputStream(indexFile), StandardCharsets.UTF_8)) {
            StringBuilder sb = new StringBuilder();
            int c;
            while ((c = reader.read()) != -1) sb.append((char) c);
            PatchRegistryEntry[] parsed = new Json().fromJson(PatchRegistryEntry[].class, sb.toString());
            if (parsed != null) {
                for (PatchRegistryEntry e : parsed) entries.add(e);
            }
        } catch (IOException e) {
            Gdx.app.error("PatchRegistry", "Failed to read " + indexFile, e);
        }
    }

    public void save() {
        if (indexFile == null) return;
        File parent = indexFile.getParentFile();
        if (parent != null && !parent.exists() && !parent.mkdirs()) {
            Gdx.app.error("PatchRegistry", "Cannot create " + parent);
            return;
        }
        String json = new Json().toJson(entries.toArray(new PatchRegistryEntry[0]), PatchRegistryEntry[].class);
        try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(indexFile), StandardCharsets.UTF_8)) {
            writer.write(json);
        } catch (IOException e) {
            Gdx.app.error("PatchRegistry", "Failed to write " + indexFile, e);
        }
    }

    /** Returns entries for the given game hash, sorted by {@code order} ascending. */
    public List<PatchRegistryEntry> entriesFor(String gameHash) {
        List<PatchRegistryEntry> result = new ArrayList<>();
        if (gameHash == null) return result;
        String key = gameHash.toUpperCase(Locale.ROOT);
        for (PatchRegistryEntry e : entries) {
            if (e.getGameHash() != null && key.equals(e.getGameHash().toUpperCase(Locale.ROOT))) {
                result.add(e);
            }
        }
        result.sort((a, b) -> Integer.compare(a.getOrder(), b.getOrder()));
        return result;
    }

    public PatchRegistryEntry find(String gameHash, String patchId) {
        if (gameHash == null || patchId == null) return null;
        String hashKey = gameHash.toUpperCase(Locale.ROOT);
        for (PatchRegistryEntry e : entries) {
            if (e.getPatchId() != null && e.getPatchId().equals(patchId)
                    && e.getGameHash() != null && hashKey.equals(e.getGameHash().toUpperCase(Locale.ROOT))) {
                return e;
            }
        }
        return null;
    }

    /** Inserts or updates an entry; caller decides when to {@link #save()}. */
    public void upsert(PatchRegistryEntry entry) {
        PatchRegistryEntry existing = find(entry.getGameHash(), entry.getPatchId());
        if (existing == null) {
            entries.add(entry);
        } else {
            existing.setEnabled(entry.isEnabled());
            existing.setForceEnable(entry.isForceEnable());
            existing.setOrder(entry.getOrder());
        }
    }

    public void remove(String gameHash, String patchId) {
        PatchRegistryEntry existing = find(gameHash, patchId);
        if (existing != null) entries.remove(existing);
    }
}
