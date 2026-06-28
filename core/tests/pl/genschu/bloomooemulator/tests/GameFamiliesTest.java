package pl.genschu.bloomooemulator.tests;

import org.junit.jupiter.api.Test;
import pl.genschu.bloomooemulator.logic.GameEntry;
import pl.genschu.bloomooemulator.logic.GameFamilies;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Family resolution by canonical DLL hash and by display-name fallback (the path
 * that keeps patches matching after a no-CD patch changes the engine DLL hash).
 */
class GameFamiliesTest {

    private static final String CZARODZIEJE_RETAIL = "A70F97E970C6301166E90E8872F499DA59961464";
    private static final String CZARODZIEJE_NOCD = "FA0FEA9915077A15E89B6CEE44DFE48AEBB00429"; // user's cracked DLL

    @Test
    void resolvesKnownHashCaseInsensitively() {
        assertEquals("reksio-czarodzieje", GameFamilies.familyFor(CZARODZIEJE_RETAIL));
        assertEquals("reksio-czarodzieje", GameFamilies.familyFor(CZARODZIEJE_RETAIL.toLowerCase()));
        assertEquals("reksio-ufo", GameFamilies.familyFor("5F6CBB33576D4F5493F141DDEE70CD108EA0F9D5"));
    }

    @Test
    void unknownHashHasNoFamily() {
        assertNull(GameFamilies.familyFor(CZARODZIEJE_NOCD));
        assertNull(GameFamilies.familyFor(null));
    }

    @Test
    void resolvesFamilyFromNamePrefix() {
        assertEquals("reksio-czarodzieje", GameFamilies.familyFromName("Reksio i Czarodzieje"));
        assertEquals("reksio-ufo", GameFamilies.familyFromName("Reksio i UFO (seria 2 GRY!, w pakiecie z Miastem SeKretów)"));
        assertEquals("reksio-wehikul-czasu", GameFamilies.familyFromName("Reksio i Wehikuł Czasu"));
        assertNull(GameFamilies.familyFromName("Nieznana gra"));
        assertNull(GameFamilies.familyFromName(null));
    }

    @Test
    void gameEntryResolveFamilyPrecedence() {
        GameEntry game = new GameEntry();
        game.setGameName("Reksio i Czarodzieje");
        game.setDllHash(CZARODZIEJE_NOCD);                 // unknown hash → name fallback
        assertEquals("reksio-czarodzieje", game.resolveFamily());

        game.setDllHash(CZARODZIEJE_RETAIL);               // known hash takes over
        assertEquals("reksio-czarodzieje", game.resolveFamily());

        game.setFamilyOverride("custom-family");           // explicit override wins over everything
        assertEquals("custom-family", game.resolveFamily());

        game.setFamilyOverride("   ");                     // blank clears the override
        assertNull(game.getFamilyOverride());
        assertEquals("reksio-czarodzieje", game.resolveFamily());
    }

    @Test
    void hashWinsButNameIsTheFallback() {
        // canonical hash → resolved by hash regardless of name
        assertEquals("reksio-czarodzieje", GameFamilies.familyFor(CZARODZIEJE_RETAIL, "anything"));
        // cracked DLL (unknown hash) → resolved by the cached display name
        assertEquals("reksio-czarodzieje", GameFamilies.familyFor(CZARODZIEJE_NOCD, "Reksio i Czarodzieje"));
        // neither known → null
        assertNull(GameFamilies.familyFor(CZARODZIEJE_NOCD, "Nieznana gra"));
    }
}
