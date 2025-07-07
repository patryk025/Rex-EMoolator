package pl.genschu.bloomooemulator.interpreter.variable;

import com.badlogic.gdx.Gdx;
import pl.genschu.bloomooemulator.engine.debug.PerformanceMonitor;
import pl.genschu.bloomooemulator.interpreter.Context;
import pl.genschu.bloomooemulator.interpreter.exceptions.BreakException;
import pl.genschu.bloomooemulator.interpreter.exceptions.ClassMethodNotFoundException;
import pl.genschu.bloomooemulator.interpreter.exceptions.ClassMethodNotImplementedException;
import pl.genschu.bloomooemulator.interpreter.exceptions.VariableUnsupportedOperationException;
import pl.genschu.bloomooemulator.interpreter.factories.VariableFactory;
import pl.genschu.bloomooemulator.interpreter.variable.types.*;
import pl.genschu.bloomooemulator.loader.CNVParser;
import pl.genschu.bloomooemulator.utils.ArgumentsHelper;
import pl.genschu.bloomooemulator.utils.SignalAndParams;

import java.util.*;

import static pl.genschu.bloomooemulator.interpreter.util.VariableHelper.getVariableFromObject;

public abstract class Variable implements Cloneable {
	protected String name;
	protected Map<String, Attribute> attributes;
	protected Map<String, List<Method>> methods;
	protected Map<String, Signal> signals;
	protected Context context;
	protected List<Variable> clones;

	private String iniSection;

	private Map<String, String> pendingSignals = new HashMap<>(); // unprocessed signals

	private Map<Variable, Boolean> isCollidingMap = new HashMap<>();

