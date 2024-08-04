package pl.genschu.bloomooemulator.interpreter.variable;

import com.badlogic.gdx.Gdx;
import pl.genschu.bloomooemulator.interpreter.Context;
import pl.genschu.bloomooemulator.interpreter.exceptions.ClassMethodNotFoundException;
import pl.genschu.bloomooemulator.interpreter.exceptions.ClassMethodNotImplementedException;
import pl.genschu.bloomooemulator.interpreter.exceptions.VariableUnsupportedOperationException;
import pl.genschu.bloomooemulator.interpreter.variable.types.BoolVariable;
import pl.genschu.bloomooemulator.interpreter.variable.types.DoubleVariable;
import pl.genschu.bloomooemulator.interpreter.variable.types.IntegerVariable;
import pl.genschu.bloomooemulator.interpreter.variable.types.StringVariable;

import java.util.*;

public abstract class Variable {
	protected String name;
	protected Map<String, Attribute> attributes;
	protected Map<String, List<Method>> methods;
	protected Map<String, Signal> signals;
	protected Context context;

	private Map<String, String> pendingSignals = new HashMap<>(); // unprocessed signals

	public Variable(String name, Context context) {
		this.name = name;
		this.attributes = new HashMap<>();
		this.methods = new HashMap<>();
		this.signals = new HashMap<>();
		this.context = context;

		this.setMethod("ADDBEHAVIOUR", new Method(
				List.of(
						new Parameter("STRING", "behaviourName", true),
						new Parameter("STRING", "behaviourVariable", true)
				),
				"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				return null;
			}
		});
		this.setMethod("CLONE", new Method(
				"void?"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				return null;
			}
		});
		this.setMethod("CLONE", new Method(
				List.of(
						new Parameter("INTEGER", "amount", true)
				),
				"void?"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				return null;
			}
		});
		this.setMethod("GETCLONEINDEX", new Method(
				"INTEGER"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				return null;
			}
		});
		this.setMethod("MSGBOX", new Method(
				"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				return null;
			}
		});
		this.setMethod("REMOVEBEHAVIOUR", new Method(
				List.of(
						new Parameter("STRING", "behaviourName", true)
				),
				"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				return null;
			}
		});
	}

	public String getName() {
		return name;
	}

	public Variable convertTo(String type) {
		if(this.getType().equals(type)) // typ jest ten sam i nie ma po co konwertować
			return this;

		if(!(this.getType().equals("STRING") || this.getType().equals("BOOL") || this.getType().equals("INTEGER") || this.getType().equals("DOUBLE"))) {
			throw new VariableUnsupportedOperationException(this, "CONV");
		}

		if(!(type.equals("STRING") || type.equals("BOOL") || type.equals("INTEGER") || type.equals("DOUBLE"))) {
			throw new VariableUnsupportedOperationException(this, type, "CONV");
		}

		switch(this.getType()) {
			case "INTEGER":
				return ((IntegerVariable) this).convert(type);
			case "DOUBLE":
				return ((DoubleVariable) this).convert(type);
			case "BOOL":
				return ((BoolVariable) this).convert(type);
			case "STRING":
				return ((StringVariable) this).convert(type);
			default:
				return this;
		}
	}

	public String getType() {
		return "VOID";
	}

	public Object getValue() {
		return null;
	}

	public Variable fireMethod(String methodName, Object... params) {
		List<String> paramsTypes = new ArrayList<>();
		for(Object param : params) {
			paramsTypes.add(((Variable) param).getType());
		}
		Method method = this.getMethod(methodName, paramsTypes);
		try {
			return method.execute(List.of(params));
		} catch (ClassMethodNotFoundException | ClassMethodNotImplementedException | ClassCastException e) {
			Gdx.app.error("Variable", "Method call error in class " + this.getType() + ": " + e.getMessage());
			return null;
		}
    }

	public void setAttribute(String name, Attribute attribute) {
		attributes.put(name, attribute);
	}

	public void setAttribute(String name, String attribute) {
		setAttribute(name, new Attribute(name, attribute));
	}

	public Attribute getAttribute(String name) {
		return attributes.get(name);
	}

	public void setMethod(String name, Method method) {
		if (!methods.containsKey(name)) {
			methods.put(name, new ArrayList<>());
		}
		methods.get(name).add(method);
	}

	public Method getMethod(String name, List<String> paramTypes) {
		List<Method> methodList = methods.get(name);
		if (methodList != null) {
			for (Method method : methodList) {
				List<String> methodParamTypes = method.getParameterTypes();

				int argsCount = paramTypes.size();

				for(int i = 0; i < methodParamTypes.size(); i++) {
					String methodParamType = methodParamTypes.get(i);
					if(methodParamType.endsWith("...")) {
						if(i != methodParamTypes.size() - 1) {
							Gdx.app.error("Variable", "VarArgs can be only the last parameter in method " + name + ". Other situations are not supported for now. Probably it's a error in method definition.");
						}
						else {
							String tmpMethodParamType = methodParamType.substring(0, methodParamType.length() - 3);
							methodParamTypes = methodParamTypes.subList(0, i);

							while(methodParamTypes.size() < argsCount) {
								methodParamTypes.add(tmpMethodParamType);
							}
						}

						break;
					}
				}

				if (isParameterListMatching(methodParamTypes, paramTypes)) {
					return method;
				}
			}
		}
		throw new ClassMethodNotFoundException(name, paramTypes);
	}

	private boolean isParameterListMatching(List<String> methodParamTypes, List<String> paramTypes) {
		int requiredParams = 0;
		if (methodParamTypes.size() != paramTypes.size()) {
			// get number of required parameters
			for (String methodParamType : methodParamTypes) {
				if (!methodParamType.endsWith("?")) {
					requiredParams++;
				}
			}

			if(paramTypes.size() < requiredParams) {
				return false;
			}
		}
		else {
			requiredParams = paramTypes.size();
		}

		for (int i = 0; i < requiredParams; i++) {
			String methodParamType = methodParamTypes.get(i);
			String paramType = paramTypes.get(i);

			if (!isTypeCompatible(methodParamType, paramType)) {
				return false;
			}
		}
		return true;
	}

	private boolean isTypeCompatible(String methodParamType, String paramType) {
		if(methodParamType.endsWith("?")) {
			methodParamType = methodParamType.substring(0, methodParamType.length() - 1);
		}

		if (methodParamType.equals(paramType)) {
			return true;
		}

		if(methodParamType.equals("mixed")) {
			return true;
		}

		String[] primitiveMethods = {"INTEGER", "DOUBLE", "BOOL", "STRING"};
        return Arrays.asList(primitiveMethods).contains(methodParamType) && Arrays.asList(primitiveMethods).contains(paramType);
    }

	public void setSignal(String name, Signal signal) {
		signals.put(name, signal);
	}

	public Signal getSignal(String name) {
		return signals.get(name);
	}

	public void emitSignal(String name) {
		this.emitSignal(name, null);
	}

	public void emitSignal(String name, Object argument) {
		Gdx.app.log("Variable", "Emitting signal " + name + " with argument " + argument);
		String signalName = name;
		if(argument != null) {
			signalName += "^" + argument;
		}
		Signal signal = this.getSignal(signalName);
		if (signal != null) {
			signal.execute(argument);
		}
		else {
			signal = this.getSignal(name);
			if (signal != null) {
				signal.execute(null);
			}
		}
	}

	protected void set(Object value) {
		Object currentValue = getAttribute("VALUE").getValue();
		if(value.toString().equals(currentValue.toString())) {
			Gdx.app.log("Variable", "Emitting signal ONBRUTALCHANGED for variable " + this.getName() + ", class type " + this.getType());
			emitSignal("ONBRUTALCHANGED", value.toString());
		}
		else {
			Gdx.app.log("Variable", "Emitting signal ONCHANGED for variable " + this.getName() + ", class type " + this.getType());
			emitSignal("ONCHANGED", value.toString());
		}
		getAttribute("VALUE").setValue(value);
	}

	public Context getContext() {
		return this.context;
	}

	public void setContext(Context context) {
		this.context = context;
	}

	@Override
	public String toString() {
		return ""; // technicznie zmienne nie będące BOOLem, INTEGERem, DOUBLE, czy STRINGiem podczas próby wypisania wartości wywalają silnik, my zrobimy pustą wartość
	}

	public void addPendingSignal(String signalName, String signalCode) {
		pendingSignals.put(signalName, signalCode);
	}

	public Map<String, String> getPendingSignals() {
		return pendingSignals;
	}
}
