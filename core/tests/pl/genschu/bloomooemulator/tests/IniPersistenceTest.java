package pl.genschu.bloomooemulator.tests;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import pl.genschu.bloomooemulator.TestEnvironment;
import pl.genschu.bloomooemulator.builders.MethodHelper;
import pl.genschu.bloomooemulator.engine.Game;
import pl.genschu.bloomooemulator.engine.ini.INIManager;
import pl.genschu.bloomooemulator.interpreter.context.Context;
import pl.genschu.bloomooemulator.interpreter.runtime.ExecutionContext;
import pl.genschu.bloomooemulator.interpreter.values.BoolValue;
import pl.genschu.bloomooemulator.interpreter.values.IntValue;
import pl.genschu.bloomooemulator.loader.CNVParser;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TOINI persistence follows CMC_Variable::init / saveINI (BlooMooDLL RiWC):
 * the section is the name of the object whose script declares the variable,
 * a missing entry is seeded with the initial value, and only TOINI variables
 * are read from the INI.
 */
class IniPersistenceTest {
    @BeforeAll static void boot() { TestEnvironment.init(); }

    private static final String PRZYGODA_CNV = """
            OBJECT=S_BFIRSTINTRO
            S_BFIRSTINTRO:TYPE=BOOL
            S_BFIRSTINTRO:VALUE=TRUE
            S_BFIRSTINTRO:TOINI=TRUE

            OBJECT=S_INUMALCH
            S_INUMALCH:TYPE=INTEGER
            S_INUMALCH:TOINI=TRUE

            OBJECT=PLAIN
            PLAIN:TYPE=INTEGER
            PLAIN:VALUE=5
            """;

    private final AtomicLong now = new AtomicLong();
    private final Game game = new Game(null, null, now::get);
    private final INIManager ini = new INIManager();
    private final Context app = new Context(new ExecutionContext(), null, game);

    IniPersistenceTest() {
        game.setGameINI(ini);
        app.setIniSection("GAME");
    }

    private Context loadEpisode() throws IOException {
        Context episode = new Context(new ExecutionContext(), app, game);
        episode.setIniSection("PRZYGODA");
        new CNVParser().parse(new ByteArrayInputStream(PRZYGODA_CNV.getBytes(StandardCharsets.UTF_8)),
                "Przygoda.cnv", episode);
        return episode;
    }

    private Context scene(Context episode, String name) {
        Context scene = new Context(new ExecutionContext(), episode, game);
        scene.setIniSection(name);
        return scene;
    }

    @Test void missingEntriesAreSeededWithInitialValueUnderDeclaringSection() throws IOException {
        loadEpisode();
        assertEquals("TRUE", ini.get("PRZYGODA", "S_BFIRSTINTRO"));
        assertEquals("0", ini.get("PRZYGODA", "S_INUMALCH"));
        assertNull(ini.get("PRZYGODA", "PLAIN"));
    }

    @Test void setFromAnotherSceneWritesDeclaringSectionAndSurvivesReload() throws IOException {
        Context episode = loadEpisode();
        Context csIntro2 = scene(episode, "CS_INTRO2");

        MethodHelper.callWithContext(csIntro2, "S_BFIRSTINTRO", "SET", new BoolValue(false));

        assertEquals("FALSE", ini.get("PRZYGODA", "S_BFIRSTINTRO"));
        assertNull(ini.get("CS_INTRO2", "S_BFIRSTINTRO"));

        Context reloaded = loadEpisode();
        assertEquals(new BoolValue(false), reloaded.getVariable("S_BFIRSTINTRO").value());
    }

    @Test void variablesWithoutToIniIgnoreTheIni() throws IOException {
        ini.put("PRZYGODA", "PLAIN", "9");
        Context episode = loadEpisode();
        assertEquals(new IntValue(5), episode.getVariable("PLAIN").value());
    }

    @Test void lookupIsCaseInsensitiveLikeGetPrivateProfileString() throws IOException {
        ini.put("Przygoda", "s_bfirstintro", "FALSE");
        Context episode = loadEpisode();
        assertEquals(new BoolValue(false), episode.getVariable("S_BFIRSTINTRO").value());

        MethodHelper.callWithContext(episode, "S_BFIRSTINTRO", "SET", new BoolValue(true));
        assertEquals("TRUE", ini.get("Przygoda", "s_bfirstintro"));
        assertEquals(1, ini.getSections().length);
    }
}
