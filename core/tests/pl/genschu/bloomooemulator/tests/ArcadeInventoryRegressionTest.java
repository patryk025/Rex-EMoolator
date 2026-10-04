package pl.genschu.bloomooemulator.tests;

import com.badlogic.gdx.Gdx;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import pl.genschu.bloomooemulator.TestEnvironment;
import pl.genschu.bloomooemulator.builders.MethodHelper;
import pl.genschu.bloomooemulator.engine.Game;
import pl.genschu.bloomooemulator.engine.filesystem.LocalFileSystem;
import pl.genschu.bloomooemulator.interpreter.context.Context;
import pl.genschu.bloomooemulator.interpreter.runtime.ExecutionContext;
import pl.genschu.bloomooemulator.interpreter.values.IntValue;
import pl.genschu.bloomooemulator.interpreter.values.StringValue;
import pl.genschu.bloomooemulator.interpreter.variable.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ArcadeInventoryRegressionTest {
    @TempDir Path tempDir;

    @BeforeAll
    static void boot() { TestEnvironment.init(); }

    @Test
    void loadedSceneSignalRemovesHeldCloneBeforeReusingInventorySlot() throws Exception {
        Context scene = new Context(new ExecutionContext());
        Context loaded = new Context(new ExecutionContext(), scene);
        scene.addAdditionalContext(loaded);
        Game game = new Game(null, null);
        game.setLanguage("POL");
        game.getVfs().mountAssets(new LocalFileSystem(tempDir.toFile()));
        scene.setGame(game);
        loaded.setGame(game);
        game.setCurrentSceneContext(scene);
        Path assets = Files.createDirectories(tempDir.resolve("DANE"));
        Files.copy(Gdx.files.internal("../assets/test-assets/MLYNEK.ANN").file().toPath(),
                assets.resolve("NEXT_ITEM.ANN"));

        scene.setVariable("ARCADE", new SceneVariable("ARCADE"));
        scene.setVariable("ANNITEM0", new AnimoVariable("ANNITEM0"));
        MethodHelper.callWithContext(scene, "ANNITEM0", "CLONE", new IntValue(1));
        Variable oldClone = scene.store().get("ANNITEM0_1");

        // TELEP_PODWORKO's signal executes with its CNVLOADER context, while
        // the inventory animations and their clones belong to the ARCADE parent.
        MethodHelper.callWithContext(loaded, "ARCADE", "REMOVECLONES",
                new StringValue("ANNITEM0"), new IntValue(-1), new IntValue(-1));
        assertFalse(scene.hasVariable("ANNITEM0_1"));
        assertFalse(scene.getGraphicsVariables().containsKey("ANNITEM0_1"));
        MethodHelper.callWithContext(loaded, "ANNITEM0", "RESETCLONES");
        MethodHelper.callWithContext(loaded, "ANNITEM0", "LOAD", new StringValue("NEXT_ITEM.ANN"));
        assertNull(loaded.store().get("ANNITEM0"), "LOAD must replace the parent slot, not shadow it");
        AnimoVariable next = (AnimoVariable) scene.getVariable("ANNITEM0");
        assertTrue(next.data().imagesCount() > 0);
        assertSame(next, loaded.getVariable("ANNITEM0"));
        assertSame(next, scene.getGraphicsVariables().get("ANNITEM0"));

        MethodHelper.callWithContext(scene, "ANNITEM0", "CLONE", new IntValue(1));
        AnimoVariable nextClone = (AnimoVariable) scene.store().get("ANNITEM0_1");
        assertNotSame(oldClone, nextClone);
        assertSame(next.state().currentImage, nextClone.state().currentImage);
        assertEquals("NEXT_ITEM.ANN", nextClone.state().filename);
    }

    @Test
    void hoverHighlightCanReadInventoryIconOpacity() {
        Context scene = new Context(new ExecutionContext());
        scene.setVariable("ANNITEM0", new AnimoVariable("ANNITEM0"));
        scene.setVariable("ANNSELECT", new AnimoVariable("ANNSELECT"));
        scene.setVariable("SHOW", BehaviourVariable.fromScript("SHOW", "{ANNSELECT^SHOW();}", Map.of()));
        scene.setVariable("HIDE", BehaviourVariable.fromScript("HIDE", "{ANNSELECT^HIDE();}", Map.of()));
        Variable hover = BehaviourVariable.fromScript("HOVER",
                "{@IF(ANNITEM0^GETOPACITY(),\"_\",\"255\",\"SHOW\",\"HIDE\");}", Map.of());
        for (int opacity : new int[]{0, 128, 255, 0}) {
            MethodHelper.callWithContext(scene, "ANNITEM0", "SETOPACITY", new IntValue(opacity));
            MethodHelper.callWithContext(scene, hover, "RUN");
            assertEquals(opacity == 255, ((AnimoVariable) scene.getVariable("ANNSELECT")).isVisible());
        }
    }

    @Test
    void removingClonesDoesNotResetNumberingAndSparseRangesStillRemoveAll() {
        Context scene = new Context(new ExecutionContext());
        scene.setVariable("ARCADE", new SceneVariable("ARCADE"));
        scene.setVariable("ITEM", new AnimoVariable("ITEM"));
        MethodHelper.callWithContext(scene, "ITEM", "CLONE", new IntValue(3));
        MethodHelper.callWithContext(scene, "ARCADE", "REMOVECLONES",
                new StringValue("ITEM"), new IntValue(1), new IntValue(2));
        MethodHelper.callWithContext(scene, "ITEM", "CLONE");
        assertTrue(scene.hasVariable("ITEM_4"));
        MethodHelper.callWithContext(scene, "ARCADE", "REMOVECLONES",
                new StringValue("ITEM"), new IntValue(-1), new IntValue(-1));
        assertFalse(scene.hasVariable("ITEM_3"));
        assertFalse(scene.hasVariable("ITEM_4"));
        MethodHelper.callWithContext(scene, "ITEM", "RESETCLONES");
        MethodHelper.callWithContext(scene, "ITEM", "CLONE");
        assertTrue(scene.hasVariable("ITEM_1"));
        MethodHelper.callWithContext(scene, "ITEM", "RESETCLONES");
        assertTrue(scene.hasVariable("ITEM_1"), "RESETCLONES only resets numbering, not live clones");
    }
}
