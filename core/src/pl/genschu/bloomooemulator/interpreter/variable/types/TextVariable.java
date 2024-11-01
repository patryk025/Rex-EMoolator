package pl.genschu.bloomooemulator.interpreter.variable.types;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import pl.genschu.bloomooemulator.interpreter.exceptions.ClassMethodNotImplementedException;
import pl.genschu.bloomooemulator.interpreter.Context;
import pl.genschu.bloomooemulator.interpreter.variable.Attribute;
import pl.genschu.bloomooemulator.interpreter.variable.Method;
import pl.genschu.bloomooemulator.interpreter.variable.Parameter;
import pl.genschu.bloomooemulator.interpreter.variable.Variable;
import pl.genschu.bloomooemulator.objects.FontKerning;
import pl.genschu.bloomooemulator.objects.Rectangle;
import pl.genschu.bloomooemulator.utils.ArgumentsHelper;

import java.util.List;

public class TextVariable extends Variable {
	private String text;
	private boolean isVisible;
	private int priority;
	private Rectangle rect;
	private String vJustify;
	private String hJustify;
	private FontVariable font;

	public TextVariable(String name, Context context) {
		super(name, context);
	}

	@Override
	protected void setMethods() {
		super.setMethods();

		this.setMethod("SETJUSTIFY", new Method(
				List.of(
						new Parameter("INTEGER", "xLeft", true),
						new Parameter("INTEGER", "yLower", true),
						new Parameter("INTEGER", "xRight", true),
						new Parameter("INTEGER", "yUpper", true),
						new Parameter("STRING", "hJustify", true),
						new Parameter("STRING", "vJustify", true)
				),
				"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				int xLeft = ArgumentsHelper.getInteger(arguments.get(0));
				int yLower = ArgumentsHelper.getInteger(arguments.get(1));
				int xRight = ArgumentsHelper.getInteger(arguments.get(2));
				int yUpper = ArgumentsHelper.getInteger(arguments.get(3));
				String hJustify = ArgumentsHelper.getString(arguments.get(4));
				String vJustify = ArgumentsHelper.getString(arguments.get(5));

				rect = new Rectangle(xLeft, yLower, xRight, yUpper);
				sethJustify(hJustify);
				setvJustify(vJustify);
				return null;
			}
		});
		this.setMethod("SETTEXT", new Method(
				List.of(
						new Parameter("STRING", "text", true)
				),
				"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				String text = ArgumentsHelper.getString(arguments.get(0));
				setText(text);
				return null;
			}
		});
		this.setMethod("SHOW", new Method(
				"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				isVisible = true;
				return null;
			}
		});
		this.setMethod("HIDE", new Method(
				"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				isVisible = false;
				return null;
			}
		});
	}

	public void renderText(Batch batch) {
		renderText(batch, font, rect.getXLeft(), rect.getYTop(), 2);
	}

	public void renderText(Batch batch, FontVariable fontVariable, float startX, float startY, float lineSpacing) {
		if(fontVariable == null) {
			Gdx.app.error("TextVariable", "Font is not set!");
			return;
		}

		float x = startX;
		float y = startY;

		for (int i = 0; i < text.length(); i++) {
			char currentChar = text.charAt(i);

			if (currentChar == '|') {
				y -= fontVariable.getCharHeight() + lineSpacing;
				x = startX;
				continue;
			}

			TextureRegion charTexture = fontVariable.getCharTexture(currentChar);
			if (charTexture == null) {
				continue;
			}

			FontKerning kerning = fontVariable.getCharKerning(currentChar);

			int leftKerning = kerning.getLeft();
			float adjustedX = x + leftKerning;
			float adjustedY = 600 - y - charTexture.getRegionHeight();

			//Gdx.app.log("TextVariable", "Char: " + currentChar + " x: " + adjustedX + " y: " + y);

			batch.setColor(1, 1, 1, 1);
			batch.setBlendFunction(GL20.GL_ONE_MINUS_SRC_COLOR, GL20.GL_ONE);

			batch.draw(charTexture, adjustedX, adjustedY);

			batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

			int rightKerning = kerning.getRight();
			x += charTexture.getRegionWidth() - rightKerning;
		}
	}

	@Override
	public String getType() {
		return "TEXT";
	}

	@Override
	public void setAttribute(String name, Attribute attribute) {
		List<String> knownAttributes = List.of("FONT", "HJUSTIFY", "MONITORCOLLISION", "MONITORCOLLISIONALPHA", "PRIORITY", "RECT", "TEXT", "TOCANVAS", "VISIBLE", "VJUSTIFY");
		if(knownAttributes.contains(name)) {
			super.setAttribute(name, attribute);
			switch (name) {
				case "PRIORITY":
					priority = Integer.parseInt(getAttribute("PRIORITY").getValue().toString());
					break;
				case "VISIBLE":
					isVisible = attribute.getValue().toString().equals("TRUE");
					break;
				case "RECT":
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
					break;
				case "TEXT":
					text = getAttribute("TEXT").getValue().toString();
					break;
				case "FONT":
					String fontName = getAttribute("FONT").getValue().toString();
					font = context.getVariable(fontName) instanceof FontVariable ? (FontVariable) context.getVariable(fontName) : null;
					break;
			}
		}
	}

	public boolean isVisible() {
		try {
			return isVisible
					&& this.getAttribute("TOCANVAS").getValue().toString().equals("TRUE");
		} catch (NullPointerException e) {
			return false;
		}
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public void setVisible(boolean visible) {
		isVisible = visible;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public Rectangle getRect() {
		return rect;
	}

	public void setRect(Rectangle rect) {
		this.rect = rect;
	}

	public String getvJustify() {
		return vJustify;
	}

	public void setvJustify(String vJustify) {
		this.vJustify = vJustify;
	}

	public String gethJustify() {
		return hJustify;
	}

	public void sethJustify(String hJustify) {
		this.hJustify = hJustify;
	}

	public FontVariable getFont() {
		return font;
	}

	public void setFont(FontVariable font) {
		this.font = font;
	}
}
