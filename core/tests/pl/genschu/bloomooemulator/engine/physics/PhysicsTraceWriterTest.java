package pl.genschu.bloomooemulator.engine.physics;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.api.parallel.ResourceLock;
import org.junit.jupiter.api.parallel.Resources;
import pl.genschu.bloomooemulator.world.GameObject;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PhysicsTraceWriterTest {
    @Test
    void writesDeterministicFramesCompatibleWithFridaSchema(@TempDir Path tempDir) throws Exception {
        Path output = tempDir.resolve("physics.jsonl");
        GameObject id7a = object(7, 1, 2, 3, true);
        GameObject id3 = object(3, -1, -2, -3, false);
        GameObject id7b = object(7, 4, 5, 6, true);

        try (PhysicsTraceWriter writer = new PhysicsTraceWriter(output, "emulator-test")) {
            writer.writeFrame(0.01, 0.01, List.of(id7a, id3, id7b));
            writer.writeFrame(0.05, 0.02, List.of(id7a, id3, id7b));
        }

        List<String> lines = Files.readAllLines(output);
        assertEquals(3, lines.size());
        assertTrue(lines.get(0).contains("\"schema\":1"));
        assertTrue(lines.get(0).contains("\"coordinate_system\":\"sekai_world_x_y_up_z\""));

        String firstFrame = lines.get(1);
        assertTrue(firstFrame.contains("\"frame\":0,\"reported_dt\":0.01,\"dt\":0.01,\"time\":0.01"));
        assertTrue(firstFrame.indexOf("\"id\":3") < firstFrame.indexOf("\"id\":7"),
                "objects should be sorted by ID");
        assertTrue(firstFrame.contains("\"id\":7,\"ordinal\":0"));
        assertTrue(firstFrame.contains("\"id\":7,\"ordinal\":1"));
        assertTrue(firstFrame.contains("\"x\":-1.0,\"y\":-2.0,\"z\":-3.0"));
        assertTrue(lines.get(2).contains("\"frame\":1,\"reported_dt\":0.05,\"dt\":0.02,\"time\":0.03"));
    }

    @Test
    @ResourceLock(Resources.SYSTEM_PROPERTIES)
    void odeEngineEnablesTraceOnlyWhenConfigured(@TempDir Path tempDir) throws Exception {
        Path output = tempDir.resolve("configured.jsonl");
        String previous = System.getProperty(PhysicsTraceWriter.PROPERTY_PATH);
        System.setProperty(PhysicsTraceWriter.PROPERTY_PATH, output.toString());

        ODEPhysicsEngine physics = new ODEPhysicsEngine();
        try {
            physics.init();
            physics.stepSimulation();
        } finally {
            physics.shutdown();
            if (previous == null) {
                System.clearProperty(PhysicsTraceWriter.PROPERTY_PATH);
            } else {
                System.setProperty(PhysicsTraceWriter.PROPERTY_PATH, previous);
            }
        }

        List<String> lines = Files.readAllLines(output);
        assertEquals(2, lines.size());
        assertTrue(lines.get(1).contains("\"type\":\"frame\",\"frame\":0"));
    }

    private static GameObject object(int id, float x, float y, float z, boolean dynamic) {
        return GameObject.builder()
                .id(id)
                .position(x, y, z)
                .rigidBody(dynamic)
                .build();
    }
}
