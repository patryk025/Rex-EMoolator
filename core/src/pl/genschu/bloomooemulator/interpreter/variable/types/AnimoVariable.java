package pl.genschu.bloomooemulator.interpreter.variable.types;

import pl.genschu.bloomooemulator.interpreter.exceptions.ClassMethodNotImplementedException;
import pl.genschu.bloomooemulator.interpreter.Context;
import pl.genschu.bloomooemulator.interpreter.variable.Attribute;
import pl.genschu.bloomooemulator.interpreter.variable.Method;
import pl.genschu.bloomooemulator.interpreter.variable.Parameter;
import pl.genschu.bloomooemulator.interpreter.variable.Variable;
import pl.genschu.bloomooemulator.loader.AnimoLoader;
import pl.genschu.bloomooemulator.objects.Event;
import pl.genschu.bloomooemulator.objects.Image;
import pl.genschu.bloomooemulator.utils.ArgumentsHelper;

import java.util.List;

public class AnimoVariable extends Variable {
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

	public AnimoVariable(String name, Context context) {
		super(name, context);

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
				return new StringVariable("", currentEvent.getFrameData().get(currentFrameNumber).getName(), context);
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
				new Parameter("String", "event", true)
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
					return new IntegerVariable("", posX+currentEvent.getFrameData().get(currentFrameNumber).getOffsetX(), context);
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
					return new IntegerVariable("", posY+currentEvent.getFrameData().get(currentFrameNumber).getOffsetY(), context);
				}
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
				getAttribute("VISIBLE").setValue("FALSE");
				return null;
			}
		});
		this.setMethod("ISNEAR", new Method(
			List.of(
				new Parameter("STRING", "varAnimo", true),
				new Parameter("INTEGER", "distance", true)
			),
			"BOOL"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method (I'm checking how it works)
				throw new ClassMethodNotImplementedException("Method ISNEAR is not implemented yet");
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
				return new BoolVariable("", getAttribute("VISIBLE").getValue().equals("TRUE"), context);
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
				new Parameter("BOOL", "unknown", true)
			),
			"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				throw new ClassMethodNotImplementedException("Method SETASBUTTON is not implemented yet");
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
				new Parameter("STRING", "event", true),
				new Parameter("INTEGER", "frameNumber", false)
			),
			"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				String eventName = ArgumentsHelper.getString(arguments.get(0));
				try {
					int eventNumber = Integer.parseInt(eventName);
					currentEvent = events.get(eventNumber);
					currentFrameNumber = arguments.size() >= 2 ? Integer.parseInt(((Variable) arguments.get(1)).getValue().toString()) : 0;
					currentImageNumber = currentEvent.getFramesNumbers().get(currentFrameNumber);
					currentImage = currentEvent.getFrames().get(currentImageNumber);
					return null;
				} catch (NumberFormatException e) {
					for (Event event : events) {
						if (event.getName().equals(eventName)) {
							currentEvent = event;
							currentFrameNumber = arguments.size() >= 2 ? Integer.parseInt(((Variable) arguments.get(1)).getValue().toString()) : 0;
							currentImageNumber = currentEvent.getFramesNumbers().get(currentFrameNumber);
							currentImage = currentEvent.getFrames().get(currentImageNumber);
							break;
						}
					}
					return null;
				}
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
				// TODO: implement this method
				throw new ClassMethodNotImplementedException("Method SETOPACITY is not implemented yet");
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
				getAttribute("VISIBLE").setValue("TRUE");
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
				currentImage = currentEvent.getFrames().get(currentImageNumber);

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
	public String getType() {
		return "ANIMO";
	}

	@Override
	public void setAttribute(String name, Attribute attribute) {
		List<String> knownAttributes = List.of("ASBUTTON", "FILENAME", "FLUSHAFTERPLAYED", "FPS", "MONITORCOLLISION", "MONITORCOLLISIONALPHA", "PRELOAD", "PRIORITY", "RELEASE", "TOCANVAS", "VISIBLE");
		if(knownAttributes.contains(name)) {
			super.setAttribute(name, attribute);
			if(name.equals("FPS")) {
				setFps(Integer.parseInt(getAttribute("FPS").getValue().toString()));
			}
			else if(name.equals("FILENAME")) {
				AnimoLoader.loadAnimo(this);
				currentEvent = events.get(0);
				currentFrameNumber = 0;
				currentImageNumber = currentEvent.getFramesNumbers().get(currentFrameNumber);
				currentImage = currentEvent.getFrames().get(currentImageNumber);
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

		elapsedTime += deltaTime;
		if (elapsedTime >= frameDuration) {
			elapsedTime -= frameDuration;
			currentFrameNumber += direction;

			if (currentFrameNumber >= currentEvent.getFrames().size()) {
				currentFrameNumber = currentEvent.getFrames().size() - 1;

				if(currentEvent.getLoopBy() == 0) { // TODO: check, how this value works
					isPlaying = false;
					emitSignal("ONFINISHED", currentEvent.getName());
				}
				else {
					currentFrameNumber = 0;
					currentImageNumber = currentEvent.getFramesNumbers().get(currentFrameNumber);
					currentImage = currentEvent.getFrames().get(currentFrameNumber);
				}
			}
			else {
				currentImageNumber = currentEvent.getFramesNumbers().get(currentFrameNumber);
				currentImage = currentEvent.getFrames().get(currentFrameNumber);
			}
		}
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

	public int getOpacity() {
		return opacity;
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
	}

	public int getPosY() {
		return posY;
	}

	public void setPosY(int posY) {
		this.posY = posY;
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
		this.currentFrameNumber = currentFrameNumber;
	}

	public int getCurrentImageNumber() {
		return currentImageNumber;
	}

	public void setCurrentImageNumber(int currentImageNumber) {
		this.currentImageNumber = currentImageNumber;
	}

	public Image getCurrentImage() {
		if(currentImage == null) {
			currentImage = currentEvent.getFrames().get(currentImageNumber);
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
}
