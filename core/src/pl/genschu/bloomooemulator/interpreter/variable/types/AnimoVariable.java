package pl.genschu.bloomooemulator.interpreter.variable.types;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import pl.genschu.bloomooemulator.engine.filters.Filter;
import pl.genschu.bloomooemulator.interpreter.exceptions.ClassMethodNotImplementedException;
import pl.genschu.bloomooemulator.interpreter.Context;
import pl.genschu.bloomooemulator.interpreter.variable.Attribute;
import pl.genschu.bloomooemulator.interpreter.variable.Method;
import pl.genschu.bloomooemulator.interpreter.variable.Parameter;
import pl.genschu.bloomooemulator.interpreter.variable.Variable;
import pl.genschu.bloomooemulator.loader.AnimoLoader;
import pl.genschu.bloomooemulator.objects.Event;
import pl.genschu.bloomooemulator.objects.FrameData;
import pl.genschu.bloomooemulator.objects.Image;
import pl.genschu.bloomooemulator.objects.Rectangle;
import pl.genschu.bloomooemulator.utils.ArgumentsHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AnimoVariable extends Variable implements Cloneable {
	private int imagesCount = 0;
	private int colorDepth;
	private int eventsCount = 0;
	private String description;
	private int fps;
	private float frameDuration;
	private int opacity;
	private String signature;
	private List<Event> events;
	private List<Image> images;

	private Event currentEvent;
	private int currentFrameNumber = 0;
	private int currentImageNumber = 0;
	private Image currentImage;
	private boolean isPlaying = false;
	private boolean isVisible = true;
	private int posX = 0;
	private int posY = 0;
	private int maxWidth = 0;
	private int maxHeight = 0;
	private int direction = 1;
	private int priority = 0;
	private float elapsedTime = 0f;

	private int anchorX = 0;
	private int anchorY = 0;

	private Rectangle rect;
	private Music currentSfx;

	private boolean asButton;
	private boolean changeCursor;
	private boolean isFocused = false;
	private boolean isPressed = false;
	private boolean wasPressed = false;

	private boolean monitorCollision = false;

	private List<Filter> filters = new ArrayList<>();

	public AnimoVariable(String name, Context context) {
		super(name, context);

		rect = new Rectangle(0, 0, 0, 0);
	}

	@Override
	public String getType() {
		return "ANIMO";
	}

	@Override
	protected void setMethods() {
		super.setMethods();

		this.setMethod("GETANCHOR", new Method(
				List.of(
						new Parameter("STRING", "arrayName", true)
				),
				"INTEGER"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				String arrayName = ArgumentsHelper.getString(arguments.get(0));
				ArrayVariable array = (ArrayVariable) context.getVariable(arrayName);
				array.elements.add(new IntegerVariable("", anchorX, context));
				array.elements.add(new IntegerVariable("", anchorY, context));
				return null;
			}
		});
		this.setMethod("GETCENTERX", new Method(
				List.of(
						new Parameter("BOOL", "unknown", false)
				),
				"INTEGER"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				return new IntegerVariable("", rect.getXLeft()+rect.getWidth()/2, context);
			}
		});
		this.setMethod("GETCENTERY", new Method(
				List.of(
						new Parameter("BOOL", "unknown", false)
				),
				"INTEGER"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				return new IntegerVariable("", rect.getYTop()+rect.getHeight()/2, context);
			}
		});
		this.setMethod("GETCFRAMEINEVENT", new Method(
				"INTEGER"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				return new IntegerVariable("", currentFrameNumber, context);
			}
		});
		this.setMethod("GETCURRFRAMEPOSX", new Method(
				"INTEGER"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				return new IntegerVariable("", currentImage.offsetX, context);
			}
		});
		this.setMethod("GETCURRFRAMEPOSY", new Method(
				"INTEGER"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				return new IntegerVariable("", currentImage.offsetY, context);
			}
		});
		this.setMethod("GETENDX", new Method(
				"INTEGER"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				return new IntegerVariable("", rect.getXLeft()+rect.getWidth(), context);
			}
		});
		this.setMethod("GETENDY", new Method(
				"INTEGER"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				return new IntegerVariable("", rect.getYTop()+rect.getHeight(), context);
			}
		});
		this.setMethod("GETEVENTNAME", new Method(
				"STRING"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				return new StringVariable("", currentEvent.getName(), context);
			}
		});
		this.setMethod("GETFRAME", new Method(
				"INTEGER"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				return new IntegerVariable("", currentImageNumber, context);
			}
		});
		this.setMethod("GETFRAMENAME", new Method(
				"STRING"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				return new StringVariable("", !currentEvent.getFrameData().isEmpty() ? currentEvent.getFrameData().get(currentFrameNumber).getName() : "NULL", context);
			}
		});
		this.setMethod("GETHEIGHT", new Method(
				"INTEGER"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				return new IntegerVariable("", currentImage.height, context);
			}
		});
		this.setMethod("GETMAXWIDTH", new Method(
				"INTEGER"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				return new IntegerVariable("", maxWidth, context);
			}
		});
		this.setMethod("GETMAXHEIGHT", new Method(
				"INTEGER"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				return new IntegerVariable("", maxHeight, context);
			}
		});
		this.setMethod("GETNAME", new Method(
				"STRING"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				return new StringVariable("", getName(), context);
			}
		});
		this.setMethod("GETNOE", new Method(
				"INTEGER"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				return new IntegerVariable("", eventsCount, context);
			}
		});
		this.setMethod("GETNOF", new Method(
				"INTEGER"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				return new IntegerVariable("", imagesCount, context);
			}
		});
		this.setMethod("GETNOFINEVENT", new Method(
				List.of(
						new Parameter("STRING", "event", true)
				),
				"INTEGER"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				String eventName = ArgumentsHelper.getString(arguments.get(0));

				try {
					int eventNumber = Integer.parseInt(eventName);
					return new IntegerVariable("", events.get(eventNumber).getFramesCount(), context);
				} catch (NumberFormatException e) {
					int framesNumber = 0;
					for (Event event : events) {
						if (event.getName().equals(eventName)) {
							framesNumber = event.getFramesCount();
							break;
						}
					}
					return new IntegerVariable("", framesNumber, context);
				}
			}
		});
		this.setMethod("GETPOSITIONX", new Method(
				List.of(
						new Parameter("BOOL", "absolute", false)
				),
				"INTEGER"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				boolean absolute = !arguments.isEmpty(); // it seems like it must be any argument

				if(absolute) {
					return new IntegerVariable("", posX, context);
				} else {
					try {
						return new IntegerVariable("", rect.getXLeft(), context);
					} catch (IndexOutOfBoundsException e) {
						return new IntegerVariable("", posX, context);
					}
				}
			}
		});
		this.setMethod("GETPOSITIONY", new Method(
				List.of(
						new Parameter("BOOL", "absolute", false)
				),
				"INTEGER"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				boolean absolute = !arguments.isEmpty(); // it seems like it must be any argument

				if(absolute) {
					return new IntegerVariable("", posY, context);
				} else {
					try {
						return new IntegerVariable("", rect.getYTop(), context);
					} catch (IndexOutOfBoundsException e) {
						return new IntegerVariable("", posY, context);
					}
				}
			}
		});
		this.setMethod("GETPRIORITY", new Method(
				List.of(),
				"INTEGER"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				return new IntegerVariable("", priority, context);
			}
		});
		this.setMethod("GETWIDTH", new Method(
				"INTEGER"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				return new IntegerVariable("", currentImage.width, context);
			}
		});
		this.setMethod("HIDE", new Method(
				"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				isPlaying = false;
				hide();
				return null;
			}
		});
		this.setMethod("ISAT", new Method(
				List.of(
						new Parameter("INTEGER", "posX", true),
						new Parameter("INTEGER", "posY", true),
						new Parameter("BOOL", "checkAlpha?", true)
				),
				"BOOL"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				int posX = ArgumentsHelper.getInteger(arguments.get(0));
				int posY = ArgumentsHelper.getInteger(arguments.get(1));
				boolean checkAlpha = ArgumentsHelper.getBoolean(arguments.get(2)); // TODO: check how it works

				return new BoolVariable("", isAt(posX, posY), context);
			}
		});
		this.setMethod("ISNEAR", new Method(
				List.of(
						new Parameter("STRING", "varAnimo", true),
						new Parameter("INTEGER", "iouThreshold", true)
				),
				"BOOL"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				String varAnimo = ArgumentsHelper.getString(arguments.get(0));
				int iouThreshold = ArgumentsHelper.getInteger(arguments.get(1));

				Variable var = context.getVariable(varAnimo);
				if(!(var instanceof AnimoVariable) && !(var instanceof ImageVariable)) {
					return new BoolVariable("", false, context);
				}

				Rectangle rect1 = getRect();
				Rectangle rect2;
				if(var instanceof AnimoVariable) {
					rect2 = ((AnimoVariable) var).getRect();
				}
				else {
					rect2 = ((ImageVariable) var).getRect();
				}

				return new BoolVariable("", isNear(rect1, rect2, iouThreshold), context);
			}
		});
		this.setMethod("ISPLAYING", new Method(
				List.of(
						new Parameter("STRING", "event", false)
				),
				"BOOL"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: check if it works correct
				String eventName = arguments.isEmpty() ? null : ArgumentsHelper.getString(arguments.get(0));
				if(eventName == null) {
					return new BoolVariable("", isPlaying, context);
				}
				return new BoolVariable("", currentEvent.getName().equals(eventName) && isPlaying, context);
			}
		});
		this.setMethod("ISVISIBLE", new Method(
				"BOOL"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				return new BoolVariable("", isVisible, context);
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
				String path = ArgumentsHelper.getString(arguments.get(0));
				setAttribute("FILENAME", path);
				AnimoLoader.loadAnimo(AnimoVariable.this);
				show();
				return null;
			}
		});
		this.setMethod("MONITORCOLLISION", new Method(
				List.of(
						new Parameter("BOOL", "monitorAlpha", true)
				),
				"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				boolean monitorAlpha = ArgumentsHelper.getBoolean(arguments.get(0));
				setAttribute("MONITORCOLLISION", "TRUE");
				setAttribute("MONITORCOLLISIONALPHA", monitorAlpha ? "TRUE" : "FALSE");

				context.getGame().getQuadTree().insert(AnimoVariable.this);
				context.getGame().getCollisionMonitoredVariables().add(AnimoVariable.this);

				monitorCollision = true;

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
				int offsetX = ArgumentsHelper.getInteger(arguments.get(0));
				int offsetY = ArgumentsHelper.getInteger(arguments.get(1));

				posX += offsetX;
				posY += offsetY;
				//Gdx.app.log("updateRect()", "MOVE");
				updateRect();

				return null;
			}
		});
		this.setMethod("NEXTFRAME", new Method(
				"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				currentFrameNumber++;

				if(currentFrameNumber >= currentEvent.getFramesNumbers().size()) {
					currentFrameNumber = 0;
				}

				setCurrentImageNumber(currentEvent.getFramesNumbers().get(currentFrameNumber));
				//Gdx.app.log("updateRect()", "NEXTFRAME");
				show();
				return null;
			}
		});
		this.setMethod("NPLAY", new Method(
				List.of(
						new Parameter("INTEGER", "eventId", true)
				),
				"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				int eventId = ArgumentsHelper.getInteger(arguments.get(0));
				try {
					currentEvent = events.get(eventId);
					currentFrameNumber = 0;
					setCurrentImageNumber(currentEvent.getFramesNumbers().get(currentFrameNumber));
					playSfx();
					emitSignal("ONSTARTED", currentEvent.getName());
					isPlaying = true;
					//Gdx.app.log("updateRect()", "NPLAY");
					show();
				} catch (IndexOutOfBoundsException e) {
					Gdx.app.error("AnimoVariable", "Event with id " + eventId + " not found");
				}
				return null;
			}
		});
		this.setMethod("PAUSE", new Method(
				"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				isPlaying = false;
				return null;
			}
		});
		this.setMethod("PLAY", new Method(
				"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				currentFrameNumber = 0;
				setCurrentImageNumber(currentEvent.getFramesNumbers().get(currentFrameNumber));
				playSfx();
				emitSignal("ONSTARTED", currentEvent.getName());
				isPlaying = true;
				show();
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
				String eventName = ArgumentsHelper.getString(arguments.get(0), false);
				for(Event event : events) {
					if(event.getName().equalsIgnoreCase(eventName)) {
						currentEvent = event;
						currentFrameNumber = 0;
						setCurrentImageNumber(currentEvent.getFramesNumbers().get(currentFrameNumber));
						playSfx();
						emitSignal("ONSTARTED", currentEvent.getName());
						isPlaying = true;
						//Gdx.app.log("updateRect()", "PLAY");
						show();

						break;
					}
				}
				return null;
			}
		});
		this.setMethod("PREVFRAME", new Method(
				"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				currentFrameNumber--;
				if(currentFrameNumber < 0) {
					currentFrameNumber = currentEvent.getFramesNumbers().size() - 1;
				}
				setCurrentImageNumber(currentEvent.getFramesNumbers().get(currentFrameNumber));
				//Gdx.app.log("updateRect()", "PREVFRAME");
				show();
				return null;
			}
		});
		this.setMethod("REMOVEMONITORCOLLISION", new Method(
				List.of(),
				"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				setAttribute("MONITORCOLLISION", "FALSE");
				context.getGame().getQuadTree().remove(AnimoVariable.this);
				context.getGame().getCollisionMonitoredVariables().remove(AnimoVariable.this);

				monitorCollision = false;

				return null;
			}
		});
		this.setMethod("RESUME", new Method(
				"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				isPlaying = true;
				return null;
			}
		});
		this.setMethod("SETANCHOR", new Method(
				List.of(
						new Parameter("STRING", "anchor", true)
				),
				"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				String anchor = ArgumentsHelper.getString(arguments.get(0), false);

				switch (anchor) {
					case "CENTER":
						anchorX = rect.getXLeft() + rect.getWidth() / 2;
						anchorY = rect.getYTop() + rect.getHeight() / 2;
						break;
					case "LEFTUPPER":
						anchorX = rect.getXLeft();
						anchorY = rect.getYTop();
						break;
					case "RIGHTUPPER":
						anchorX = rect.getXRight();
						anchorY = rect.getYTop();
						break;
					case "LEFTLOWER":
						anchorX = rect.getXLeft();
						anchorY = rect.getYBottom();
						break;
					case "RIGHTLOWER":
						anchorX = rect.getXRight();
						anchorY = rect.getYBottom();
						break;
					case "LEFT":
						anchorX = rect.getXLeft();
						anchorY = rect.getYTop() + rect.getHeight() / 2;
						break;
					case "RIGHT":
						anchorX = rect.getXRight();
						anchorY = rect.getYTop() + rect.getHeight() / 2;
						break;
					case "TOP":
						anchorX = rect.getXLeft() + rect.getWidth() / 2;
						anchorY = rect.getYTop();
						break;
					case "BOTTOM":
						anchorX = rect.getXLeft() + rect.getWidth() / 2;
						anchorY = rect.getYBottom();
						break;
					default:
						Gdx.app.log("AnimoVariable", "Unknown anchor: " + anchor);
						break;
				}

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
				anchorX = ArgumentsHelper.getInteger(arguments.get(0));
				anchorY = ArgumentsHelper.getInteger(arguments.get(1));
				return null;
			}
		});
		this.setMethod("SETASBUTTON", new Method(
				List.of(
						new Parameter("BOOL", "enabled", true),
						new Parameter("BOOL", "changeCursor", true)
				),
				"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				boolean enabled = ArgumentsHelper.getBoolean(arguments.get(0));
				boolean changeCursor = ArgumentsHelper.getBoolean(arguments.get(1));

				if (!enabled && asButton && isPressed) {
					Gdx.app.log("AnimoVariable", "Disabling pressed animation button without triggering release events");
					if (context.getGame().getInputManager() != null &&
							context.getGame().getInputManager().getActiveButton() == AnimoVariable.this) {
						context.getGame().getInputManager().clearActiveButton(AnimoVariable.this);
					}
					isPressed = false;
					wasPressed = false;
					isFocused = false;
				}

				if(enabled) {
					getContext().addButtonVariable(AnimoVariable.this);
				}
				else {
					getContext().removeButtonVariable(AnimoVariable.this);
				}
				setAsButton(enabled);
				setChangeCursor(changeCursor);
				show();

				return null;
			}
		});
		this.setMethod("SETBACKWARD", new Method(
				"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				direction = -1;
				return null;
			}
		});
		this.setMethod("SETFORWARD", new Method(
				"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				direction = 1;
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
				int fps = ArgumentsHelper.getInteger(arguments.get(0));
				setFps(fps);
				return null;
			}
		});
		this.setMethod("SETFRAME", new Method(
				List.of(
						new Parameter("INTEGER", "frameNumber", true)
				),
				"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				int frameNumber = ArgumentsHelper.getInteger(arguments.get(0));
				currentFrameNumber = 0;
				setCurrentImageNumber(frameNumber);
				//show();
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
				String eventName = ArgumentsHelper.getString(arguments.get(0));
				for (Event event : events) {
					if (event.getName().equalsIgnoreCase(eventName)) {
						currentEvent = event;
						break;
					}
				}
				if(arguments.size() >= 2) {
					currentFrameNumber = ArgumentsHelper.getInteger(arguments.get(1));
				}
				else {
					currentFrameNumber = 0;
				}
				if(!currentEvent.getFrames().isEmpty()) {
					setCurrentImageNumber(currentEvent.getFramesNumbers().get(currentFrameNumber));
				}
				else {
					currentImageNumber = 0;
					currentImage = null;
					//Gdx.app.log("updateRect()", "SETFRAME (empty)");
					rect.setXLeft(0);
					rect.setYTop(0);
					rect.setXRight(1);
					rect.setYBottom(1);
				}
				//show();
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
				throw new ClassMethodNotImplementedException("Method SETFRAMENAME is not implemented yet");
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
				opacity = ArgumentsHelper.getInteger(arguments.get(0));
				setAttribute("OPACITY", new Attribute("INTEGER", opacity));
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
				posX = ArgumentsHelper.getInteger(arguments.get(0)) - anchorX;
				posY = ArgumentsHelper.getInteger(arguments.get(1)) - anchorY;
				//Gdx.app.log("updateRect()", "SETPOSITION");
				updateRect();
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
				priority = ArgumentsHelper.getInteger(arguments.get(0));
				setAttribute("PRIORITY", new Attribute("INTEGER", priority));
				return null;
			}
		});
		this.setMethod("SHOW", new Method(
				List.of(
						new Parameter("BOOL", "unknown", false)
				),
				"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				show();
				return null;
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
				boolean emitSignal = arguments.isEmpty();

				if(!emitSignal) {
					emitSignal = ArgumentsHelper.getBoolean(arguments.get(0));
				}

				currentFrameNumber = 0;
				setCurrentImageNumber(currentEvent.getFramesNumbers().get(currentFrameNumber));
				isPlaying = false;

				if(emitSignal) {
					emitSignal("ONFINISHED", currentEvent.getName());
				}
				return null;
			}
		});
		this.setMethod("TOP", new Method(
				List.of(
						new Parameter("BOOL", "unknown", false)
				),
				"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				throw new ClassMethodNotImplementedException("Method TOP is not implemented yet");
			}
		});
	}

	@Override
	public void setAttribute(String name, Attribute attribute) {
		List<String> knownAttributes = List.of("ASBUTTON", "FILENAME", "FLUSHAFTERPLAYED", "FPS", "MONITORCOLLISION", "MONITORCOLLISIONALPHA", "PRELOAD", "PRIORITY", "RELEASE", "TOCANVAS", "VISIBLE");
		if(knownAttributes.contains(name)) {
			super.setAttribute(name, attribute);
			switch (name) {
				case "FPS":
					setFps(Integer.parseInt(getAttribute("FPS").getValue().toString()));
					break;
				case "FILENAME":
					String filename = getAttribute("FILENAME").getValue().toString();
					if(!filename.endsWith(".ANN")) {
						filename = filename + ".ANN";
					}
					getAttribute("FILENAME").setValue(filename);
					try {
						AnimoLoader.loadAnimo(this);
						if (events != null && !events.isEmpty()) {
							for(Event event : events) {
								if(!event.getFrames().isEmpty()) {
									currentEvent = event;
									break;
								}
							}
							currentFrameNumber = 0;
							if (!currentEvent.getFrames().isEmpty()) {
								setCurrentImageNumber(currentEvent.getFramesNumbers().get(0));
							}
						}
					} catch (Exception e) {
						Gdx.app.error("AnimoVariable", "Error loading ANIMO variable: " + filename, e);
					}
					break;
				case "PRIORITY":
					priority = Integer.parseInt(getAttribute("PRIORITY").getValue().toString());
					break;
				case "VISIBLE":
					changeVisibility(attribute.getValue().toString().equals("TRUE"));
					break;
				case "ASBUTTON":
					setAsButton(attribute.getValue().toString().equals("TRUE"));
					changeCursor = true;
					break;
				case "MONITORCOLLISION":
					monitorCollision = attribute.getValue().toString().equals("TRUE");
					break;
			}
		}
	}

	public void updateAnimation(float deltaTime) {
		if (currentEvent == null) {
			return;
		}

		if (!isPlaying) {
			return;
		}

		if(currentEvent.getFramesCount() == 0) {
			isPlaying = false;
			return;
		}

		elapsedTime += deltaTime;
		if (elapsedTime >= frameDuration) {
			elapsedTime -= frameDuration;

			if(elapsedTime >= frameDuration) {
				elapsedTime = 0; // only when engine is lagging, set elapsedTime to zero to eliminate animation fast forward
			}

			currentFrameNumber += direction;

			if (currentFrameNumber >= currentEvent.getFrames().size()) {
				currentFrameNumber = currentEvent.getFrames().size() - 1;

				if(currentEvent.getLoopBy() == 0) { // TODO: check, how this value works
					isPlaying = false;
					emitSignal("ONFINISHED", currentEvent.getName());
				}
				else {
					currentFrameNumber = 0;
					setCurrentImageNumber(currentEvent.getFramesNumbers().get(currentFrameNumber));
					playSfx();
				}
			}
			else {
				setCurrentImageNumber(currentEvent.getFramesNumbers().get(currentFrameNumber));
				playSfx();
			}
		}
	}

	private void playSfx() {
		if (!currentEvent.getFrameData().get(currentFrameNumber).getSfxAudio().isEmpty()) {
			Gdx.app.log("AnimoVariable", "Playing sfx in animo " + currentEvent.getName());
			int randomIndex = getRandomIndex(currentEvent.getFrameData().get(currentFrameNumber).getSfxAudio());
			Music music = currentEvent.getFrameData().get(currentFrameNumber).getSfxAudio().get(randomIndex);

			if(currentSfx != null) currentSfx.stop();
			currentSfx = music;
			currentSfx.play();
		}
	}

	private int getRandomIndex(List<Music> musicList) {
		return new Random().nextInt(musicList.size());
	}


	public Rectangle getRect() {
		return rect;
	}

	private void updateRect() {
		//Gdx.app.debug("AnimoVariable ("+this.getName()+")", "Updating rect...");
		if (monitorCollision) {
			context.getGame().getQuadTree().remove(this);
		}

		if (currentImage == null) {
			rect.setXLeft(posX);
			rect.setYTop(posY);
			rect.setXRight(posX + 1);
			rect.setYBottom(posY - 1);
		} else {
			try {
				FrameData frameData = currentEvent != null && !currentEvent.getFrameData().isEmpty()
						? currentEvent.getFrameData().get(currentFrameNumber)
						: null;

				int frameOffsetX = frameData != null ? frameData.getOffsetX() : 0;
				int frameOffsetY = frameData != null ? frameData.getOffsetY() : 0;

				rect.setXLeft(posX + frameOffsetX + currentImage.offsetX);
				rect.setYTop(posY + frameOffsetY + currentImage.offsetY);
				rect.setXRight(rect.getXLeft() + currentImage.width);
				rect.setYBottom(rect.getYTop() - currentImage.height);
			} catch (Exception e) {
				Gdx.app.error("AnimoVariable", "Error updating rect: " + e.getMessage());
				rect.setXLeft(posX);
				rect.setYTop(posY);
				rect.setXRight(posX + 1);
				rect.setYBottom(posY - 1);
			}
		}

		if (monitorCollision) {
			context.getGame().getQuadTree().insert(this);
		}

		//Gdx.app.debug("AnimoVariable ("+this.getName()+")", "Rect: " + rect);
	}

	private void updateRect(Image image) {
		//Gdx.app.debug("AnimoVariable ("+this.getName()+")", "Updating rect from Image...");
		rect.setXLeft(image.offsetX - posX);
		rect.setYTop(image.offsetY - posY);
		rect.setXRight(rect.getXLeft() + image.width);
		rect.setYBottom(rect.getYTop() - image.height);

		//Gdx.app.debug("AnimoVariable ("+this.getName()+")", "Rect: " + rect);
	}

	public boolean isColliding(Rectangle rect1, Rectangle rect2) {
		return isNear(rect1, rect2, 0);
	}

	public boolean isNear(Rectangle rect1, Rectangle rect2, int iouThreshold) {
		// get intersection area
		int intersectionX = Math.max(rect1.getXLeft(), rect2.getXLeft());
		int intersectionY = Math.max(rect1.getYBottom(), rect2.getYBottom());
		int intersectionWidth = Math.min(rect1.getXRight(), rect2.getXRight()) - intersectionX;
		int intersectionHeight = Math.min(rect1.getYTop(), rect2.getYTop()) - intersectionY;

		// check for overlap
		if (intersectionWidth <= 0 || intersectionHeight <= 0) {
			return false;  // no overlap
		}

		// calculating area of intersection
		int intersectionArea = intersectionWidth * intersectionHeight;

		// calculating union area (sum of areas minus intersection)
		int rect1Area = rect1.area();
		int rect2Area = rect2.area();
		int unionArea = rect1Area + rect2Area - intersectionArea;

		// calculating iou as a percentage
		double iou = (double) intersectionArea / unionArea * 100;

		// return true if iou is greater than threshold
		return iou >= iouThreshold;
	}

	public boolean isAt(int x, int y) {
		// TODO: check how it works
		return x >= rect.getXLeft() && x <= rect.getXRight() && y >= rect.getYTop() && y <= rect.getYBottom();
	}

	@Override
	public Method getMethod(String name, List<String> paramTypes) {
		Method method = super.getMethod(name, paramTypes);
		if (currentImage == null) {
			getCurrentImage();
		}
		return method;
	}

	public int getImagesCount() {
		return imagesCount;
	}

	public void setImagesCount(int imagesCount) {
		this.imagesCount = imagesCount;
	}

	public int getColorDepth() {
		return colorDepth;
	}

	public void setColorDepth(int colorDepth) {
		this.colorDepth = colorDepth;
	}

	public int getEventsCount() {
		return eventsCount;
	}

	public void setEventsCount(int eventsCount) {
		this.eventsCount = eventsCount;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getFps() {
		return fps;
	}

	public void setFps(int fps) {
		this.fps = fps;
		this.frameDuration = 1f / fps;
	}

	public float getOpacity() {
		int opacity = this.opacity;
		if(this.currentEvent != null) {
			opacity *= this.currentEvent.getOpacity();
			opacity /= 255;

			if(currentImage != null && !currentEvent.getFrameData().isEmpty()) {
				opacity *= currentEvent.getFrameData().get(currentFrameNumber).getOpacity();
				opacity /= 255;
			}
		}

		return opacity / 255f;
	}

	public void setOpacity(int opacity) {
		this.opacity = opacity;
	}

	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}

	public List<Event> getEvents() {
		return events;
	}

	public void setEvents(List<Event> events) {
		this.events = events;
	}

	public List<Image> getImages() {
		return images;
	}

	public void setImages(List<Image> images) {
		this.images = images;
	}

	public int getPosX() {
		return posX;
	}

	public void setPosX(int posX) {
		this.posX = posX;
		updateRect();
	}

	public int getPosY() {
		return posY;
	}

	public void setPosY(int posY) {
		this.posY = posY;
		updateRect();
	}

	public Event getCurrentEvent() {
		return currentEvent;
	}

	public void setCurrentEvent(Event currentEvent) {
		this.currentEvent = currentEvent;
	}

	public int getCurrentFrameNumber() {
		return currentFrameNumber;
	}

	public void setCurrentFrameNumber(int currentFrameNumber) {
		if (currentFrameNumber >= currentEvent.getFrames().size()) {
			currentFrameNumber = currentEvent.getFrames().size() - 1;
		}
		this.currentFrameNumber = currentFrameNumber;
		this.currentImageNumber = currentEvent.getFramesNumbers().get(currentFrameNumber);
		this.currentImage = currentEvent.getFrames().get(currentImageNumber);
		updateRect();
	}

	public int getCurrentImageNumber() {
		return currentImageNumber;
	}

	public void setCurrentImageNumber(int currentImageNumber) {
		//boolean isFrameChanged = this.currentImageNumber != currentImageNumber;
		this.currentImageNumber = currentImageNumber;
		this.currentImage = getImages().get(currentImageNumber);
		updateRect();
		//if(isFrameChanged) {
			emitSignal("ONFRAMECHANGED", currentEvent != null ? currentEvent.getName() : null);
		//}
	}

	public Image getCurrentImage() {
		if (currentImage == null) {
			if (currentEvent == null) {
				if (images != null && !images.isEmpty()) {
					currentImage = images.get(0);
				}
				else {
					AnimoLoader.loadAnimo(this);
					if(!images.isEmpty())
						return getCurrentImage();
				}
			} else {
				if (!currentEvent.getFrames().isEmpty()) {
					currentImage = currentEvent.getFrames().get(currentImageNumber);
				} else if (images != null && !images.isEmpty()) {
					currentImage = images.get(0);
				}
			}
			if (currentImage != null) {
				updateRect();
			}
		}
		return currentImage;
	}

	public void setCurrentImage(Image currentImage) {
		this.currentImage = currentImage;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public boolean isPlaying() {
		return isPlaying;
	}

	public void setPlaying(boolean playing) {
		isPlaying = playing;
	}

	public boolean isVisible() {
		return isVisible;
	}

	public boolean isRenderedOnCanvas() {
		try {
			return this.getAttribute("TOCANVAS").getValue().toString().equals("TRUE");
		} catch (NullPointerException e) {
			return false;
		}
	}

	public boolean hasEvent(String eventName) {
		for(Event event : events) {
			if(event.getName().equals(eventName)) {
				return true;
			}
		}
		return false;
	}

	public void changeVisibility(boolean visibility) {
		if(visibility) show();
		else hide();
	}

	private void show() {
		getAttribute("VISIBLE").setValue("TRUE");
		isVisible = true;
	}

	private void hide() {
		getAttribute("VISIBLE").setValue("FALSE");
		isVisible = false;
	}

	public List<Event> getEventsWithPrefix(String prefix) {
		List<Event> eventsWithPrefix = new ArrayList<>();
		for(Event event : events) {
			if(event.getName().startsWith(prefix+"_")) {
				eventsWithPrefix.add(event);
			}
		}
		return eventsWithPrefix;
	}

	public int getMaxWidth() {
		return maxWidth;
	}

	public void setMaxWidth(int maxWidth) {
		this.maxWidth = maxWidth;
	}

	public int getMaxHeight() {
		return maxHeight;
	}

	public void setMaxHeight(int maxHeight) {
		this.maxHeight = maxHeight;
	}

	public boolean isAsButton() {
		return asButton;
	}

	public void setAsButton(boolean asButton) {
		this.asButton = asButton;
		if(asButton) {
			fireMethod("PLAY", new StringVariable("", "ONNOEVENT", context));
		}
		show();
	}

	public boolean isChangeCursor() {
		return changeCursor;
	}

	public void setChangeCursor(boolean changeCursor) {
		this.changeCursor = changeCursor;
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

	public void setPressed(boolean pressed) {
		isPressed = pressed;
	}

	public boolean isWasPressed() {
		return wasPressed;
	}

	public void setWasPressed(boolean wasPressed) {
		this.wasPressed = wasPressed;
	}

	public void addFilter(Filter filter) {
		filters.add(filter);
	}

	public void removeFilter(Filter filter) {
		filters.remove(filter);
	}

	public boolean hasFilters() {
		return !filters.isEmpty();
	}

	public List<Filter> getFilters() {
		return filters;
	}

	@Override
	public AnimoVariable clone() {
		AnimoVariable clone = (AnimoVariable) super.clone();
		this.rect = new Rectangle(rect.getXLeft(), rect.getYBottom(), rect.getXRight(), rect.getYTop());
		return clone;
	}
}
