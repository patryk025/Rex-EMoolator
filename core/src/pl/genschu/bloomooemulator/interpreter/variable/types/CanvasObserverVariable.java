package pl.genschu.bloomooemulator.interpreter.variable.types;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import pl.genschu.bloomooemulator.interpreter.exceptions.ClassMethodNotImplementedException;
import pl.genschu.bloomooemulator.interpreter.Context;
import pl.genschu.bloomooemulator.interpreter.variable.Attribute;
import pl.genschu.bloomooemulator.interpreter.variable.Method;
import pl.genschu.bloomooemulator.interpreter.variable.Parameter;
import pl.genschu.bloomooemulator.interpreter.variable.Variable;
import pl.genschu.bloomooemulator.saver.ImageSaver;
import pl.genschu.bloomooemulator.utils.ArgumentsHelper;

import java.nio.ByteBuffer;
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
				String imgFileName = ArgumentsHelper.getString(arguments.get(0));

				Pixmap pixmap = new Pixmap(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), Pixmap.Format.RGB565);

				Gdx.gl.glReadPixels(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), GL20.GL_RGB, GL20.GL_UNSIGNED_SHORT_5_6_5, pixmap.getPixels());

				flipPixmapVertically(pixmap);

				ByteBuffer buffer = pixmap.getPixels();
				byte[] byteArray = new byte[buffer.remaining()];
				buffer.get(byteArray);
				pixmap.dispose();

				ImageSaver.saveScreenshot(CanvasObserverVariable.this, imgFileName, byteArray);

				return null;
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

	// modified from https://stackoverflow.com/a/44403355/19906361
	public static void flipPixmapVertically(Pixmap p) {
		int w = p.getWidth();
		int h = p.getHeight();
		int hold;

		p.setBlending(Pixmap.Blending.None);

		for (int y = 0; y < h / 2; y++) {
			for (int x = 0; x < w; x++) {
				// Zamień piksele na osi Y (z góry na dół)
				hold = p.getPixel(x, y);
				p.drawPixel(x, y, p.getPixel(x, h - y - 1));
				p.drawPixel(x, h - y - 1, hold);
			}
		}

		p.setBlending(Pixmap.Blending.SourceOver);
	}
}
