package pl.genschu.bloomooemulator.tests;

import org.junit.jupiter.api.Test;
import pl.genschu.bloomooemulator.patch.PatchCatalog;
import pl.genschu.bloomooemulator.patch.PatchManifest;
import pl.genschu.bloomooemulator.patch.PatchSourceType;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Parsing + merge semantics of {@link PatchCatalog}. Gdx-free: exercises only the
 * pure {@code parse}/{@code mergeFrom} paths, not the bundled-asset loader.
 */
class PatchCatalogTest {

    @Test
    void parsesPatchesArrayWithSourceAndRelations() {
        String json = "{ \"patches\": [" +
                "  { \"id\": \"a\", \"name\": \"A\", \"targetFamily\": \"fam\"," +
                "    \"source\": { \"type\": \"URL\", \"url\": \"http://x/a.zip\" } }," +
                "  { \"id\": \"b\", \"supersedes\": [\"a\"] }" +
                "] }";

        List<PatchManifest> list = PatchCatalog.parse(json);

        assertEquals(2, list.size());
        PatchManifest a = list.get(0);
        assertEquals("a", a.getId());
        assertEquals("fam", a.getTargetFamily());
        assertEquals(PatchSourceType.URL, a.getSource().getType());
        assertEquals("http://x/a.zip", a.getSource().getUrl());
        assertArrayEquals(new String[]{"a"}, list.get(1).getSupersedes());
    }

    @Test
    void skipsEntriesWithoutUsableId() {
        String json = "{ \"patches\": [ { \"name\": \"noid\" }, { \"id\": \"\" }, { \"id\": \"ok\" } ] }";

        List<PatchManifest> list = PatchCatalog.parse(json);

        assertEquals(1, list.size());
        assertEquals("ok", list.get(0).getId());
    }

    @Test
    void emptyOrBlankInputYieldsEmptyList() {
        assertTrue(PatchCatalog.parse(null).isEmpty());
        assertTrue(PatchCatalog.parse("   ").isEmpty());
        assertTrue(PatchCatalog.parse("{}").isEmpty());
    }

    @Test
    void remoteOverridesBundledByIdAndAppendsNew() {
        PatchCatalog catalog = new PatchCatalog();
        catalog.mergeFrom(PatchCatalog.parse(
                "{ \"patches\": [ { \"id\": \"a\", \"version\": \"1.0\" }, { \"id\": \"b\" } ] }"));
        catalog.mergeFrom(PatchCatalog.parse(
                "{ \"patches\": [ { \"id\": \"a\", \"version\": \"2.0\" }, { \"id\": \"c\" } ] }"));

        List<PatchManifest> all = catalog.all();
        assertEquals(3, all.size());
        // remote wins on the shared id…
        assertEquals("2.0", catalog.byId("a").getVersion());
        // …without disturbing original order, new id appended last
        assertEquals("a", all.get(0).getId());
        assertEquals("b", all.get(1).getId());
        assertEquals("c", all.get(2).getId());
    }

    @Test
    void remoteFetchFailureIsNonFatalAndEmpty() {
        // Malformed/unreachable endpoint must degrade to an empty list, never throw.
        assertTrue(PatchCatalog.loadRemote("::not-a-valid-url::").isEmpty());
    }
}
