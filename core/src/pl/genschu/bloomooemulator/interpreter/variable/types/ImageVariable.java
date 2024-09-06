package pl.genschu.bloomooemulator.interpreter.variable.types;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.TextureData;
import pl.genschu.bloomooemulator.interpreter.exceptions.ClassMethodNotImplementedException;
import pl.genschu.bloomooemulator.interpreter.Context;
import pl.genschu.bloomooemulator.interpreter.variable.Attribute;
import pl.genschu.bloomooemulator.interpreter.variable.Method;
import pl.genschu.bloomooemulator.interpreter.variable.Parameter;
import pl.genschu.bloomooemulator.interpreter.variable.Variable;
import pl.genschu.bloomooemulator.loader.ImageLoader;
import pl.genschu.bloomooemulator.objects.Image;
import pl.genschu.bloomooemulator.objects.Rectangle;
import pl.genschu.bloomooemulator.utils.ArgumentsHelper;

import java.util.List;

public class ImageVariable extends Variable implements Cloneable {
	Image image;
	private int posX;
	private int posY;
	private float opacity;
	private Rectangle rect;
	private Rectangle clippingRect;

	private boolean isVisible = true;

	public ImageVariable(String name, Context context) {
		super(name, context);

		rect = new Rectangle(0, 0, 0, 0);
		clippingRect = null;
		opacity = 255;
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
				if(image == null) return new IntegerVariable("", 0, context);
				else {
					if(image.getImageTexture() == null) return new IntegerVariable("", 0, context);
					else {
						Pixmap pixmap = image.getImageTexture().getTextureData().consumePixmap();
						Color color = new Color(pixmap.getPixel(posX, posY));
						int alpha = (int) (color.a * 255f);
						return new IntegerVariable("", alpha, context);
					}
				}
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
				if(image == null) return new IntegerVariable("", 0, context);
				else {
					if(image.getImageTexture() == null) return new IntegerVariable("", 0, context);
					else {
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
				}
			}
		});
		this.setMethod("GETPOSITIONX", new Method(
				"INTEGER"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				return new IntegerVariable("", posX+image.offsetX, context);
			}
		});
		this.setMethod("GETPOSITIONY", new Method(
				"INTEGER"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				return new IntegerVariable("", posY+image.offsetY, context);
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
				// TODO: implement this method
				throw new ClassMethodNotImplementedException("Method MERGEALPHA is not implemented yet");
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
	public void setAttribute(String name, Attribute attribute) {
		List<String> knownAttributes = List.of("FILENAME", "MONITORCOLLISION", "MONITORCOLLISIONALPHA", "PRELOAD", "PRIORITY", "RELEASE", "TOCANVAS", "VISIBLE");
		if(knownAttributes.contains(name)) {
			super.setAttribute(name, attribute);
			if(name.equals("FILENAME")) {
				ImageLoader.loadImage(this);
				updateRect();
			}
			if(name.equals("VISIBLE")) {
				changeVisibility(attribute.getValue().toString().equals("TRUE"));
			}
		}
	}

	public Rectangle getRect() {
		return rect;
	}

	private void updateRect() {
		if(image == null) return;
		rect.setXLeft(posX + image.offsetX);
		rect.setYTop(posY + image.offsetY);
		rect.setXRight(rect.getXLeft() + image.width);
		rect.setYBottom(rect.getYTop() - image.height);
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

	public boolean isVisible() {
		return isVisible
				&&  this.getAttribute("TOCANVAS").getValue().toString().equals("TRUE");
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

	@Override
    public ImageVariable clone() {
        ImageVariable clone = (ImageVariable) super.clone();
		this.rect = new Rectangle(rect.getXLeft(), rect.getYBottom(), rect.getXRight(), rect.getYTop());
        return clone;
    }
}
