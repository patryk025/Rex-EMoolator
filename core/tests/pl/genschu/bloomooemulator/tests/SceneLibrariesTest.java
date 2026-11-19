package pl.genschu.bloomooemulator.tests;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import pl.genschu.bloomooemulator.TestEnvironment;
import pl.genschu.bloomooemulator.builders.ContextBuilder;
import pl.genschu.bloomooemulator.engine.Game;
import pl.genschu.bloomooemulator.interpreter.context.Context;
import pl.genschu.bloomooemulator.interpreter.variable.SceneVariable;
import pl.genschu.bloomooemulator.loader.CNVParser;

import java.io.IOException;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class SceneLibrariesTest {
    @BeforeAll
    static void boot() { TestEnvironment.init(); }

    @Test
    void parsesAndNormalizesLibraryNamesWithoutRepairingInvalidNames() throws IOException {
        Context context = new ContextBuilder().build();
        new CNVParser().parseString("OBJECT=S\nS:TYPE=SCENE\nS:DLLS= inertia ,\tWORLD\t,inertia,,IN ERTIA,\n", context);
        SceneVariable scene = (SceneVariable) context.getVariable("S");
        assertEquals(Set.of("INERTIA", "WORLD", "IN ERTIA"), scene.dlls());
        assertTrue(scene.isDllLoaded(" inertia "));
        assertFalse(scene.isDllLoaded("MATRIX"));
        assertFalse(scene.isDllLoaded(null));
        assertThrows(UnsupportedOperationException.class, () -> scene.dlls().add("MATRIX"));
        assertEquals(scene.dlls(), scene.withMusic("MUSIC.WAV").dlls());
    }

    @Test
    void gatesEveryExtensionAndKeepsBuiltInTypesAvailable() throws IOException {
        Context context = new ContextBuilder().build();
        Game game = new Game(null, null);
        context.setGame(game);
        CNVParser parser = new CNVParser();
        parser.parseString("OBJECT=S\nS:TYPE=SCENE\nS:DLLS=inertia,kolorowanka,matrix,world\n", context);
        SceneVariable enabled = (SceneVariable) context.getVariable("S");
        for (String type : Set.of("INERTIA", "KOLOROWANKA", "MATRIX", "WORLD")) {
            game.setCurrentSceneVariable(enabled);
            parser.parseString("OBJECT=YES_" + type + "\nYES_" + type + ":TYPE=" + type, context);
            assertTrue(context.store().has("YES_" + type), type);
            game.setCurrentSceneVariable(new SceneVariable("EMPTY"));
            parser.parseString("OBJECT=NO_" + type + "\nNO_" + type + ":TYPE=" + type, context);
            assertFalse(context.store().has("NO_" + type), type);
        }
        parser.parseString("OBJECT=N\nN:TYPE=INTEGER\nN:VALUE=7", context);
        assertTrue(context.store().has("N"));
    }

    @Test
    void missingGameOrSceneDoesNotPreventParsingRemainingObjects() throws IOException {
        for (boolean attachGame : new boolean[]{false, true}) {
            Context context = new ContextBuilder().build();
            if (attachGame) context.setGame(new Game(null, null));
            new CNVParser().parseString("OBJECT=I\nI:TYPE=INERTIA\nOBJECT=N\nN:TYPE=INTEGER\nN:VALUE=7", context);
            assertFalse(context.store().has("I"));
            assertTrue(context.store().has("N"));
        }
    }
}
