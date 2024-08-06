package pl.genschu.bloomooemulator.interpreter.variable.types;

import pl.genschu.bloomooemulator.interpreter.exceptions.ClassMethodNotImplementedException;
import pl.genschu.bloomooemulator.interpreter.Context;
import pl.genschu.bloomooemulator.interpreter.variable.Attribute;
import pl.genschu.bloomooemulator.interpreter.variable.Method;
import pl.genschu.bloomooemulator.interpreter.variable.Parameter;
import pl.genschu.bloomooemulator.interpreter.variable.Variable;
import pl.genschu.bloomooemulator.loader.ImageLoader;
import pl.genschu.bloomooemulator.objects.Image;
import pl.genschu.bloomooemulator.objects.Rectangle;

import java.util.List;

import static pl.genschu.bloomooemulator.interpreter.util.VariableHelper.getValueFromString;

public class ImageVariable extends Variable {
	Image image;
	private int posX;
	private int posY;
	private int opacity;
	private Rectangle rect;

	public ImageVariable(String name, Context context) {
		super(name, context);

		rect = new Rectangle(0, 0, 0, 0);

		this.setMethod("GETALPHA", new Method(
			List.of(
				new Parameter("INTEGER", "posX", true),
				new Parameter("INTEGER", "posY", true)
			),
			"INTEGER"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				throw new ClassMethodNotImplementedException("Method GETALPHA is not implemented yet");
			}
		});
		this.setMethod("GETHEIGHT", new Method(
			"INTEGER"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				throw new ClassMethodNotImplementedException("Method GETHEIGHT is not implemented yet");
			}
		});
		this.setMethod("GETPIXEL", new Method(
			List.of(
				new Parameter("INTEGER", "posX", true),
				new Parameter("INTEGER", "posY", true)
			),
			"INTEGER"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				throw new ClassMethodNotImplementedException("Method GETPIXEL is not implemented yet");
			}
		});
		this.setMethod("GETPOSITIONX", new Method(
			"INTEGER"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				return new IntegerVariable("", posX, context);
			}
		});
		this.setMethod("GETPOSITIONY", new Method(
			"INTEGER"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				return new IntegerVariable("", posY, context);
			}
		});
		this.setMethod("GETWIDTH", new Method(
			"INTEGER"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				return new IntegerVariable("", image.width, context);
			}
		});
		this.setMethod("HIDE", new Method(
			"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				setAttribute("VISIBLE", "FALSE");
				return null;
			}
		});
		this.setMethod("INVALIDATE", new Method(
			"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				throw new ClassMethodNotImplementedException("Method INVALIDATE is not implemented yet");
			}
		});
		this.setMethod("LOAD", new Method(
			List.of(
				new Parameter("STRING", "path", true)
			),
			"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				throw new ClassMethodNotImplementedException("Method LOAD is not implemented yet");
			}
		});
		this.setMethod("MERGEALPHA", new Method(
			List.of(
				new Parameter("INTEGER", "posX", true),
				new Parameter("INTEGER", "posY", true),
				new Parameter("STRING", "mask", true)
			),
			"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				throw new ClassMethodNotImplementedException("Method MERGEALPHA is not implemented yet");
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
				int offsetX = getValueFromString((Variable) arguments.get(0));
				int offsetY = getValueFromString((Variable) arguments.get(1));
				posX += offsetX;
				posY += offsetY;
				updateRect();
				return null;
			}
		});
		this.setMethod("SETCLIPPING", new Method(
			List.of(
				new Parameter("INTEGER", "xLeft", true),
				new Parameter("INTEGER", "yTop", true),
				new Parameter("INTEGER", "xRight", true),
				new Parameter("INTEGER", "yBottom", true)
			),
			"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				throw new ClassMethodNotImplementedException("Method SETCLIPPING is not implemented yet");
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
				opacity = getValueFromString((Variable) arguments.get(0));
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
				posX = getValueFromString((Variable) arguments.get(0));
				posY = getValueFromString((Variable) arguments.get(1));
				updateRect();
				return null;
			}
		});
		this.setMethod("SETPRIORITY", new Method(
			List.of(
				new Parameter("INTEGER", "priority", true)
			),
			"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				setAttribute("PRIORITY", getValueFromString((Variable) arguments.get(0)) + "");
				return null;
			}
		});
		this.setMethod("SHOW", new Method(
			"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				setAttribute("VISIBLE", "TRUE");
				return null;
			}
		});
	}

	@Override
	public String getType() {
		return "IMAGE";
	}

	@Override
	public void setAttribute(String name, Attribute attribute) {
		List<String> knownAttributes = List.of("FILENAME", "MONITORCOLLISION", "MONITORCOLLISIONALPHA", "PRELOAD", "PRIORITY", "RELEASE", "TOCANVAS", "VISIBLE");
		if(knownAttributes.contains(name)) {
			super.setAttribute(name, attribute);
			if(name.equals("FILENAME")) {
				ImageLoader.loadImage(this);
				updateRect();
			}
		}
	}

	public Rectangle getRect() {
		return rect;
	}

	private void updateRect() {
		if(image == null) return;
		rect.setXLeft(image.offsetX - posX);
		rect.setYTop(image.offsetY - posY);
		rect.setXRight(rect.getXLeft() + image.width);
		rect.setYBottom(rect.getYTop() + image.height);
	}

	public Image getImage() {
		return image;
	}

	public void setImage(Image image) {
		this.image = image;
	}

	public int getPosX() {
		return posX;
	}

	public void setPosX(int posX) {
		this.posX = posX;
	}

	public int getPosY() {
		return posY;
	}

	public void setPosY(int posY) {
		this.posY = posY;
	}

	public int getOpacity() {
		return opacity;
	}

	public void setOpacity(int opacity) {
		this.opacity = opacity;
	}
}
