package pl.genschu.bloomooemulator.engine.debug;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

import java.util.ArrayList;
import java.util.List;

/**
 * Lazily loads the bundled {@code scene_lists.json} catalog of known ARCADE /
 * CUTSCENE scene names, keyed by {@link SceneLoaderScripts#familyId}. Used by
 * the debug scene loader to offer a type-to-filter pick list; entirely optional
 * — a missing file or family just falls back to free-text entry.
 *
 * <p>Expected format:
 * <pre>
 * {
 *   "NEMO": { "arcade": ["A", "B"], "cutscenes": ["CS_INTRO"] },
 *   "CZARODZIEJE": { "arcade": ["ARC_01"] }
 * }
 * </pre>
 */
public class SceneNameCatalog {
    private static final String CATALOG_PATH = "scene_lists.json";

    private JsonValue root;
    private boolean loaded;

    private void ensureLoaded() {
        if (loaded) {
            return;
        }
        loaded = true;
        try {
            FileHandle handle = Gdx.files.internal(CATALOG_PATH);
            if (handle.exists()) {
                root = new JsonReader().parse(handle);
            }
        } catch (Exception e) {
            Gdx.app.error("SceneNameCatalog", "Failed to load " + CATALOG_PATH + ": " + e.getMessage(), e);
        }
    }

    /**
     * @param familyId game family key (see {@link SceneLoaderScripts#familyId})
     * @param arcade   {@code true} for ARCADE names, {@code false} for cutscenes
     * @return the known scene names, or an empty list when unavailable.
     */
    public List<String> names(String familyId, boolean arcade) {
        ensureLoaded();
        List<String> result = new ArrayList<>();
        if (root == null || familyId == null) {
            return result;
        }
        JsonValue game = root.get(familyId);
        if (game == null) {
            return result;
        }
        JsonValue array = game.get(arcade ? "arcade" : "cutscenes");
        if (array == null) {
            return result;
        }
        for (JsonValue entry = array.child; entry != null; entry = entry.next) {
            String name = entry.asString();
            if (name != null && !name.isBlank()) {
                result.add(name);
            }
        }
        return result;
    }
}
