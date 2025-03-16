package pl.genschu.bloomooemulator.interpreter.variable.types;

import com.badlogic.gdx.Gdx;
import pl.genschu.bloomooemulator.interpreter.Context;
import pl.genschu.bloomooemulator.interpreter.variable.*;
import pl.genschu.bloomooemulator.loader.SEQParser;
import pl.genschu.bloomooemulator.objects.Event;
import pl.genschu.bloomooemulator.utils.ArgumentsHelper;

import java.util.*;

public class SequenceVariable extends Variable {
	public enum EventType {
		SEQUENCE,
		SIMPLE,
		SPEAKING
	}

	public enum SequenceMode {
		PARAMETER,
		SEQUENCE,
		RANDOM
	}

	public static class SequenceEvent {
		private String name;
		private EventType type;
		private SequenceEvent parent;
		private List<SequenceEvent> subEvents;
		private AnimoVariable animation;
		private SoundVariable sound;
		private SequenceMode mode;
		private boolean isPlaying;
		private boolean isPaused;

		private String prefix;
		private boolean hasStartAnimation;
		private boolean hasEndAnimation;
		private int currentAnimationNumber;
		private boolean inStartPhase;
		private boolean inMainPhase;
		private boolean inEndPhase;

		public SequenceEvent(String name, EventType type) {
			this.name = name;
			this.type = type;
			this.parent = null;
			this.subEvents = new ArrayList<>();
			this.isPlaying = false;
			this.isPaused = false;
			this.currentAnimationNumber = 1;
			this.inStartPhase = false;
			this.inMainPhase = false;
			this.inEndPhase = false;
		}

		public AnimoVariable getAnimation() {
			return animation;
		}

		public void setAnimation(AnimoVariable animation) {
			this.animation = animation;
		}

		public SoundVariable getSound() {
			return sound;
		}

		public List<SequenceEvent> getSubEvents() {
			return subEvents;
		}

		public void setSound(SoundVariable sound) {
			this.sound = sound;
		}

		public void setMode(SequenceMode mode) {
			this.mode = mode;
		}

		public void setHasStartAnimation(boolean hasStartAnimation) {
			this.hasStartAnimation = hasStartAnimation;
		}

		public void setHasEndAnimation(boolean hasEndAnimation) {
			this.hasEndAnimation = hasEndAnimation;
		}

		public void setPrefix(String prefix) {
			this.prefix = prefix;
		}

		public void setParent(SequenceEvent parent) {
			this.parent = parent;
		}
	}

	private List<SequenceEvent> events;
	private SequenceEvent currentEvent;
	private SequenceMode mode;
	private boolean isVisible;
	private boolean isPlaying;
	private boolean isPaused;
	private Random random;
	private Map<String, SequenceEvent> eventsByName;

	public SequenceVariable(String name, Context context) {
		super(name, context);
		this.events = new ArrayList<>();
		this.eventsByName = new HashMap<>();
		this.isVisible = true;
		this.isPlaying = false;
		this.isPaused = false;
		this.random = new Random();
	}

	@Override
	public String getType() {
		return "SEQUENCE";
	}

