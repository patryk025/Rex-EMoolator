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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StringVariable extends Variable {
	private static final Map<String, List<Method>> METHOD_TEMPLATES = createMethodTemplates();

	public StringVariable(String name, String value, final Context context) {
		super(name, context);
		if(value.startsWith("\"") && value.endsWith("\"")) {
			value = value.substring(1, value.length() - 1);
		}
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
		this.methods = METHOD_TEMPLATES;
	}

	private static Map<String, List<Method>> createMethodTemplates() {
		Map<String, List<Method>> methods = newTemplateMap(baseMethodTemplates());

		addMethodTemplate(methods, "ADD", new Method(
				List.of(
						new Parameter("STRING", "stringValue", true)
				),
				"STRING"
		) {
			@Override
			public Variable execute(Variable self, List<Object> arguments) {
				StringVariable selfVar = (StringVariable) self;
				String value = selfVar.GET();
				selfVar.set(value + ArgumentsHelper.getString(arguments.get(0)));
				return selfVar;
			}
		});
		addMethodTemplate(methods, "CHANGEAT", new Method(
				List.of(
						new Parameter("INTEGER", "index", true),
						new Parameter("STRING", "stringValue", true)
				),
				"void"
		) {
			@Override
			public Variable execute(Variable self, List<Object> arguments) {
				StringVariable selfVar = (StringVariable) self;
				//TODO: do sprawdzenia
				String value = selfVar.GET();
				int index = ArgumentsHelper.getInteger(arguments.get(0));
				value = value.substring(0, index) + ArgumentsHelper.getString(arguments.get(1)) + value.substring(index + 1);
				selfVar.set(value);
				return null;
			}
		});
		addMethodTemplate(methods, "COPYFILE", new Method(
				List.of(
						new Parameter("STRING", "source", true),
						new Parameter("STRING", "destination", true)
				),
				"BOOL"
		) {
			@Override
			public Variable execute(Variable self, List<Object> arguments) {
				StringVariable selfVar = (StringVariable) self;
				String sourcePathString = FileUtils.resolveRelativePath(selfVar, ArgumentsHelper.getString(arguments.get(0)));
				String destinationPathString = FileUtils.resolveRelativePath(selfVar, ArgumentsHelper.getString(arguments.get(1)));

				File sourceFile = new File(sourcePathString);
				File destinationFile = new File(destinationPathString);

				if (!sourceFile.exists()) {
					Gdx.app.error("COPYFILE", "Source file does not exist: " + sourcePathString);
					return VariableFactory.createVariable("BOOL", "COPYFILE_RESULT", "FALSE", selfVar.getContext());
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

					return VariableFactory.createVariable("BOOL", "COPYFILE_RESULT", "TRUE", selfVar.getContext());
				} catch (IOException e) {
					Gdx.app.error("COPYFILE", "Error copying file from " + sourcePathString + " to " + destinationPathString, e);
					return VariableFactory.createVariable("BOOL", "COPYFILE_RESULT", "FALSE", selfVar.getContext());
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
		addMethodTemplate(methods, "CUT", new Method(
				List.of(
						new Parameter("INTEGER", "index", true),
						new Parameter("INTEGER", "length", true)
				),
				"void"
		) {
			@Override
			public Variable execute(Variable self, List<Object> arguments) {
				StringVariable selfVar = (StringVariable) self;
				String value = selfVar.GET();
				int index = ArgumentsHelper.getInteger(arguments.get(0));
				int length = ArgumentsHelper.getInteger(arguments.get(1));
				value = value.substring(index, index + length);
				selfVar.set(value);
				return null;
			}
		});
		addMethodTemplate(methods, "FIND", new Method(
				List.of(
						new Parameter("STRING", "needle", true),
						new Parameter("INTEGER", "offset", false)
				),
				"INTEGER"
		) {
			@Override
			public Variable execute(Variable self, List<Object> arguments) {
				StringVariable selfVar = (StringVariable) self;
				String value = selfVar.GET();
				String needle = ArgumentsHelper.getString(arguments.get(0));
				int offset = 0;
				if (arguments.size() > 1 && arguments.get(1) != null) {
					offset = ArgumentsHelper.getInteger(arguments.get(1));
				}
				int index = value.indexOf(needle, offset);
				return new IntegerVariable("", index, selfVar.context);
			}
		});
		addMethodTemplate(methods, "GET", new Method(
				"STRING"
		) {
			@Override
			public Variable execute(Variable self, List<Object> arguments) {
				return (StringVariable) self;
			}
		});
		addMethodTemplate(methods, "LENGTH", new Method(
				"INTEGER"
		) {
			@Override
			public Variable execute(Variable self, List<Object> arguments) {
				StringVariable selfVar = (StringVariable) self;
				String value = selfVar.GET();
				return new IntegerVariable("", value.length(), selfVar.context);
			}
		});
		addMethodTemplate(methods, "REPLACEAT", new Method(
				List.of(
						new Parameter("INTEGER", "index", true),
						new Parameter("INTEGER", "length", true),
						new Parameter("STRING", "replacement", true)
				),
				"void"
		) {
			@Override
			public Variable execute(Variable self, List<Object> arguments) {
				StringVariable selfVar = (StringVariable) self;
				String value = selfVar.GET();
				int index = ArgumentsHelper.getInteger(arguments.get(0));
				int length = ArgumentsHelper.getInteger(arguments.get(1));
				String replacement = ArgumentsHelper.getString(arguments.get(2));

				value = value.substring(0, index) + replacement + value.substring(index + length);
				selfVar.set(value);
				return null;
			}
		});
		addMethodTemplate(methods, "RESETINI", new Method(
				"void"
		) {
			@Override
			public Variable execute(Variable self, List<Object> arguments) {
				StringVariable selfVar = (StringVariable) self;
				if(selfVar.getAttribute("DEFAULT") != null) {
					selfVar.set(selfVar.getAttribute("DEFAULT").getValue());
				}
				else if(selfVar.getAttribute("INIT_VALUE") != null) {
					selfVar.set(selfVar.getAttribute("INIT_VALUE").getValue());
				}
				else {
					selfVar.set("");
				}
				return null;
			}
		});
		addMethodTemplate(methods, "SET", new Method(
				List.of(
						new Parameter("STRING", "value", true)
				),
				"STRING"
		) {
			@Override
			public Variable execute(Variable self, List<Object> arguments) {
				StringVariable selfVar = (StringVariable) self;
				String value = ArgumentsHelper.getString(arguments.get(0));
				selfVar.set(value);
				return null;
			}
		});
		addMethodTemplate(methods, "SUB", new Method(
				List.of(
						new Parameter("INTEGER", "index", true),
						new Parameter("INTEGER", "length", true)
				),
				"void"
		) {
			@Override
			public Variable execute(Variable self, List<Object> arguments) {
				StringVariable selfVar = (StringVariable) self;
				String value = selfVar.GET();
				int index = ArgumentsHelper.getInteger(arguments.get(0));
				int length = ArgumentsHelper.getInteger(arguments.get(1));

				if (index < 0 || index > value.length()) {
					throw new IndexOutOfBoundsException("Index out of bounds: " + index);
				}

				String newValue = value.substring(0, index) + value.substring(index + length);
				selfVar.set(newValue);
				return null;
			}
		});
		addMethodTemplate(methods, "UPPER", new Method(
				"void"
		) {
			@Override
			public Variable execute(Variable self, List<Object> arguments) {
				StringVariable selfVar = (StringVariable) self;
				String value = selfVar.GET();
				selfVar.set(value.toUpperCase());
				return null;
			}
		});

		return Collections.unmodifiableMap(methods);
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
			String numericPrefix = this.GET().replaceFirst("^([-+]?\\d*\\.?\\d+).*", "$1");
			double value = Double.parseDouble(numericPrefix);
			if (clipToBool) {
				return value != 0.0 ? 1 : 0;
			}
			return (int) value; // tu tnie po przecinku, ale przy obliczeniach zaokrągla. Weird
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
			String numericPrefix = this.GET().replaceFirst("^([-+]?\\d*\\.?\\d+([eE][-+]?\\d+)?).*", "$1");
			double value = Double.parseDouble(numericPrefix);
			if (clipToBool) {
				return value != 0.0 ? 1.0 : 0.0;
			}
			return value;
		}
		catch(NumberFormatException e) {
			return 0;
		}
	}
	public boolean toBool() {
		return Boolean.parseBoolean(this.GET());
	}

	public Variable convert(String type) {
        switch (type) {
            case "DOUBLE":
                return new DoubleVariable(this.getName(), this.toDouble(), this.context);
            case "BOOL":
                return new BoolVariable(this.getName(), this.toBool(), this.context);
            case "INTEGER":
                return new IntegerVariable(this.getName(), this.toInt(), this.context);
            default:
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
