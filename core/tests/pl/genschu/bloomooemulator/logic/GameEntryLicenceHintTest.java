package pl.genschu.bloomooemulator.logic;

import com.badlogic.gdx.utils.Json;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class GameEntryLicenceHintTest {

    @Test
    void legacySkipFieldEnablesTheNewVisualHint() {
        GameEntry entry = new Json().fromJson(
                GameEntry.class, "{\"skipLicenceCode\":true}");

        assertTrue(entry.isShowLicenceCodeHint());
        String serialized = new Json().toJson(entry);
        assertTrue(serialized.contains("skipLicenceCode:true"), serialized);
        assertTrue(new Json().fromJson(GameEntry.class, serialized).isShowLicenceCodeHint());
    }
}
