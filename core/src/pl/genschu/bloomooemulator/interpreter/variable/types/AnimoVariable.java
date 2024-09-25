package pl.genschu.bloomooemulator.interpreter.variable.types;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
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

import java.util.List;
import java.util.Random;

public class AnimoVariable extends Variable implements Cloneable{
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
	private int endPosX = 0;
	private int endPosY = 0;
	private int centerX = 0;
	private int centerY = 0;
	private int maxWidth = 0;
	private int maxHeight = 0;
	private int direction = 1;
	private int priority = 0;
	private float elapsedTime = 0f;

	private Rectangle rect;
	private Music currentSfx;

	private boolean asButton;
	private boolean changeCursor;
	private boolean isFocused = false;
	private boolean isPressed = false;
	private boolean wasPressed = false;

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

		this.setMethod("GETCENTERX", new Method(
				List.of(
						new Parameter("BOOL", "unknown", false)
				),
				"INTEGER"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				return new IntegerVariable("", centerX, context);
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
				return new IntegerVariable("", centerY, context);
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
				return new IntegerVariable("", currentEvent.getFrameData().get(currentFrameNumber).getOffsetX(), context);
			}
		});
		this.setMethod("GETCURRFRAMEPOSY", new Method(
				"INTEGER"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				return new IntegerVariable("", currentEvent.getFrameData().get(currentFrameNumber).getOffsetY(), context);
			}
		});
		this.setMethod("GETENDX", new Method(
				"INTEGER"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				return new IntegerVariable("", endPosX, context);
			}
		});
		this.setMethod("GETENDY", new Method(
				"INTEGER"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				return new IntegerVariable("", endPosY, context);
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
						return new IntegerVariable("", posX + currentEvent.getFrameData().get(currentFrameNumber).getOffsetX() + currentImage.offsetX, context);
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
						return new IntegerVariable("", posY + currentEvent.getFrameData().get(currentFrameNumber).getOffsetY() + currentImage.offsetY, context);
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
				hide();
				return null;
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

				currentImageNumber = currentEvent.getFramesNumbers().get(currentFrameNumber);
				currentImage = currentEvent.getFrames().get(currentImageNumber);
				//Gdx.app.log("updateRect()", "NEXTFRAME");
				show();
				updateRect();
				emitSignal("ONFRAMECHANGED", currentEvent.getName());
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
				currentEvent = events.get(eventId);
				currentFrameNumber = 0;
				isPlaying = true;
				//Gdx.app.log("updateRect()", "NPLAY");
				show();
				updateRect();
				playSfx();
				emitSignal("ONSTARTED", currentEvent.getName());
				emitSignal("ONFRAMECHANGED", currentEvent.getName());
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
				List.of(
						new Parameter("STRING", "anim", true)
				),
				"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				String eventName = ArgumentsHelper.getString(arguments.get(0));
				for(Event event : events) {
					if(event.getName().equals(eventName)) {
						currentEvent = event;
						currentFrameNumber = 0;
						currentImage = currentEvent.getFrames().get(currentFrameNumber);
						isPlaying = true;
						//Gdx.app.log("updateRect()", "PLAY");
						show();
						updateRect();
						playSfx();
						emitSignal("ONSTARTED", currentEvent.getName());
						emitSignal("ONFRAMECHANGED", currentEvent.getName());
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
				currentImageNumber = currentEvent.getFramesNumbers().get(currentFrameNumber);
				currentImage = currentEvent.getFrames().get(currentImageNumber);
				//Gdx.app.log("updateRect()", "PREVFRAME");
				show();
				updateRect();
				emitSignal("ONFRAMECHANGED", currentEvent.getName());
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
				// TODO: implement this method
				throw new ClassMethodNotImplementedException("Method SETANCHOR is not implemented yet");
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
				throw new ClassMethodNotImplementedException("Method SETANCHOR is not implemented yet");
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
				boolean changeCursor = ArgumentsHelper.getBoolean(arguments.get(1)); // TODO: make this working

				if(enabled) {
					getContext().addButtonVariable(AnimoVariable.this);
				}
				else {
					getContext().getButtonsVariables().remove(getName());
				}
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

				currentImageNumber = frameNumber;
				try {
					currentImage = getImages().get(currentImageNumber);
					emitSignal("ONFRAMECHANGED");
					//Gdx.app.log("updateRect()", "SETFRAME");
					updateRect(currentImage);
				} catch (IndexOutOfBoundsException ignored) {}
				show();
				return null;
			}
		});
		this.setMethod("SETFRAME", new Method(
				List.of(
						new Parameter("STRING", "event", true),
						new Parameter("STRING", "frameName", true)
				),
				"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				String eventName = ArgumentsHelper.getString(arguments.get(0));
				Variable variable = getContext().getVariable(eventName);
				if(variable != null) {
					eventName = variable.getValue().toString();
				}
				try {
					int eventNumber = Integer.parseInt(eventName);
					currentEvent = events.get(eventNumber);
				} catch (NumberFormatException e) {
					for (Event event : events) {
						if (event.getName().equals(eventName)) {
							currentEvent = event;
							break;
						}
					}
				}
				if(arguments.size() >= 2) {
					String frameName = ArgumentsHelper.getString(arguments.get(1));
					int tmpIndex = 0;
					boolean found = false;
					for(FrameData frameData : currentEvent.getFrameData()) {
						if(frameData.getName().equals(frameName)) {
							currentFrameNumber = tmpIndex;
							found = true;
							break;
						}
						tmpIndex++;
					}
					if(!found) {
						currentFrameNumber = 0;
					}
				}
				else {
					currentFrameNumber = 0;
				}
				if(!currentEvent.getFrames().isEmpty()) {
					currentImageNumber = currentEvent.getFramesNumbers().get(currentFrameNumber);
					currentImage = currentEvent.getFrames().get(currentFrameNumber);
					//Gdx.app.log("updateRect()", "SETFRAME");
					updateRect();
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
				emitSignal("ONFRAMECHANGED", currentEvent.getName());
				show();
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
				posX = ArgumentsHelper.getInteger(arguments.get(0));
				posY = ArgumentsHelper.getInteger(arguments.get(1));
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
				currentImageNumber = currentEvent.getFramesNumbers().get(currentFrameNumber);
				currentImage = getImages().get(currentImageNumber);
				//Gdx.app.log("updateRect()", "STOP");
				updateRect();
				isPlaying = false;

				if(emitSignal) {
					emitSignal("ONFINISHED", currentEvent.getName());
					emitSignal("ONFINISHED__SEQ^" + currentEvent.getName()); // emit generic signal for sequence
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
						setCurrentFrameNumber(0);
						updateRect();
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

				playSfx();

				if(currentEvent.getLoopBy() == 0) { // TODO: check, how this value works
					isPlaying = false;
					emitSignal("ONFINISHED", currentEvent.getName());
					emitSignal("ONFINISHED__SEQ^" + currentEvent.getName()); // emit generic signal for sequence
				}
				else {
					currentFrameNumber = 0;
					currentImageNumber = currentEvent.getFramesNumbers().get(currentFrameNumber);
					currentImage = currentEvent.getFrames().get(currentFrameNumber);
				}
			}
			else {
				playSfx();
				currentImageNumber = currentEvent.getFramesNumbers().get(currentFrameNumber);
				currentImage = currentEvent.getFrames().get(currentFrameNumber);
			}

			//Gdx.app.log("updateRect()", "updateAnimation");
			updateRect();
			emitSignal("ONFRAMECHANGED", currentEvent.getName());
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
		/*Gdx.app.error("DEBUG ANIMO "+getName(),
				"currentFrameNumber: " + currentFrameNumber +
						", currentEvent.getName(): " + currentEvent.getName() +
						", currentEvent.getFramesCount(): " + currentEvent.getFramesCount() +
						", currentImageNumber: " + currentImageNumber +
						", posX: " + posX +
						", posY: " + posY +
						", currentImage.offsetX: " + currentImage.offsetX +
						", currentImage.offsetY: " + currentImage.offsetY +
						", currentImage.width: " + currentImage.width +
						", currentImage.height: " + currentImage.height +
						", frameData.getOffsetX(): " + (!currentEvent.getFrameData().isEmpty() ? currentEvent.getFrameData().get(currentFrameNumber).getOffsetX() : null) +
						", frameData.getOffsetY(): " + (!currentEvent.getFrameData().isEmpty() ? currentEvent.getFrameData().get(currentFrameNumber).getOffsetY() : null) +
						", frameData.getOpacity(): " + (!currentEvent.getFrameData().isEmpty() ? currentEvent.getFrameData().get(currentFrameNumber).getOpacity() : null));*/

		//Gdx.app.log("DEBUG ANIMO "+getName(), "Rect before: " + getRect().toString());

		try {
			FrameData frameData = currentEvent.getFrameData().get(currentFrameNumber);

			rect.setXLeft(posX + frameData.getOffsetX() + currentImage.offsetX);
			rect.setYTop(posY + frameData.getOffsetY() + currentImage.offsetY);
			rect.setXRight(rect.getXLeft() + currentImage.width);
			rect.setYBottom(rect.getYTop() - currentImage.height);

			endPosX = rect.getXRight();
			endPosY = rect.getYBottom();
			centerX = rect.getXLeft() + currentImage.width / 2;
			centerY = rect.getYTop() - currentImage.height / 2;
		} catch (IndexOutOfBoundsException e) {
			if(currentImage != null)
				updateRect(currentImage);
			else {
				// last try, use only posX and posY
				rect.setXLeft(posX);
				rect.setYTop(posY);
				rect.setXRight(rect.getXLeft() + 1);
				rect.setYBottom(rect.getYTop() - 1);

				endPosX = rect.getXRight();
				endPosY = rect.getYBottom();
				centerX = endPosX;
				centerY = endPosY;
			}
		}

		//Gdx.app.log("DEBUG ANIMO "+getName(), "Rect after: " + getRect().toString());
	}

	private void updateRect(Image image) {
		rect.setXLeft(image.offsetX - posX);
		rect.setYTop(image.offsetY - posY);
		rect.setXRight(rect.getXLeft() + image.width);
		rect.setYBottom(rect.getYTop() - image.height);

		endPosX = rect.getXRight();
		endPosY = rect.getYBottom();
		centerX = rect.getXLeft() + image.width / 2;
		centerY = rect.getYTop() - image.height / 2;
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

	@Override
	public Method getMethod(String name, List<String> paramTypes) {
		return super.getMethod(name, paramTypes);
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
	}

	public int getCurrentImageNumber() {
		return currentImageNumber;
	}

	public void setCurrentImageNumber(int currentImageNumber) {
		this.currentImageNumber = currentImageNumber;
	}

	public Image getCurrentImage() {
		if(currentImage == null) {
			if(!currentEvent.getFrames().isEmpty()) {
				currentImage = currentEvent.getFrames().get(currentImageNumber);
			}
			else {
				currentImage = null;
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
		try {
			return isVisible
					&& this.getAttribute("TOCANVAS").getValue().toString().equals("TRUE");
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

	@Override
    public AnimoVariable clone() {
        AnimoVariable clone = (AnimoVariable) super.clone();
        this.rect = new Rectangle(rect.getXLeft(), rect.getYBottom(), rect.getXRight(), rect.getYTop());
        return clone;
    }
}
