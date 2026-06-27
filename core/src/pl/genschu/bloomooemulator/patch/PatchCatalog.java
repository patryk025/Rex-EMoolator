package pl.genschu.bloomooemulator.patch;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * In-memory list of <em>available</em> patches (not necessarily installed yet):
 * each entry is a {@link PatchManifest} whose {@link PatchSource} says where to
 * download the payload from. Two layers feed it:
 * <ul>
 *   <li>a bundled {@code patch_catalog.json} shipped in the app assets — the
 *       baseline everyone who builds the project gets, and</li>
 *   <li>an optional remote catalog fetched at runtime, whose entries override or
 *       add to the bundled ones by {@code id} (see {@link #mergeFrom}).</li>
 * </ul>
 *
 * <p>On-disk shape:
 * <pre>
 * { "patches": [ { …manifest… }, { …manifest… } ] }
 * </pre>
 *
 * <p>Distinct from {@link PatchRegistry} (per-game enable state of installed
 * patches) and {@link PatchManager} (discovery + mounting of installed patches).
 * The catalog only answers "what can I install, and from where?".
 */
public class PatchCatalog {
    private static final String BUNDLED_PATH = "patch_catalog.json";

    /** Default remote catalog endpoint, overridable via {@link #load(String)}. */
    public static final String DEFAULT_REMOTE_URL = "https://rexemoolator.genschu.pl/patch_catalog.json";

    private static final Logger LOGGER = Logger.getLogger(PatchCatalog.class.getName());

    /** Entries by id, in insertion order: bundled first, then remote-only additions. */
    private final Map<String, PatchManifest> entries = new LinkedHashMap<>();

    /** Loads the catalog bundled with the app. Missing/unreadable → empty catalog. */
    public static PatchCatalog loadBundled() {
        PatchCatalog catalog = new PatchCatalog();
        String json = readBundled();
        if (json != null) {
            catalog.mergeFrom(parse(json));
        }
        return catalog;
    }

    /**
     * Reads the bundled catalog. Prefers a classpath resource so it works in the
     * Swing launcher (which has no initialised libGDX), falling back to
     * {@code Gdx.files} for asset-only platforms such as Android.
     */
    private static String readBundled() {
        try (InputStream in = PatchCatalog.class.getResourceAsStream("/" + BUNDLED_PATH)) {
            if (in != null) {
                return readString(in);
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Failed to read bundled " + BUNDLED_PATH + " from classpath", e);
        }
        try {
            if (Gdx.files != null) {
                FileHandle handle = Gdx.files.internal(BUNDLED_PATH);
                if (handle.exists()) {
                    return handle.readString("UTF-8");
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Failed to read bundled " + BUNDLED_PATH + " via Gdx", e);
        }
        return null;
    }

    /**
     * Builds a catalog from a raw catalog JSON document. Lets callers that have
     * already obtained the bundled JSON by other means (e.g. Android's
     * {@code AssetManager}, where neither the classpath nor {@code Gdx.files} is
     * usable in the plain launcher Activity) feed it in directly.
     */
    public static PatchCatalog fromJson(String json) {
        PatchCatalog catalog = new PatchCatalog();
        catalog.mergeFrom(parse(json));
        return catalog;
    }

    /**
     * Bundled baseline with the remote catalog merged on top (remote wins by id).
     * A remote that is offline, errors, or serves junk is non-fatal — you still get
     * the bundled entries (see {@link #loadRemote}).
     */
    public static PatchCatalog load(String remoteUrl) {
        PatchCatalog catalog = loadBundled();
        if (remoteUrl != null && !remoteUrl.isBlank()) {
            catalog.mergeFrom(loadRemote(remoteUrl));
        }
        return catalog;
    }

    /** {@link #load(String)} against {@link #DEFAULT_REMOTE_URL}. */
    public static PatchCatalog loadWithDefaultRemote() {
        return load(DEFAULT_REMOTE_URL);
    }

    /**
     * Fetches and parses a remote catalog. Best-effort: any failure (offline, non-200,
     * malformed JSON) yields an empty list rather than throwing, so callers can merge
     * unconditionally.
     */
    public static List<PatchManifest> loadRemote(String url) {
        try {
            return parse(fetchString(url));
        } catch (Exception e) {
            LOGGER.warning("Remote catalog unavailable (" + url + "): " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Parses a catalog document into manifests. Entries without a usable {@code id}
     * are skipped. Pure (no {@code Gdx} dependency), so it is unit-testable without
     * a running application and reusable for remote payloads.
     */
    public static List<PatchManifest> parse(String json) {
        List<PatchManifest> result = new ArrayList<>();
        if (json == null || json.isBlank()) {
            return result;
        }
        JsonValue root = new JsonReader().parse(json);
        if (root == null) {
            return result;
        }
        JsonValue patches = root.get("patches");
        if (patches == null) {
            return result;
        }
        Json mapper = new Json();
        mapper.setIgnoreUnknownFields(true); // forward-compat: tolerate manifest fields newer than this build
        for (JsonValue entry = patches.child; entry != null; entry = entry.next) {
            PatchManifest manifest = mapper.readValue(PatchManifest.class, entry);
            if (manifest != null && manifest.getId() != null && !manifest.getId().isBlank()) {
                result.add(manifest);
            }
        }
        return result;
    }

    /**
     * Merges manifests on top of this catalog: an id already present is replaced
     * (keeping its original position), a new id is appended. This is how a remote
     * catalog overrides/extends the bundled baseline.
     */
    public void mergeFrom(List<PatchManifest> manifests) {
        if (manifests == null) {
            return;
        }
        for (PatchManifest manifest : manifests) {
            if (manifest != null && manifest.getId() != null && !manifest.getId().isBlank()) {
                entries.put(manifest.getId(), manifest);
            }
        }
    }

    /** Convenience overload of {@link #mergeFrom(List)} for another catalog. */
    public void mergeFrom(PatchCatalog other) {
        if (other != null) {
            mergeFrom(other.all());
        }
    }

    /** All entries, bundled order with remote-only additions last. */
    public List<PatchManifest> all() {
        return new ArrayList<>(entries.values());
    }

    public PatchManifest byId(String id) {
        return id == null ? null : entries.get(id);
    }

    public boolean isEmpty() {
        return entries.isEmpty();
    }

    public int size() {
        return entries.size();
    }

    private static String fetchString(String url) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) URI.create(url).toURL().openConnection();
        conn.setInstanceFollowRedirects(true);
        conn.setConnectTimeout(10000);
        conn.setReadTimeout(15000);
        conn.setRequestProperty("User-Agent", "RexEMoolator");
        try {
            int code = conn.getResponseCode();
            if (code != HttpURLConnection.HTTP_OK) {
                throw new IOException("HTTP " + code);
            }
            try (InputStream in = new BufferedInputStream(conn.getInputStream())) {
                return readString(in);
            }
        } finally {
            conn.disconnect();
        }
    }

    private static String readString(InputStream in) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[8192];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
        return new String(out.toByteArray(), StandardCharsets.UTF_8);
    }
}
