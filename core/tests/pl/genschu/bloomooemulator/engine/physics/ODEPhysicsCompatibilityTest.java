package pl.genschu.bloomooemulator.engine.physics;

import org.junit.jupiter.api.Test;
import pl.genschu.bloomooemulator.engine.compatibility.CompatibilityProfile;
import pl.genschu.bloomooemulator.engine.compatibility.EngineVariant;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ODEPhysicsCompatibilityTest {
    @Test
    void reksioICzarodziejeAlwaysUsesOnePhysicsStep() {
        ODEPhysicsEngine engine = new ODEPhysicsEngine();
        engine.configureCompatibility(new CompatibilityProfile(
                EngineVariant.PIKLIB_8,
                "reksio-czarodzieje"
        ));

        assertEquals(1, engine.calculateSubstepCount(0.03));
    }

    @Test
    void reksioIWehikulCzasuUsesSubsteps() {
        ODEPhysicsEngine engine = new ODEPhysicsEngine();
        engine.configureCompatibility(new CompatibilityProfile(
                EngineVariant.PIKLIB_8,
                "reksio-wehikul-czasu"
        ));

        assertEquals(2, engine.calculateSubstepCount(0.03));
    }
}
