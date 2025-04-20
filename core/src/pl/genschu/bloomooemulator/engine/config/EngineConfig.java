package pl.genschu.bloomooemulator.engine.config;

import com.badlogic.gdx.Application;

/**
 * Class containing game engine configuration.
 * Manages debugging settings, logging levels, etc.
 */
public class EngineConfig {
    // debug settings
    private boolean debugGraphics = false;
    private boolean debugGraphicsBounds = false;
    private boolean debugVariables = false;
    private boolean debugButtons = false;
    private boolean debugCollisions = false;
    private boolean debugMatrix = false;

    // render settings
    private boolean maintainAspectRatio = true;
    private boolean vsync = true;
    private int targetFPS = 60;

    // log level settings
    private int logLevel = Application.LOG_DEBUG;

    // sound settings
    private boolean soundEnabled = true;
    private float musicVolume = 1.0f;
    private float soundVolume = 1.0f;

    public EngineConfig() {}

    // getters, setters, whatever
    public boolean isDebugGraphics() {
        return debugGraphics;
    }

    public boolean isDebugGraphicsBounds() {
        return debugGraphicsBounds;
    }

    public void setDebugGraphics(boolean debugGraphics) {
        this.debugGraphics = debugGraphics;
    }

    public boolean isDebugVariables() {
        return debugVariables;
    }

    public void setDebugVariables(boolean debugVariables) {
        this.debugVariables = debugVariables;
    }

    public boolean isDebugButtons() {
        return debugButtons;
    }

    public void setDebugButtons(boolean debugButtons) {
        this.debugButtons = debugButtons;
    }

    public boolean isDebugCollisions() {
        return debugCollisions;
    }

    public void setDebugCollisions(boolean debugCollisions) {
        this.debugCollisions = debugCollisions;
    }

    public void toggleDebugGraphics() {
        this.debugGraphics = !this.debugGraphics;
    }

    public void toggleDebugGraphicsBounds() {
        this.debugGraphicsBounds = !this.debugGraphicsBounds;
    }

    public void toggleDebugVariables() {
        this.debugVariables = !this.debugVariables;
    }

    public void toggleDebugButtons() {
        this.debugButtons = !this.debugButtons;
    }

    public void toggleDebugCollisions() {
        this.debugCollisions = !this.debugCollisions;
    }

    public boolean isDebugMatrix() {
        return debugMatrix;
    }

    public void toggleDebugMatrix() {
        this.debugMatrix = !this.debugMatrix;
    }

    public boolean isMaintainAspectRatio() {
        return maintainAspectRatio;
    }

    public void setMaintainAspectRatio(boolean maintainAspectRatio) {
        this.maintainAspectRatio = maintainAspectRatio;
    }

    public boolean isVsync() {
        return vsync;
    }

    public void setVsync(boolean vsync) {
        this.vsync = vsync;
    }

    public int getTargetFPS() {
        return targetFPS;
    }

    public void setTargetFPS(int targetFPS) {
        this.targetFPS = targetFPS;
    }

    public int getLogLevel() {
        return logLevel;
    }

    public void setLogLevel(int logLevel) {
        this.logLevel = logLevel;
    }

    public boolean isSoundEnabled() {
        return soundEnabled;
    }

    public void setSoundEnabled(boolean soundEnabled) {
        this.soundEnabled = soundEnabled;
    }

    public float getMusicVolume() {
        return musicVolume;
    }

    public void setMusicVolume(float musicVolume) {
        this.musicVolume = Math.max(0.0f, Math.min(1.0f, musicVolume));
    }

    public float getSoundVolume() {
        return soundVolume;
    }

    public void setSoundVolume(float soundVolume) {
        this.soundVolume = Math.max(0.0f, Math.min(1.0f, soundVolume));
    }

    /**
     * Loads configuration from a file (to be implemented)
     * @param configPath Path to the configuration file
     */
    public void loadFromFile(String configPath) {
        // TODO: Implementacja ładowania konfiguracji z pliku
    }

    /**
     * Saves configuration to a file (to be implemented)
     * @param configPath Path to the configuration file
     */
    public void saveToFile(String configPath) {
        // TODO: Implementacja zapisywania konfiguracji do pliku
    }

    /**
     * Restore default settings
     */
    public void resetToDefaults() {
        debugGraphics = false;
        debugVariables = false;
        debugButtons = false;
        debugCollisions = false;

        maintainAspectRatio = true;
        vsync = true;
        targetFPS = 60;

        logLevel = Application.LOG_DEBUG;

        soundEnabled = true;
        musicVolume = 1.0f;
        soundVolume = 1.0f;
    }
}