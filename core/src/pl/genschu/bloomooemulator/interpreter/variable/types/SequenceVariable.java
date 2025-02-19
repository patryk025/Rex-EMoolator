package pl.genschu.bloomooemulator.interpreter.variable.types;

import com.badlogic.gdx.Gdx;
import pl.genschu.bloomooemulator.interpreter.Context;
import pl.genschu.bloomooemulator.interpreter.variable.*;
import pl.genschu.bloomooemulator.loader.SEQParser;
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
				List.of(new Parameter("STRING", "eventName", false)),
				"void") {
			@Override
			public Variable execute(List<Object> arguments) {
				String eventName = arguments.isEmpty() ? null : ArgumentsHelper.getString(arguments.get(0));
				stopSequence(eventName);
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
			if (currentEvent.animation != null) {
				currentEvent.animation.fireMethod("PAUSE");
			}
			if (currentEvent.sound != null) {
				currentEvent.sound.fireMethod("PAUSE");
			}
			currentEvent.isPaused = true;
		}
	}

	private void resumeSequence() {
		isPaused = false;
		if (currentEvent != null && isPlaying) {
			if (currentEvent.animation != null) {
				currentEvent.animation.fireMethod("RESUME");
			}
			if (currentEvent.sound != null) {
				currentEvent.sound.fireMethod("RESUME");
			}
			currentEvent.isPaused = false;
		}
	}

	private void stopSequence(String eventName) {
		if (eventName != null) {
			SequenceEvent event = eventsByName.get(eventName);
			if (event != null) {
				stopEvent(event);
			}
		} else {
			if (currentEvent != null) {
				stopEvent(currentEvent);
			}
			isPlaying = false;
			currentEvent = null;
		}
	}

	private void stopEvent(SequenceEvent event) {
		event.isPlaying = false;
		event.inStartPhase = false;
		event.inMainPhase = false;
		event.inEndPhase = false;

		if (event.animation != null) {
			event.animation.fireMethod("STOP");
		}
		if (event.sound != null) {
			event.sound.fireMethod("STOP");
		}

		// Zatrzymaj wszystkie możliwe animacje dla SPEAKING
		if (event.type == EventType.SPEAKING) {
			stopAnimationIfExists(event.prefix + "_START");
			stopAnimationIfExists(event.prefix + "_STOP");
			for (int i = 1; i <= 10; i++) { // Bezpieczny limit do 10
				stopAnimationIfExists(event.prefix + "_" + i);
			}
		}

		for (SequenceEvent subEvent : event.subEvents) {
			stopEvent(subEvent);
		}
	}

	private void stopAnimationIfExists(String animName) {
		Variable anim = context.getVariable(animName);
		if (anim instanceof AnimoVariable) {
			anim.fireMethod("STOP");
		}
	}

	private void playEvent(String eventName) {
		SequenceEvent event = eventsByName.get(eventName);
		if (event != null) {
			stopSequence(null); // Stop current sequence
			currentEvent = event;
			isPlaying = true;
			startEvent(event);
		}
	}

	private void startEvent(SequenceEvent event) {
		event.isPlaying = true;

		if (event.type == EventType.SEQUENCE) {
			if (event.mode == SequenceMode.SEQUENCE) {
				startSequentialEvent(event);
			} else if (event.mode == SequenceMode.RANDOM) {
				startRandomEvent(event);
			} else if (event.mode == SequenceMode.PARAMETER) {
				// not sure, what's happening in this mode
				if (!event.subEvents.isEmpty()) {
					startEvent(event.subEvents.get(0));
				}
			}
		} else {
			startBasicEvent(event);
		}
	}

	private void startBasicEvent(SequenceEvent event) {
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
		event.inStartPhase = false;
		event.inMainPhase = true;

		// play audio
		if (event.sound != null) {
			event.sound.fireMethod("PLAY");
		}

		// run animo loop
		playSpeakingMainAnimation(event);
	}

	private void playSpeakingMainAnimation(SequenceEvent event) {
		String mainAnimName = event.prefix + "_" + event.currentAnimationNumber;

		event.animation.fireMethod("PLAY", new StringVariable("", mainAnimName, context));

		event.animation.setSignal("ONFINISHED", new Signal() {
			@Override
			public void execute(Object argument) {
				// check if next event exists
				String nextAnimName = event.prefix + "_" + (event.currentAnimationNumber + 1);
				if (event.animation.hasEvent(nextAnimName)) {
					event.currentAnimationNumber++;
				} else {
					event.currentAnimationNumber = 1;
				}
				if (event.inMainPhase) {
					playSpeakingMainAnimation(event);
				}
			}
		});
	}

	private void playSpeakingEndAnimation(SequenceEvent event) {
		event.inMainPhase = false;
		event.inEndPhase = true;

		String endAnimName = event.prefix + "_STOP";

		if (event.animation.hasEvent(endAnimName)) {
			event.animation.fireMethod("PLAY", new StringVariable("", endAnimName, context));

			event.animation.setSignal("ONFINISHED", new Signal() {
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
		if (!event.subEvents.isEmpty()) {
			startEvent(event.subEvents.get(0));
		}
	}

	private void startRandomEvent(SequenceEvent event) {
		if (!event.subEvents.isEmpty()) {
			int index = random.nextInt(event.subEvents.size());
			startEvent(event.subEvents.get(index));
		}
	}

	private void handleEventFinished(SequenceEvent event) {
		event.isPlaying = false;
		emitSignal("ONFINISHED", event.name);

		// If this is a sub-event, find its parent and handle next event
		for (SequenceEvent parentEvent : events) {
			if (parentEvent.subEvents.contains(event)) {
				int currentIndex = parentEvent.subEvents.indexOf(event);
				if (parentEvent.mode == SequenceMode.SEQUENCE &&
						currentIndex < parentEvent.subEvents.size() - 1) {
					startEvent(parentEvent.subEvents.get(currentIndex + 1));
					return;
				}
				handleEventFinished(parentEvent);
				return;
			}
		}
	}

	@Override
	public void setAttribute(String name, Attribute attribute) {
		List<String> knownAttributes = List.of("FILENAME");
		if (knownAttributes.contains(name)) {
			super.setAttribute(name, attribute);
		}
	}

	public AnimoVariable getCurrentAnimo() {
		return currentEvent != null ? currentEvent.animation : null;
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

	public void updateAnimation(float deltaTime) {
		if (isPlaying() && getCurrentAnimo() != null) {
			getCurrentAnimo().updateAnimation(deltaTime);
		}
	}
}