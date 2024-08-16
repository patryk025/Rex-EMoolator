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

	private Variable gfxOnMove;
	private Variable gfxVariable;
	private Variable gfxOnClick;

	private Variable currentGfx;

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

				Variable variable = context.getVariable(varName);

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

		loadGfx();

		if (focused) {
			if (gfxOnMove != null) {
				gfxOnMove.setAttribute("VISIBLE", new Attribute("BOOL", "TRUE"));
				if (gfxVariable != null) {
					gfxVariable.setAttribute("VISIBLE", new Attribute("BOOL", "FALSE"));
				}
				currentGfx = gfxOnMove;
			}
		} else {
			if (gfxOnMove != null) {
				gfxOnMove.setAttribute("VISIBLE", new Attribute("BOOL", "FALSE"));
			}
			if (gfxVariable != null) {
				gfxVariable.setAttribute("VISIBLE", new Attribute("BOOL", "TRUE"));
			}
			currentGfx = gfxVariable;
		}

		if (gfxOnClick != null) {
			gfxOnClick.setAttribute("VISIBLE", new Attribute("BOOL", "FALSE"));
		}
	}

	private void loadGfx() {
		if(getAttribute("GFXSTANDARD") != null && gfxVariable == null) {
			gfxVariable = context.getVariable(getAttribute("GFXSTANDARD").getValue().toString());
		}

		if(getAttribute("GFXONMOVE") != null && gfxOnMove == null) {
			gfxOnMove = context.getVariable(getAttribute("GFXONMOVE").getValue().toString());
		}

		if(getAttribute("GFXONCLICK") != null && gfxOnClick == null) {
			gfxOnClick = context.getVariable(getAttribute("GFXONCLICK").getValue().toString());
		}
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

		loadGfx();

		if (pressed && gfxOnClick != null) {
			gfxOnClick.setAttribute("VISIBLE", new Attribute("BOOL", "TRUE"));
			if (gfxVariable != null) {
				gfxVariable.setAttribute("VISIBLE", new Attribute("BOOL", "FALSE"));
			}
			if (gfxOnMove != null) {
				gfxOnMove.setAttribute("VISIBLE", new Attribute("BOOL", "FALSE"));
			}

			currentGfx = gfxOnClick;
		} else {
			setFocused(isFocused);
		}
	}


	public boolean isEnabled() {
		return getAttribute("ENABLE").getValue().toString().equals("TRUE");
	}

	public Rectangle getRect() {
		if(rect == null) {
			if (getAttribute("GFXSTANDARD") != null) {
				Gdx.app.log("ButtonVariable", "Using GFXSTANDARD as RECT: " + getAttribute("GFXSTANDARD").getValue());

				loadGfx();

				if (gfxVariable == null) {
					return null;
				}

				if (gfxVariable instanceof ImageVariable) {
					ImageVariable imageVariable = (ImageVariable) gfxVariable;
					rect = imageVariable.getRect();
				} else if (gfxVariable instanceof AnimoVariable) {
					AnimoVariable animoVariable = (AnimoVariable) gfxVariable;
					rect = animoVariable.getRect();
				}
			}
			else if(getAttribute("RECT") != null) {
				Variable rectVariable = context.getVariable(getAttribute("RECT").getValue().toString());
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
			if(name.equals("RECT")) {
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
			}
		}
	}
}
