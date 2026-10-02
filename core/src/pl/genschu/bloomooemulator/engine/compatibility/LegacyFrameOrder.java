package pl.genschu.bloomooemulator.engine.compatibility;

/** Observable ordering of script input, canvas presentation and managers in one frame. */
public enum LegacyFrameOrder {
    INPUT_MANAGERS_RENDER,
    INPUT_RENDER_MANAGERS,
    RENDER_INPUT_MANAGERS;

    /** Runs one host frame; input/managers are skipped when no pulse was admitted. */
    public void execute(boolean runLegacyPulse,
                        Runnable legacyInput,
                        Runnable render,
                        Runnable managers) {
        switch (this) {
            case INPUT_MANAGERS_RENDER -> {
                if (runLegacyPulse) {
                    legacyInput.run();
                    managers.run();
                }
                render.run();
            }
            case INPUT_RENDER_MANAGERS -> {
                if (runLegacyPulse) {
                    legacyInput.run();
                }
                render.run();
                if (runLegacyPulse) {
                    managers.run();
                }
            }
            case RENDER_INPUT_MANAGERS -> {
                render.run();
                if (runLegacyPulse) {
                    legacyInput.run();
                    managers.run();
                }
            }
        }
    }
}
