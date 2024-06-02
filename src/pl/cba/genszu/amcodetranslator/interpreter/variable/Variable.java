package pl.cba.genszu.amcodetranslator.interpreter.variable;

import pl.cba.genszu.amcodetranslator.interpreter.Context;

import java.util.Map;
import java.util.HashMap;

public abstract class Variable {
	protected String type;
	protected Map<String, Attribute> attributes;
	protected Map<String, Method> methods;
	protected Map<String, Signal> signals;
	protected Context context;

	public Variable(String type, Context context) {
		this.type = type;
		this.attributes = new HashMap<>();
		this.methods = new HashMap<>();
		this.signals = new HashMap<>();
		this.context = context;
	}

	public String getType() {
		return type;
	}

	public void setAttribute(String name, Attribute attribute) {
		attributes.put(name, attribute);
	}

	public Attribute getAttribute(String name) {
		return attributes.get(name);
	}

	public void setMethod(String name, Method method) {
		methods.put(name, method);
	}

	public Method getMethod(String name) {
		return methods.get(name);
	}

	public void setSignal(String name, Signal signal) {
		signals.put(name, signal);
	}

	public Signal getSignal(String name) {
		return signals.get(name);
	}

	public void emitSignal(String name, Object argument) {
		Signal signal = signals.get(name);
		if (signal != null) {
			signal.execute(argument);
		}
	}
}