	public Variable(String name, Context context) {
		this.name = name;
		this.attributes = new HashMap<>();
		this.methods = new HashMap<>();
		this.signals = new HashMap<>();
		this.clones = new ArrayList<>();
		this.context = context;

		this.setMethods();

		try {
			this.iniSection = context.getGame().getCurrentScene().toUpperCase();
		} catch (Exception e) {
			this.iniSection = "NO_SCENE";
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	protected void setMethods() {
		this.setMethod("ADDBEHAVIOUR", new Method(
				List.of(
						new Parameter("STRING", "signalName", true),
						new Parameter("STRING", "behaviourName", true)
				),
				"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				String signalName = ArgumentsHelper.getString(arguments.get(0));
				String behaviourName = ArgumentsHelper.getString(arguments.get(1));

				if(signalName.contains("$")) {
					signalName = signalName.replace("$", "^");
				}

				SignalAndParams signalAndParams = new CNVParser().processEventCode(behaviourName, context);

				if (signalAndParams == null || signalAndParams.behaviourVariable == null) {
					Gdx.app.error("VARIABLE", "Error in ADDBEHAVIOUR: Failed to get behaviour variable for signal " + signalName);
					return null;
				}

				String finalSignalName = signalName;
				setSignal(signalName, new Signal() {
					@Override
					public void execute(Object argument) {
						List<Object> arguments = new ArrayList<>();
						if(signalAndParams.params != null)
							for(String param : signalAndParams.params) {
								arguments.add(getVariableFromObject(param, context));
							}
						Variable oldThis = signalAndParams.behaviourVariable.getContext().getThisVariable();
						signalAndParams.behaviourVariable.getContext().setThisVariable(Variable.this);
						signalAndParams.behaviourVariable.getMethod(signalAndParams.behaviourVariable.getAttribute("CONDITION") != null ? "RUNC" : "RUN", Collections.singletonList("mixed"))
								.execute(!arguments.isEmpty() ? arguments : null);
						signalAndParams.behaviourVariable.getContext().setThisVariable(oldThis);
						Gdx.app.log("Signal", "Signal " + finalSignalName + " done");
					}
				});
				return null;
			}
		});
		this.setMethod("CLONE", new Method(
				List.of(
						new Parameter("INTEGER", "amount", false)
				),
				"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				int amount = 1;
				if(!arguments.isEmpty()) {
					amount = ArgumentsHelper.getInteger(arguments.get(0));
				}

				for(int i = 0; i < amount; i++) {
					Variable cloneVar = Variable.this.clone();
					String newName = cloneVar.getName()+"_"+(getClones().size()+1);
					cloneVar.setName(newName);
					context.setVariable(newName, cloneVar);
					clones.add(cloneVar);
				}

				return null;
			}
		});
		this.setMethod("GETCLONEINDEX", new Method(
				"INTEGER"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				if(getName().contains("_"))
					return new IntegerVariable("", Integer.parseInt(getName().substring(getName().lastIndexOf("_") + 1)), context);
				return new IntegerVariable("", 0, context);
			}
		});
		this.setMethod("MSGBOX", new Method(
				"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				throw new ClassMethodNotImplementedException("Method MSGBOX is not implemented yet");
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
				String behaviourName = ArgumentsHelper.getString(arguments.get(0));

				removeSignal(behaviourName);
				return null;
			}
		});
		this.setMethod("SEND", new Method(
				List.of(
						new Parameter("STRING", "signal", true)
				),
				"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				String signal = ArgumentsHelper.getString(arguments.get(0));

				emitSignal("ONSIGNAL", signal);
				return null;
			}
		});
	}

	public String getType() {
		return "VOID";
	}

	public Object getValue() {
		return null;
	}

	@Override
	public Variable clone() {
        try {
            Variable cloneVar = (Variable) super.clone();

			cloneVar.attributes = new HashMap<>();
			for(Map.Entry<String, Attribute> entry : this.attributes.entrySet()) {
				Attribute currentAttr = entry.getValue();
				cloneVar.attributes.put(entry.getKey(), new Attribute(currentAttr.getType(), currentAttr.getValue().toString()));
			}
			cloneVar.methods = new HashMap<>();
			cloneVar.setMethods();
			cloneVar.signals = new HashMap<>();
			cloneVar.context = this.context;
			cloneVar.clones = new ArrayList<>();

			return cloneVar;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e); // it should not be thrown
        }
    }

	public void init() {
		// this method initialises the variable, in this place is no-op, every class should override it
	}

	public Variable fireMethod(String methodName, Object... params) {
		List<String> paramsTypes = new ArrayList<>();
		for(Object param : params) {
			paramsTypes.add(((Variable) param).getType());
		}
		Method method = this.getMethod(methodName, paramsTypes);
		try {
			PerformanceMonitor.startOperation("(" + this.getType() + ") " + methodName);
			return method.execute(List.of(params));
		} catch (ClassMethodNotFoundException | ClassMethodNotImplementedException | ClassCastException e) {
			Gdx.app.error("Variable", "Method call error in variable " + this.getName() + " of class " + this.getType() + ": " + e.getMessage(), e);
			return null;
		} finally {
			PerformanceMonitor.endOperation("(" + this.getType() + ") " + methodName);
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
		Method toReturn = null;
		if (methodList != null) {
			for (Method method : methodList) {
				List<String> methodParamTypes = method.getParameterTypes();

				boolean foundVarargs = false;
				String varargsType = null;

				for (String methodParamType : methodParamTypes) {
                    if (methodParamType.endsWith("...")) {
                        foundVarargs = true;
                        varargsType = methodParamType.substring(0, methodParamType.length() - 3);
                        break;
                    }
                }

				if (foundVarargs) {
					List<String> baseMethodParamTypes = new ArrayList<>(methodParamTypes);
                    baseMethodParamTypes.removeIf(param -> param.endsWith("..."));

					if (paramTypes.size() >= baseMethodParamTypes.size()) {
						while (baseMethodParamTypes.size() < paramTypes.size()) {
							baseMethodParamTypes.add(varargsType);
						}
					}

					methodParamTypes = baseMethodParamTypes;
				}

				if (isParameterListMatching(methodParamTypes, paramTypes)) {
					if (toReturn == null || toReturn.getParameterTypes().size() < method.getParameterTypes().size()) {
						toReturn = method;
					}
				}
			}
		}

		if (toReturn != null) {
			return toReturn;
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

		String[] primitiveMethods = {"INTEGER", "DOUBLE", "BOOL", "STRING", "EXPRESSION" /* expression returns primitives */};
        return Arrays.asList(primitiveMethods).contains(methodParamType) && Arrays.asList(primitiveMethods).contains(paramType);
    }

	public void setSignal(String name, Signal signal) {
		signals.put(name, signal);
	}

	public Signal getSignal(String name) {
		return signals.get(name);
	}
    
    public void removeSignal(String name) {
        signals.remove(name);
    }

	public void emitSignal(String name) {
		this.emitSignal(name, null);
	}

	public void emitSignal(String name, Object argument) {
		Gdx.app.log("Variable", "Emitting signal " + name + " for variable " + this.getName() + " with argument " + argument);
		// Gdx.app.log("Variable", "Setting THIS variable to " + this.getName());
		Variable oldThis = context.getThisVariable();
		if(!(this instanceof ConditionVariable) && !(this instanceof BehaviourVariable)) {
			context.setThisVariable(this);
		}

		try {
			String signalName = name;
			if (argument != null) {
				signalName += "^" + argument;
			}
			Signal signal = this.getSignal(signalName);
			if (signal != null) {
				Gdx.app.log(this.getClass().getSimpleName(), "Executing signal " + signalName + "...");
				signal.execute(argument);
			} else {
				//Gdx.app.log(this.getClass().getSimpleName(), "Signal "+signalName+" not found. Looking for generic "+name+"...");
				signal = this.getSignal(name);
				if (signal != null) {
					Gdx.app.log(this.getClass().getSimpleName(), "Executing signal " + name + "...");
					signal.execute(null);
				}
				/*else {
					Gdx.app.log(this.getClass().getSimpleName(), "Signal "+name+" not found. Omitting...");
				}*/
			}
		} catch (BreakException ignored) {} // simple break

		context.setThisVariable(oldThis);
	}

	protected void set(Object value) {
		Object currentValue = getAttribute("VALUE").getValue();
		getAttribute("VALUE").setValue(value);
		Gdx.app.log("Variable", "Emitting signal ONBRUTALCHANGED for variable " + this.getName() + ", class type " + this.getType());
		emitSignal("ONBRUTALCHANGED", value.toString());
		if (!value.toString().equals(currentValue.toString())) {
			Gdx.app.log("Variable", "Emitting signal ONCHANGED for variable " + this.getName() + ", class type " + this.getType());
			emitSignal("ONCHANGED", value.toString());
		}

		if (getAttribute("TOINI") != null && getAttribute("TOINI").getBool()) {
			String section = getIniSection();

			if (section == null) {
				section = context.getGame().findINISectionForVariable(this.getName().toUpperCase());
				setIniSection(section);
			}

			Gdx.app.log("Variable", "Saving variable " + this.getName() + " to INI section " + section);
			context.getGame().getGameINI().put(section, this.getName().toUpperCase(), value.toString());
		}
	}

	public boolean isColliding(Variable variable) {
		return this.isCollidingMap.containsKey(variable);
	}

	public void setColliding(Variable variable) {
		this.isCollidingMap.put(variable, true);
		this.emitSignal("ONCOLLISION", variable.getName());
	}

	public void releaseColliding(Variable variable) {
		this.isCollidingMap.remove(variable);
		this.emitSignal("ONCOLLISIONFINISHED", variable.getName());
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

	public List<Variable> getClones() {
		return clones;
	}

	public void setClones(List<Variable> clones) {
		this.clones = clones;
	}

	public Map<String, Signal> getSignals() {
		return signals;
	}

	public String getIniSection() {
		return iniSection;
	}

	public void setIniSection(String iniSection) {
		this.iniSection = iniSection;
	}
}
