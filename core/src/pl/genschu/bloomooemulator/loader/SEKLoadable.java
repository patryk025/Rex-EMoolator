package pl.genschu.bloomooemulator.loader;

import pl.genschu.bloomooemulator.engine.physics.IPhysicsEngine;

/**
 * Interface for variables that can be loaded by SEKLoader.
 */
public interface SEKLoadable {
    String getName();
    IPhysicsEngine getPhysicsEngine();
    String getSekVersion();
    void setSekVersion(String version);
    String getFilename();
    pl.genschu.bloomooemulator.engine.Game getGameReference();
}
