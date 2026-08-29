package pl.genschu.bloomooemulator.geometry.coordinates;

/** A bottom-left-origin rectangle ready to pass to the logical OpenGL renderer. */
public record OpenGlRect(double x, double y, double width, double height) {}
