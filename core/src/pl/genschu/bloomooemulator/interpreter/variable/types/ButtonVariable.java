package pl.genschu.bloomooemulator.interpreter.variable.types;

import com.badlogic.gdx.Gdx;
import pl.genschu.bloomooemulator.engine.decision.events.AnimoEvent;
import pl.genschu.bloomooemulator.engine.decision.states.AnimoState;
import pl.genschu.bloomooemulator.engine.decision.trees.ButtonStateTransitionTree;
import pl.genschu.bloomooemulator.engine.decision.events.ButtonEvent;
import pl.genschu.bloomooemulator.engine.decision.states.ButtonState;
import pl.genschu.bloomooemulator.interpreter.Context;
import pl.genschu.bloomooemulator.interpreter.variable.*;
import pl.genschu.bloomooemulator.objects.Rectangle;
import pl.genschu.bloomooemulator.utils.ArgumentsHelper;

import java.util.List;

public class ButtonVariable extends Variable {
	private Rectangle rect = null;

	private ButtonState state = ButtonState.INIT;

	private Variable rectVariable;
	private Variable gfxOnMove;
	private Variable gfxVariable;
	private Variable gfxOnClick;
	private Variable currentGfx;

	private Variable soundStandard = null;
	private Variable soundOnMove = null;
	private Variable soundOnClick = null;

	public ButtonVariable(String name, Context context) {
		super(name, context);
	}

