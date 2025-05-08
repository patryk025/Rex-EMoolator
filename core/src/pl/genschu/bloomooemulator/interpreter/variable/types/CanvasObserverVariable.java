package pl.genschu.bloomooemulator.interpreter.variable.types;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.TextureData;
import pl.genschu.bloomooemulator.interpreter.exceptions.ClassMethodNotImplementedException;
import pl.genschu.bloomooemulator.interpreter.Context;
import pl.genschu.bloomooemulator.interpreter.variable.Attribute;
import pl.genschu.bloomooemulator.interpreter.variable.Method;
import pl.genschu.bloomooemulator.interpreter.variable.Parameter;
import pl.genschu.bloomooemulator.interpreter.variable.Variable;
import pl.genschu.bloomooemulator.objects.Image;
import pl.genschu.bloomooemulator.objects.Rectangle;
import pl.genschu.bloomooemulator.saver.ImageSaver;
import pl.genschu.bloomooemulator.utils.ArgumentsHelper;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CanvasObserverVariable extends Variable {
	// TODO: implement canvas observer

	public CanvasObserverVariable(String name, Context context) {
		super(name, context);
	}

	@Override
	public String getType() {
		return "CANVAS_OBSERVER";
	}

	@Override
	protected void setMethods() {
		super.setMethods();

		this.setMethod("ADD", new Method(
				List.of(
						new Parameter("STRING", "animoVar", true)
				),
				"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				throw new ClassMethodNotImplementedException("Method ADD is not implemented yet");
			}
		});
		this.setMethod("ENABLENOTIFY", new Method(
				List.of(
						new Parameter("BOOL", "enable", true)
				),
				"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				throw new ClassMethodNotImplementedException("Method ENABLENOTIFY is not implemented yet");
			}
		});
		this.setMethod("GETGRAPHICSAT", new Method(
				List.of(
						new Parameter("INTEGER", "posX", true),
						new Parameter("INTEGER", "posY", true),
						new Parameter("BOOL", "onlyVisible", false),
						new Parameter("INTEGER", "minZ", false),
						new Parameter("INTEGER", "maxZ", false),
						new Parameter("BOOL", "includeAlpha", false)
				),
				"STRING"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				int posX = ArgumentsHelper.getInteger(arguments.get(0));
				int posY = ArgumentsHelper.getInteger(arguments.get(1));
				boolean onlyVisible = arguments.size() > 2 && ArgumentsHelper.getBoolean(arguments.get(2));
				int minZ = arguments.size() > 3 ? ArgumentsHelper.getInteger(arguments.get(3)) : Integer.MIN_VALUE;
				int maxZ = arguments.size() > 4 ? ArgumentsHelper.getInteger(arguments.get(4)) : Integer.MAX_VALUE;
				boolean includeAlpha = arguments.size() > 5 && ArgumentsHelper.getBoolean(arguments.get(5));

				List<Variable> drawList = new ArrayList<>(context.getGraphicsVariables().values());

				drawList.sort((v1, v2) -> {
					Attribute priorityAttr1 = v1.getAttribute("PRIORITY");
					Attribute priorityAttr2 = v2.getAttribute("PRIORITY");
					int priority1 = priorityAttr1 != null ? Integer.parseInt(priorityAttr1.getValue().toString()) : 0;
					int priority2 = priorityAttr2 != null ? Integer.parseInt(priorityAttr2.getValue().toString()) : 0;
					return Integer.compare(priority1, priority2);
				});
				Collections.reverse(drawList);

				for (Variable variable : drawList) {
					boolean visible = false;
					if (variable instanceof ImageVariable) {
						visible = ((ImageVariable) variable).isVisible();
					} else if (variable instanceof AnimoVariable) {
						visible = ((AnimoVariable) variable).isVisible();
					}

					if (onlyVisible && !visible) {
						continue;
					}

					int z = variable.getAttribute("PRIORITY") != null ?
							Integer.parseInt(variable.getAttribute("PRIORITY").getValue().toString()) : 0;
					if (z < minZ || z > maxZ) {
						continue;
					}

					Rectangle rect = getRect(variable);
					if (rect == null) continue;

					boolean containsPoint = false;
					if (includeAlpha) {
						containsPoint = rect.contains(posX, posY);
					} else {
						if (rect.contains(posX, posY)) {
							Image image = getImage(variable);
							int relativeX = posX - rect.getXLeft();
							int relativeY = posY - rect.getYTop();
							int alpha = 255;

							if (image != null && image.getImageTexture() != null) {
								TextureData textureData = image.getImageTexture().getTextureData();
								if (!textureData.isPrepared()) {
									textureData.prepare();
								}
								Pixmap pixmap = textureData.consumePixmap();
								int pixel = pixmap.getPixel(relativeX, relativeY);
								alpha = (pixel & 0xFF);

								if (textureData.disposePixmap()) {
									pixmap.dispose();
								}
							}

							containsPoint = alpha > 0;
						}
					}

					if (containsPoint) {
						return new StringVariable("", variable.getName(), context);
					}
				}

				return new StringVariable("", "NULL", context);
			}
		});
		this.setMethod("MOVEBKG", new Method(
				List.of(
						new Parameter("INTEGER", "deltaX", true),
						new Parameter("INTEGER", "deltaY", true)
				),
				"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				throw new ClassMethodNotImplementedException("Method MOVEBKG is not implemented yet");
			}
		});
		this.setMethod("PASTE", new Method(
				List.of(
						new Parameter("STRING", "varName", true),
						new Parameter("INTEGER", "posX", true),
						new Parameter("INTEGER", "posY", true)
				),
				"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				throw new ClassMethodNotImplementedException("Method PASTE is not implemented yet");
			}
		});
		this.setMethod("REDRAW", new Method(
				"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				throw new ClassMethodNotImplementedException("Method REDRAW is not implemented yet");
			}
		});
		this.setMethod("REFRESH", new Method(
				"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				Gdx.app.error("CanvasObserverVariable", "Currently refresh is not supported"); // It's just for make less log spam
				return null;
			}
		});
		this.setMethod("REMOVE", new Method(
				List.of(
						new Parameter("STRING", "varName1...varNameN", true)
				),
				"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
                for (Object argument : arguments) {
                    String name = ArgumentsHelper.getString(argument);
                    Variable var = getContext().getVariable(name);

                    // I will simply hide it instead of removing it
                    var.setAttribute("VISIBLE", new Attribute("BOOL", "FALSE"));
                    var.setAttribute("TOCANVAS", new Attribute("BOOL", "FALSE"));
                }
				return null;
			}
		});
		this.setMethod("SAVE", new Method(
				List.of(
						new Parameter("STRING", "imgFileName", true),
						new Parameter("DOUBLE", "xScaleFactor", true),
						new Parameter("DOUBLE", "yScaleFactor", true),
						new Parameter("INTEGER", "xLeft", false),
						new Parameter("INTEGER", "yTop", false),
						new Parameter("INTEGER", "xRight", false),
						new Parameter("INTEGER", "yBottom", false)
				),
				"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: add scaling and cropping
				String imgFileName = ArgumentsHelper.getString(arguments.get(0));
				double xScaleFactor = ArgumentsHelper.getDouble(arguments.get(1));
				double yScaleFactor = ArgumentsHelper.getDouble(arguments.get(2));
				int xLeft = 0;
				int yTop = 0;
				int xRight = 800;
				int yBottom = 600;

				// Sprawdzanie, czy są podane parametry przycięcia
				if (arguments.size() == 7) {
					xLeft = ArgumentsHelper.getInteger(arguments.get(3));
					yTop = ArgumentsHelper.getInteger(arguments.get(4));
					xRight = ArgumentsHelper.getInteger(arguments.get(5));
					yBottom = ArgumentsHelper.getInteger(arguments.get(6));
				}

				Pixmap pixmap = getContext().getGame().getLastFrame();

				if(pixmap == null) {
					Gdx.app.error("CanvasObserverVariable", "Pixmap is null, screenshots may be not captured correctly");
					pixmap = new Pixmap(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), Pixmap.Format.RGB565);

					Gdx.gl.glReadPixels(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), GL20.GL_RGB, GL20.GL_UNSIGNED_SHORT_5_6_5, pixmap.getPixels());
				}

				flipPixmapVertically(pixmap);

				Pixmap croppedPixmap = new Pixmap(xRight - xLeft, yBottom - yTop, pixmap.getFormat());
				croppedPixmap.drawPixmap(pixmap, 0, 0, xLeft, yTop, xRight - xLeft, yBottom - yTop);
				pixmap.dispose();

				Pixmap scaledPixmap = new Pixmap((int)((xRight - xLeft) * xScaleFactor), (int)((yBottom - yTop) * yScaleFactor), croppedPixmap.getFormat());
				scaledPixmap.drawPixmap(croppedPixmap, 0, 0, croppedPixmap.getWidth(), croppedPixmap.getHeight(), 0, 0, scaledPixmap.getWidth(), scaledPixmap.getHeight());
				croppedPixmap.dispose();

				int width = scaledPixmap.getWidth();
				int height = scaledPixmap.getHeight();

				ImageSaver.saveScreenshot(CanvasObserverVariable.this, imgFileName, pixmapToByteArray(scaledPixmap), width, height);
				scaledPixmap.dispose();

				return null;
			}
		});
		this.setMethod("SETBACKGROUND", new Method(
				List.of(
						new Parameter("STRING", "imageName", true)
				),
				"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// hacky way for now
				String imageName = ArgumentsHelper.getString(arguments.get(0));
				// check if it is not a variable
				Variable var = context.getVariable(imageName);
				if (var != null && var.getType().equals("IMAGE")) {
					context.getGame().getCurrentSceneVariable().setBackground((ImageVariable) var);
					return null;
				}
				ImageVariable image = new ImageVariable("", context);
				image.setAttribute("FILENAME", new Attribute("STRING", imageName));
				image.init();
				context.getGame().getCurrentSceneVariable().setBackground(image);
				return null;
			}
		});
		this.setMethod("SETBKGPOS", new Method(
				List.of(
						new Parameter("INTEGER", "posX", true),
						new Parameter("INTEGER", "posY", true)
				),
				"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				throw new ClassMethodNotImplementedException("Method SETBKGPOS is not implemented yet");
			}
		});
	}

	@Override
	public void setAttribute(String name, Attribute attribute) {
		return; // no fields in this class
	}

	// modified from https://stackoverflow.com/a/44403355/19906361
	public static void flipPixmapVertically(Pixmap p) {
		int w = p.getWidth();
		int h = p.getHeight();
		int hold;

		p.setBlending(Pixmap.Blending.None);

		for (int y = 0; y < h / 2; y++) {
			for (int x = 0; x < w; x++) {
				// Zamień piksele na osi Y (z góry na dół)
				hold = p.getPixel(x, y);
				p.drawPixel(x, y, p.getPixel(x, h - y - 1));
				p.drawPixel(x, h - y - 1, hold);
			}
		}

		p.setBlending(Pixmap.Blending.SourceOver);
	}

	private byte[] pixmapToByteArray(Pixmap pixmap) {
		ByteBuffer buffer = pixmap.getPixels();
		byte[] byteArray = new byte[buffer.remaining()];
		buffer.get(byteArray);
		return byteArray;
	}

	private Rectangle getRect(Variable variable) {
		if(variable instanceof ImageVariable) {
			return ((ImageVariable) variable).getRect();
		}
		if(variable instanceof AnimoVariable) {
			return ((AnimoVariable) variable).getRect();
		}
		return null;
	}

	private Image getImage(Variable variable) {
		if(variable instanceof ImageVariable) {
			return ((ImageVariable) variable).getImage();
		}
		if(variable instanceof AnimoVariable) {
			return ((AnimoVariable) variable).getCurrentImage();
		}
		return null;
	}
}
