package pl.genschu.bloomooemulator.geometry.coordinates;

/** A point in native Sekai/ODE world coordinates: centred origin and Y growing up. */
public record PhysicsPoint(double x, double y, double z) {}
