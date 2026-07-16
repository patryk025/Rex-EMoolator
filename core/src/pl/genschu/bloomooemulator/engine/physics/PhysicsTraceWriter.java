package pl.genschu.bloomooemulator.engine.physics;

import org.ode4j.math.DVector3C;
import org.ode4j.ode.DBody;
import pl.genschu.bloomooemulator.world.GameObject;

import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Optional JSONL trace of the state immediately after each WORLD/MOVEOBJECTS physics step.
 *
 * <p>The format is intentionally shared with {@code tools/riwc_physics_trace_frida.js}.
 * Coordinates are raw Sekai world coordinates: origin in the centre of an unscrolled
 * 800x600 canvas, X right, Y up, and Z unchanged.
 */
final class PhysicsTraceWriter implements Closeable {
    static final String ENV_PATH = "REX_PHYSICS_TRACE";
    static final String PROPERTY_PATH = "rex.physics.trace";
    static final int SCHEMA_VERSION = 2;
    private static final int FLUSH_INTERVAL = 120;

    private final BufferedWriter writer;
    private long frame;
    private double time;

    static PhysicsTraceWriter openConfigured() throws IOException {
        String configuredPath = System.getProperty(PROPERTY_PATH);
        if (configuredPath == null || configuredPath.isBlank()) {
            configuredPath = System.getenv(ENV_PATH);
        }
        if (configuredPath == null || configuredPath.isBlank()) {
            return null;
        }
        return new PhysicsTraceWriter(Path.of(configuredPath.trim()), "emulator");
    }

    PhysicsTraceWriter(Path path, String source) throws IOException {
        Path absolutePath = path.toAbsolutePath();
        Path parent = absolutePath.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
        writer = Files.newBufferedWriter(
                absolutePath,
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING,
                StandardOpenOption.WRITE);
        writer.write("{\"type\":\"meta\",\"schema\":" + SCHEMA_VERSION
                + ",\"source\":" + jsonString(source)
                + ",\"coordinate_system\":\"sekai_world_x_y_up_z\"}");
        writer.newLine();
        writer.flush();
    }

    void writeFrame(double reportedDt, double dt, List<GameObject> objects) throws IOException {
        if (Double.isFinite(dt) && dt > 0.0) {
            time += dt;
        }

        // HashMap-backed engine storage does not guarantee iteration order. Sorting makes
        // traces reproducible; ordinal disambiguates legal duplicate IDs loaded from SEK.
        List<GameObject> ordered = new ArrayList<>(objects);
        ordered.sort(Comparator.comparingInt(GameObject::getId));
        Map<Integer, Integer> ordinals = new HashMap<>();

        StringBuilder line = new StringBuilder(256 + ordered.size() * 96);
        line.append("{\"type\":\"frame\",\"frame\":").append(frame)
                .append(",\"reported_dt\":").append(jsonNumber(reportedDt))
                .append(",\"dt\":").append(jsonNumber(dt))
                .append(",\"time\":").append(jsonNumber(time))
                .append(",\"objects\":[");

        boolean first = true;
        for (GameObject object : ordered) {
            if (!first) {
                line.append(',');
            }
            first = false;

            int ordinal = ordinals.getOrDefault(object.getId(), 0);
            ordinals.put(object.getId(), ordinal + 1);
            DVector3C velocity = object.getBody() instanceof DBody body
                    ? body.getLinearVel()
                    : null;
            line.append("{\"id\":").append(object.getId())
                    .append(",\"ordinal\":").append(ordinal)
                    .append(",\"x\":").append(jsonNumber(object.getX()))
                    .append(",\"y\":").append(jsonNumber(object.getY()))
                    .append(",\"z\":").append(jsonNumber(object.getZ()))
                    .append(",\"active\":").append(object.isActive())
                    .append(",\"dynamic\":").append(object.isRigidBody())
                    .append(",\"gravity_center\":").append(object.isGravityCenter())
                    .append(",\"mass\":").append(jsonNumber(object.getMass()))
                    .append(",\"g\":").append(jsonNumber(object.getG()))
                    .append(",\"max_velocity\":").append(jsonNumber(object.getMaxVelocity()))
                    .append(",\"vx\":").append(velocity != null ? jsonNumber(velocity.get0()) : "null")
                    .append(",\"vy\":").append(velocity != null ? jsonNumber(velocity.get1()) : "null")
                    .append(",\"vz\":").append(velocity != null ? jsonNumber(velocity.get2()) : "null")
                    .append('}');
        }
        line.append("]}");

        writer.write(line.toString());
        writer.newLine();
        frame++;
        if (frame % FLUSH_INTERVAL == 0) {
            writer.flush();
        }
    }

    private static String jsonNumber(double value) {
        return Double.isFinite(value) ? Double.toString(value) : "null";
    }

    private static String jsonString(String value) {
        StringBuilder escaped = new StringBuilder(value.length() + 2).append('"');
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            switch (c) {
                case '"' -> escaped.append("\\\"");
                case '\\' -> escaped.append("\\\\");
                case '\n' -> escaped.append("\\n");
                case '\r' -> escaped.append("\\r");
                case '\t' -> escaped.append("\\t");
                default -> escaped.append(c);
            }
        }
        return escaped.append('"').toString();
    }

    @Override
    public void close() throws IOException {
        writer.close();
    }
}
