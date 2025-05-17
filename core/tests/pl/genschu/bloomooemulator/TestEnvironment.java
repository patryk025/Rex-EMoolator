package pl.genschu.bloomooemulator;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration;
import com.badlogic.gdx.backends.headless.mock.graphics.MockGraphics;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GL30;

import static org.mockito.Mockito.mock;

public class TestEnvironment {
    private static boolean initialised = false;

    public static void init() {
        if (initialised) return;

        new HeadlessApplication(
                new ApplicationAdapter() {},
                new HeadlessApplicationConfiguration()
        );
        Gdx.gl   = Gdx.gl20 = mock(GL20.class);
        Gdx.gl30 = mock(GL30.class);
        Gdx.app.setLogLevel(Application.LOG_NONE);
        initialised = true;
    }

    public static void enableLogs() {
        Gdx.app.setLogLevel(Application.LOG_DEBUG);
    }

    public static void disableLogs() {
        Gdx.app.setLogLevel(Application.LOG_NONE);
    }
}
