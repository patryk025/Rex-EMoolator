package pl.genschu.bloomooemulator.interpreter.variable.types;

import com.badlogic.gdx.audio.Sound;
import pl.genschu.bloomooemulator.interpreter.exceptions.ClassMethodNotImplementedException;
import pl.genschu.bloomooemulator.interpreter.Context;
import pl.genschu.bloomooemulator.interpreter.variable.Attribute;
import pl.genschu.bloomooemulator.interpreter.variable.Method;
import pl.genschu.bloomooemulator.interpreter.variable.Parameter;
import pl.genschu.bloomooemulator.interpreter.variable.Variable;
import pl.genschu.bloomooemulator.loader.SoundLoader;

import java.util.List;

public class SoundVariable extends Variable {
	private Sound sound;
	private long soundId;
	private boolean isPlaying;

	public SoundVariable(String name, Context context) {
		super(name, context);

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
				setAttribute("FILENAME", new Attribute("STRING", ((Variable) arguments.get(0)).getValue().toString()));
				return null;
			}
		});
		this.setMethod("PAUSE", new Method(
			"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				sound.pause(soundId);
				isPlaying = false;
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
				if(sound == null) {
					loadSound();
				}
				soundId = sound.play();
				isPlaying = true;
				sound.setVolume(soundId, 1.0f);
				return null;
			}
		});
		this.setMethod("RESUME", new Method(
			"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				sound.resume(soundId);
				isPlaying = true;
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
				sound.stop(soundId);
				isPlaying = false;
				return null;
			}
		});
	}

	@Override
	public String getType() {
		return "SOUND";
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

	public Sound getSound() {
		return sound;
	}

	public void setSound(Sound sound) {
		this.sound = sound;
	}
}
