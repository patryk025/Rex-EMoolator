package pl.genschu.bloomooemulator.interpreter.v1.variable.types;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.TimeUtils;
import pl.genschu.bloomooemulator.interpreter.v1.Context;
import pl.genschu.bloomooemulator.interpreter.v1.variable.Attribute;
import pl.genschu.bloomooemulator.interpreter.v1.variable.Method;
import pl.genschu.bloomooemulator.interpreter.v1.variable.Parameter;
import pl.genschu.bloomooemulator.interpreter.v1.variable.Variable;
import pl.genschu.bloomooemulator.loader.SoundLoader;
import pl.genschu.bloomooemulator.utils.ArgumentsHelper;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class SoundVariable extends Variable {
    private static final Map<String, List<Method>> METHOD_TEMPLATES = createMethodTemplates();

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
        this.methods = METHOD_TEMPLATES;
    }

    private static Map<String, List<Method>> createMethodTemplates() {
        Map<String, List<Method>> methods = newTemplateMap(baseMethodTemplates());

        addMethodTemplate(methods, "ISPLAYING", new Method(
                "BOOL"
        ) {
            @Override
            public Variable execute(Variable self, List<Object> arguments) {
                SoundVariable selfVar = (SoundVariable) self;
                return new BoolVariable("", selfVar.isPlaying, selfVar.getContext());
            }
        });
        addMethodTemplate(methods, "LOAD", new Method(
                List.of(
                        new Parameter("STRING", "path", true)
                ),
                "void"
        ) {
            @Override
            public Variable execute(Variable self, List<Object> arguments) {
                SoundVariable selfVar = (SoundVariable) self;
                if(selfVar.sound != null) {
                    selfVar.sound.stop();
                    selfVar.sound.dispose();
                    selfVar.sound = null;
                }
                selfVar.isPlaying = false;
                selfVar.setAttribute("FILENAME", new Attribute("STRING", ArgumentsHelper.getString(arguments.get(0))));
                selfVar.init();
                return null;
            }
        });
        addMethodTemplate(methods, "PAUSE", new Method(
                "void"
        ) {
            @Override
            public Variable execute(Variable self, List<Object> arguments) {
                SoundVariable selfVar = (SoundVariable) self;
                selfVar.pause();
                return null;
            }
        });
        addMethodTemplate(methods, "PLAY", new Method(
                List.of(
                        new Parameter("STRING", "unknown", false)
                ),
                "void"
        ) {
            @Override
            public Variable execute(Variable self, List<Object> arguments) {
                SoundVariable selfVar = (SoundVariable) self;
                selfVar.play();
                return null;
            }
        });
        addMethodTemplate(methods, "RESUME", new Method(
                "void"
        ) {
            @Override
            public Variable execute(Variable self, List<Object> arguments) {
                SoundVariable selfVar = (SoundVariable) self;
                selfVar.resume();
                return null;
            }
        });
        addMethodTemplate(methods, "SETFREQ", new Method(
                List.of(
                        new Parameter("INTEGER", "freq", false)
                ),
                "void"
        ) {
            @Override
            public Variable execute(Variable self, List<Object> arguments) {
                SoundVariable selfVar = (SoundVariable) self;
                selfVar.currentSampleRate = ArgumentsHelper.getInteger(arguments.get(0));
                selfVar.sound.setPitch(selfVar.soundId, (float) selfVar.currentSampleRate / selfVar.sampleRate);
                return null;
            }
        });
        addMethodTemplate(methods, "STOP", new Method(
                List.of(
                        new Parameter("BOOL", "emitSignal", false)
                ),
                "void"
        ) {
            @Override
            public Variable execute(Variable self, List<Object> arguments) {
                SoundVariable selfVar = (SoundVariable) self;
                boolean emitSignal = false;
                if(!arguments.isEmpty()) {
                    emitSignal = ArgumentsHelper.getBoolean(arguments.get(0));
                }
                selfVar.stop(emitSignal);
                return null;
            }
        });

        return Collections.unmodifiableMap(methods);
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

    public void play() {
        try {
            sound.stop();
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

    public void stop(boolean emitSignal) {
        try {
            sound.stop();
            context.getGame().getPlayingAudios().remove(SoundVariable.this);
        } catch(NullPointerException ignored) {}
        isPlaying = false;

        if(emitSignal) {
            emitSignal("ONFINISHED");
        }
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
