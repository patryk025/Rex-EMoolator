package pl.genschu.bloomooemulator.interpreter.variable.types;

import com.badlogic.gdx.Gdx;
import pl.genschu.bloomooemulator.interpreter.Context;
import pl.genschu.bloomooemulator.interpreter.exceptions.ClassMethodNotImplementedException;
import pl.genschu.bloomooemulator.interpreter.variable.Attribute;
import pl.genschu.bloomooemulator.interpreter.variable.Method;
import pl.genschu.bloomooemulator.interpreter.variable.Parameter;
import pl.genschu.bloomooemulator.interpreter.variable.Variable;
import pl.genschu.bloomooemulator.loader.AnimoLoader;
import pl.genschu.bloomooemulator.loader.SEQParser;

import java.io.IOException;
import java.util.*;

public class SequenceVariable extends Variable {
	private List<SequenceEvent> events = new ArrayList<>();
	private Map<String, SequenceEvent> eventMap = new HashMap<>();
	private String currentEventName;
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
			isPlaying = true;
			event.play();
		} else {
			Gdx.app.error("SequenceVariable", "Event not found: " + eventName);
		}
	}

	@Override
	public String getType() {
		return "SEQUENCE";
	}

	@Override
	public void setAttribute(String name, Attribute attribute) {
		List<String> knownAttributes = List.of("FILENAME");
		if(knownAttributes.contains(name)) {
			super.setAttribute(name, attribute);
		}
	}

	static class SequenceEvent {
		private final String name;
		private String nextEventName;
		private final SequenceVariable sequenceVariable;

		public SequenceEvent(String name, SequenceVariable sequenceVariable) {
			this.name = name;
			this.sequenceVariable = sequenceVariable;
		}

		public void play() {
			Gdx.app.log("SequenceEvent", "Playing event: " + name);
		}

		public String getName() {
			return name;
		}

		public String getNextEventName() {
			return nextEventName;
		}

		public void setNextEventName(String nextEventName) {
			this.nextEventName = nextEventName;
		}
	}

	class SimpleEvent extends SequenceEvent {
		private final StringVariable event;
		private final AnimoVariable animoVariable;

		public SimpleEvent(String name, String filename, String event) {
			super(name, SequenceVariable.this);
			this.event = new StringVariable("", event, getContext());
			this.animoVariable = new AnimoVariable("", getContext());
			this.animoVariable.setAttribute("FILENAME", filename);
			AnimoLoader.loadAnimo(this.animoVariable);
		}

		@Override
		public void play() {
			this.animoVariable.getMethod("PLAY", Collections.singletonList("STRING")).execute(List.of(event));
			super.play();
		}
	}

	class SpeakingEvent extends SequenceEvent {
		private final String prefix;
		private final boolean starting;
		private final boolean ending;
		private final AnimoVariable animoVariable;
		private final SoundVariable soundVariable;

		public SpeakingEvent(String name, String animofn, String prefix, String wavfn, boolean starting, boolean ending) {
			super(name, SequenceVariable.this);
			this.animoVariable = new AnimoVariable("", getContext());
			this.animoVariable.setAttribute("FILENAME", animofn);
			this.prefix = prefix;
			this.soundVariable = new SoundVariable("", getContext());
			this.soundVariable.setAttribute("FILENAME", wavfn);
			this.starting = starting;
			this.ending = ending;
		}

		@Override
		public void play() {
			if (starting) {
				triggerEvent(prefix + "_START");
			}

			triggerEvent(prefix); // TODO: add _1 or something?
			// TODO: play audio

			if (ending) {
				triggerEvent(prefix + "_STOP");
			}
			super.play();
		}

		private void triggerEvent(String eventName) {
			this.animoVariable.getMethod("RUN", Collections.singletonList("STRING")).execute(List.of(eventName));
		}
	}
}
