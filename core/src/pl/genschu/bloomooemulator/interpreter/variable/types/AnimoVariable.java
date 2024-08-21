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

	private Rectangle rect;
	private Music currentSfx;

	public AnimoVariable(String name, Context context) {
		super(name, context);

		rect = new Rectangle(0, 0, 0, 0);

		this.setMethod("GETCENTERX", new Method(
			List.of(
				new Parameter("BOOL", "unknown", false)
			),
			"INTEGER"
		) {
			@Override
			public Variable execute(List<Object> arguments, Variable variable) {
				return new IntegerVariable("", ((AnimoVariable) variable).centerX, context);
			}
		});
		this.setMethod("GETCENTERY", new Method(
			List.of(
				new Parameter("BOOL", "unknown", false)
			),
			"INTEGER"
		) {
			@Override
			public Variable execute(List<Object> arguments, Variable variable) {
				return new IntegerVariable("", ((AnimoVariable) variable).centerY, context);
			}
		});
		this.setMethod("GETCFRAMEINEVENT", new Method(
			"INTEGER"
		) {
			@Override
			public Variable execute(List<Object> arguments, Variable variable) {
				return new IntegerVariable("", ((AnimoVariable) variable).currentFrameNumber, context);
			}
		});
		this.setMethod("GETCURRFRAMEPOSX", new Method(
			"INTEGER"
		) {
			@Override
			public Variable execute(List<Object> arguments, Variable variable) {
				return new IntegerVariable("", ((AnimoVariable) variable).currentEvent.getFrameData().get(currentFrameNumber).getOffsetX(), context);
			}
		});
		this.setMethod("GETCURRFRAMEPOSY", new Method(
			"INTEGER"
		) {
			@Override
			public Variable execute(List<Object> arguments, Variable variable) {
				return new IntegerVariable("", ((AnimoVariable) variable).currentEvent.getFrameData().get(currentFrameNumber).getOffsetY(), context);
			}
		});
		this.setMethod("GETENDX", new Method(
			"INTEGER"
		) {
			@Override
			public Variable execute(List<Object> arguments, Variable variable) {
				return new IntegerVariable("", ((AnimoVariable) variable).endPosX, context);
			}
		});
		this.setMethod("GETENDY", new Method(
			"INTEGER"
		) {
			@Override
			public Variable execute(List<Object> arguments, Variable variable) {
				return new IntegerVariable("", ((AnimoVariable) variable).endPosY, context);
			}
		});
		this.setMethod("GETEVENTNAME", new Method(
			"STRING"
		) {
			@Override
			public Variable execute(List<Object> arguments, Variable variable) {
				return new StringVariable("", ((AnimoVariable) variable).currentEvent.getName(), context);
			}
		});
		this.setMethod("GETFRAME", new Method(
			"INTEGER"
		) {
			@Override
			public Variable execute(List<Object> arguments, Variable variable) {
				return new IntegerVariable("", ((AnimoVariable) variable).currentImageNumber, context);
			}
		});
		this.setMethod("GETFRAMENAME", new Method(
			"STRING"
		) {
			@Override
			public Variable execute(List<Object> arguments, Variable variable) {
				return new StringVariable("", ((AnimoVariable) variable).currentEvent.getFrameData().get(currentFrameNumber).getName(), context);
			}
		});
		this.setMethod("GETHEIGHT", new Method(
			"INTEGER"
		) {
			@Override
			public Variable execute(List<Object> arguments, Variable variable) {
				return new IntegerVariable("", ((AnimoVariable) variable).currentImage.height, context);
			}
		});
		this.setMethod("GETMAXWIDTH", new Method(
			"INTEGER"
		) {
			@Override
			public Variable execute(List<Object> arguments, Variable variable) {
				return new IntegerVariable("", ((AnimoVariable) variable).maxWidth, context);
			}
		});
		this.setMethod("GETMAXHEIGHT", new Method(
				"INTEGER"
		) {
			@Override
			public Variable execute(List<Object> arguments, Variable variable) {
				return new IntegerVariable("", ((AnimoVariable) variable).maxHeight, context);
			}
		});
		this.setMethod("GETNOE", new Method(
			"INTEGER"
		) {
			@Override
			public Variable execute(List<Object> arguments, Variable variable) {
				return new IntegerVariable("", ((AnimoVariable) variable).eventsCount, context);
			}
		});
		this.setMethod("GETNOF", new Method(
				"INTEGER"
		) {
			@Override
			public Variable execute(List<Object> arguments, Variable variable) {
				return new IntegerVariable("", ((AnimoVariable) variable).imagesCount, context);
			}
		});
		this.setMethod("GETNOFINEVENT", new Method(
			List.of(
				new Parameter("String", "event", true)
			),
			"INTEGER"
		) {
			@Override
			public Variable execute(List<Object> arguments, Variable variable) {
				String eventName = ArgumentsHelper.getString(arguments.get(0));

				try {
					int eventNumber = Integer.parseInt(eventName);
					return new IntegerVariable("", ((AnimoVariable) variable).events.get(eventNumber).getFramesCount(), context);
				} catch (NumberFormatException e) {
					int framesNumber = 0;
					for (Event event : ((AnimoVariable) variable).events) {
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
			public Variable execute(List<Object> arguments, Variable variable) {
				boolean absolute = !arguments.isEmpty(); // it seems like it must be any argument
				AnimoVariable var = (AnimoVariable) variable;

				if(absolute) {
					return new IntegerVariable("", var.posX, context);
				} else {
					try {
						return new IntegerVariable("", var.posX + var.currentEvent.getFrameData().get(var.currentFrameNumber).getOffsetX() + var.currentImage.offsetX, context);
					} catch (IndexOutOfBoundsException e) {
						return new IntegerVariable("", var.posX, context);
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
			public Variable execute(List<Object> arguments, Variable variable) {
				boolean absolute = !arguments.isEmpty(); // it seems like it must be any argument
				AnimoVariable var = (AnimoVariable) variable;

				if(absolute) {
					return new IntegerVariable("", var.posY, context);
				} else {
					try {
						return new IntegerVariable("", var.posY + var.currentEvent.getFrameData().get(var.currentFrameNumber).getOffsetY() + var.currentImage.offsetY, context);
					} catch (IndexOutOfBoundsException e) {
						return new IntegerVariable("", var.posY, context);
					}
				}
			}
		});
		this.setMethod("GETPRIORITY", new Method(
				List.of(),
				"INTEGER"
		) {
			@Override
			public Variable execute(List<Object> arguments, Variable variable) {
				return new IntegerVariable("", ((AnimoVariable) variable).priority, context);
			}
		});
		this.setMethod("GETWIDTH", new Method(
			"INTEGER"
		) {
			@Override
			public Variable execute(List<Object> arguments, Variable variable) {
				return new IntegerVariable("", ((AnimoVariable) variable).currentImage.width, context);
			}
		});
		this.setMethod("HIDE", new Method(
			"void"
		) {
			@Override
			public Variable execute(List<Object> arguments, Variable variable) {
				variable.getAttribute("VISIBLE").setValue("FALSE");
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
			public Variable execute(List<Object> arguments, Variable variable) {
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
			public Variable execute(List<Object> arguments, Variable variable) {
				// TODO: check if it works correct
				String eventName = arguments.isEmpty() ? null : ArgumentsHelper.getString(arguments.get(0));
				if(eventName == null) {
					return new BoolVariable("", ((AnimoVariable) variable).isPlaying, context);
				}
				return new BoolVariable("", ((AnimoVariable) variable).currentEvent.getName().equals(eventName) && ((AnimoVariable) variable).isPlaying, context);
			}
		});
		this.setMethod("ISVISIBLE", new Method(
			"BOOL"
		) {
			@Override
			public Variable execute(List<Object> arguments, Variable variable) {
				return new BoolVariable("", variable.getAttribute("VISIBLE").getValue().equals("TRUE"), context);
			}
		});
		this.setMethod("LOAD", new Method(
			List.of(
				new Parameter("STRING", "path", true)
			),
			"void"
		) {
			@Override
			public Variable execute(List<Object> arguments, Variable variable) {
				String path = ArgumentsHelper.getString(arguments.get(0));
				variable.setAttribute("FILENAME", path);
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
			public Variable execute(List<Object> arguments, Variable variable) {
				int offsetX = ArgumentsHelper.getInteger(arguments.get(0));
				int offsetY = ArgumentsHelper.getInteger(arguments.get(1));

				((AnimoVariable) variable).posX += offsetX;
				((AnimoVariable) variable).posY += offsetY;
				((AnimoVariable) variable).updateRect();

				return null;
			}
		});
		this.setMethod("NEXTFRAME", new Method(
			"void"
		) {
			@Override
			public Variable execute(List<Object> arguments, Variable variable) {
				AnimoVariable var = (AnimoVariable) variable;
				var.currentFrameNumber++;

				if(var.currentFrameNumber >= var.currentEvent.getFramesNumbers().size()) {
					var.currentFrameNumber = 0;
				}

				var.currentImageNumber = var.currentEvent.getFramesNumbers().get(var.currentFrameNumber);
				var.currentImage = var.currentEvent.getFrames().get(var.currentImageNumber);
				var.updateRect();
				var.emitSignal("ONFRAMECHANGED", var.currentEvent.getName());
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
			public Variable execute(List<Object> arguments, Variable variable) {
				AnimoVariable var = (AnimoVariable) variable;

				int eventId = ArgumentsHelper.getInteger(arguments.get(0));
				var.currentEvent = var.events.get(eventId);
				var.currentFrameNumber = 0;
				var.isPlaying = true;
				var.updateRect();
				var.getAttribute("VISIBLE").setValue("TRUE");
				var.playSfx();
				var.emitSignal("ONSTARTED", var.currentEvent.getName());
				var.emitSignal("ONFRAMECHANGED", var.currentEvent.getName());
				return null;
			}
		});
		this.setMethod("PAUSE", new Method(
			"void"
		) {
			@Override
			public Variable execute(List<Object> arguments, Variable variable) {
				((AnimoVariable) variable).isPlaying = false;
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
			public Variable execute(List<Object> arguments, Variable variable) {
				AnimoVariable var = (AnimoVariable) variable;

				String eventName = ArgumentsHelper.getString(arguments.get(0));
				for(Event event : var.events) {
					if(event.getName().equals(eventName)) {
						var.currentEvent = event;
						var.currentFrameNumber = 0;
						var.currentImage = var.currentEvent.getFrames().get(var.currentFrameNumber);
						var.isPlaying = true;
						var.updateRect();
						try {
							var.getAttribute("VISIBLE").setValue("TRUE");
						} catch (NullPointerException e) {
							var.setAttribute("VISIBLE", new Attribute("BOOL", "TRUE"));
						}
						var.playSfx();
						var.emitSignal("ONSTARTED", var.currentEvent.getName());
						var.emitSignal("ONFRAMECHANGED", var.currentEvent.getName());
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
			public Variable execute(List<Object> arguments, Variable variable) {
				AnimoVariable var = (AnimoVariable) variable;

				var.currentFrameNumber--;
				if(var.currentFrameNumber < 0) {
					var.currentFrameNumber = var.currentEvent.getFramesNumbers().size() - 1;
				}
				var.currentImageNumber = var.currentEvent.getFramesNumbers().get(var.currentFrameNumber);
				var.currentImage = var.currentEvent.getFrames().get(var.currentImageNumber);
				var.updateRect();
				var.emitSignal("ONFRAMECHANGED", var.currentEvent.getName());
				return null;
			}
		});
		this.setMethod("RESUME", new Method(
			"void"
		) {
			@Override
			public Variable execute(List<Object> arguments, Variable variable) {
				((AnimoVariable) variable).isPlaying = true;
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
			public Variable execute(List<Object> arguments, Variable variable) {
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
			public Variable execute(List<Object> arguments, Variable variable) {
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
			public Variable execute(List<Object> arguments, Variable variable) {
				// TODO: implement this method
				throw new ClassMethodNotImplementedException("Method SETASBUTTON is not implemented yet");
			}
		});
		this.setMethod("SETBACKWARD", new Method(
			"void"
		) {
			@Override
			public Variable execute(List<Object> arguments, Variable variable) {
				((AnimoVariable) variable).direction = -1;
				return null;
			}
		});
		this.setMethod("SETFORWARD", new Method(
			"void"
		) {
			@Override
			public Variable execute(List<Object> arguments, Variable variable) {
				((AnimoVariable) variable).direction = 1;
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
			public Variable execute(List<Object> arguments, Variable variable) {
				int fps = ArgumentsHelper.getInteger(arguments.get(0));
				((AnimoVariable) variable).setFps(fps);
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
			public Variable execute(List<Object> arguments, Variable variable) {
				AnimoVariable var = (AnimoVariable) variable;

				int frameNumber = ArgumentsHelper.getInteger(arguments.get(0));
				var.currentEvent = var.events.get(0);
				var.currentFrameNumber = 0;

				var.currentImageNumber = frameNumber;
				var.currentImage = var.getImages().get(var.currentImageNumber);
				var.emitSignal("ONFRAMECHANGED");
				var.updateRect(var.currentImage);
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
			public Variable execute(List<Object> arguments, Variable variable) {
				AnimoVariable annVar = (AnimoVariable) variable;

				String eventName = ArgumentsHelper.getString(arguments.get(0));
				Variable var = getContext().getVariable(eventName);
				if(var != null) {
					eventName = var.getValue().toString();
				}
				try {
					int eventNumber = Integer.parseInt(eventName);
					annVar.currentEvent = annVar.events.get(eventNumber);
				} catch (NumberFormatException e) {
					for (Event event : annVar.events) {
						if (event.getName().equals(eventName)) {
							annVar.currentEvent = event;
							break;
						}
					}
				}
				if(arguments.size() >= 2) {
					String frameName = ArgumentsHelper.getString(arguments.get(1));
					int tmpIndex = 0;
					boolean found = false;
					for(FrameData frameData : annVar.currentEvent.getFrameData()) {
						if(frameData.getName().equals(frameName)) {
							annVar.currentFrameNumber = tmpIndex;
							found = true;
							break;
						}
						tmpIndex++;
					}
					if(!found) {
						annVar.currentFrameNumber = 0;
					}
				}
				else {
					annVar.currentFrameNumber = 0;
				}
				if(!annVar.currentEvent.getFrames().isEmpty()) {
					annVar.currentImageNumber = annVar.currentEvent.getFramesNumbers().get(annVar.currentFrameNumber);
					annVar.currentImage = annVar.currentEvent.getFrames().get(annVar.currentFrameNumber);
					annVar.updateRect();
				}
				else {
					annVar.currentImageNumber = 0;
					annVar.currentImage = null;
					annVar.rect.setXLeft(0);
					annVar.rect.setYTop(0);
					annVar.rect.setXRight(1);
					annVar.rect.setYBottom(1);
				}
				annVar.emitSignal("ONFRAMECHANGED", annVar.currentEvent.getName());
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
			public Variable execute(List<Object> arguments, Variable variable) {
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
			public Variable execute(List<Object> arguments, Variable variable) {
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
			public Variable execute(List<Object> arguments, Variable variable) {
				((AnimoVariable) variable).posX = ArgumentsHelper.getInteger(arguments.get(0));
				((AnimoVariable) variable).posY = ArgumentsHelper.getInteger(arguments.get(1));
				((AnimoVariable) variable).updateRect();
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
			public Variable execute(List<Object> arguments, Variable variable) {
				((AnimoVariable) variable).priority = ArgumentsHelper.getInteger(arguments.get(0));
				variable.setAttribute("PRIORITY", new Attribute("INTEGER", priority));
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
			public Variable execute(List<Object> arguments, Variable variable) {
				variable.getAttribute("VISIBLE").setValue("TRUE");
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
			public Variable execute(List<Object> arguments, Variable variable) {
				AnimoVariable var = (AnimoVariable) variable;

				boolean emitSignal = arguments.isEmpty();

				if(!emitSignal) {
					emitSignal = ArgumentsHelper.getBoolean(arguments.get(0));
				}

				var.currentFrameNumber = 0;
				var.currentImageNumber = var.currentEvent.getFramesNumbers().get(var.currentFrameNumber);
				var.currentImage = var.currentEvent.getFrames().get(var.currentImageNumber);
				var.updateRect();
				var.isPlaying = false;

				if(emitSignal) {
					var.emitSignal("ONFINISHED", var.currentEvent.getName());
					var.emitSignal("ONFINISHED__SEQ^" + var.currentEvent.getName()); // emit generic signal for sequence
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
			public Variable execute(List<Object> arguments, Variable variable) {
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
            switch (name) {
                case "FPS":
                    setFps(Integer.parseInt(getAttribute("FPS").getValue().toString()));
                    break;
                case "FILENAME":
                    AnimoLoader.loadAnimo(this);
                    currentEvent = events.get(0);
                    currentFrameNumber = 0;
                    if (!currentEvent.getFrames().isEmpty()) {
                        currentImageNumber = currentEvent.getFramesNumbers().get(currentFrameNumber);
                        currentImage = currentEvent.getFrames().get(currentImageNumber);
                        updateRect();
                    } else {
                        currentImageNumber = 0;
                        currentImage = null;
                    }
                    break;
                case "PRIORITY":
                    priority = Integer.parseInt(getAttribute("PRIORITY").getValue().toString());
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

		if(currentEvent.getFrames().isEmpty()) {
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
		int opacity = this.opacity;
		if(this.currentEvent != null) {
			opacity *= this.currentEvent.getOpacity();

			if(currentImage != null && !currentEvent.getFrameData().isEmpty()) {
				opacity *= currentEvent.getFrameData().get(currentFrameNumber).getOpacity();
			}
		}

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
			return this.getAttribute("VISIBLE").getValue().toString().equals("TRUE")
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
}
