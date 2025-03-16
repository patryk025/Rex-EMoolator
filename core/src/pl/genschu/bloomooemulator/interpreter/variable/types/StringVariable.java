package pl.genschu.bloomooemulator.interpreter.variable.types;

import com.badlogic.gdx.Gdx;
import pl.genschu.bloomooemulator.interpreter.exceptions.ClassMethodNotImplementedException;
import pl.genschu.bloomooemulator.interpreter.Context;
import pl.genschu.bloomooemulator.interpreter.factories.VariableFactory;
import pl.genschu.bloomooemulator.interpreter.variable.Attribute;
import pl.genschu.bloomooemulator.interpreter.variable.Method;
import pl.genschu.bloomooemulator.interpreter.variable.Parameter;
import pl.genschu.bloomooemulator.interpreter.variable.Variable;
import pl.genschu.bloomooemulator.utils.ArgumentsHelper;
import pl.genschu.bloomooemulator.utils.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

public class StringVariable extends Variable {
	public StringVariable(String name, String value, final Context context) {
		super(name, context);
		setAttribute("VALUE", new Attribute("STRING", value));
		super.setAttribute("INIT_VALUE", new Attribute("STRING", value));
	}

	@Override
	public String getType() {
		return "STRING";
	}

	@Override
	public Object getValue() {
		return this.getAttribute("VALUE").getValue();
	}

