package pl.genschu.bloomooemulator.interpreter.variable;

/**
 * Observer interface for playback events on AnimoVariable and SoundVariable.
 *
 * Allows SequenceVariable (and potentially other composites) to be notified
 * when an animation or sound starts/finishes without replacing signal handlers.
 */
public interface PlaybackObserver {
    void onPlaybackStarted(Variable source, String eventName);
    void onPlaybackFinished(Variable source, String eventName);
}