	@Override
	protected void setMethods() {
		super.setMethods();

		this.setMethod("GETEVENTNAME", new Method("STRING") {
			@Override
			public Variable execute(List<Object> arguments) {
				if (currentEvent != null) {
					return new StringVariable("", currentEvent.name, context);
				}
				return new StringVariable("", "", context);
			}
		});

		this.setMethod("HIDE", new Method("void") {
			@Override
			public Variable execute(List<Object> arguments) {
				hideSequence();
				return null;
			}
		});

		this.setMethod("ISPLAYING", new Method("BOOL") {
			@Override
			public Variable execute(List<Object> arguments) {
				return new BoolVariable("", isPlaying, context);
			}
		});

		this.setMethod("PAUSE", new Method("void") {
			@Override
			public Variable execute(List<Object> arguments) {
				pauseSequence();
				return null;
			}
		});

		this.setMethod("PLAY", new Method(
				List.of(new Parameter("STRING", "eventName", true)),
				"void") {
			@Override
			public Variable execute(List<Object> arguments) {
				if(events.isEmpty()) {
					loadSequence();
				}

				String eventName = ArgumentsHelper.getString(arguments.get(0));
				playEvent(eventName);
				return null;
			}
		});

		this.setMethod("RESUME", new Method("void") {
			@Override
			public Variable execute(List<Object> arguments) {
				resumeSequence();
				return null;
			}
		});

		this.setMethod("SHOW", new Method("void") {
			@Override
			public Variable execute(List<Object> arguments) {
				if(events.isEmpty()) {
					loadSequence();
				}

				showSequence();
				return null;
			}
		});

		this.setMethod("STOP", new Method(
				List.of(new Parameter("BOOL", "emitSignal", false)),
				"void") {
			@Override
			public Variable execute(List<Object> arguments) {
				boolean emitSignal = !arguments.isEmpty() && ArgumentsHelper.getBoolean(arguments.get(0));
				stopSequence(emitSignal);
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

	private void hideSequence() {
		isVisible = false;
		if (currentEvent != null) {
			if (currentEvent.animation != null) {
				currentEvent.animation.fireMethod("HIDE");
			}
		}
		for (SequenceEvent event : events) {
			if (event.animation != null) {
				event.animation.fireMethod("HIDE");
			}
		}
	}

	private void showSequence() {
		isVisible = true;
		if (currentEvent != null && isPlaying && !isPaused) {
			if (currentEvent.animation != null) {
				currentEvent.animation.fireMethod("SHOW");
			}
		}
	}

	private void pauseSequence() {
		isPaused = true;
		if (currentEvent != null) {
			if(currentEvent.type == EventType.SEQUENCE) {
				for (SequenceEvent subEvent : currentEvent.subEvents) {
					if(subEvent.isPlaying) {
						if (subEvent.animation != null) {
							subEvent.animation.fireMethod("PAUSE");
						}
						if (subEvent.sound != null) {
							subEvent.sound.fireMethod("PAUSE");
						}
						subEvent.isPaused = true;
						return;
					}
				}
			}
			else {
				if (currentEvent.animation != null) {
					currentEvent.animation.fireMethod("PAUSE");
				}
				if (currentEvent.sound != null) {
					currentEvent.sound.fireMethod("PAUSE");
				}
				currentEvent.isPaused = true;
			}
		}
	}

	private void resumeSequence() {
		isPaused = false;
		if (currentEvent != null) {
			if(currentEvent.type == EventType.SEQUENCE) {
				for (SequenceEvent subEvent : currentEvent.subEvents) {
					if (subEvent.isPlaying && subEvent.isPaused) {
						if (subEvent.animation != null) {
							subEvent.animation.fireMethod("RESUME");
						}
						if (subEvent.sound != null) {
							subEvent.sound.fireMethod("RESUME");
						}
						subEvent.isPaused = false;
						return;
					}
				}
			}
			else {
				if (currentEvent.isPlaying && currentEvent.isPaused) {
					if (currentEvent.animation != null) {
						currentEvent.animation.fireMethod("RESUME");
					}
					if (currentEvent.sound != null) {
						currentEvent.sound.fireMethod("RESUME");
					}
					currentEvent.isPaused = false;
				}
			}
		}
	}

	private void stopSequence(boolean emitSignal) {
		Gdx.app.log("SequenceVariable", "stopSequence: " + currentEvent);
		if (currentEvent != null) {
			stopEvent(currentEvent);
			if (emitSignal) {
				emitSignal("ONFINISHED", currentEvent.name);
			}
			currentEvent = null;
			isPlaying = false;
			isPaused = false;
		}
	}

	private void stopEvent(SequenceEvent event) {
		Gdx.app.log("SequenceVariable", "stopEvent: " + event.name);
		event.isPlaying = false;
		event.inStartPhase = false;
		event.inMainPhase = false;
		event.inEndPhase = false;

		if (event.type == EventType.SEQUENCE) {
			for (SequenceEvent subEvent : event.subEvents) {
				if (subEvent.isPlaying) {
					stopEvent(subEvent);
				}
			}
		} else {
			if (event.animation != null) {
				if(event.animation.isPlaying())
					event.animation.fireMethod("STOP");
			}
			if (event.sound != null) {
				if(event.sound.isPlaying())
					event.sound.fireMethod("STOP");
			}
		}
	}

	private void playEvent(String eventName) {
		Gdx.app.log("SequenceVariable", "playEvent: " + eventName);
		SequenceEvent event = eventsByName.get(eventName);
		if (event != null) {
			stopSequence(false); // Stop current sequence
			currentEvent = event;
			isPlaying = true;
			startEvent(event);
		}
	}

	private void startEvent(SequenceEvent event) {
		Gdx.app.log("SequenceVariable", "startEvent: " + event.name);
		event.isPlaying = true;

		if (event.type == EventType.SEQUENCE) {
			if (event.mode == SequenceMode.SEQUENCE) {
				startSequentialEvent(event);
			} else if (event.mode == SequenceMode.RANDOM) {
				startRandomEvent(event);
			} else if (event.mode == SequenceMode.PARAMETER) {
				// not sure, what's happening in this mode
				if (!event.subEvents.isEmpty()) {
					int index = Integer.parseInt(event.name) - 1;
					if (index >= 0 && index < event.subEvents.size()) {
						startEvent(event.subEvents.get(index));
					} else {
						Gdx.app.error("SequenceVariable", "Invalid PARAMETER index: " + index);
					}
				}
			}
		} else {
			startBasicEvent(event);
		}
	}

	private void startBasicEvent(SequenceEvent event) {
		Gdx.app.log("SequenceVariable", "startBasicEvent: " + event.name);
		if (event.type == EventType.SPEAKING) {
			startSpeakingEvent(event);
		} else {
			if (event.animation != null) {
				event.animation.fireMethod("PLAY", new StringVariable("", event.prefix, context));
			}
			if (event.sound != null) {
				event.sound.fireMethod("PLAY");
			}
		}

		// Add ONFINISHED handler for audio in SPEAKING type
		if (event.type == EventType.SPEAKING && event.sound != null) {
			event.sound.setSignal("ONFINISHED", new Signal() {
				@Override
				public void execute(Object argument) {
					event.inMainPhase = false;
					if (event.hasEndAnimation) {
						playSpeakingEndAnimation(event);
					} else {
						handleEventFinished(event);
					}
				}
			});
		}
	}

	private void startSpeakingEvent(SequenceEvent event) {
		Gdx.app.log("SequenceVariable", "startSpeakingEvent: " + event.name);
		event.inStartPhase = event.hasStartAnimation;
		event.inMainPhase = false;
		event.inEndPhase = false;
		event.currentAnimationNumber = 1;

		if (event.hasStartAnimation) {
			playSpeakingStartAnimation(event);
		} else {
			startSpeakingMainPhase(event);
		}
	}

	private void playSpeakingStartAnimation(SequenceEvent event) {
		Gdx.app.log("SequenceVariable", "playSpeakingStartAnimation: " + event.name);
		String startAnimName = event.prefix + "_START";

		if (event.animation.hasEvent(startAnimName)) {
			event.animation.fireMethod("PLAY", new StringVariable("", startAnimName, context));

			event.animation.setSignal("ONFINISHED", new Signal() {
				@Override
				public void execute(Object argument) {
					startSpeakingMainPhase(event);
				}
			});
		} else {
			// if no starting animo, start immediately
			startSpeakingMainPhase(event);
		}
	}

	private void startSpeakingMainPhase(SequenceEvent event) {
		Gdx.app.log("SequenceVariable", "startSpeakingMainPhase: " + event.name);
		event.inStartPhase = false;
		event.inMainPhase = true;

		// play audio
		if (event.sound != null) {
			try {
				event.sound.fireMethod("PLAY");

				// run animo loop
				playSpeakingMainAnimation(event);
			} catch (Exception e) {
				Gdx.app.error("SequenceVariable", "Something went wrong while trying to play audio for " + event.name);

				event.inMainPhase = false;
				if (event.hasEndAnimation) {
					playSpeakingEndAnimation(event);
				} else {
					handleEventFinished(event);
				}
			}
		}
		else { // no sound, no speaking, skipping
			event.inMainPhase = false;
			if (event.hasEndAnimation) {
				playSpeakingEndAnimation(event);
			} else {
				handleEventFinished(event);
			}
		}
	}

	private void playSpeakingMainAnimation(SequenceEvent event) {
		Gdx.app.log("SequenceVariable", "playSpeakingMainAnimation: " + event.name);
		List<Event> eventsWithPrefix = event.animation.getEventsWithPrefix(event.prefix);
		List<Integer> validNumbers = new ArrayList<>();
		for (Event e : eventsWithPrefix) {
			String name = e.getName();
			if (!name.endsWith("_START") && !name.endsWith("_STOP")) {
				validNumbers.add(Integer.parseInt(name.substring(name.lastIndexOf("_") + 1)));
			}
		}

		if (validNumbers.isEmpty()) {
			Gdx.app.error("SequenceVariable", "No valid main animations for prefix: " + event.prefix);
			event.inMainPhase = false;
			if (event.hasEndAnimation) {
				playSpeakingEndAnimation(event);
			} else {
				handleEventFinished(event);
			}
			return;
		}

		int nextAnimNumber = validNumbers.get(random.nextInt(validNumbers.size()));
		String mainAnimName = event.prefix + "_" + nextAnimNumber;

		event.animation.fireMethod("PLAY", new StringVariable("", mainAnimName, context));

		event.animation.setSignal("ONFINISHED", new Signal() {
			@Override
			public void execute(Object argument) {
				if (event.inMainPhase && event.isPlaying && (event.sound == null || event.sound.isPlaying())) {
					playSpeakingMainAnimation(event);
				}
			}
		});
	}

	private void playSpeakingEndAnimation(SequenceEvent event) {
		Gdx.app.log("SequenceVariable", "playSpeakingEndAnimation: " + event.name);
		event.inMainPhase = false;
		event.inEndPhase = true;

		String endAnimName = event.prefix + "_STOP";

		if (event.animation.hasEvent(endAnimName)) {
			event.animation.fireMethod("PLAY", new StringVariable("", endAnimName, context));

			event.animation.setSignal("ONFINISHED^" + endAnimName, new Signal() {

				@Override
				public void execute(Object argument) {
					handleEventFinished(event);
				}
			});
		} else { // if no ending animo, end immediately
			handleEventFinished(event);
		}
	}

	private void startSequentialEvent(SequenceEvent event) {
		Gdx.app.log("SequenceVariable", "startSequentialEvent: " + event.name);
		if (!event.subEvents.isEmpty()) {
			emitSignal("ONSTARTED", event.name);
			startEvent(event.subEvents.get(0));
		}
	}

	private void startRandomEvent(SequenceEvent event) {
		Gdx.app.log("SequenceVariable", "startRandomEvent: " + event.name);
		if (!event.subEvents.isEmpty()) {
			int index = random.nextInt(event.subEvents.size());
			startEvent(event.subEvents.get(index));
		}
	}

	private void handleEventFinished(SequenceEvent event) {
		Gdx.app.log("SequenceVariable", "handleEventFinished: " + event.name);
		event.isPlaying = false;

		// If this is a sub-event, find its parent and handle next event
		SequenceEvent parentEvent = event.parent;
		if (parentEvent != null) {
			int currentIndex = parentEvent.subEvents.indexOf(event);
			if (parentEvent.mode == SequenceMode.SEQUENCE &&
					currentIndex < parentEvent.subEvents.size() - 1) {
				// Next event, play it
				startEvent(parentEvent.subEvents.get(currentIndex + 1));
			} else {
				// End of event
				stopEvent(parentEvent);
				if (parentEvent.mode == SequenceMode.SEQUENCE &&
						currentIndex == parentEvent.subEvents.size() - 1) {
					emitSignal("ONFINISHED", parentEvent.name);
				}
				else {
					emitSignal("ONFINISHED", event.name);
				}
				if (parentEvent.parent == null) {
					isPlaying = false;
				}
			}
		} else {
			// End of sequence
			stopEvent(event);
			emitSignal("ONFINISHED", event.name);
			isPlaying = false;
		}
	}

	@Override
	public void setAttribute(String name, Attribute attribute) {
		List<String> knownAttributes = List.of("FILENAME");
		if (knownAttributes.contains(name)) {
			super.setAttribute(name, attribute);
		}
	}

	public boolean isVisible() {
		return isVisible;
	}

	public String getCurrentEventName() {
		return currentEvent != null ? currentEvent.name : "";
	}

	public boolean isPlaying() {
		return isPlaying && !isPaused;
	}

	public Map<String, SequenceEvent> getEventsByName() {
		return eventsByName;
	}

	public List<SequenceEvent> getEvents() {
		return events;
	}
}