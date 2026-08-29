package pl.genschu.bloomooemulator.tests;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.I18NBundle;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import pl.genschu.bloomooemulator.TestEnvironment;

import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Guards the shared {@code assets/gui_strings} bundle that both the desktop launcher
 * and the engine startup screen read. The engine goes through
 * {@code GuiStrings}, which resolves the bundle via {@code Gdx.files.internal} against
 * the packaged assets; tests run with the module directory as the working directory,
 * so they reach the same files through {@code ../assets}.
 */
class GuiStringsBundleTest {
    private static final String BUNDLE_PATH = "../assets/gui_strings/translation";

    @BeforeAll
    static void boot() {
        TestEnvironment.init();
    }

    @Test
    void polishBundleProvidesLoadingLabelInUtf8() {
        I18NBundle bundle = I18NBundle.createBundle(
                Gdx.files.internal(BUNDLE_PATH), Locale.forLanguageTag("pl-PL"));

        assertThat(bundle.get("loading")).isEqualTo("Ładowanie...");
    }

    /**
     * Asks for the root explicitly. A named locale would not do: when a locale has no
     * file of its own, the bundle falls back to the JVM default locale before it
     * reaches the root, so on a Polish machine {@code en_US} resolves to Polish.
     */
    @Test
    void rootBundleProvidesEnglishLoadingLabel() {
        I18NBundle bundle = I18NBundle.createBundle(
                Gdx.files.internal(BUNDLE_PATH), Locale.ROOT);

        assertThat(bundle.get("loading")).isEqualTo("Loading...");
    }

    /**
     * The English translation is the bundle's root, so every locale resolves to
     * something. Without it an untranslated system language throws
     * {@link java.util.MissingResourceException} instead. Which language a locale
     * without its own file lands on depends on the JVM default locale, so this only
     * asserts that the lookup succeeds.
     */
    @Test
    void untranslatedLocaleStillResolves() {
        I18NBundle bundle = I18NBundle.createBundle(
                Gdx.files.internal(BUNDLE_PATH), Locale.GERMANY);

        assertThat(bundle.get("loading")).isNotEmpty();
    }
}
