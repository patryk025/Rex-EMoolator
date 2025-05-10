package pl.genschu.bloomooemulator;

public class TestEnvironment {
    private static boolean initialised = false;

    public static void init() {
        if (initialised) return;

        new com.badlogic.gdx.backends.headless.HeadlessApplication(
                new com.badlogic.gdx.ApplicationAdapter() {},
                new com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration()
        );
        initialised = true;
    }
}
