package pl.genschu.bloomooemulator.engine.physics;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.genschu.bloomooemulator.TestEnvironment;
import pl.genschu.bloomooemulator.geometry.points.Point3D;
import pl.genschu.bloomooemulator.interpreter.values.BoolValue;
import pl.genschu.bloomooemulator.interpreter.values.DoubleValue;
import pl.genschu.bloomooemulator.interpreter.values.IntValue;
import pl.genschu.bloomooemulator.interpreter.variable.ImageVariable;
import pl.genschu.bloomooemulator.interpreter.variable.WorldVariable;
import pl.genschu.bloomooemulator.loader.SEKLoader;
import pl.genschu.bloomooemulator.world.GameObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * WORLD route following on a SEK graph, driven the way the ARCADE scripts drive it:
 * MOVEOBJECTS, then FOLLOWPATH, GETMOVEDISTANCE and GETANGLE every frame.
 */
class ODEPhysicsPathTest {
    private static final int WALKER = 100;
    private static final int ROUTES = 9000;
    private static final double STEP = 0.03;

    private WorldVariable world;
    private IPhysicsEngine physics;

    @BeforeAll
    static void boot() {
        TestEnvironment.init();
    }

    @BeforeEach
    void setUp() throws IOException {
        world = new WorldVariable("WPATH");
        physics = world.getPhysicsEngine();
        physics.init();
        // A horizontal row of nodes 10 apart, y = -200 in physics space (canvas y = 500):
        // x = -100 .. 100; the last five (x >= 60) carry tag 10.
        SEKLoader.loadSek(world, new ByteArrayInputStream(routeSek()));
        physics.setGravity(0, 0, 0);
        // Same dynamics as the walkers in the ARCADE masks: friction(2500), maxVel(10000).
        physics.createBody(WALKER, 1, -1, 2500, 0, 0, 10000, 1, 2, 10, 10, 10);
    }

    @AfterEach
    void tearDown() {
        world.state().dispose();
    }

    @Test
    void sekRoutePointsKeepTheirTags() {
        GameObject routes = physics.getGameObjects().stream()
                .filter(go -> go.getPathfinder() != null).findFirst().orElseThrow();

        assertEquals(0, routes.getPathfinder().graph().node(0).tag());
        assertEquals(10, routes.getPathfinder().graph().node(20).tag());
        assertEquals(List.of(0, 10), List.of(
                routes.getPointsData().getTags().get(15),
                routes.getPointsData().getTags().get(16)));
    }

    @Test
    void setActiveOnARouteGraphCutsOffTheTaggedNodes_stawPondRegression() {
        teleport(300, 500);

        findPath(498, 500);
        assertEquals(100.0, lastWaypoint().x, "all nodes active: the route reaches the far end");

        world.callMethod("SETACTIVE", new IntValue(ROUTES), new IntValue(10), new BoolValue(false));
        findPath(498, 500);
        assertEquals(50.0, lastWaypoint().x, "the route must stop at the last node outside the cut-off group");

        world.callMethod("SETACTIVE", new IntValue(ROUTES), new IntValue(10), new BoolValue(true));
        findPath(498, 500);
        assertEquals(100.0, lastWaypoint().x);
    }

    @Test
    void teleportIsNotReportedAsMovement_sceneEntryFacingRegression() {
        // Scene entry: SETPOSITION, then FINDPATH to the same spot. Nothing moves, so the
        // scripts must not see a move distance and replay a walking animation.
        teleport(300, 500);
        findPath(300, 500);

        assertEquals(1, walker().getIsAtGoal(), "start and target share a node: ATGOAL");
        assertTrue(walker().getPath().isEmpty());

        physics.stepSimulation(STEP);
        followPath();

        assertEquals(0.0, physics.getMoveDistance(WALKER));
        assertEquals(0, angle());
    }

    @Test
    void walkerCoastsPastTheLastWaypointAndStopsFacingItsSide() {
        // 179, not 180: Sekai's 32-bit 180/pi is slightly short and World.dll truncates.
        assertEquals(179, walkAndReturnAngleOfTheStoppingFrame(481, 500, 320, 500), "walking left");
        assertEquals(0, walkAndReturnAngleOfTheStoppingFrame(319, 500, 480, 500), "walking right");
    }

    @Test
    void waypointExactlyAtTheArrivalRadiusNeitherSteersNorCounts() {
        // Sekai steers only when the waypoint is farther than the radius and advances only when
        // it is nearer, so a resting body exactly on the radius stays put (the Reksio i Wehikuł
        // Czasu scripts nudge every start position by 0.2 px).
        teleport(480, 500);
        physics.setSpeed(WALKER, 0, 0, 0);
        findPath(320, 500);

        for (int frame = 0; frame < 5; frame++) {
            physics.stepSimulation(STEP);
            followPath();
        }

        assertEquals(80.0, physics.getPosition(WALKER)[0]);
        assertEquals(60.0, walker().getCurrentPathPoint().x);
    }

