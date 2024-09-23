package pl.genschu.bloomooemulator.interpreter.variable.types;

import com.badlogic.gdx.Gdx;
import pl.genschu.bloomooemulator.interpreter.exceptions.ClassMethodNotImplementedException;
import pl.genschu.bloomooemulator.interpreter.Context;
import pl.genschu.bloomooemulator.interpreter.variable.Attribute;
import pl.genschu.bloomooemulator.interpreter.variable.Method;
import pl.genschu.bloomooemulator.interpreter.variable.Parameter;
import pl.genschu.bloomooemulator.interpreter.variable.Variable;
import pl.genschu.bloomooemulator.objects.Rectangle;
import pl.genschu.bloomooemulator.utils.ArgumentsHelper;

import java.util.List;

public class TextVariable extends Variable {
	private String text;
	private boolean isVisible;
	private int priority;
	private Rectangle rect;
	private String vJustify;
	private String hJustify;
	private FontVariable font;

	public TextVariable(String name, Context context) {
		super(name, context);
	}

	@Override
	protected void setMethods() {
		super.setMethods();

		this.setMethod("SETJUSTIFY", new Method(
				List.of(
						new Parameter("INTEGER", "xLeft", true),
						new Parameter("INTEGER", "yLower", true),
						new Parameter("INTEGER", "xRight", true),
						new Parameter("INTEGER", "yUpper", true),
						new Parameter("STRING", "hJustify", true),
						new Parameter("STRING", "vJustify", true)
				),
				"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				int xLeft = ArgumentsHelper.getInteger(arguments.get(0));
				int yLower = ArgumentsHelper.getInteger(arguments.get(1));
				int xRight = ArgumentsHelper.getInteger(arguments.get(2));
				int yUpper = ArgumentsHelper.getInteger(arguments.get(3));
				String hJustify = ArgumentsHelper.getString(arguments.get(4));
				String vJustify = ArgumentsHelper.getString(arguments.get(5));

				rect = new Rectangle(xLeft, yLower, xRight, yUpper);
				sethJustify(hJustify);
				setvJustify(vJustify);
				return null;
			}
		});
		this.setMethod("SETTEXT", new Method(
				List.of(
						new Parameter("STRING", "text", true)
				),
				"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				String text = ArgumentsHelper.getString(arguments.get(0));
				setText(text);
				return null;
			}
		});
		this.setMethod("SHOW", new Method(
				"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				throw new ClassMethodNotImplementedException("Method SHOW is not implemented yet");
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
	}

	@Override
	public String getType() {
		return "TEXT";
	}

	@Override
	public void setAttribute(String name, Attribute attribute) {
		List<String> knownAttributes = List.of("FONT", "HJUSTIFY", "MONITORCOLLISION", "MONITORCOLLISIONALPHA", "PRIORITY", "RECT", "TEXT", "TOCANVAS", "VISIBLE", "VJUSTIFY");
		if(knownAttributes.contains(name)) {
			super.setAttribute(name, attribute);
			switch (name) {
				case "PRIORITY":
					priority = Integer.parseInt(getAttribute("PRIORITY").getValue().toString());
					break;
				case "VISIBLE":
					isVisible = attribute.getValue().toString().equals("TRUE");
					break;
				case "RECT":
					String rectRaw = getAttribute("RECT").getValue().toString();
					if(rectRaw.contains(",")) {
						String[] rectSplit = rectRaw.split(",");
						int xLeft = Integer.parseInt(rectSplit[0]);
						int yBottom = Integer.parseInt(rectSplit[1]);
						int xRight = Integer.parseInt(rectSplit[2]);
						int yTop = Integer.parseInt(rectSplit[3]);
						int height = yTop - yBottom;
						rect = new Rectangle(xLeft, yBottom-height, xRight, yTop-height);
					}
					else {
						Variable rectVariable = context.getVariable(rectRaw);
						if(rectVariable != null) {
							if (rectVariable instanceof ImageVariable) {
								ImageVariable imageVariable = (ImageVariable) rectVariable;
								rect = imageVariable.getRect();
							} else if (rectVariable instanceof AnimoVariable) {
								AnimoVariable animoVariable = (AnimoVariable) rectVariable;
								rect = animoVariable.getRect();
							}
						}
					}
					break;
				case "TEXT":
					text = getAttribute("TEXT").getValue().toString();
					break;
			}
		}
	}

	public boolean isVisible() {
		try {
			return isVisible
					&& this.getAttribute("TOCANVAS").getValue().toString().equals("TRUE");
		} catch (NullPointerException e) {
			return false;
		}
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
		// TODO: generate texture from font to draw text
	}

	public void setVisible(boolean visible) {
		isVisible = visible;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public Rectangle getRect() {
		return rect;
	}

	public void setRect(Rectangle rect) {
		this.rect = rect;
	}

	public String getvJustify() {
		return vJustify;
	}

	public void setvJustify(String vJustify) {
		this.vJustify = vJustify;
	}

	public String gethJustify() {
		return hJustify;
	}

	public void sethJustify(String hJustify) {
		this.hJustify = hJustify;
	}

	public FontVariable getFont() {
		return font;
	}

	public void setFont(FontVariable font) {
		this.font = font;
	}
}
