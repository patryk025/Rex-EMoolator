package pl.cba.genszu.amcodetranslator.interpreter.variable;

import pl.cba.genszu.amcodetranslator.interpreter.Context;
import pl.cba.genszu.amcodetranslator.interpreter.exceptions.VariableUnsupportedOperationException;
import pl.cba.genszu.amcodetranslator.interpreter.variable.types.BoolVariable;
import pl.cba.genszu.amcodetranslator.interpreter.variable.types.DoubleVariable;
import pl.cba.genszu.amcodetranslator.interpreter.variable.types.IntegerVariable;
import pl.cba.genszu.amcodetranslator.interpreter.variable.types.StringVariable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public abstract class Variable {
	protected String name;
	protected Map<String, Attribute> attributes;
	protected Map<String, List<Method>> methods;
	protected Map<String, Signal> signals;
	protected Context context;

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
			public Object execute(List<Object> arguments) {
				// TODO: implement this method
				return null;
			}
		});
		this.setMethod("CLONE", new Method(
				"void?"
		) {
			@Override
			public Object execute(List<Object> arguments) {
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
			public Object execute(List<Object> arguments) {
				// TODO: implement this method
				return null;
			}
		});
		this.setMethod("GETCLONEINDEX", new Method(
				"INTEGER"
		) {
			@Override
			public Object execute(List<Object> arguments) {
				// TODO: implement this method
				return null;
			}
		});
		this.setMethod("MSGBOX", new Method(
				"void"
		) {
			@Override
			public Object execute(List<Object> arguments) {
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
			public Object execute(List<Object> arguments) {
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
		return (Variable) method.execute(List.of(params));
	}

	public void setAttribute(String name, Attribute attribute) {
		attributes.put(name, attribute);
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
				if (method.getParameterTypes().equals(paramTypes)) {
					return method;
				}
			}
		}
		throw new NoSuchMethodError("No method found with name: " + name + " and parameter types: " + paramTypes);
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
		String signalName = name;
		if(argument != null) {
			signalName += "^" + argument;
		}
		Signal signal = this.getSignal(signalName);
		if (signal != null) {
			signal.execute(argument);
		}
	}

	public Context getContext() {
		return this.context;
	}

	@Override
	public String toString() {
		return ""; // technicznie zmienne nie będące BOOLem, INTEGERem, DOUBLE, czy STRINGiem podczas próby wypisania wartości wywalają silnik, my zrobimy pustą wartość
	}
}