    @Test
    void goalStateIsReportedOnceToALinkedVariable() {
        physics.linkVariable(new ImageVariable("ANNREX"), WALKER);
        teleport(300, 500);
        findPath(300, 500);
        assertEquals(1, walker().getIsAtGoal());

        physics.stepSimulation(STEP);

        assertEquals(0, walker().getIsAtGoal(), "ATGOAL is consumed by the frame that signals it");
    }

    @Test
    void missingRouteGraphIsReportedAsNoPath() {
        teleport(300, 500);

        world.callMethod("FINDPATH", new IntValue(WALKER), new IntValue(1234),
                new IntValue(400), new IntValue(500), new IntValue(0), new BoolValue(false));

        assertEquals(2, walker().getIsAtGoal());
    }

    /** Walks between two canvas points and returns GETANGLE of the frame in which the body stops. */
    private int walkAndReturnAngleOfTheStoppingFrame(int fromX, int fromY, int toX, int toY) {
        teleport(fromX, fromY);
        physics.setSpeed(WALKER, 0, 0, 0);
        findPath(toX, toY);
        walker().pollIsAtGoal();

        boolean coasted = false;
        for (int frame = 0; frame < 200; frame++) {
            physics.stepSimulation(STEP);
            followPath();
            double speed = Math.hypot(physics.getSpeed(WALKER)[0], physics.getSpeed(WALKER)[1]);

            if (walker().getPath().isEmpty() && speed > 0) {
                coasted = true; // route finished, friction has not stopped the body yet
            }
            if (frame > 0 && speed == 0.0) {
                assertTrue(coasted, "the body must keep moving after the last waypoint");
                assertEquals(1, walker().getIsAtGoal());
                assertTrue(physics.getMoveDistance(WALKER) > 0,
                        "the stopping frame still reports movement, so scripts apply its angle");
                return angle();
            }
        }
        throw new AssertionError("the walker never stopped");
    }

    private GameObject walker() {
        return physics.getGameObjects().stream()
                .filter(go -> go.getId() == WALKER).findFirst().orElseThrow();
    }

    private Point3D lastWaypoint() {
        return walker().getPath().peekLast();
    }

    private void teleport(int canvasX, int canvasY) {
        world.callMethod("SETPOSITION", new IntValue(WALKER),
                new IntValue(canvasX), new IntValue(canvasY), new IntValue(0));
    }

    private void findPath(int canvasX, int canvasY) {
        world.callMethod("FINDPATH", new IntValue(WALKER), new IntValue(ROUTES),
                new IntValue(canvasX), new IntValue(canvasY), new IntValue(0), new BoolValue(false));
    }

    private void followPath() {
        world.callMethod("FOLLOWPATH", new IntValue(WALKER),
                new IntValue(20), new DoubleValue(0.5), new DoubleValue(300));
    }

    private int angle() {
        return ((IntValue) world.callMethod("GETANGLE", new IntValue(WALKER)).returnValue()).value();
    }

    private static byte[] routeSek() throws IOException {
        int nodes = 21;
        ByteBuffer entity = ByteBuffer.allocate(4096).order(ByteOrder.LITTLE_ENDIAN);
        entity.putInt(ROUTES);          // entity id
        entity.putInt(0);               // flags: not a body
        for (int i = 0; i < 9; i++) {
            entity.putFloat(0f);        // position, rotation, dimensions
        }
        entity.putInt(0);               // no properties
        entity.putInt(nodes);
        entity.putInt(nodes - 1);
        for (int i = 0; i < nodes; i++) {
            float x = -100 + 10 * i;
            entity.putFloat(x).putFloat(-200f).putFloat(0f);
            entity.putInt(x >= 60 ? 10 : 0);
        }
        for (int i = 0; i + 1 < nodes; i++) {
            entity.putInt(i).putInt(i + 1).putInt(3);
        }

        ByteArrayOutputStream sek = new ByteArrayOutputStream();
        sek.write("SEKAI81080701915".getBytes(StandardCharsets.US_ASCII));
        sek.write("004".getBytes(StandardCharsets.US_ASCII));
        ByteBuffer header = ByteBuffer.allocate(12).order(ByteOrder.LITTLE_ENDIAN);
        header.putInt(1);               // entity count
        header.putInt(4);               // entity type: route points
        header.putInt(entity.position());
        sek.write(header.array());
        sek.write(entity.array(), 0, entity.position());
        return sek.toByteArray();
    }
}
