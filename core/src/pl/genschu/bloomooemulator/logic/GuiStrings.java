package pl.genschu.bloomooemulator.logic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.I18NBundle;

import java.util.Locale;

/**
 * Emulator chrome strings, read from the same {@code assets/gui_strings} bundle the
 * desktop launcher uses.
 *
 * <p>The launcher reads it through {@link java.util.ResourceBundle}, which only works
 * because {@code assets/} sits on the desktop classpath. Anything in {@code :core} has
 * to go through {@link Gdx#files} instead so that Android, where the same directory is
 * packed as APK assets, resolves it too.
 *
 * <p>Every lookup falls back rather than throwing - a missing translation must never
 * take down the engine.
 */
public final class GuiStrings {
    private static final String BUNDLE_PATH = "gui_strings/translation";

    private static I18NBundle bundle;
    private static boolean bundleResolved;

    private GuiStrings() {}

    /** Returns the translated string for {@code key}, or {@code fallback} if unavailable. */
    public static synchronized String get(String key, String fallback) {
        I18NBundle resolved = bundle();
        if (resolved == null) {
            return fallback;
        }
        try {
            return resolved.get(key);
        } catch (RuntimeException e) {
            return fallback;
        }
    }

    private static I18NBundle bundle() {
        if (!bundleResolved) {
            bundleResolved = true;
            bundle = load();
        }
        return bundle;
    }

    private static I18NBundle load() {
        try {
            if (Gdx.files == null) {
                return null;
            }
            FileHandle base = Gdx.files.internal(BUNDLE_PATH);
            // translation.properties is the English root of the bundle, so any locale
            // resolves - an untranslated system language lands on English instead of
            // making I18NBundle throw.
            return I18NBundle.createBundle(base, Locale.getDefault());
        } catch (RuntimeException e) {
            return null;
        }
    }
}
