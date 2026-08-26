package pl.genschu.bloomooemulator.geometry.coordinates;

/** Axis-aligned bounds in native Sekai/ODE world coordinates. */
public record PhysicsBox(PhysicsPoint min, PhysicsPoint max) {
    public PhysicsBox {
        if (min.x() > max.x() || min.y() > max.y() || min.z() > max.z()) {
            throw new IllegalArgumentException("Physics box must be normalized");
        }
    }
}
