package pl.genschu.bloomooemulator.interpreter.variable.types;

import pl.genschu.bloomooemulator.interpreter.exceptions.ClassMethodNotImplementedException;
import pl.genschu.bloomooemulator.interpreter.Context;
import pl.genschu.bloomooemulator.interpreter.variable.Attribute;
import pl.genschu.bloomooemulator.interpreter.variable.Method;
import pl.genschu.bloomooemulator.interpreter.variable.Parameter;
import pl.genschu.bloomooemulator.interpreter.variable.Variable;
import pl.genschu.bloomooemulator.loader.AnimoLoader;

import java.util.*;

public class SequenceVariable extends Variable {
	public SequenceVariable(String name, Context context) {
		super(name, context);

		this.setMethod("GETEVENTNAME", new Method(
			"STRING"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				throw new ClassMethodNotImplementedException("Method GETEVENTNAME is not implemented yet");
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
				// TODO: implement this method
				throw new ClassMethodNotImplementedException("Method ISPLAYING is not implemented yet");
			}
		});
		this.setMethod("PAUSE", new Method(
			"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				throw new ClassMethodNotImplementedException("Method PAUSE is not implemented yet");
			}
		});
		this.setMethod("PLAY", new Method(
			List.of(
				new Parameter("STRING", "sequenceName", true)
			),
			"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				throw new ClassMethodNotImplementedException("Method PLAY is not implemented yet");
			}
		});
		this.setMethod("RESUME", new Method(
			"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				throw new ClassMethodNotImplementedException("Method RESUME is not implemented yet");
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
				// TODO: implement this method
				throw new ClassMethodNotImplementedException("Method STOP is not implemented yet");
			}
		});
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
        private String name;
        private String mode; // TODO: check what it change
        private List<SequenceEvent> events;
		private Map<String, Integer> seqevent;
        
        public SequenceEvent(String name) {
            this.name = name;
            this.mode = "SEQUENCE";
            this.events = new ArrayList<>();
			this.seqevent = new HashMap<>();
        }

		public void play() {
			for(SequenceEvent event : events) {
				event.play();
			}
		}
        
        public void add(SequenceEvent event) {
            this.events.add(event);
        }

		public void addSeqevent(String name, int seqVal) {
			this.seqevent.put(name, seqVal);
		}

        public String getName() {
            return this.name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getMode() {
            return this.mode;
        }

        public void setMode(String mode) {
            this.mode = mode;
        }

        public List<SequenceEvent> getEvents() {
            return this.events;
        }

        public void setEvents(List<SequenceEvent> events) {
            this.events = events;
        }
    }

    class SimpleEvent extends SequenceEvent {
        private final StringVariable event;
		private AnimoVariable animoVariable;
    
        public SimpleEvent(String name, String filename, String event) {
            super(name);
            this.event = new StringVariable("", event, getContext());
			this.animoVariable = new AnimoVariable("", getContext());
			this.animoVariable.setAttribute("FILENAME", filename);
			AnimoLoader.loadAnimo(this.animoVariable);
        }

		@Override
        public void play() {
            this.animoVariable.getMethod("PLAY", Collections.singletonList("STRING")).execute(List.of(event));
        }

		public AnimoVariable getAnimoVariable() {
			return animoVariable;
		}

		public void setAnimoVariable(AnimoVariable animoVariable) {
			this.animoVariable = animoVariable;
		}
	}
    
    class SpeakingEvent extends SequenceEvent {
        private String prefix;
        private boolean starting;
        private boolean ending;

		private AnimoVariable animoVariable;
		private SoundVariable soundVariable;
    
        public SpeakingEvent(String name, String animofn, String prefix, String wavfn, boolean starting, boolean ending) {
            super(name);
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
        }
    
        private void triggerEvent(String eventName) {
            this.animoVariable.getMethod("RUN", Collections.singletonList("STRING")).execute(List.of(eventName));
        }
    }
}