	@Override
	protected void setMethods() {
		super.setMethods();

		this.setMethod("DISABLE", new Method("void") {
			@Override
			public Variable execute(List<Object> arguments) {
				if (context.getGame().getInputManager() != null &&
						context.getGame().getInputManager().getActiveButton() == ButtonVariable.this) {
					context.getGame().getInputManager().clearActiveButton(ButtonVariable.this);
				}

				changeState(ButtonEvent.DISABLE);
				return null;
			}
		});

		this.setMethod("DISABLEBUTVISIBLE", new Method("void") {
			@Override
			public Variable execute(List<Object> arguments) {
				if (context.getGame().getInputManager() != null &&
						context.getGame().getInputManager().getActiveButton() == ButtonVariable.this) {
					context.getGame().getInputManager().clearActiveButton(ButtonVariable.this);
				}

				changeState(ButtonEvent.DISABLE_BUT_VISIBLE);
				return null;
			}
		});

		this.setMethod("ENABLE", new Method("void") {
			@Override
			public Variable execute(List<Object> arguments) {
				changeState(ButtonEvent.ENABLE);
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
				// Redirect argument to GFXes
				if (gfxVariable != null) {
					gfxVariable.fireMethod("SETPRIORITY", arguments.get(0));
				}
				if (gfxOnMove != null) {
					gfxOnMove.fireMethod("SETPRIORITY", arguments.get(0));
				}
				if (gfxOnClick != null) {
					gfxOnClick.fireMethod("SETPRIORITY", arguments.get(0));
				}
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

	public void stopAllSounds() {
		if(soundStandard != null) {
			try {
				((SoundVariable) soundStandard).getSound().stop();
			} catch (NullPointerException e) {
				Gdx.app.log("ButtonVariable", "Sound in soundStandard is null");
			}
		}

		if(soundOnMove != null) {
			try {
				((SoundVariable) soundOnMove).getSound().stop();
			} catch (NullPointerException e) {
				Gdx.app.log("ButtonVariable", "Sound in soundOnMove is null");
			}
		}

		if(soundOnClick != null) {
			try {
				((SoundVariable) soundOnClick).getSound().stop();
			} catch (NullPointerException e) {
				Gdx.app.log("ButtonVariable", "Sound in soundOnClick is null");
			}
		}
	}

	public void playSndIfExists(Variable sound) {
		if(sound != null) {
			((SoundVariable) sound).getSound().play();
		}
	}

	private void showStandard() {
		showImage(gfxOnMove, false);
		showImage(gfxOnClick, false);

		// check if graphic is not already visible
		if(gfxVariable != null) {
			if(gfxVariable instanceof AnimoVariable) {
				if(!((AnimoVariable) gfxVariable).isVisible())
					showImage(gfxVariable, true);
			}
			else if(gfxVariable instanceof ImageVariable) {
				if(!((ImageVariable) gfxVariable).isVisible())
					showImage(gfxVariable, true);
			}
		}
	}

	private void showOnMove() {
		// check if GFXONMOVE exists
		if(gfxOnMove == null) {
			return;
		}

		showImage(gfxVariable, false);
		showImage(gfxOnClick, false);
		showImage(gfxOnMove, true);
	}

	private void showOnClick() {
		// check if GFXONCLICK exists
		if(gfxOnClick == null) {
			return;
		}

		showImage(gfxVariable, false);
		showImage(gfxOnMove, false);
		showImage(gfxOnClick, true);
	}

	public void changeState(ButtonEvent event) {
		ButtonState oldState = state;
		state = ButtonStateTransitionTree.evaluate(this, event);

		if(oldState != state) {
			switch (state) {
				case STANDARD:
					showStandard();

					stopAllSounds();
					playSndIfExists(soundStandard);

					if(oldState == ButtonState.HOVERED)
						emitSignal("ONFOCUSOFF");
					break;
				case HOVERED:
					showOnMove();

					stopAllSounds();
					if(oldState == ButtonState.STANDARD)
						playSndIfExists(soundOnMove);

					if(oldState == ButtonState.PRESSED) {
						emitSignal("ONRELEASED");
						if(isEnabled())
							emitSignal("ONACTION");
					}

					if(isEnabled())
						emitSignal("ONFOCUSON");
					break;
				case PRESSED:
					showOnClick();

					stopAllSounds();
					if(oldState == ButtonState.HOVERED)
						playSndIfExists(soundOnClick);

					emitSignal("ONCLICKED");
					break;
				case DISABLED:
					if(oldState == ButtonState.INIT) {
						// check if animo isn't playing before assigning to button
						if(gfxVariable != null) {
							if((gfxVariable instanceof AnimoVariable && !((AnimoVariable) gfxVariable).isPlaying())
								|| gfxVariable instanceof ImageVariable)
									showImage(gfxVariable, false);
						}
						showImage(gfxOnMove, false);
						showImage(gfxOnClick, false);
					}
					else {
						showImage(gfxVariable, false);
						showImage(gfxOnMove, false);
						showImage(gfxOnClick, false);
					}

					stopAllSounds();
					break;
				case DISABLED_BUT_VISIBLE:
					showStandard();

					stopAllSounds();
					break;
			}

			Gdx.app.debug("ButtonVariable", getName() + " state changed from " + oldState + " to " + state + " because of event " + event);
		}
	}

	@Override
	public void init() {
		//load();
		//getRect();
		setDefaultState();
	}

	public void load() {
		if(getAttribute("RECT") != null && rectVariable == null) {
			if(!getAttribute("RECT").toString().contains(",")) {
				rectVariable = context.getVariable(getAttribute("RECT").getValue().toString());
			}
		}
		if(getAttribute("GFXSTANDARD") != null && gfxVariable == null) {
			gfxVariable = context.getVariable(getAttribute("GFXSTANDARD").getValue().toString());
			currentGfx = gfxVariable;
		}

		if(getAttribute("GFXONMOVE") != null && gfxOnMove == null) {
			gfxOnMove = context.getVariable(getAttribute("GFXONMOVE").getValue().toString());
		}

		if(getAttribute("GFXONCLICK") != null && gfxOnClick == null) {
			gfxOnClick = context.getVariable(getAttribute("GFXONCLICK").getValue().toString());
		}

		if(getAttribute("SNDSTANDARD") != null && soundStandard == null) {
			soundStandard = context.getVariable(getAttribute("SNDSTANDARD").getValue().toString());
		}

		if(getAttribute("SNDONMOVE") != null && soundOnMove == null) {
			soundOnMove = context.getVariable(getAttribute("SNDONMOVE").getValue().toString());
		}

		if(getAttribute("SNDONCLICK") != null && soundOnClick == null) {
			soundOnClick = context.getVariable(getAttribute("SNDONCLICK").getValue().toString());
		}
	}

	private void setDefaultState() {
		boolean isEnabled = getAttribute("ENABLE") != null && getAttribute("ENABLE").getBool();

		if(getState() == ButtonState.INIT) {
			if (!isEnabled) {
				changeState(ButtonEvent.DISABLE);
			} else {
				changeState(ButtonEvent.ENABLE);
			}
		}
	}

	public Variable getCurrentImage() {
		return currentGfx;
	}

	public Variable getSoundStandard() {
		return soundStandard;
	}

	public Variable getSoundOnMove() {
		return soundOnMove;
	}

	public Variable getSoundOnClick() {
		return soundOnClick;
	}

	public ButtonState getState() {
		return state;
	}

	public Rectangle getRect() {
		if(rect == null) {
			if (getAttribute("GFXSTANDARD") != null) {
				Gdx.app.log("ButtonVariable", "Using GFXSTANDARD as RECT: " + getAttribute("GFXSTANDARD").getValue());

				if (gfxVariable instanceof ImageVariable) {
					ImageVariable imageVariable = (ImageVariable) gfxVariable;
					rect = imageVariable.getRect();
				} else if (gfxVariable instanceof AnimoVariable) {
					AnimoVariable animoVariable = (AnimoVariable) gfxVariable;
					rect = animoVariable.getRect();
				}
			}
			else if(getAttribute("RECT") != null) {
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
				animoVariable.setAttribute("TOCANVAS", new Attribute("BOOL", "TRUE"));

				if(visible) {
					if(animoVariable.getAnimationState() == AnimoState.HIDDEN)
						animoVariable.changeAnimoState(AnimoEvent.PLAY);
					else {
						animoVariable.changeAnimoState(AnimoEvent.SHOW);
					}
				}
				else {
					animoVariable.changeAnimoState(AnimoEvent.STOP, false); // first stop
					animoVariable.changeAnimoState(AnimoEvent.HIDE); // then hide
				}
			}
		}
	}

	public boolean isEnabled() {
		return state != ButtonState.DISABLED && state != ButtonState.DISABLED_BUT_VISIBLE;
	}

	@Override
	public String getType() {
		return "BUTTON";
	}

	@Override
	public void setAttribute(String name, Attribute attribute) {
		List<String> knownAttributes = List.of("DRAGGABLE", "ENABLE", "GFXONCLICK", "GFXONMOVE", "GFXSTANDARD", "RECT", "SNDONMOVE", "SNDONCLICK", "SNDSTANDARD");
		if (knownAttributes.contains(name)) {
			super.setAttribute(name, attribute);
			switch (name) {
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
