package pl.genschu.bloomooemulator.interpreter.variable.types;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import pl.genschu.bloomooemulator.interpreter.Context;
import pl.genschu.bloomooemulator.interpreter.variable.Attribute;
import pl.genschu.bloomooemulator.interpreter.variable.Method;
import pl.genschu.bloomooemulator.interpreter.variable.Parameter;
import pl.genschu.bloomooemulator.interpreter.variable.Variable;
import pl.genschu.bloomooemulator.objects.FontCropping;
import pl.genschu.bloomooemulator.geometry.shapes.Box2D;
import pl.genschu.bloomooemulator.utils.ArgumentsHelper;

import java.util.Arrays;
import java.util.List;

public class TextVariable extends Variable {
	private String text;
	private boolean isVisible;
	private int priority;
	private Box2D rect;
	private String vJustify;
	private String hJustify;
	private FontVariable font;

	public TextVariable(String name, Context context) {
		super(name, context);
		this.text = "";
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

				rect = new Box2D(xLeft, yLower, xRight, yUpper);
				sethJustify(hJustify);
				setvJustify(vJustify);
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
		renderText(batch, font, rect.getXLeft(), rect.getYTop(), 2, hJustify, vJustify);
	}

	public void renderText(Batch batch, FontVariable fontVariable, float startX, float startY, float lineSpacing, String hJustify, String vJustify) {
		if (fontVariable == null) {
			//Gdx.app.error("TextVariable", "Font is not set!");
			return;
		}

		if(text.isEmpty()) {
			return;
		}

		float[] totalTextWidth = calculateTotalTextWidths(fontVariable);
		float totalTextHeight = calculateTotalTextHeight(fontVariable, lineSpacing);

		// Adjust horizontal justification
		if ("CENTER".equals(hJustify)) {
			startX += (getRect().getWidth() / 2f) - (totalTextWidth[0] / 2);
		} else if ("RIGHT".equals(hJustify)) {
			startX += getRect().getWidth() - totalTextWidth[0];
		}

		// Adjust vertical justification
		if ("CENTER".equals(vJustify)) {
			startY += totalTextHeight / 2;
		} else if ("BOTTOM".equals(vJustify)) {
			startY += totalTextHeight;
		}

		float x = startX;
		float y = startY;
		int lineNumber = 0;

		for (int i = 0; i < text.length(); i++) {
			char currentChar = text.charAt(i);

			// Newline handling (using '|' as the newline character)
			if (currentChar == '|') {
				y -= fontVariable.getCharHeight() + lineSpacing;
				if ("CENTER".equals(hJustify)) {
					startX += (getRect().getWidth() / 2f) - (totalTextWidth[0] / 2);
				} else if ("RIGHT".equals(hJustify)) {
					startX += getRect().getWidth() - totalTextWidth[0];
				}
				x = startX;
				continue;
			}

			TextureRegion charTexture = fontVariable.getCharTexture(currentChar);
			if (charTexture == null) {
				continue;  // Skip if the character texture is not found
			}

			FontCropping kerning = fontVariable.getCharCropping(currentChar);
			int leftKerning = kerning.getLeft();
			int rightKerning = kerning.getRight();

			float adjustedX = x + leftKerning;
			float adjustedY = 600-y - charTexture.getRegionHeight();

			// Set color and apply color inversion
			batch.setColor(1, 1, 1, 1);
			batch.draw(charTexture, adjustedX, adjustedY);

			// Move the x position for the next character
			x += charTexture.getRegionWidth() - rightKerning;
		}
	}

	private float[] calculateTotalTextWidths(FontVariable fontVariable) {
		float[] widths = new float[1];
		int lineCount = 0;
		float currentWidth = 0;
		for (int i = 0; i < text.length(); i++) {
			char currentChar = text.charAt(i);
			if (currentChar == '|') {
				widths[lineCount] = currentWidth;
				lineCount++;
				widths = Arrays.copyOf(widths, lineCount + 1);
				currentWidth = 0;
			} else {
				TextureRegion charTexture = fontVariable.getCharTexture(currentChar);
				FontCropping kerning = fontVariable.getCharCropping(currentChar);
				currentWidth += (charTexture.getRegionWidth() - kerning.getRight());
			}
		}
		widths[lineCount] = currentWidth;
		return widths;
	}

	private float calculateTotalTextHeight(FontVariable fontVariable, float lineSpacing) {
		int lineCount = 1;
		for (int i = 0; i < text.length(); i++) {
			if (text.charAt(i) == '|') {
				lineCount++;
			}
		}
		return lineCount * (fontVariable.getCharHeight() + lineSpacing) - lineSpacing;
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
						rect = new Box2D(xLeft, yBottom-height, xRight, yTop-height);
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
				case "HJUSTIFY":
					hJustify = getAttribute("HJUSTIFY").getValue().toString();
					break;
				case "VJUSTIFY":
					vJustify = getAttribute("VJUSTIFY").getValue().toString();
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

	public Box2D getRect() {
		return rect;
	}

	public void setRect(Box2D rect) {
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
