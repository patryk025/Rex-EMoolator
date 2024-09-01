package pl.genschu.bloomooemulator.interpreter.variable.types;

import com.badlogic.gdx.Gdx;
import pl.genschu.bloomooemulator.interpreter.Context;
import pl.genschu.bloomooemulator.interpreter.variable.*;
import pl.genschu.bloomooemulator.loader.SEQParser;
import pl.genschu.bloomooemulator.utils.ArgumentsHelper;

import java.util.*;

public class SequenceVariable extends Variable {
	private final Map<String, AnimoVariable> animoCache = new HashMap<>();
	protected Map<String, SequenceEvent> eventMap = new LinkedHashMap<>();
	private String currentEventName;
	private AnimoVariable currentAnimo;
	private boolean isPlaying;
    private boolean isVisible = true;

	public SequenceVariable(String name, Context context) {
		super(name, context);
	}

	@Override
	protected void setMethods() {
		super.setMethods();

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
				isVisible = false;
				return null;
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
				getCurrentAnimo().setPlaying(false);
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
				if(eventMap.isEmpty()) {
					loadSequence();
				}

				for(AnimoVariable animoCache : animoCache.values()) {
					animoCache.setPlaying(false);
					try {
						getAttribute("VISIBLE").setValue("FALSE");
					} catch (NullPointerException e) {
						setAttribute("VISIBLE", new Attribute("BOOL", "FALSE"));
					}
				}

				String eventName = ArgumentsHelper.getString(arguments.get(0));
				playEvent(eventName);
				return null;
			}
		});
		this.setMethod("STOP", new Method(
				List.of(
						new Parameter("STRING", "eventName", false)
				),
				"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				if(!arguments.isEmpty()) {
					String eventName = ArgumentsHelper.getString(arguments.get(0));
					if (currentEventName.equals(eventName)) {
						isPlaying = false;
						getCurrentAnimo().setPlaying(false);
					}
				} else {
					isPlaying = false;
					getCurrentAnimo().setPlaying(false);
				}
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
			}
		}
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
    
    public boolean isVisible() {
		return isVisible;
	}

	@Override
	public void setAttribute(String name, Attribute attribute) {
		List<String> knownAttributes = List.of("FILENAME");
		if (knownAttributes.contains(name)) {
			super.setAttribute(name, attribute);
		}
	}

	public AnimoVariable getCurrentAnimo() {
		return currentAnimo;
	}

	public Map<String, SequenceEvent> getEventMap() {
		return eventMap;
	}

	public Map<String, AnimoVariable> getAnimoCache() {
		return animoCache;
	}

	public static class SequenceEvent extends SequenceVariable{
		protected final SequenceVariable parent;
		private final String mode;
		private final Queue<SequenceEvent> eventQueue;

		public SequenceEvent(String name, SequenceVariable parent, String mode) {
			super(name, parent.getContext());
			this.parent = parent;
			this.mode = mode;
			this.eventQueue = new LinkedList<>();
		}

		public void play(SequenceVariable parent) {
			if (this.mode != null) {
				if(this.mode.equals("SEQUENCE")) {
					eventQueue.addAll(this.getEventMap().values());
					playNextEvent(parent);
				}
				else if(this.mode.equals("RANDOM")) {
					List<SequenceEvent> events = new ArrayList<>(this.eventMap.values());
					Collections.shuffle(events);
					eventQueue.add(events.get(0));
					playNextEvent(parent);
				}
			}
		}

		private void playNextEvent(SequenceVariable parent) {
			if (eventQueue.isEmpty()) {
				parent.emitSignal("ONFINISHED", parent.currentEventName);
				return;
			}

			SequenceEvent nextEvent = eventQueue.poll();

			Gdx.app.log("SequenceEvent", "Playing next event: " + nextEvent.getName());
			Gdx.app.log("SequenceEvent", "Queue size: " + eventQueue.size());

			Signal oldGenericSignal = null;
			Signal oldSignal = null;

			if(nextEvent instanceof SpeakingEvent) {
				oldGenericSignal = ((SpeakingEvent) nextEvent).soundVariable.getSignal("ONFINISHED");
			}
			else if(nextEvent instanceof SimpleEvent) {
				oldGenericSignal = ((SimpleEvent) nextEvent).animoVariable.getSignal("ONFINISHED");
				oldSignal = ((SimpleEvent) nextEvent).animoVariable.getSignal("ONFINISHED^"+nextEvent.getName());
			}

			Signal finalOldSignal = oldSignal;
			Signal finalOldGenericSignal = oldGenericSignal;
			nextEvent.setSignal("ONFINISHED", new Signal() {
				@Override
				public void execute(Object argument) {
					emitSignal("ONFINISHED", nextEvent.getName());
					parent.emitSignal("ONFINISHED", nextEvent.getName());
					playNextEvent(parent);
					if(finalOldSignal != null) finalOldSignal.execute(argument);
					else if(finalOldGenericSignal != null) finalOldGenericSignal.execute(argument);
				}
			});

			nextEvent.play(parent);
		}
	}

	public class SimpleEvent extends SequenceEvent {
		private final StringVariable event;
		private final AnimoVariable animoVariable;

		public SimpleEvent(String name, String filename, String event, SequenceVariable parent) {
			super(name, parent, null);
			this.event = new StringVariable("", event, parent.getContext());
			this.animoVariable = loadAnimoVariable(filename, parent);
		}

		private AnimoVariable loadAnimoVariable(String filename, SequenceVariable parent) {
			AnimoVariable animoVar = parent.animoCache.get(filename);
			if (animoVar == null) {
				// at first try to find ANIMO variable with the same filename
				animoVar = findAnimoWithFileName(filename, parent);
				if(animoVar == null) {
					animoVar = new AnimoVariable(filename.replace(".ANN", ""), parent.getContext());
					animoVar.setAttribute("FILENAME", filename);
					animoVar.setAttribute("TOCANVAS", new Attribute("BOOL", "TRUE"));
				}
				parent.animoCache.put(filename, animoVar);
			}
			return animoVar;
		}

		@Override
		public void play(SequenceVariable parent) {
			Gdx.app.log("SimpleEvent", "Playing event " + event);
			this.animoVariable.getMethod("PLAY", Collections.singletonList("STRING")).execute(List.of(event));

			this.animoVariable.setSignal("ONFINISHED__SEQ^" + event, new Signal() { // setting generic event (non existent in original engine)
				@Override
				public void execute(Object argument) {
					emitSignal("ONFINISHED", SimpleEvent.this.getName());
					parent.emitSignal("ONFINISHED", SimpleEvent.this.getName());
				}
			});
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
			super(name, parent, null);
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
				// at first try to find ANIMO variable with the same filename
				animoVar = findAnimoWithFileName(filename, parent);
				if(animoVar == null) {
					animoVar = new AnimoVariable(filename.replace(".ANN", ""), parent.getContext());
					animoVar.setAttribute("FILENAME", filename);
					animoVar.setAttribute("TOCANVAS", new Attribute("BOOL", "TRUE"));
				}
				parent.animoCache.put(filename, animoVar);
			}
			return animoVar;
		}

		@Override
		public void play(SequenceVariable parent) {
			final int[] animoNumber = {1};
            
			Signal onMainFinished = new Signal() {
				@Override
				public void execute(Object argument) {
					parent.getCurrentAnimo().setPlaying(false);

					if (ending) {
						playAnimation(parent, prefix + "_STOP", new Signal() {
							@Override
							public void execute(Object argument) {
								emitSignal("ONFINISHED", SpeakingEvent.this.getName());
								parent.emitSignal("ONFINISHED", SpeakingEvent.this.getName());
							}
						});
					} else {
						emitSignal("ONFINISHED", SpeakingEvent.this.getName());
						parent.emitSignal("ONFINISHED", SpeakingEvent.this.getName());
					}
				}
			};

			Signal onStartFinished = new Signal() {
				@Override
				public void execute(Object argument) {
					playAnimation(parent, prefix + "_" + animoNumber[0], new Signal() {
						@Override
						public void execute(Object argument) {
							if(currentAnimo.hasEvent(prefix + "_" + (animoNumber[0]+1))) {
								animoNumber[0] += 1;
							}
							else {
								animoNumber[0] = 1;
							}
							playAnimation(parent, prefix + "_" + animoNumber[0], this);
						}
					});
                    try {
					    playSound(onMainFinished);
                    } catch(Exception e) {
                        Gdx.app.error("SpeakingEvent", "Error on sound playing: "+e.getMessage(), e);
                        onMainFinished.execute(null);
                    }
				}
			};

			if (starting) {
				playAnimation(parent, prefix + "_START", onStartFinished);
			} else {
				playAnimation(parent, prefix + "_" + animoNumber[0], new Signal() {
                    @Override
				    public void execute(Object argument) {
						if(!currentAnimo.hasEvent(prefix + "_" + (++animoNumber[0]))) {
							animoNumber[0] = 1;
						}
						playAnimation(parent, prefix + "_" + animoNumber[0], this);
					}
                });
                try {
					playSound(onMainFinished);
                } catch(Exception e) {
                    Gdx.app.error("SpeakingEvent", "Error on sound playing: "+e.getMessage(), e);
                    onMainFinished.execute(null);
                }
			}

		}

		private void playAnimation(SequenceVariable parent, String event, Signal onFinished) {
			Gdx.app.log("SpeakingEvent", "Playing animation " + event);
			StringVariable prefixVar = new StringVariable("", event, parent.getContext());
			this.animoVariable.getMethod("PLAY", Collections.singletonList("STRING")).execute(List.of(prefixVar));
			this.animoVariable.setSignal("ONFINISHED__SEQ^" + event, onFinished);
			parent.setPlaying(true);
			parent.setCurrentAnimo(animoVariable);
		}

		private void playSound(Signal onFinished) {
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

	private AnimoVariable findAnimoWithFileName(String filename, SequenceVariable parent) {
		Gdx.app.log("SequenceVariable", "Looking for ANIMO variable with filename: " + filename);
		List<Variable> graphicsVariables = new ArrayList<>(parent.getContext().getGraphicsVariables().values());

		for(Variable variable : graphicsVariables) {
			if(variable instanceof AnimoVariable) {
				AnimoVariable animoVar = (AnimoVariable) variable;
				if(animoVar.getAttribute("FILENAME").getValue().equals(filename)) {
					Gdx.app.log("SequenceVariable", "Found ANIMO variable with filename: " + filename);
					animoVar.setAttribute("TOCANVAS", new Attribute("BOOL", "TRUE"));
					animoVar.setAttribute("VISIBLE", new Attribute("BOOL", "TRUE"));
					return animoVar;
				}
			}
		}

		Gdx.app.log("SequenceVariable", "Could not find ANIMO variable with filename: " + filename + ". Looking for variable with name: " + filename);
		Variable animoVar = getContext().getVariable(filename);
		if(animoVar instanceof AnimoVariable) {
			Gdx.app.log("SequenceVariable", "Found ANIMO variable with name: " + filename);
			return (AnimoVariable) animoVar;
		}

		Gdx.app.log("SequenceVariable", "Could not find ANIMO variable with filename: " + filename);
		return null;
	}

	public String getCurrentEventName() {
		return currentEventName;
	}

	public void setCurrentEventName(String currentEventName) {
		this.currentEventName = currentEventName;
	}
}