	@Override
	protected void setMethods() {
		super.setMethods();

		this.setMethod("ADD", new Method(
				List.of(
						new Parameter("STRING", "stringValue", true)
				),
				"STRING"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				String value = GET();
				set(value + ArgumentsHelper.getString(arguments.get(0)));
				return StringVariable.this;
			}
		});
		this.setMethod("CHANGEAT", new Method(
				List.of(
						new Parameter("INTEGER", "index", true),
						new Parameter("STRING", "stringValue", true)
				),
				"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				//TODO: do sprawdzenia
				String value = GET();
				int index = ArgumentsHelper.getInteger(arguments.get(0));
				value = value.substring(0, index) + ArgumentsHelper.getString(arguments.get(1)) + value.substring(index + 1);
				set(value);
				return null;
			}
		});
		this.setMethod("COPYFILE", new Method(
				List.of(
						new Parameter("STRING", "source", true),
						new Parameter("STRING", "destination", true)
				),
				"BOOL"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				String sourcePathString = FileUtils.resolveRelativePath(StringVariable.this, ArgumentsHelper.getString(arguments.get(0)));
				String destinationPathString = FileUtils.resolveRelativePath(StringVariable.this, ArgumentsHelper.getString(arguments.get(1)));

				File sourceFile = new File(sourcePathString);
				File destinationFile = new File(destinationPathString);

				if (!sourceFile.exists()) {
					Gdx.app.error("COPYFILE", "Source file does not exist: " + sourcePathString);
					return VariableFactory.createVariable("BOOL", "COPYFILE_RESULT", "FALSE", getContext());
				}

				FileInputStream inputStream = null;
				FileOutputStream outputStream = null;
				try {
					inputStream = new FileInputStream(sourceFile);
					outputStream = new FileOutputStream(destinationFile);

					byte[] buffer = new byte[1024];
					int length;
					while ((length = inputStream.read(buffer)) > 0) {
						outputStream.write(buffer, 0, length);
					}

					return VariableFactory.createVariable("BOOL", "COPYFILE_RESULT", "TRUE", getContext());
				} catch (IOException e) {
					Gdx.app.error("COPYFILE", "Error copying file from " + sourcePathString + " to " + destinationPathString, e);
					return VariableFactory.createVariable("BOOL", "COPYFILE_RESULT", "FALSE", getContext());
				} finally {
					try {
						if (inputStream != null) {
							inputStream.close();
						}
						if (outputStream != null) {
							outputStream.close();
						}
					} catch (IOException e) {
						Gdx.app.error("COPYFILE", "Error closing file streams", e);
					}
				}
			}
		});
		this.setMethod("CUT", new Method(
				List.of(
						new Parameter("INTEGER", "index", true),
						new Parameter("INTEGER", "length", true)
				),
				"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				String value = GET();
				int index = ArgumentsHelper.getInteger(arguments.get(0));
				int length = ArgumentsHelper.getInteger(arguments.get(1));
				value = value.substring(index, index + length);
				set(value);
				return null;
			}
		});
		this.setMethod("FIND", new Method(
				List.of(
						new Parameter("STRING", "needle", true),
						new Parameter("INTEGER", "offset", false)
				),
				"INTEGER"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				String value = GET();
				String needle = ArgumentsHelper.getString(arguments.get(0));
				int offset = 0;
				if (arguments.size() > 1 && arguments.get(1) != null) {
					offset = ArgumentsHelper.getInteger(arguments.get(1));
				}
				int index = value.indexOf(needle, offset);
				return VariableFactory.createVariable("INTEGER", "", String.valueOf(index), context);
			}
		});
		this.setMethod("GET", new Method(
				List.of(
						new Parameter("INTEGER", "index", false),
						new Parameter("INTEGER", "length", false)
				),
				"STRING"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				String value = GET();
				int index = 0;
				if(arguments.isEmpty()) {
					return StringVariable.this;
				}
				if(arguments.get(0) != null)
					index = ArgumentsHelper.getInteger(arguments.get(0));
				int length = 1;
				if (arguments.size() > 1 && arguments.get(1) != null) {
					length = ArgumentsHelper.getInteger(arguments.get(1));
				}
				int endIndex = Math.min(index + length, value.length());
				try {
					return VariableFactory.createVariable("STRING", "", value.substring(index, endIndex), context);
				} catch (StringIndexOutOfBoundsException e) {
					return VariableFactory.createVariable("STRING", "", "", context);
				}
			}
		});
		this.setMethod("LENGTH", new Method(
				"INTEGER"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				String value = GET();
				return VariableFactory.createVariable("INTEGER", "", String.valueOf(value.length()), context);
			}
		});
		this.setMethod("REPLACEAT", new Method(
				List.of(
						new Parameter("INTEGER", "index", true),
						new Parameter("STRING", "stringValue", true)
				),
				"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				String value = GET();

				int index = ArgumentsHelper.getInteger(arguments.get(0));
				String stringValue = ArgumentsHelper.getString(arguments.get(1));

				String newValue;
				if (index < 0 || index > value.length()) {
					throw new IndexOutOfBoundsException("Index out of bounds: " + index);
				}

				if (index + stringValue.length() > value.length()) {
					newValue = value.substring(0, index) + stringValue;
				} else {
					newValue = value.substring(0, index) + stringValue + value.substring(index + stringValue.length());
				}

				set(newValue);
				return null;
			}
		});
		this.setMethod("RESETINI", new Method(
				"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				if(getAttribute("DEFAULT") != null) {
					set(getAttribute("DEFAULT").getValue());
				}
				else if(getAttribute("INIT_VALUE") != null) {
					set(getAttribute("INIT_VALUE").getValue());
				}
				else {
					set("");
				}
				return null;
			}
		});
		this.setMethod("SET", new Method(
				List.of(
						new Parameter("STRING", "value", true)
				),
				"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				String value = ArgumentsHelper.getString(arguments.get(0));
				set(value);
				return null;
			}
		});
		this.setMethod("SUB", new Method(
				List.of(
						new Parameter("INTEGER", "index", true),
						new Parameter("INTEGER", "length", true)
				),
				"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				String value = GET();
				int index = ArgumentsHelper.getInteger(arguments.get(0));
				int length = ArgumentsHelper.getInteger(arguments.get(1));

				String newValue = value.substring(0, index) + value.substring(index + length);
				set(newValue);
				return null;
			}
		});
		this.setMethod("UPPER", new Method(
				"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				String value = GET();
				set(value.toUpperCase());
				return null;
			}
		});
	}

	@Override
	public void setAttribute(String name, Attribute attribute) {
		List<String> knownAttributes = List.of("TOINI", "VALUE");
		if(knownAttributes.contains(name)) {
			super.setAttribute(name, attribute);
		}
	}

	public Attribute getAttribute(String name) {
		List<String> knownAttributes = List.of("TOINI", "VALUE");
		if(knownAttributes.contains(name)) {
			return super.getAttribute(name);
		}
		return null;
	}

	public int toInt() {
		return toInt(false);
	}

	public int toInt(boolean clipToBool) {
		try {
			if(clipToBool) {
				return Integer.parseInt((String) this.getValue()) != 0 ? 1 : 0;
			}
			return Integer.parseInt((String) this.getValue());
		}
		catch(NumberFormatException e) {
			return 0;
		}
	}

	public double toDouble() {
		return toDouble(false);
	}

	public double toDouble(boolean clipToBool) {
		try {
			return Double.parseDouble((String) this.getValue());
		}
		catch(NumberFormatException e) {
			return 0;
		}
	}

	public boolean toBool() {
		try {
			return Boolean.parseBoolean((String) this.getValue());
		}
		catch(NumberFormatException e) {
			return false;
		}
	}

	public Variable convert(String type) {
		if(type.equals("DOUBLE")) {
			return new DoubleVariable(this.getName(), this.toDouble(), this.context);
		}
		else if(type.equals("BOOL")) {
			return new BoolVariable(this.getName(), this.toBool(), this.context);
		}
		else if(type.equals("INTEGER")) {
			return new IntegerVariable(this.getName(), this.toInt(), this.context);
		}
		else {
			return this;
		}
	}

	public String GET() {
		return (String) this.getValue();
	}

	@Override
	public String toString() {
		return (String) this.getValue();
	}
}
