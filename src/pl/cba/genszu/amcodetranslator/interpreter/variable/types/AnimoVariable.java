package pl.cba.genszu.amcodetranslator.interpreter.variable.types;

import pl.cba.genszu.amcodetranslator.interpreter.Context;
import pl.cba.genszu.amcodetranslator.interpreter.variable.Attribute;
import pl.cba.genszu.amcodetranslator.interpreter.variable.Method;
import pl.cba.genszu.amcodetranslator.interpreter.variable.Parameter;
import pl.cba.genszu.amcodetranslator.interpreter.variable.Variable;

import java.util.List;

public class AnimoVariable extends Variable {
	public AnimoVariable(String name, Context context) {
		super(name, context);

		this.setMethod("GETCENTERX", new Method(
			"INTEGER"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method GETCENTERX is not implemented yet");
				return null;
			}
		});
		this.setMethod("GETCENTERX", new Method(
			List.of(
				new Parameter("BOOL", "unknown", true)
			),
			"INTEGER"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method GETCENTERX is not implemented yet");
				return null;
			}
		});
		this.setMethod("GETCENTERY", new Method(
			"INTEGER"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method GETCENTERY is not implemented yet");
				return null;
			}
		});
		this.setMethod("GETCENTERY", new Method(
			List.of(
				new Parameter("BOOL", "unknown", true)
			),
			"INTEGER"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method GETCENTERY is not implemented yet");
				return null;
			}
		});
		this.setMethod("GETCFRAMEINEVENT", new Method(
			"INTEGER"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method GETCFRAMEINEVENT is not implemented yet");
				return null;
			}
		});
		this.setMethod("GETCFRAMEINEVENT", new Method(
			List.of(
				new Parameter("STRING", "event", true)
			),
			"INTEGER"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method GETCFRAMEINEVENT is not implemented yet");
				return null;
			}
		});
		this.setMethod("GETCURRFRAMEPOSX", new Method(
			"INTEGER"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method GETCURRFRAMEPOSX is not implemented yet");
				return null;
			}
		});
		this.setMethod("GETCURRFRAMEPOSY", new Method(
			"INTEGER"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method GETCURRFRAMEPOSY is not implemented yet");
				return null;
			}
		});
		this.setMethod("GETENDX", new Method(
			"INTEGER"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method GETENDX is not implemented yet");
				return null;
			}
		});
		this.setMethod("GETENDY", new Method(
			"INTEGER"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method GETENDY is not implemented yet");
				return null;
			}
		});
		this.setMethod("GETEVENTNAME", new Method(
			"STRING"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method GETEVENTNAME is not implemented yet");
				return null;
			}
		});
		this.setMethod("GETFRAME", new Method(
			"INTEGER"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method GETFRAME is not implemented yet");
				return null;
			}
		});
		this.setMethod("GETFRAMENAME", new Method(
			"STRING"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method GETFRAMENAME is not implemented yet");
				return null;
			}
		});
		this.setMethod("GETHEIGHT", new Method(
			"INTEGER"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method GETHEIGHT is not implemented yet");
				return null;
			}
		});
		this.setMethod("GETMAXWIDTH", new Method(
			"INTEGER"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method GETMAXWIDTH is not implemented yet");
				return null;
			}
		});
		this.setMethod("GETNOE", new Method(
			"INTEGER"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method GETNOE is not implemented yet");
				return null;
			}
		});
		this.setMethod("GETNOFINEVENT", new Method(
			List.of(
				new Parameter("INTEGER", "eventNumber?", true)
			),
			"INTEGER"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method GETNOFINEVENT is not implemented yet");
				return null;
			}
		});
		this.setMethod("GETNOFINEVENT", new Method(
			List.of(
				new Parameter("String", "event", true)
			),
			"INTEGER"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method GETNOFINEVENT is not implemented yet");
				return null;
			}
		});
		this.setMethod("GETPOSITIONX", new Method(
			"INTEGER"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method GETPOSITIONX is not implemented yet");
				return null;
			}
		});
		this.setMethod("GETPOSITIONX", new Method(
			List.of(
				new Parameter("BOOL", "unknown", true)
			),
			"INTEGER"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method GETPOSITIONX is not implemented yet");
				return null;
			}
		});
		this.setMethod("GETPOSITIONY", new Method(
			"INTEGER"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method GETPOSITIONY is not implemented yet");
				return null;
			}
		});
		this.setMethod("GETPOSITIONY", new Method(
			List.of(
				new Parameter("BOOL", "unknown", true)
			),
			"INTEGER"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method GETPOSITIONY is not implemented yet");
				return null;
			}
		});
		this.setMethod("GETWIDTH", new Method(
			"INTEGER"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method GETWIDTH is not implemented yet");
				return null;
			}
		});
		this.setMethod("HIDE", new Method(
			"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method HIDE is not implemented yet");
				return null;
			}
		});
		this.setMethod("ISNEAR", new Method(
			List.of(
				new Parameter("STRING", "varAnimo", true),
				new Parameter("INTEGER", "distance", true)
			),
			"BOOL"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method ISNEAR is not implemented yet");
				return null;
			}
		});
		this.setMethod("ISPLAYING", new Method(
			"BOOL"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method ISPLAYING is not implemented yet");
				return null;
			}
		});
		this.setMethod("ISPLAYING", new Method(
			List.of(
				new Parameter("STRING", "event", true)
			),
			"BOOL"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method ISPLAYING is not implemented yet");
				return null;
			}
		});
		this.setMethod("ISVISIBLE", new Method(
			"BOOL"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method ISVISIBLE is not implemented yet");
				return null;
			}
		});
		this.setMethod("LOAD", new Method(
			List.of(
				new Parameter("STRING", "path", true)
			),
			"void/BOOLEAN?"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method LOAD is not implemented yet");
				return null;
			}
		});
		this.setMethod("MOVE", new Method(
			List.of(
				new Parameter("INTEGER", "offsetX", true),
				new Parameter("INTEGER", "offsetY", true)
			),
			"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method MOVE is not implemented yet");
				return null;
			}
		});
		this.setMethod("NEXTFRAME", new Method(
			"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method NEXTFRAME is not implemented yet");
				return null;
			}
		});
		this.setMethod("NPLAY", new Method(
			List.of(
				new Parameter("INTEGER", "eventNumber", true)
			),
			"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method NPLAY is not implemented yet");
				return null;
			}
		});
		this.setMethod("NPLAY", new Method(
			List.of(
				new Parameter("STRING", "event", true)
			),
			"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method NPLAY is not implemented yet");
				return null;
			}
		});
		this.setMethod("PAUSE", new Method(
			"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method PAUSE is not implemented yet");
				return null;
			}
		});
		this.setMethod("PLAY", new Method(
			List.of(
				new Parameter("INTEGER", "animNumber?", true)
			),
			"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method PLAY is not implemented yet");
				return null;
			}
		});
		this.setMethod("PLAY", new Method(
			List.of(
				new Parameter("STRING", "anim", true)
			),
			"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method PLAY is not implemented yet");
				return null;
			}
		});
		this.setMethod("PLAY", new Method(
			List.of(
				new Parameter("STRING", "anim", true),
				new Parameter("INTEGER", "frameNo?", true)
			),
			"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method PLAY is not implemented yet");
				return null;
			}
		});
		this.setMethod("PREVFRAME", new Method(
			"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method PREVFRAME is not implemented yet");
				return null;
			}
		});
		this.setMethod("RESUME", new Method(
			"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method RESUME is not implemented yet");
				return null;
			}
		});
		this.setMethod("RUN", new Method(
			"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method RUN is not implemented yet");
				return null;
			}
		});
		this.setMethod("SETANCHOR", new Method(
			List.of(
				new Parameter("Enum<STRING>", "anchor", true)
			),
			"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method SETANCHOR is not implemented yet");
				return null;
			}
		});
		this.setMethod("SETANCHOR", new Method(
			List.of(
				new Parameter("INTEGER", "offsetX", true),
				new Parameter("INTEGER", "offsetY", true)
			),
			"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method SETANCHOR is not implemented yet");
				return null;
			}
		});
		this.setMethod("SETASBUTTON", new Method(
			List.of(
				new Parameter("BOOL", "enabled", true),
				new Parameter("BOOL", "unknown", true)
			),
			"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method SETASBUTTON is not implemented yet");
				return null;
			}
		});
		this.setMethod("SETBACKWARD", new Method(
			"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method SETBACKWARD is not implemented yet");
				return null;
			}
		});
		this.setMethod("SETFORWARD", new Method(
			"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method SETFORWARD is not implemented yet");
				return null;
			}
		});
		this.setMethod("SETFPS", new Method(
			List.of(
				new Parameter("INTEGER", "fps", true)
			),
			"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method SETFPS is not implemented yet");
				return null;
			}
		});
		this.setMethod("SETFPS", new Method(
			List.of(
				new Parameter("STRING", "fps", true)
			),
			"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method SETFPS is not implemented yet");
				return null;
			}
		});
		this.setMethod("SETFRAME", new Method(
			List.of(
				new Parameter("STRING", "event", true),
				new Parameter("INTEGER", "frameNumber", true)
			),
			"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method SETFRAME is not implemented yet");
				return null;
			}
		});
		this.setMethod("SETFRAME", new Method(
			List.of(
				new Parameter("INTEGER", "eventNumber?", true)
			),
			"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method SETFRAME is not implemented yet");
				return null;
			}
		});
		this.setMethod("SETFRAME", new Method(
			List.of(
				new Parameter("INTEGER", "eventNumber?", true),
				new Parameter("INTEGER", "frameNumber", true)
			),
			"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method SETFRAME is not implemented yet");
				return null;
			}
		});
		this.setMethod("SETFRAME", new Method(
			List.of(
				new Parameter("STRING", "eventNumber?", true),
				new Parameter("STRING", "frameName?", true)
			),
			"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method SETFRAME is not implemented yet");
				return null;
			}
		});
		this.setMethod("SETFRAMENAME", new Method(
			List.of(
				new Parameter("INTEGER", "eventNumber?", true),
				new Parameter("INTEGER", "frameNumber?", true),
				new Parameter("STRING", "name", true)
			),
			"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method SETFRAMENAME is not implemented yet");
				return null;
			}
		});
		this.setMethod("SETOPACITY", new Method(
			List.of(
				new Parameter("STRING", "opacity", true)
			),
			"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method SETOPACITY is not implemented yet");
				return null;
			}
		});
		this.setMethod("SETOPACITY", new Method(
			List.of(
				new Parameter("INTEGER", "opacity", true)
			),
			"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method SETOPACITY is not implemented yet");
				return null;
			}
		});
		this.setMethod("SETPOSITION", new Method(
			List.of(
				new Parameter("INTEGER", "posX", true),
				new Parameter("INTEGER", "posY", true)
			),
			"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method SETPOSITION is not implemented yet");
				return null;
			}
		});
		this.setMethod("SETPRIORITY", new Method(
			List.of(
				new Parameter("INTEGER", "posZ", true)
			),
			"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method SETPRIORITY is not implemented yet");
				return null;
			}
		});
		this.setMethod("SHOW", new Method(
			"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method SHOW is not implemented yet");
				return null;
			}
		});
		this.setMethod("SHOW", new Method(
			List.of(
				new Parameter("BOOL", "unknown", true)
			),
			"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method SHOW is not implemented yet");
				return null;
			}
		});
		this.setMethod("STOP", new Method(
			"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method STOP is not implemented yet");
				return null;
			}
		});
		this.setMethod("STOP", new Method(
			List.of(
				new Parameter("BOOL", "emitSignal", true)
			),
			"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method STOP is not implemented yet");
				return null;
			}
		});
		this.setMethod("TOP", new Method(
			"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method TOP is not implemented yet");
				return null;
			}
		});
		this.setMethod("TOP", new Method(
			List.of(
				new Parameter("BOOL", "unknown", true)
			),
			"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method TOP is not implemented yet");
				return null;
			}
		});
	}

	@Override
	public String getType() {
		return "ANIMO";
	}

	@Override
	public void setAttribute(String name, Attribute attribute) {
		List<String> knownAttributes = List.of("ASBUTTON", "FILENAME", "FLUSHAFTERPLAYED", "FPS", "MONITORCOLLISION", "MONITORCOLLISIONALPHA", "PRELOAD", "PRIORITY", "RELEASE", "TOCANVAS", "VISIBLE");
		if(knownAttributes.contains(name)) {
			super.setAttribute(name, attribute);
		}
	}

}
