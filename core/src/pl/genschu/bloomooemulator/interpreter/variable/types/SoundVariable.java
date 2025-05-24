package pl.genschu.bloomooemulator.interpreter.variable.types;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.TimeUtils;
import pl.genschu.bloomooemulator.interpreter.Context;
import pl.genschu.bloomooemulator.interpreter.variable.Attribute;
import pl.genschu.bloomooemulator.interpreter.variable.Method;
import pl.genschu.bloomooemulator.interpreter.variable.Parameter;
import pl.genschu.bloomooemulator.interpreter.variable.Variable;
import pl.genschu.bloomooemulator.loader.SoundLoader;
import pl.genschu.bloomooemulator.utils.ArgumentsHelper;

import java.util.List;

public class SoundVariable extends Variable {
    private Sound sound;
    private boolean isPlaying;
    private long soundId;
    private int currentSampleRate;
    private int sampleRate;
    private int channels;
    private int bitsPerSample;
    private float duration;
    private float playStartTime;

    public SoundVariable(String name, Context context) {
        super(name, context);
    }

    @Override
    protected void setMethods() {
        super.setMethods();

        this.setMethod("ISPLAYING", new Method(
                "BOOL"
        ) {
            @Override
            public Variable execute(List<Object> arguments) {
                return new BoolVariable("", isPlaying, getContext());
            }
        });
        this.setMethod("LOAD", new Method(
                List.of(
                        new Parameter("STRING", "path", true)
                ),
                "void"
        ) {
            @Override
            public Variable execute(List<Object> arguments) {
                if(sound != null) {
                    sound.stop();
                    sound.dispose();
                    sound = null;
                }
                isPlaying = false;
                setAttribute("FILENAME", new Attribute("STRING", ArgumentsHelper.getString(arguments.get(0))));
                init();
                return null;
            }
        });
        this.setMethod("PAUSE", new Method(
                "void"
        ) {
            @Override
            public Variable execute(List<Object> arguments) {
                pause();
                return null;
            }
        });
        this.setMethod("PLAY", new Method(
                List.of(
                        new Parameter("STRING", "unknown", false)
                ),
                "void"
        ) {
            @Override
            public Variable execute(List<Object> arguments) {
                try {
                    soundId = sound.play();
                    isPlaying = true;
                    sound.setVolume(soundId, 1.0f);
                    sound.setPitch(soundId, (float) currentSampleRate/sampleRate);
                    sound.setLooping(soundId, false);
                    playStartTime = TimeUtils.nanoTime() / 1_000_000_000f;
                    context.getGame().getPlayingAudios().add(SoundVariable.this);
                    emitSignal("ONSTARTED");
                } catch(Exception e) {
                    Gdx.app.log("SoundVariable", "Error on playing sound: "+e.getMessage(), e);
                    emitSignal("ONFINISHED");
                }
                return null;
            }
        });
        this.setMethod("RESUME", new Method(
                "void"
        ) {
            @Override
            public Variable execute(List<Object> arguments) {
                resume();
                return null;
            }
        });
        this.setMethod("SETFREQ", new Method(
                List.of(
                        new Parameter("INTEGER", "freq", false)
                ),
                "void"
        ) {
            @Override
            public Variable execute(List<Object> arguments) {
                currentSampleRate = ArgumentsHelper.getInteger(arguments.get(0));
                sound.setPitch(soundId, (float) currentSampleRate/sampleRate);
                return null;
            }
        });
        this.setMethod("STOP", new Method(
                List.of(
                        new Parameter("BOOL", "emitSignal", false)
                ),
                "void"
        ) {
            @Override
            public Variable execute(List<Object> arguments) {
                try {
                    sound.stop();
					context.getGame().getPlayingAudios().remove(SoundVariable.this);
                } catch(NullPointerException ignored) {}
                isPlaying = false;

                if(!arguments.isEmpty()) {
                    if(ArgumentsHelper.getBoolean(arguments.get(0))) {
                        emitSignal("ONFINISHED");
                    }
                }
                return null;
            }
        });
    }

    @Override
    public String getType() {
        return "SOUND";
    }

    @Override
    public void init() {
        loadSound();
    }

    @Override
    public void setAttribute(String name, Attribute attribute) {
        List<String> knownAttributes = List.of("FILENAME", "FLUSHAFTERPLAYED", "PRELOAD", "RELEASE");
        if(knownAttributes.contains(name)) {
            super.setAttribute(name, attribute);
        }
    }

    public void loadSound() {
        if(sound == null) {
            SoundLoader.loadSound(this);
        }
    }

    public void update() {
        if (isPlaying && playStartTime > 0) {
            float now = TimeUtils.nanoTime() / 1_000_000_000f;
            if (now - playStartTime >= duration / ((float) currentSampleRate / sampleRate)) {
                isPlaying = false;
                context.getGame().getPlayingAudios().remove(SoundVariable.this);
                emitSignal("ONFINISHED");
            }
        }
    }

    public void pause() {
        if(sound != null) {
            sound.pause();
        }
        isPlaying = false;
    }

    public void resume() {
        if(sound != null) {
            sound.play();
        }
        isPlaying = true;
    }

    public Sound getSound() {
        return sound;
    }

    public void setSound(Sound sound) {
        this.sound = sound;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setDuration(float duration) {
        this.duration = duration;
    }

    public void setSampleRate(int sampleRate) {
        this.sampleRate = sampleRate;
		this.currentSampleRate = sampleRate;
    }

    public void setChannels(int channels) {
        this.channels = channels;
    }

    public void setBitsPerSample(int bitsPerSample) {
        this.bitsPerSample = bitsPerSample;
    }
}
