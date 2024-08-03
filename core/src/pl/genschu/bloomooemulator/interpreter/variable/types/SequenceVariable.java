package pl.genschu.bloomooemulator.interpreter.variable.types;

import com.badlogic.gdx.Gdx;
import pl.genschu.bloomooemulator.interpreter.Context;
import pl.genschu.bloomooemulator.interpreter.exceptions.ClassMethodNotImplementedException;
import pl.genschu.bloomooemulator.interpreter.variable.*;
import pl.genschu.bloomooemulator.loader.SEQParser;

import java.util.*;

public class SequenceVariable extends Variable {
	private final Map<String, AnimoVariable> animoCache = new HashMap<>();
	protected Map<String, SequenceEvent> eventMap = new HashMap<>();
	private String currentEventName;
	private AnimoVariable currentAnimo;
	private boolean isPlaying;

	public SequenceVariable(String name, Context context) {
		super(name, context);

		this.setMethod("GETEVENTNAME", new Method(
				"STRING"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				return new StringVariable("", currentEventName, getContext());
			}
		});
		this.setMethod("HIDE", new Method(
				"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				throw new ClassMethodNotImplementedException("Method HIDE is not implemented yet");
			}
		});
		this.setMethod("ISPLAYING", new Method(
				"BOOL"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				return new BoolVariable("", isPlaying, getContext());
			}
		});
		this.setMethod("PAUSE", new Method(
				"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				isPlaying = false;
				return null;
			}
		});
		this.setMethod("PLAY", new Method(
				List.of(
						new Parameter("STRING", "eventName", true)
				),
				"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				String eventName = ((StringVariable) arguments.get(0)).GET();
				playEvent(eventName);
				return null;
			}
		});
	}

	public void loadSequence() {
		try {
			SEQParser.parseFile(this);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private void playEvent(String eventName) {
		SequenceEvent event = eventMap.get(eventName);
		if (event != null) {
			currentEventName = eventName;
			event.play(this); // Pass the parent object to the play method
		} else {
			Gdx.app.error("SequenceVariable", "Event not found: " + eventName);
		}
	}

	public void updateAnimation(float deltaTime) {
		if (isPlaying && currentAnimo != null) {
			currentAnimo.updateAnimation(deltaTime);
			if (!currentAnimo.isPlaying()) {
				isPlaying = false;
				emitSignal("ONFINISHED", currentEventName);
			}
		}
	}

	public String getCurrentEventName() {
		return currentEventName;
	}

	public void setCurrentEventName(String currentEventName) {
		this.currentEventName = currentEventName;
	}

	public void setCurrentAnimo(AnimoVariable currentAnimo) {
		this.currentAnimo = currentAnimo;
	}

	public boolean isPlaying() {
		return isPlaying;
	}

	public void setPlaying(boolean playing) {
		isPlaying = playing;
	}

	@Override
	public String getType() {
		return "SEQUENCE";
	}

	@Override
	public void setAttribute(String name, Attribute attribute) {
		List<String> knownAttributes = List.of("FILENAME");
		if (knownAttributes.contains(name)) {
			super.setAttribute(name, attribute);
			loadSequence();
		}
	}

	public AnimoVariable getCurrentAnimo() {
		return currentAnimo;
	}

	public Map<String, SequenceEvent> getEventMap() {
		return eventMap;
	}

	public void setEventMap(Map<String, SequenceEvent> eventMap) {
		this.eventMap = eventMap;
	}

	public Map<String, AnimoVariable> getAnimoCache() {
		return animoCache;
	}

	public class SequenceEvent extends SequenceVariable{
		protected final SequenceVariable parent;

		public SequenceEvent(String name, SequenceVariable parent) {
			super(name, parent.getContext());
			this.parent = parent;
		}

		public void play(SequenceVariable parent) {
			Gdx.app.log("SequenceEvent", "Playing from sequence subtype not supported."); // That's how engine works
		}
	}

	public class SimpleEvent extends SequenceEvent {
		private final StringVariable event;
		private final AnimoVariable animoVariable;

		public SimpleEvent(String name, String filename, String event, SequenceVariable parent) {
			super(name, parent);
			this.event = new StringVariable("", event, parent.getContext());
			this.animoVariable = loadAnimoVariable(filename, parent);
		}

		private AnimoVariable loadAnimoVariable(String filename, SequenceVariable parent) {
			AnimoVariable animoVar = parent.animoCache.get(filename);
			if (animoVar == null) {
				animoVar = new AnimoVariable(filename.replace(".ANN", ""), parent.getContext());
				animoVar.setAttribute("FILENAME", filename);
				parent.animoCache.put(filename, animoVar);
			}
			return animoVar;
		}

		@Override
		public void play(SequenceVariable parent) {
			Gdx.app.log("SimpleEvent", "Playing event " + parent.currentEventName);
			this.animoVariable.getMethod("PLAY", Collections.singletonList("STRING")).execute(List.of(event));
			parent.setPlaying(true);
			parent.setCurrentAnimo(animoVariable);
		}
	}

	public class SpeakingEvent extends SequenceEvent {
		private final String prefix;
		private final boolean starting;
		private final boolean ending;
		private final AnimoVariable animoVariable;
		private final SoundVariable soundVariable;

		public SpeakingEvent(String name, String animofn, String prefix, String wavfn, boolean starting, boolean ending, SequenceVariable parent) {
			super(name, parent);
			this.animoVariable = loadAnimoVariable(animofn, parent);
			this.prefix = prefix;
			this.soundVariable = new SoundVariable("", parent.getContext());
			this.soundVariable.setAttribute("FILENAME", wavfn);
			this.starting = starting;
			this.ending = ending;
		}

		private AnimoVariable loadAnimoVariable(String filename, SequenceVariable parent) {
			AnimoVariable animoVar = parent.animoCache.get(filename);
			if (animoVar == null) {
				animoVar = new AnimoVariable(filename.replace(".ANN", ""), parent.getContext());
				animoVar.setAttribute("FILENAME", filename);
				parent.animoCache.put(filename, animoVar);
			}
			return animoVar;
		}

		@Override
		public void play(SequenceVariable parent) {
			Signal onMainFinished = new Signal() {
				@Override
				public void execute(Object argument) {
					parent.getCurrentAnimo().setPlaying(false);

					if (ending) {
						playAnimation(parent, prefix + "_STOP", new Signal() {
							@Override
							public void execute(Object argument) {
								parent.emitSignal("ONFINISHED", parent.currentEventName);
							}
						});
					} else {
						parent.emitSignal("ONFINISHED", parent.currentEventName);
					}
				}
			};

			Signal onStartFinished = new Signal() {
				@Override
				public void execute(Object argument) {
					playAnimation(parent, prefix + "_1", new Signal() {
						@Override
						public void execute(Object argument) {
							playAnimation(parent, prefix + "_1", this);
						}
					});
					playSound(parent, onMainFinished);
				}
			};

			if (starting) {
				playAnimation(parent, prefix + "_START", onStartFinished);
			} else {
				playAnimation(parent, prefix, onMainFinished);
			}

		}

		private void playAnimation(SequenceVariable parent, String event, Signal onFinished) {
			Gdx.app.log("SpeakingEvent", "Playing animation " + event);
			StringVariable prefixVar = new StringVariable("", event, parent.getContext());
			this.animoVariable.getMethod("PLAY", Collections.singletonList("STRING")).execute(List.of(prefixVar));
			this.animoVariable.setSignal("ONFINISHED^" + event, onFinished);
			parent.setPlaying(true);
			parent.setCurrentAnimo(animoVariable);
		}

		private void playSound(SequenceVariable parent, Signal onFinished) {
			this.soundVariable.getMethod("PLAY", Collections.emptyList()).execute(Collections.emptyList());
			this.soundVariable.setSignal("ONFINISHED", onFinished);
		}

		public void emitSignal(String signalName) {
			Signal signal = getSignal(signalName);
			if (signal != null) {
				signal.execute(null);
			}
		}
	}
}
