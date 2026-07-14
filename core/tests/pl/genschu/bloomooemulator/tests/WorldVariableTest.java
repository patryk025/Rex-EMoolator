package pl.genschu.bloomooemulator.tests;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import pl.genschu.bloomooemulator.TestEnvironment;
import pl.genschu.bloomooemulator.builders.ContextBuilder;
import pl.genschu.bloomooemulator.engine.Game;
import pl.genschu.bloomooemulator.engine.filesystem.LocalFileSystem;
import pl.genschu.bloomooemulator.engine.physics.ODEPhysicsEngine;
import pl.genschu.bloomooemulator.interpreter.context.Context;
import pl.genschu.bloomooemulator.interpreter.values.DoubleValue;
import pl.genschu.bloomooemulator.interpreter.values.IntValue;
import pl.genschu.bloomooemulator.interpreter.variable.WorldVariable;
import pl.genschu.bloomooemulator.loader.CNVParser;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WorldVariableTest {

    @BeforeAll
    static void boot() {
        TestEnvironment.init();
    }

    @Test
    void declarativeFilenameInitializesPhysicsAndLoadsSek_footballMatchRegression(
            @TempDir Path tempDir) throws IOException {
        Path assets = Files.createDirectories(tempDir.resolve("DANE"));
        Files.write(assets.resolve("BOISKO.SEK"), emptySek004());

        Game game = new Game(null, null);
        game.getVfs().mountAssets(new LocalFileSystem(tempDir.toFile()));
        Context context = new ContextBuilder().build();
        context.setGame(game);

        String cnv = """
                OBJECT=WORLD
                WORLD:TYPE=WORLD
                WORLD:FILENAME=BOISKO.SEK
                """;

        new CNVParser().parse(
                new ByteArrayInputStream(cnv.getBytes(StandardCharsets.UTF_8)),
                "FootballMatch.cnv",
                context);

        WorldVariable world = (WorldVariable) context.getVariable("WORLD");
        try {
            assertEquals("BOISKO.SEK", world.getFilename());
            assertEquals("004", world.getSekVersion());

            world.callMethod("SETGRAVITY", new DoubleValue(0), new DoubleValue(0), new DoubleValue(0));
            world.callMethod("ADDBODY",
                    new IntValue(101),
                    new DoubleValue(1), new DoubleValue(1), new DoubleValue(1),
                    new DoubleValue(0), new DoubleValue(0), new DoubleValue(100),
                    new IntValue(0), new IntValue(2),
                    new DoubleValue(8), new DoubleValue(0), new DoubleValue(0));
            assertEquals(1, world.getPhysicsEngine().getGameObjects().size());
        } finally {
            world.state().dispose();
        }
    }

    @Test
    void gravityCenterAttractsBallWithoutSetGOnBall_footballMatchRegression() {
        ODEPhysicsEngine physics = new ODEPhysicsEngine();
        physics.init();
        try {
            physics.setGravity(0, 0, 0);
            physics.createBody(101, 10, 1, 0, 0, 0, 100_000, 0, 2, 8, 0, 0);
            physics.createBody(1000, 1, 1, 0, 0, 0, 100_000, 1, 2, 8, 0, 0);
            physics.setPosition(101, 100, 0, 0);
            physics.setPosition(1000, 0, 0, 0);
            physics.setGravityCenter(101, true);
            physics.setG(101, 100_000);

            physics.stepSimulation(0.01);

            assertTrue(physics.getSpeed(1000)[0] > 0,
                    "Ball should accelerate toward a positive-G player without SETG on the ball");
        } finally {
            physics.shutdown();
        }
    }

    @Test
    void bodyTypeZeroPlayerStaysFixedAndStillCollidesWithBall_footballMatchRegression() {
        ODEPhysicsEngine physics = new ODEPhysicsEngine();
        physics.init();
        try {
            physics.setGravity(0, 0, 0);
            physics.createBody(101, 10, 1, 0, 0, 0, 100_000, 0, 2, 8, 0, 0);
            physics.createBody(102, 10, 1, 0, 0, 0, 100_000, 0, 2, 8, 0, 0);
            physics.createBody(1000, 1, 1, 0, 0, 0, 100_000, 1, 2, 8, 0, 0);
            physics.setPosition(101, 100, 100, 0);
            physics.setPosition(102, 140, 100, 0);
            physics.setPosition(1000, 115, 100, 0);
            physics.setGravityCenter(101, true);
            physics.setGravityCenter(102, true);
            physics.setG(101, 100_000);
            physics.setG(102, 100_000);

            physics.stepSimulation(0.01);

            assertEquals(100, physics.getPosition(101)[0], 0.0001);
            assertEquals(140, physics.getPosition(102)[0], 0.0001);
            assertTrue(physics.getCollision(1000, 101),
                    "A static player must still report its collision with the dynamic ball");
        } finally {
            physics.shutdown();
        }
    }

    private static byte[] emptySek004() throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        output.write("SEKAI81080701915".getBytes(StandardCharsets.US_ASCII));
        output.write("004".getBytes(StandardCharsets.US_ASCII));
        output.write(new byte[4]);
        return output.toByteArray();
    }
}
