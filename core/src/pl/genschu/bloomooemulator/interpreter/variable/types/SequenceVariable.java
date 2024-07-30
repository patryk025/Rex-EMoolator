package pl.genschu.bloomooemulator.interpreter.variable.types;

import pl.genschu.bloomooemulator.interpreter.exceptions.ClassMethodNotImplementedException;
import pl.genschu.bloomooemulator.interpreter.Context;
import pl.genschu.bloomooemulator.interpreter.variable.Attribute;
import pl.genschu.bloomooemulator.interpreter.variable.Method;
import pl.genschu.bloomooemulator.interpreter.variable.Parameter;
import pl.genschu.bloomooemulator.interpreter.variable.Variable;

import java.util.List;
import java.util.ArrayList;

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

    class SequenceEvent {
        private String name;
        private String mode;
        private List<SequenceEvent> events;
        
        public SequenceEvent(String name) {
            this.name = name;
            this.mode = "SEQUENCE";
            this.events = new ArrayList<>();
        }
        
        public void add(SequenceEvent event) {
            this.events.add(event);
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
        private String filename;
        private String event;
    
        public SimpleEvent(String name, String filename, String event) {
            super(name);
            this.filename = filename;
            this.event = event;
        }
        
        public void play() {
            // TODO: play animo
        }
    }
    
    class SpeakingEvent extends SequenceEvent {
        private String animofn;
        private String prefix;
        private String wavfn;
        private boolean starting;
        private boolean ending;
    
        public SpeakingEvent(String name, String animofn, String prefix, String wavfn, boolean starting, boolean ending) {
            super(name);
            this.animofn = animofn;
            this.prefix = prefix;
            this.wavfn = wavfn;
            this.starting = starting;
            this.ending = ending;
        }
    
        public void play() {
            if (starting) {
                triggerEvent(prefix + "_START");
            }
            
            // TODO: ANIMO and SOUND
    
            if (ending) {
                triggerEvent(prefix + "_STOP");
            }
        }
    
        private void triggerEvent(String eventName) {
            // TODO: another TODO, booooring
        }
    }
}
