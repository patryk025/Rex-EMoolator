package pl.genschu.bloomooemulator.interpreter.variable.types;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.TextureData;
import pl.genschu.bloomooemulator.engine.decision.events.AnimoEvent;
import pl.genschu.bloomooemulator.engine.decision.events.ButtonEvent;
import pl.genschu.bloomooemulator.engine.filters.Filter;
import pl.genschu.bloomooemulator.interpreter.exceptions.ClassMethodNotImplementedException;
import pl.genschu.bloomooemulator.interpreter.Context;
import pl.genschu.bloomooemulator.interpreter.variable.Attribute;
import pl.genschu.bloomooemulator.interpreter.variable.Method;
import pl.genschu.bloomooemulator.interpreter.variable.Parameter;
import pl.genschu.bloomooemulator.interpreter.variable.Variable;
import pl.genschu.bloomooemulator.loader.AnimoLoader;
import pl.genschu.bloomooemulator.loader.ImageLoader;
import pl.genschu.bloomooemulator.objects.Event;
import pl.genschu.bloomooemulator.objects.Image;
import pl.genschu.bloomooemulator.objects.Rectangle;
import pl.genschu.bloomooemulator.utils.ArgumentsHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ImageVariable extends Variable implements Cloneable {
	Image image;
	private int posX;
	private int posY;
	private float opacity;
	private Rectangle rect;
	private Rectangle clippingRect;
	private int priority;

	private boolean isVisible = true;
	private boolean monitorCollision = false;

	private Map<String, Rectangle> alphaMasks = new HashMap<>();

	private List<Filter> filters = new ArrayList<>();

	public ImageVariable(String name, Context context) {
		super(name, context);

		rect = new Rectangle(0, 0, 0, 0);
		clippingRect = null;
		opacity = 255;
		priority = 0;
	}

	@Override
	public String getType() {
		return "IMAGE";
	}

	@Override
	protected void setMethods() {
		super.setMethods();

		this.setMethod("GETALPHA", new Method(
				List.of(
						new Parameter("INTEGER", "posX", true),
						new Parameter("INTEGER", "posY", true)
				),
				"INTEGER"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				int posX = ArgumentsHelper.getInteger(arguments.get(0));
				int posY = ArgumentsHelper.getInteger(arguments.get(1));

				if(image == null) return new IntegerVariable("", 0, context);

				if(image.getImageTexture() == null) return new IntegerVariable("", 0, context);

				Pixmap pixmap = image.getImageTexture().getTextureData().consumePixmap();
				Color color = new Color(pixmap.getPixel(posX, posY));
				int alpha = (int) (color.a * 255f);
				return new IntegerVariable("", alpha, context);
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
				return new IntegerVariable("", (rect.getXLeft() + rect.getXRight()) / 2, context);
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
				return new IntegerVariable("", (rect.getYTop() + rect.getYBottom()) / 2, context);
			}
		});
		this.setMethod("GETHEIGHT", new Method(
				"INTEGER"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				return new IntegerVariable("", image.height, context);
			}
		});
		this.setMethod("GETPIXEL", new Method(
				List.of(
						new Parameter("INTEGER", "posX", true),
						new Parameter("INTEGER", "posY", true)
				),
				"INTEGER"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				int posX = ArgumentsHelper.getInteger(arguments.get(0));
				int posY = ArgumentsHelper.getInteger(arguments.get(1));

				if(image == null) return new IntegerVariable("", 0, context);

				if(image.getImageTexture() == null) return new IntegerVariable("", 0, context);

				TextureData textureData = image.getImageTexture().getTextureData();
				if (!textureData.isPrepared()) {
					textureData.prepare();
				}
				Pixmap pixmap = textureData.consumePixmap();
				Color color = new Color(pixmap.getPixel(posX, posY));
				int red = (int) (color.r * 255f);
				int green = (int) (color.g * 255f);
				int blue = (int) (color.b * 255f);

				if(image.colorDepth == 16) {
					int rgb565 = (red & 0xF8) << 8 | (green & 0xFC) << 3 | (blue & 0xF8) >> 3;
					return new IntegerVariable("", rgb565, context);
				}
				else if(image.colorDepth == 15) {
					int rgb555 = (red & 0xF8) << 7 | (green & 0xF8) << 2 | (blue & 0xF8) >> 3;
					return new IntegerVariable("", rgb555, context);
				}
				return new IntegerVariable("", 255, context);
			}
		});
		this.setMethod("GETPOSITIONX", new Method(
				"INTEGER"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				return new IntegerVariable("", posX, context);
			}
		});
		this.setMethod("GETPOSITIONY", new Method(
				"INTEGER"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				return new IntegerVariable("", posY, context);
			}
		});
		this.setMethod("GETWIDTH", new Method(
				"INTEGER"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				return new IntegerVariable("", image.width, context);
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
		this.setMethod("INVALIDATE", new Method(
				"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				throw new ClassMethodNotImplementedException("Method INVALIDATE is not implemented yet");
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
				ImageLoader.loadImage(ImageVariable.this);
				show();
				return null;
			}
		});
		this.setMethod("MERGEALPHA", new Method(
				List.of(
						new Parameter("INTEGER", "posX", true),
						new Parameter("INTEGER", "posY", true),
						new Parameter("STRING", "mask", true)
				),
				"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				int posX = ArgumentsHelper.getInteger(arguments.get(0));
				int posY = ArgumentsHelper.getInteger(arguments.get(1));
				String maskName = ArgumentsHelper.getString(arguments.get(2));

				// Take the mask
				Variable maskVar = context.getVariable(maskName);
				if (!(maskVar instanceof ImageVariable)) {
					Gdx.app.log("ImageVariable", "MERGEALPHA: Mask " + maskName + " is not an IMAGE");
					return null;
				}
				ImageVariable mask = (ImageVariable) maskVar;
				if (mask.getImage() == null) {
					Gdx.app.log("ImageVariable", "MERGEALPHA: Mask " + maskName + " has no image");
					return null;
				}

				// Save the mask
				int maskWidth = mask.getImage().width;
				int maskHeight = mask.getImage().height;
				Rectangle maskRect = new Rectangle(posX, posY + maskHeight, posX + maskWidth, posY);
				alphaMasks.put(maskName, maskRect);

				Gdx.app.log("ImageVariable", "MERGEALPHA: Added mask " + maskName + " at " + posX + "," + posY);

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

				context.getGame().getQuadTree().insert(ImageVariable.this);
				context.getGame().getCollisionMonitoredVariables().add(ImageVariable.this);

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
				updateRect();
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
				context.getGame().getQuadTree().remove(ImageVariable.this);
				context.getGame().getCollisionMonitoredVariables().remove(ImageVariable.this);

				monitorCollision = false;

				return null;
			}
		});
		this.setMethod("SETCLIPPING", new Method(
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
				int xLeft = ArgumentsHelper.getInteger(arguments.get(0));
				int yBottom = ArgumentsHelper.getInteger(arguments.get(1));
				int xRight = ArgumentsHelper.getInteger(arguments.get(2));
				int yTop = ArgumentsHelper.getInteger(arguments.get(3));

				clippingRect = new Rectangle(xLeft, yBottom, xRight, yTop);
				return null;
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
				if(!context.getGame().getCurrentScene().equals("S65_ZAMEK")) // Just testing
					opacity = ArgumentsHelper.getInteger(arguments.get(0)) / 255.0f;
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
				updateRect();
				return null;
			}
		});
		this.setMethod("SETPRIORITY", new Method(
				List.of(
						new Parameter("INTEGER", "priority", true)
				),
				"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				setAttribute("PRIORITY", ""+ArgumentsHelper.getInteger(arguments.get(0)));
				return null;
			}
		});
		this.setMethod("SHOW", new Method(
				"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				show();
				return null;
			}
		});
	}

	@Override
	public void init() {
		initMissingAttributes();
		initAttributes();
		loadImage();
	}

	private void loadImage() {
		String filename = getAttribute("FILENAME").getString();
		if(!filename.endsWith(".IMG")) {
			filename = filename + ".IMG";
		}
		getAttribute("FILENAME").setValue(filename);
		try {
			ImageLoader.loadImage(this);
			updateRect();
		} catch (Exception e) {
			Gdx.app.error("ImageVariable", "Error loading IMAGE variable: " + filename, e);
		}
	}

	private void initMissingAttributes() {
		if(getAttribute("VISIBLE") == null) {
			setAttribute("VISIBLE", new Attribute("BOOL", "TRUE"));
		}
		if (getAttribute("TOCANVAS") == null) {
			setAttribute("TOCANVAS", new Attribute("BOOL", "TRUE"));
		}
		if (getAttribute("PRIORITY") == null) {
			setAttribute("PRIORITY", new Attribute("INTEGER", "0"));
		}
		if (getAttribute("MONITORCOLLISION") == null) {
			setAttribute("MONITORCOLLISION", new Attribute("BOOL", "FALSE"));
		}
		if (getAttribute("MONITORCOLLISIONALPHA") == null) {
			setAttribute("MONITORCOLLISIONALPHA", new Attribute("BOOL", "FALSE"));
		}
	}

	private void initAttributes() {
		changeVisibility(getAttribute("VISIBLE").getBool());
		priority = getAttribute("PRIORITY").getInt();
		monitorCollision = getAttribute("MONITORCOLLISION").getBool();
		//monitorCollisionAlpha = getAttribute("MONITORCOLLISIONALPHA").getBool();
	}

	@Override
	public void setAttribute(String name, Attribute attribute) {
		List<String> knownAttributes = List.of("FILENAME", "MONITORCOLLISION", "MONITORCOLLISIONALPHA", "PRELOAD", "PRIORITY", "RELEASE", "TOCANVAS", "VISIBLE");
		if(knownAttributes.contains(name)) {
			super.setAttribute(name, attribute);
		}
	}

	public Rectangle getRect() {
		return rect;
	}

	private void updateRect() {
		if(image == null) return;

		if(monitorCollision)
			context.getGame().getQuadTree().remove(this); // we need to remove variable from quadtree

		rect.setXLeft(posX);
		rect.setYTop(posY);
		rect.setXRight(rect.getXLeft() + image.width);
		rect.setYBottom(rect.getYTop() - image.height);

		if(monitorCollision)
			context.getGame().getQuadTree().insert(this); // and add it again
	}

	public boolean isAt(int x, int y) {
		// TODO: check how it works
		return x >= rect.getXLeft() && x <= rect.getXRight() && y >= rect.getYTop() && y <= rect.getYBottom();
	}

	public Image getImage() {
		return image;
	}

	public void setImage(Image image) {
		this.image = image;
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

	public float getOpacity() {
		return opacity;
	}

	public void setOpacity(float opacity) {
		this.opacity = opacity;
	}

	public Rectangle getClippingRect() {
		return clippingRect;
	}

	public void setClippingRect(Rectangle clippingRect) {
		this.clippingRect = clippingRect;
	}

	public Map<String, Rectangle> getAlphaMasks() {
		return alphaMasks;
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
    public ImageVariable clone() {
        ImageVariable clone = (ImageVariable) super.clone();
		this.rect = new Rectangle(rect.getXLeft(), rect.getYBottom(), rect.getXRight(), rect.getYTop());
        return clone;
    }
}
