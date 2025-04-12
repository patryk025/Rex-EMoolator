package pl.genschu.bloomooemulator.interpreter.variable.types;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import pl.genschu.bloomooemulator.interpreter.exceptions.ClassMethodNotImplementedException;
import pl.genschu.bloomooemulator.interpreter.Context;
import pl.genschu.bloomooemulator.interpreter.variable.Attribute;
import pl.genschu.bloomooemulator.interpreter.variable.Method;
import pl.genschu.bloomooemulator.interpreter.variable.Parameter;
import pl.genschu.bloomooemulator.interpreter.variable.Variable;
import pl.genschu.bloomooemulator.loader.SoundLoader;
import pl.genschu.bloomooemulator.utils.ArgumentsHelper;

import java.util.List;

public class SoundVariable extends Variable {
	private Music sound;
	private boolean isPlaying;

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
				if(sound == null) {
					loadSound();
				}
				try {
					sound.play();
					isPlaying = true;
					sound.setVolume(1.0f);
					context.getGame().getPlayingAudios().add(sound);
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
	public void setAttribute(String name, Attribute attribute) {
		List<String> knownAttributes = List.of("FILENAME", "FLUSHAFTERPLAYED", "PRELOAD", "RELEASE");
		if(knownAttributes.contains(name)) {
			super.setAttribute(name, attribute);

			if(name.equals("FILENAME")) {
				loadSound();
			}
		}
	}

	public void loadSound() {
		if(sound == null) {
			SoundLoader.loadSound(this);
			if(sound != null) {
				sound.setOnCompletionListener(music -> {
					emitSignal("ONFINISHED");
				});
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

	public Music getSound() {
		return sound;
	}

	public void setSound(Music sound) {
		this.sound = sound;
	}

	public boolean isPlaying() {
		return isPlaying;
	}
}
