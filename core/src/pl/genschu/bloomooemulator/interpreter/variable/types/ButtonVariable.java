package pl.genschu.bloomooemulator.interpreter.variable.types;

import com.badlogic.gdx.Gdx;
import pl.genschu.bloomooemulator.interpreter.exceptions.ClassMethodNotImplementedException;
import pl.genschu.bloomooemulator.interpreter.Context;
import pl.genschu.bloomooemulator.interpreter.variable.*;
import pl.genschu.bloomooemulator.objects.Rectangle;
import pl.genschu.bloomooemulator.utils.ArgumentsHelper;

import java.util.List;

public class ButtonVariable extends Variable {
	private Rectangle rect = null;
	private boolean isFocused = false;
	private boolean isPressed = false;
	private boolean wasPressed = false;
	private boolean isEnabled = true;

	private Variable rectVariable;
	private Variable gfxOnMove;
	private Variable gfxVariable;
	private Variable gfxOnClick;
	private Variable currentGfx;

	public ButtonVariable(String name, Context context) {
		super(name, context);
	}

	@Override
	protected void setMethods() {
		super.setMethods();

		this.setMethod("DISABLE", new Method("void") {
			@Override
			public Variable execute(List<Object> arguments) {
				setAttribute("ENABLE", new Attribute("BOOL", "FALSE"));
				isEnabled = false;
				hideGraphics();

				if(isFocused) {
					if (context.getGame().getEmulator().getActiveButton() == ButtonVariable.this) {
						context.getGame().getEmulator().setActiveButton(null);
					}
					setFocused(false);
					Signal onFocusLossSignal = getSignal("ONFOCUSOFF");
					if (onFocusLossSignal != null) {
						onFocusLossSignal.execute(null);
					}
				}
				if(currentGfx != null) {
					showImage(currentGfx, false);
					currentGfx = null;
				}
				return null;
			}
		});

		this.setMethod("DISABLEBUTVISIBLE", new Method("void") {
			@Override
			public Variable execute(List<Object> arguments) {
				if(isFocused) {
					setFocused(false);
					Signal onFocusLossSignal = getSignal("ONFOCUSOFF");
					if (onFocusLossSignal != null) {
						onFocusLossSignal.execute(null);
					}
				}
				isEnabled = false;
				updateGraphicsVisibility();
				return null;
			}
		});

		this.setMethod("ENABLE", new Method("void") {
			@Override
			public Variable execute(List<Object> arguments) {
				isEnabled = true;
				updateGraphicsVisibility();
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
					rectVariable = variable;
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
				rectVariable = null;
				return null;
			}
		});
		this.setMethod("SETSTD", new Method(
				List.of(
						new Parameter("STRING", "varName", true),
						new Parameter("BOOL", "unknown", false)
				),
				"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				if(rect != null) {
					return null;
				}

				String varName = ArgumentsHelper.getString(arguments.get(0));

				Variable variable = context.getVariable(varName);
				if(variable instanceof AnimoVariable) {
					rect = ((AnimoVariable) variable).getRect();
				}
				else if(variable instanceof ImageVariable) {
					rect = ((ImageVariable) variable).getRect();
				}
				else {
					Gdx.app.log("ButtonVariable", "Variable " + varName + " not found, RECT is not changed");
				}

				setAttribute("PRIORITY", new Attribute("PRIORITY", "0"));
				return null;
			}
		});
	}

	public void setRect(Rectangle rect) {
		this.rect = rect;
	}

	private void hideGraphics() {
		if(gfxVariable != null) {
			showImage(gfxVariable, false);
		}
		if(gfxOnMove != null) {
			showImage(gfxOnMove, false);
		}
		if(gfxOnClick != null) {
			showImage(gfxOnClick, false);
		}
	}

	private void updateGraphicsVisibility() {
		if (isPressed && gfxOnClick != null) {
			showImage(gfxVariable, false);
			showImage(gfxOnMove, false);
			showImage(gfxOnClick, true);
			currentGfx = gfxOnClick;
		} else if (isFocused && gfxOnMove != null) {
			showImage(gfxVariable, false);
			showImage(gfxOnMove, true);
			showImage(gfxOnClick, false);
			currentGfx = gfxOnMove;
		} else if (gfxVariable != null) {
			showImage(gfxVariable, true);
			showImage(gfxOnMove, false);
			showImage(gfxOnClick, false);
			currentGfx = gfxVariable;
		}
	}

	public boolean isFocused() {
		return isFocused;
	}

	public void setFocused(boolean focused) {
		isFocused = focused;
		if (gfxOnMove != null) {
			if (!focused) {
				showImage(gfxOnMove, false);
				showImage(gfxVariable, true);
				currentGfx = gfxVariable;
			} else {
				updateGraphicsVisibility();
			}
		} else {
			updateGraphicsVisibility();
		}
	}

	public void setPressed(boolean pressed) {
		isPressed = pressed;
		updateGraphicsVisibility();
	}

	public void loadImages() {
		if(getAttribute("RECT") != null && rectVariable == null) {
			if(!getAttribute("RECT").toString().contains(",")) {
				rectVariable = context.getVariable(getAttribute("RECT").getValue().toString());
			}
		}
		if(getAttribute("GFXSTANDARD") != null && gfxVariable == null) {
			gfxVariable = context.getVariable(getAttribute("GFXSTANDARD").getValue().toString());
			if(gfxVariable instanceof AnimoVariable) {
				AnimoVariable animoVariable = (AnimoVariable) gfxVariable;
				if(animoVariable.isPlaying()) {
					showImage(gfxVariable, true);
				}
				else {
					showImage(gfxVariable, isEnabled());
				}
			} else {
				showImage(gfxVariable, isEnabled());
			}
			currentGfx = gfxVariable;
		}

		if(getAttribute("GFXONMOVE") != null && gfxOnMove == null) {
			gfxOnMove = context.getVariable(getAttribute("GFXONMOVE").getValue().toString());
			showImage(gfxOnMove, false);
		}

		if(getAttribute("GFXONCLICK") != null && gfxOnClick == null) {
			gfxOnClick = context.getVariable(getAttribute("GFXONCLICK").getValue().toString());
			showImage(gfxOnClick, false);
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

	public boolean isEnabled() {
		return isEnabled;
	}

	public void setEnabled(boolean enabled) {
		isEnabled = enabled;
	}

	public Variable getCurrentImage() {
		if(currentGfx != null) {
			return currentGfx;
		}
		return rectVariable;
	}

	public Rectangle getRect() {
		if(rect == null) {
			if (getAttribute("GFXSTANDARD") != null) {
				Gdx.app.log("ButtonVariable", "Using GFXSTANDARD as RECT: " + getAttribute("GFXSTANDARD").getValue());

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

	private void showImage(Variable var, boolean visible) {
		if(var != null) {
			if(var instanceof ImageVariable) {
				ImageVariable imageVariable = (ImageVariable) var;
				imageVariable.setAttribute("VISIBLE", new Attribute("BOOL", visible ? "TRUE" : "FALSE"));
				imageVariable.setAttribute("TOCANVAS", new Attribute("BOOL", "TRUE"));
				imageVariable.changeVisibility(visible);
			}
			else if(var instanceof AnimoVariable) {
				AnimoVariable animoVariable = (AnimoVariable) var;
				animoVariable.setAttribute("VISIBLE", new Attribute("BOOL", visible ? "TRUE" : "FALSE"));
				animoVariable.setAttribute("TOCANVAS", new Attribute("BOOL", "TRUE"));
				animoVariable.changeVisibility(visible);
				if(!visible) {
					animoVariable.setPlaying(false);
				}
			}
		}
	}

	@Override
	public String getType() {
		return "BUTTON";
	}

	@Override
	public void setAttribute(String name, Attribute attribute) {
		List<String> knownAttributes = List.of("DRAGGABLE", "ENABLE", "GFXONCLICK", "GFXONMOVE", "GFXSTANDARD", "RECT", "SNDONMOVE");
		if (knownAttributes.contains(name)) {
			super.setAttribute(name, attribute);
            switch (name) {
				case "ENABLE":
					isEnabled = attribute.getValue().toString().equals("TRUE");
					if (!isEnabled) {
						hideGraphics();
					} else {
						updateGraphicsVisibility();
					}
					break;
				case "RECT":
					String rectRaw = getAttribute("RECT").getValue().toString();
					if (rectRaw.contains(",")) {
						String[] rectSplit = rectRaw.split(",");
						int xLeft = Integer.parseInt(rectSplit[0]);
						int yBottom = Integer.parseInt(rectSplit[1]);
						int xRight = Integer.parseInt(rectSplit[2]);
						int yTop = Integer.parseInt(rectSplit[3]);
						int height = yTop - yBottom;
						rect = new Rectangle(xLeft, yBottom - height, xRight, yTop - height);
					} else {
						Variable rectVariable = context.getVariable(rectRaw);
						if (rectVariable != null) {
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
            }
		}
	}
}
