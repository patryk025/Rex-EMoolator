package pl.genschu.bloomooemulator.interpreter.variable.types;

import com.badlogic.gdx.Gdx;
import pl.genschu.bloomooemulator.interpreter.exceptions.ClassMethodNotImplementedException;
import pl.genschu.bloomooemulator.interpreter.Context;
import pl.genschu.bloomooemulator.interpreter.variable.Attribute;
import pl.genschu.bloomooemulator.interpreter.variable.Method;
import pl.genschu.bloomooemulator.interpreter.variable.Parameter;
import pl.genschu.bloomooemulator.interpreter.variable.Variable;
import pl.genschu.bloomooemulator.objects.Image;
import pl.genschu.bloomooemulator.objects.Rectangle;
import pl.genschu.bloomooemulator.utils.ArgumentsHelper;

import java.util.List;

public class ButtonVariable extends Variable {
	private Rectangle rect;
	private boolean isFocused = false;
	private boolean isPressed = false;
	private boolean wasPressed = false;

	public ButtonVariable(String name, Context context) {
		super(name, context);

		this.setMethod("DISABLE", new Method(
			"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				setAttribute("ENABLE", new Attribute("BOOL", "FALSE"));
				setAttribute("VISIBLE", new Attribute("BOOL", "FALSE"));
				return null;
			}
		});
		this.setMethod("DISABLEBUTVISIBLE", new Method(
			"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				setAttribute("ENABLE", new Attribute("BOOL", "FALSE"));
				setAttribute("VISIBLE", new Attribute("BOOL", "TRUE"));
				return null;
			}
		});
		this.setMethod("ENABLE", new Method(
			"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				setAttribute("ENABLE", new Attribute("BOOL", "TRUE"));
				setAttribute("VISIBLE", new Attribute("BOOL", "TRUE"));
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
				setAttribute("PRIORITY", new Attribute("PRIORITY", arguments.get(0).toString()));
				return null;
			}
		});
		this.setMethod("SETRECT", new Method(
			List.of(
				new Parameter("STRING", "varName", true)
			),
			"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				String varName = ArgumentsHelper.getString(arguments.get(0));

				Variable variable = context.getVariable(varName, null);

				if (variable == null) {
					Gdx.app.log("ButtonVariable", "Variable " + varName + " not found, RECT is not changed");
					return null;
				}

				if(variable instanceof AnimoVariable || variable instanceof ImageVariable) {
					if(variable instanceof AnimoVariable) {
						rect = ((AnimoVariable) variable).getRect();
					}
					else {
						rect = ((ImageVariable) variable).getRect();
					}
					return null;
				}

				return null;
			}
		});
		this.setMethod("SETRECT", new Method(
			List.of(
				new Parameter("INTEGER", "xLeft", true),
				new Parameter("INTEGER", "yBottom", true),
				new Parameter("INTEGER", "xRight", true),
				new Parameter("INTEGER", "yTop", true)
			),
			"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				int xLeft = ((IntegerVariable) arguments.get(0)).GET();
				int yBottom = ((IntegerVariable) arguments.get(1)).GET();
				int xRight = ((IntegerVariable) arguments.get(2)).GET();
				int yTop = ((IntegerVariable) arguments.get(3)).GET();
				rect = new Rectangle(xLeft, yBottom, xRight, yTop);
				return null;
			}
		});
		this.setMethod("SETSTD", new Method(
			List.of(
				new Parameter("STRING", "varName", true),
				new Parameter("BOOLEAN", "unknown", false)
			),
			"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				throw new ClassMethodNotImplementedException("Method SETSTD is not implemented yet");
			}
		});
	}

	public void setRect(Rectangle rect) {
		this.rect = rect;
	}

	public boolean isFocused() {
		return isFocused;
	}

	public void setFocused(boolean focused) {
		isFocused = focused;
	}

	public boolean isPressed() {
		return isPressed;
	}

	public boolean wasPressed() {
		return wasPressed;
	}

	public void setWasPressed(boolean wasPressed) {
		this.wasPressed = wasPressed;
	}

	public void setPressed(boolean pressed) {
		isPressed = pressed;
	}

	public Rectangle getRect() {
		if(rect == null && getAttribute("GFXSTANDARD") != null) {
			Gdx.app.log("ButtonVariable", "RECT is missing, but GFXSTANDARD is present: " + getAttribute("GFXSTANDARD").getValue());
			String gfx = getAttribute("GFXSTANDARD").getValue().toString();

			Variable gfxVariable = context.getVariable(gfx, null);

			if(gfxVariable == null) {
				return null;
			}

			if(gfxVariable instanceof ImageVariable) {
				ImageVariable imageVariable = (ImageVariable) gfxVariable;
				rect = imageVariable.getRect();
			}
			else if(gfxVariable instanceof AnimoVariable) {
				AnimoVariable animoVariable = (AnimoVariable) gfxVariable;
				rect = animoVariable.getRect();
			}
		}
		return rect;
	}

	@Override
	public String getType() {
		return "BUTTON";
	}

	@Override
	public void setAttribute(String name, Attribute attribute) {
		List<String> knownAttributes = List.of("DRAGGABLE", "ENABLE", "GFXONCLICK", "GFXONMOVE", "GFXSTANDARD", "RECT", "SNDONMOVE", "VISIBLE");
		if (knownAttributes.contains(name)) {
			super.setAttribute(name, attribute);
		}
	}
}
