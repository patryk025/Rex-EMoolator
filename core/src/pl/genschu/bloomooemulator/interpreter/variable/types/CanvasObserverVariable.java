package pl.genschu.bloomooemulator.interpreter.variable.types;

import com.badlogic.gdx.Gdx;
import pl.genschu.bloomooemulator.interpreter.exceptions.ClassMethodNotImplementedException;
import pl.genschu.bloomooemulator.interpreter.Context;
import pl.genschu.bloomooemulator.interpreter.variable.Attribute;
import pl.genschu.bloomooemulator.interpreter.variable.Method;
import pl.genschu.bloomooemulator.interpreter.variable.Parameter;
import pl.genschu.bloomooemulator.interpreter.variable.Variable;
import pl.genschu.bloomooemulator.utils.ArgumentsHelper;

import java.util.List;

public class CanvasObserverVariable extends Variable {
	// TODO: implement canvas observer

	public CanvasObserverVariable(String name, Context context) {
		super(name, context);

		this.setMethod("ADD", new Method(
			List.of(
				new Parameter("STRING", "animoVar", true)
			),
			"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				throw new ClassMethodNotImplementedException("Method ADD is not implemented yet");
			}
		});
		this.setMethod("ENABLENOTIFY", new Method(
			List.of(
				new Parameter("BOOL", "enable", true)
			),
			"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				throw new ClassMethodNotImplementedException("Method ENABLENOTIFY is not implemented yet");
			}
		});
		this.setMethod("GETGRAPHICSAT", new Method(
			List.of(
				new Parameter("INTEGER", "posX", true),
				new Parameter("INTEGER", "posY", true),
				new Parameter("BOOL", "unknown", true),
				new Parameter("INTEGER", "minZ", true),
				new Parameter("INTEGER", "maxZ", true)
			),
			"STRING"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				throw new ClassMethodNotImplementedException("Method GETGRAPHICSAT is not implemented yet");
			}
		});
		this.setMethod("GETGRAPHICSAT", new Method(
			List.of(
				new Parameter("INTEGER", "posX", true),
				new Parameter("INTEGER", "posY", true),
				new Parameter("BOOL", "unknown", true),
				new Parameter("INTEGER", "minZ", true),
				new Parameter("INTEGER", "maxZ", true),
				new Parameter("BOOL", "useAlpha", true)
			),
			"STRING"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				throw new ClassMethodNotImplementedException("Method GETGRAPHICSAT is not implemented yet");
			}
		});
		this.setMethod("MOVEBKG", new Method(
			List.of(
				new Parameter("INTEGER", "deltaX", true),
				new Parameter("INTEGER", "deltaY", true)
			),
			"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				throw new ClassMethodNotImplementedException("Method MOVEBKG is not implemented yet");
			}
		});
		this.setMethod("PASTE", new Method(
			List.of(
				new Parameter("STRING", "varName", true),
				new Parameter("INTEGER", "posX", true),
				new Parameter("INTEGER", "posY", true)
			),
			"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				throw new ClassMethodNotImplementedException("Method PASTE is not implemented yet");
			}
		});
		this.setMethod("REDRAW", new Method(
			"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				throw new ClassMethodNotImplementedException("Method REDRAW is not implemented yet");
			}
		});
		this.setMethod("REFRESH", new Method(
			"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				Gdx.app.error("CanvasObserverVariable", "Currently refresh is not supported"); // It's just for make less log spam
				return null;
			}
		});
		this.setMethod("REMOVE", new Method(
			List.of(
				new Parameter("STRING", "varName1...varNameN", true)
			),
			"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				throw new ClassMethodNotImplementedException("Method REMOVE is not implemented yet");
			}
		});
		this.setMethod("SAVE", new Method(
			List.of(
				new Parameter("STRING", "imgFileName", true),
				new Parameter("mixed", "unknownParam1...unknownParamN", true)
			),
			"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				throw new ClassMethodNotImplementedException("Method SAVE is not implemented yet");
			}
		});
		this.setMethod("SETBACKGROUND", new Method(
			List.of(
				new Parameter("STRING", "imageName", true)
			),
			"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// hacky way for now
				String imageName = ArgumentsHelper.getString(arguments.get(0));
				// check if it is not a variable
				Variable var = context.getVariable(imageName);
				if (var != null && var.getType().equals("IMAGE")) {
					context.getGame().getCurrentSceneVariable().setBackground((ImageVariable) var);
					return null;
				}
				ImageVariable image = new ImageVariable("", context);
				image.setAttribute("FILENAME", new Attribute("STRING", imageName));
				context.getGame().getCurrentSceneVariable().setBackground(image);
				return null;
			}
		});
		this.setMethod("SETBKGPOS", new Method(
			List.of(
				new Parameter("INTEGER", "posX", true),
				new Parameter("INTEGER", "posY", true)
			),
			"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				throw new ClassMethodNotImplementedException("Method SETBKGPOS is not implemented yet");
			}
		});
	}

	@Override
	public String getType() {
		return "CANVAS_OBSERVER";
	}

	@Override
	public void setAttribute(String name, Attribute attribute) {
		return; // no fields in this class
	}

}
