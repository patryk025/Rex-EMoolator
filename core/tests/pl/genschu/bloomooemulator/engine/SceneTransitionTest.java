package pl.genschu.bloomooemulator.engine;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import pl.genschu.bloomooemulator.TestEnvironment;
import pl.genschu.bloomooemulator.interpreter.variable.StringVariable;
import pl.genschu.bloomooemulator.logic.GameEntry;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/**
 * GOTO/BACK timing as RE'd from CMC_Scene::domodal (BlooMoo, PIKLIB 6.1-8):
 * the request is only stored, the switch happens after the pulse, and the
 * new scene's own GOTO from __INIT__ is dropped unless it was entered directly.
 */
class SceneTransitionTest {
    @TempDir Path root;

    @BeforeAll
    static void boot() { TestEnvironment.init(); }

    @Test
    void gotoIsDeferredAndBackWalksTheHistory() throws IOException {
        writeGame();
        GameEntry entry = new GameEntry("transition-test", root.toString(), "touch", false, false, true);
        Game game = new Game(entry, null);
        game.loadGame();

        // START is the episode's start scene: entered directly, so the GOTO in
        // its __INIT__ survives, and the rest of __INIT__ still ran.
        assertEquals("START", game.getCurrentScene());
        assertTrue(game.hasPendingTransition());
        assertEquals("S", trace(game));

        game.applyPendingTransitions();
        // A was entered through the pending slot: its own GOTO("B") is dropped.
        assertEquals("A", game.getCurrentScene());
        assertFalse(game.hasPendingTransition());
        assertEquals("SA", trace(game));

        // A script keeps running in the old scene after GOTO.
        game.runScript("{EP^GOTO(\"B\");TRACE^ADD(CURRENT^GET());}");
        assertEquals("A", game.getCurrentScene());
        assertEquals("SAa", trace(game));
        game.applyPendingTransitions();
        assertEquals("B", game.getCurrentScene());
        assertEquals("A", game.getLatestScene("EP"));

        // History is START, A, B. BACK pops B and re-enters A, which is appended again.
        game.runScript("{EP^BACK();}");
        assertEquals("B", game.getCurrentScene());
        game.applyPendingTransitions();
        assertEquals("A", game.getCurrentScene());
        assertEquals("B", game.getLatestScene("EP"));

        // History is START, A, A: a second BACK lands on A again, not on START.
        game.runScript("{EP^BACK();}");
        game.applyPendingTransitions();
        assertEquals("A", game.getCurrentScene());
    }

    @Test
    void lastRequestInAPulseWins() throws IOException {
        writeGame();
        Game game = new Game(new GameEntry("transition-test", root.toString(), "touch", false, false, true), null);
        game.loadGame();
        game.applyPendingTransitions();

        game.runScript("{EP^GOTO(\"B\");EP^GOTO(\"C\");}");
        game.applyPendingTransitions();
        assertEquals("C", game.getCurrentScene());
    }

    private static String trace(Game game) {
        return ((StringVariable) game.getCurrentSceneContext().getVariable("TRACE")).getString();
    }

    private void writeGame() throws IOException {
        write("DANE/Application.def", """
                OBJECT=GAME
                GAME:TYPE=APPLICATION
                GAME:PATH=GAME
                GAME:EPISODES=EP
                GAME:STARTWITH=EP

                OBJECT=EP
                EP:TYPE=EPISODE
                EP:PATH=GAME\\EP
                EP:SCENES=START,A,B,C
                EP:STARTWITH=START

                OBJECT=START
                START:TYPE=SCENE
                START:PATH=GAME\\EP\\START

                OBJECT=A
                A:TYPE=SCENE
                A:PATH=GAME\\EP\\A

                OBJECT=B
                B:TYPE=SCENE
                B:PATH=GAME\\EP\\B

                OBJECT=C
                C:TYPE=SCENE
                C:PATH=GAME\\EP\\C
                """);
        write("DANE/GAME/GAME.cnv", """
                OBJECT=TRACE
                TRACE:TYPE=STRING
                TRACE:VALUE=
                """);
        scene("START", "{EP^GOTO(\"A\");TRACE^ADD(\"S\");}");
        scene("A", "{TRACE^ADD(\"A\");EP^GOTO(\"B\");}");
        scene("B", "{}");
        scene("C", "{}");
    }

    private void scene(String name, String init) throws IOException {
        write("DANE/GAME/EP/" + name + "/" + name + ".cnv", """
                OBJECT=CURRENT
                CURRENT:TYPE=STRING
                CURRENT:VALUE=%s

                OBJECT=__INIT__
                __INIT__:TYPE=BEHAVIOUR
                __INIT__:CODE=%s
                """.formatted(name.toLowerCase(), init));
    }

    private void write(String relative, String content) throws IOException {
        Path file = root.resolve(relative);
        Files.createDirectories(file.getParent());
        Files.writeString(file, content, StandardCharsets.UTF_8);
    }
}
